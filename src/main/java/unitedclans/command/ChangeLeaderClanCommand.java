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

public class ChangeLeaderClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public ChangeLeaderClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        try {
            Statement stmt = con.createStatement();
            if (args.length != 2) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "INVALID_COMMAND", false);
            }
            String inputClanName = args[0];
            String playerName = args[1];

            ResultSet rsSenderPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String PlayerRole = rsSenderPlayer.getString("ClanRole");
            Integer PlayerClanID = rsSenderPlayer.getInt("ClanID");
            ResultSet rsSenderClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + PlayerClanID);
            String ClanName = rsSenderClan.getString("ClanName");
            if (inputClanName == null || !rsSenderClan.next() || !Objects.equals(inputClanName, ClanName)) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_CLAN_NAME", true);
            }
            if (playerName == null) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_PLAYER_NAME", true);
            }
            ResultSet rsLeaderPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + playerName + "'");
            Integer LeaderPlayerClanID = rsLeaderPlayer.getInt("ClanID");
            if (!rsLeaderPlayer.next()) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_PLAYER_NAME", true);
            }
            if (PlayerClanID == 0) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
            }
            if (!Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "NOT_LEADER", true);
            }
            if (Objects.equals(playerSender.getName(), playerName)) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOU_ALREADY_LEADER", true);
            }
            if (PlayerClanID != LeaderPlayerClanID) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "PLAYER_NOT_YOUR_CLAN", true);
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.elder") + "' WHERE UUID IS '" + uuid + "'");
            stmt.executeUpdate("UPDATE PLAYERS SET ClanRole = '" + PlayerRole + "' WHERE PlayerName IS '" + playerName + "'");

            String successfullychangeleadermsg = LocalizationUtils.langCheck(language, "SUCCESSFULLY_CHANGE_LEADER");
            sender.sendMessage(successfullychangeleadermsg.replace("%player%", playerName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + PlayerClanID);
            String changeleadermsg = LocalizationUtils.langCheck(language, "CHANGE_LEADER");
            while (rsClanPlayers.next()) {
                String playerNameClan = rsClanPlayers.getString("PlayerName");
                if (Objects.equals(playerNameClan, playerSender.getName()) || Objects.equals(playerNameClan, playerName)) {
                    continue;
                }
                Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                playerClan.sendMessage(changeleadermsg.replace("%old-leader%", playerSender.getName()).replace("%new-leader%", playerName));
                playerClan.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            Player argPlayerName = plugin.getServer().getPlayer(playerName);
            if (argPlayerName != null) {
                argPlayerName.sendMessage(LocalizationUtils.langCheck(language, "YOU_HAVE_BEEN_LEADER"));
                argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            stmt.close();

            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}