package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.ShowClanUtils;

import java.sql.*;
import java.util.*;

public class CreateClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public CreateClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length != 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String clanNameInput = args[0];
        String clanColorInput = args[1];
        if (clanNameInput == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongclanname"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        boolean valExist = false;
        String[] colorCollection = new String[] {"AQUA", "BLACK", "BLUE", "DARK_AQUA", "DARK_BLUE", "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE" ,"DARK_RED", "GOLD", "GRAY", "GREEN", "LIGHT_PURPLE", "RED", "WHITE", "YELLOW"};
        for (Object valColor:colorCollection) {
            if (Objects.equals(clanColorInput, valColor.toString())) {
                valExist = true;
            }
        }
        if (!valExist) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongcolorname"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return valExist;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsNumberClans = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanID IS (SELECT MAX(ClanID) FROM CLANS);");
            Integer NumberClans = rsNumberClans.getInt("ClanID") + 1;

            ResultSet rsChekerPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            if (rsChekerPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.youmemberclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "';");
            if (rsChekerClanName.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.clannametaken"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("INSERT INTO CLANS (ClanID, ClanName, ClanColor) " + "VALUES (" + NumberClans + ", '" + clanNameInput + "', '" + clanColorInput + "');");
            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + NumberClans + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.leader") + "' WHERE UUID IS '" + uuid + "';");
            stmt.close();

            ShowClanUtils.showClan(plugin, con);

            String createclanmsg = UnitedClans.getInstance().getConfig().getString("messages.successcreateclan");
            sender.sendMessage(createclanmsg.replace("%clan%", clanNameInput));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}