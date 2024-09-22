package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class ChangeLeaderClanTabCompleter implements TabCompleter {
    private final DatabaseDriver dbDriver;

    public ChangeLeaderClanTabCompleter(DatabaseDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("<clan name>");
        }
        if (args.length == 2) {
            Player playerSender = (Player) sender;
            UUID uuid = playerSender.getUniqueId();
            String inputPlayer = args[1].toLowerCase();
            List<Map<String, Object>> rsSender = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
            int ClanID = (int) rsSender.get(0).get("clan_id");
            if (ClanID == 0) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> rsPlayerClan = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", ClanID);
            ArrayList<String> onlinePlayers = new ArrayList<>();
            for (Map<String, Object> i : rsPlayerClan) {
                String playerName = (String) i.get("player_name");
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
            return onlinePlayerName;
        }
        return new ArrayList<>();
    }
}
