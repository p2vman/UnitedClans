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

public class LeaveClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public LeaveClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        String clanNameInput = args[0];
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsLeavingPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String LeavingPlayerRole = rsLeavingPlayer.getString("ClanRole");
            Integer LeavingPlayerClanID = rsLeavingPlayer.getInt("ClanID");

            ResultSet rsgetClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "'");
            String getClanName = rsgetClan.getString("ClanName");
            Integer getClanID = rsgetClan.getInt("ClanID");

            if (LeavingPlayerClanID == 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (getClanName == null) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_CLAN_NAME"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (getClanID != LeavingPlayerClanID) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_MEMBER_THIS_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (Objects.equals(LeavingPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_LEADER"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.no-clan") + "' WHERE UUID IS '" + uuid + "'");
            stmt.executeUpdate("UPDATE CLANS SET CountMembers = CountMembers - 1 WHERE ClanID IS " + LeavingPlayerClanID);

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + LeavingPlayerClanID);
            String playerleavemsg = LocalizationUtils.langCheck(language, "PLAYER_LEAVE");
            while (rsClanPlayers.next()) {
                String playerNameClan = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                if (playerClan == null || playerClan == playerSender) {
                    continue;
                }
                playerClan.sendMessage(playerleavemsg.replace("%player%", playerSender.getName()));
            }
            ResultSet rsClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + LeavingPlayerClanID);
            String LeavingPlayerClanName = rsClanName.getString("ClanName");
            String successfullyleftmsg = LocalizationUtils.langCheck(language, "SUCCESSFULLY_LEFT");
            playerSender.sendMessage(successfullyleftmsg.replace("%clan%", LeavingPlayerClanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            stmt.close();

            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}