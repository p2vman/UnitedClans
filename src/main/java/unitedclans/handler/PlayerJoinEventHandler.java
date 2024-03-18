package unitedclans.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.ShowClanUtils;
import unitedclans.utils.SqliteDriver;

import java.util.*;


public class PlayerJoinEventHandler implements Listener {
    private final JavaPlugin plugin;
    private SqliteDriver sql;
    public PlayerJoinEventHandler(JavaPlugin plugin, SqliteDriver sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent playerJoin) {
        UUID uuid = playerJoin.getPlayer().getUniqueId();
        try {
            List<Map<String, Object>> rsPlayers = sql.sqlSelectData("UUID", "PLAYERS", "UUID = '" + uuid + "'");
            if (rsPlayers.isEmpty()) {
                Map<String, Object> insertMap = new HashMap<>();
                insertMap.put("UUID", uuid);
                insertMap.put("PlayerName", playerJoin.getPlayer().getName());
                insertMap.put("ClanID", 0);
                insertMap.put("ClanRole", UnitedClans.getInstance().getConfig().getString("roles.no-clan"));
                insertMap.put("Donations", 0);
                sql.sqlInsertData("PLAYERS", insertMap);
            }

            ShowClanUtils.showClan(plugin, sql);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}