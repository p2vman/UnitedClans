package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
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
        try {
            Statement stmt = con.createStatement();
            if (args.length != 2) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "INVALID_COMMAND", false);
            }
            String clanNameInput = args[0];
            String clanColorInput = args[1];

            if (clanNameInput == null) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_CLAN_NAME", true);
            }
            if (clanNameInput.length() < 3 || clanNameInput.length() > 12) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "LENGTH_CLAN_NAME", true);
            }

            boolean valExist = false;
            String[] colorCollection = new String[] {"AQUA", "BLACK", "BLUE", "DARK_AQUA", "DARK_BLUE", "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE" ,"DARK_RED", "GOLD", "GRAY", "GREEN", "LIGHT_PURPLE", "RED", "WHITE", "YELLOW"};
            for (Object valColor:colorCollection) {
                if (Objects.equals(clanColorInput, valColor.toString())) {
                    valExist = true;
                }
            }
            if (!valExist) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_COLOR_NAME", true);
            }

            ResultSet rsNumberClans = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanID IS (SELECT MAX(ClanID) FROM CLANS)");
            Integer NumberClans = rsNumberClans.getInt("ClanID") + 1;

            ResultSet rsChekerPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            if (rsChekerPlayer.getInt("ClanID") != 0) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOU_MEMBER_CLAN", true);
            }

            ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "'");
            if (rsChekerClanName.next()) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "CLAN_NAME_TAKEN", true);
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