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

import java.sql.*;
import java.util.*;

public class SetRoleClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public SetRoleClanCommand(JavaPlugin plugin, Connection con) {
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
            String playerNameInput = args[0];
            String playerRoleInput = args[1];

            if (playerNameInput == null) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_PLAYER_NAME", true);
            }
            if ((playerRoleInput == null) || (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.elder"))) && (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.member")))) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_ROLE_NAME", true);
            }
            if (Objects.equals(playerSender.getName(), playerNameInput)) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "SET_ROLE_YOURSELF", true);
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String senderRole = rsSender.getString("ClanRole");
            Integer senderClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(senderRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "NO_RIGHTS_SET_ROLE", true);
            }
            ResultSet rsPlayerName = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + playerNameInput + "'");
            Integer playerClanID = rsPlayerName.getInt("ClanID");
            if (playerClanID == 0) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "PLAYER_NOT_MEMBER_CLAN", true);
            }
            if (senderClanID != playerClanID) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "PLAYER_NOT_YOUR_CLAN", true);
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanRole = '" + playerRoleInput + "' WHERE PlayerName IS '" + playerNameInput + "'");

            String successfullychangedrolemsg = LocalizationUtils.langCheck(language, "SUCCESSFULLY_CHANGED_ROLE");
            String setPlayerRole = null;
            if (Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                setPlayerRole = LocalizationUtils.langCheck(language, "ELDER");
            } else if (Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.member"))) {
                setPlayerRole = LocalizationUtils.langCheck(language, "MEMBER");
            }
            playerSender.sendMessage(successfullychangedrolemsg.replace("%role%", setPlayerRole));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            Player argPlayerName = plugin.getServer().getPlayer(playerNameInput);
            if (argPlayerName != null) {
                String youbeenassignedmsg = LocalizationUtils.langCheck(language, "YOU_BEEN_ASSIGNED");
                argPlayerName.sendMessage(youbeenassignedmsg.replace("%role%", setPlayerRole));
                argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}