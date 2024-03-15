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
        if (args.length != 2) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String inputClanName = args[0];
        String playerName = args[1];
        if (inputClanName == null) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_CLAN_NAME"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if (playerName == null) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_PLAYER_NAME"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsSenderPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String PlayerRole = rsSenderPlayer.getString("ClanRole");
            Integer PlayerClanID = rsSenderPlayer.getInt("ClanID");
            ResultSet rsSenderClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + PlayerClanID);
            String ClanName = rsSenderClan.getString("ClanName");
            if (!rsSenderClan.next() || !Objects.equals(inputClanName, ClanName)) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_CLAN_NAME"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsLeaderPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + playerName + "'");
            Integer LeaderPlayerClanID = rsLeaderPlayer.getInt("ClanID");
            if (!rsLeaderPlayer.next()) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_PLAYER_NAME"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (PlayerClanID == 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (!Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "NOT_LEADER"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (Objects.equals(playerSender.getName(), playerName)) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_ALREADY_LEADER"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (PlayerClanID != LeaderPlayerClanID) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "PLAYER_NOT_YOUR_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
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