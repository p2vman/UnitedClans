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

public class DeleteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public DeleteClanCommand(JavaPlugin plugin, Connection con) {
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
            if (args.length != 1) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "INVALID_COMMAND", false);
            }
            String clanNameInput = args[0];

            ResultSet rsgetClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "'");
            String getClanName = rsgetClan.getString("ClanName");
            Integer getClanID = rsgetClan.getInt("ClanID");

            ResultSet rsgetClanPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            Integer getClanIDPlayer = rsgetClanPlayer.getInt("ClanID");
            String getLeaderUUID = rsgetClanPlayer.getString("ClanRole");

            if (getClanName == null) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_CLAN_NAME", true);
            }
            if (getClanIDPlayer == 0) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
            }
            if (getClanID != getClanIDPlayer) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOU_NOT_MEMBER_THIS_CLAN", true);
            }
            if (!Objects.equals(getLeaderUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "NOT_LEADER", true);
            }

            ResultSet rsUUID = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + getClanIDPlayer);
            while (rsUUID.next()) {
                String playerName = rsUUID.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerName);
                if (playerClan == null || playerClan == playerSender) {
                    continue;
                }
                playerClan.sendMessage(LocalizationUtils.langCheck(language, "LEADER_DELETE_CLAN"));
                playerClan.playSound(playerClan.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.no-clan") + "' WHERE ClanID IS " + getClanID);
            stmt.executeUpdate("DELETE FROM CLANS WHERE ClanID IS " + getClanID);
            stmt.close();

            ShowClanUtils.showClan(plugin, con);

            String deleteclanmsg = LocalizationUtils.langCheck(language, "SUCCESS_DELETE_CLAN");
            sender.sendMessage(deleteclanmsg.replace("%clan%", getClanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}