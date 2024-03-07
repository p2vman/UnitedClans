package unitedclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.sql.*;

public class ShowClanUtils {
    public static void showClan (JavaPlugin plugin, Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rsPlayerClan = stmt.executeQuery("SELECT * FROM PLAYERS");
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();
            Statement stmtClan = con.createStatement();
            while (rsPlayerClan.next()) {
                Integer getClanID = rsPlayerClan.getInt("ClanID");
                String getPlayerName = rsPlayerClan.getString("PlayerName");
                ResultSet rsClan = stmtClan.executeQuery( "SELECT * FROM CLANS WHERE ClanID IS " + getClanID);
                String clanName = rsClan.getString("ClanName");
                String clanColor = rsClan.getString("ClanColor");
                Player getPlayer = plugin.getServer().getPlayer(getPlayerName);
                if (getPlayer == null) {
                    continue;
                }
                if (getClanID == 0) {
                    getPlayer.setDisplayName(getPlayer.getName());
                    continue;
                }
                Team clan = board.getTeam(clanName);
                if (clan == null) {
                    clan = board.registerNewTeam(clanName);
                }
                clan.addPlayer(getPlayer);
                clan.setPrefix(ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName) + " ");
                getPlayer.setDisplayName(ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET) + " " + getPlayer.getName());
            }
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                onlinePlayer.setScoreboard(board);
            }
            stmt.close();
            stmtClan.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}