package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
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
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length != 2) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String clanNameInput = args[0];
        String clanColorInput = args[1];
        if (clanNameInput == null) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_CLAN_NAME"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if (clanNameInput.length() < 3 || clanNameInput.length() > 12) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "LENGTH_CLAN_NAME"));
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
            sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_COLOR_NAME"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsNumberClans = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanID IS (SELECT MAX(ClanID) FROM CLANS)");
            Integer NumberClans = rsNumberClans.getInt("ClanID") + 1;

            ResultSet rsChekerPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            if (rsChekerPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "'");
            if (rsChekerClanName.next()) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "CLAN_NAME_TAKEN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("INSERT INTO CLANS (ClanID, ClanName, ClanColor, CountMembers) " + "VALUES (" + NumberClans + ", '" + clanNameInput + "', '" + clanColorInput + "', " + 1 + ")");
            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + NumberClans + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.leader") + "' WHERE UUID IS '" + uuid + "'");
            stmt.close();

            ShowClanUtils.showClan(plugin, con);

            String createclanmsg = LocalizationUtils.langCheck(language, "SUCCESS_CREATE_CLAN");
            sender.sendMessage(createclanmsg.replace("%clan%", clanNameInput));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}