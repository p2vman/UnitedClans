package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class KickClanTabCompleter implements TabCompleter {
    private Connection con;
    public KickClanTabCompleter(Connection con) {
        this.con = con;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            try {
                Statement stmt = con.createStatement();
                Player playerSender = (Player) sender;
                UUID uuid = playerSender.getUniqueId();
                String inputPlayer = args[0].toLowerCase();
                ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
                Integer ClanID = rsSender.getInt("ClanID");
                if (ClanID == 0) {
                    return new ArrayList<>();
                }
                ResultSet rsPlayerClan = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + ClanID);
                ArrayList<String> onlinePlayers = new ArrayList<>();
                while (rsPlayerClan.next()) {
                    String playerName = rsPlayerClan.getString("PlayerName");
                    onlinePlayers.add(playerName);
                }
                List<String> onlinePlayerName = null;
                for (String onlinePlayer : onlinePlayers) {
                    if (onlinePlayer.toString().toLowerCase().startsWith(inputPlayer)) {
                        if (onlinePlayerName == null) {
                            onlinePlayerName = new ArrayList<>();
                        }
                        onlinePlayerName.add(onlinePlayer);
                    }
                }
                if (onlinePlayerName != null) {
                    Collections.sort(onlinePlayerName);
                }
                stmt.close();
                return onlinePlayerName;
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}