package unitedclans.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.ShowClanUtils;

import java.sql.*;
import java.util.*;

public class PlayerJoinEventHandler implements Listener {
    private final JavaPlugin plugin;
    private Connection con;
    public PlayerJoinEventHandler(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent playerJoin) {
        UUID uuid = playerJoin.getPlayer().getUniqueId();
        try {
            Statement stmt = con.createStatement();
            ResultSet rsPlayers = stmt.executeQuery("SELECT UUID FROM PLAYERS WHERE UUID IS '" + uuid + "'" );
            if (!rsPlayers.next()) {
                stmt.executeUpdate("INSERT INTO PLAYERS (UUID, PlayerName, ClanID, ClanRole) VALUES ('" + uuid + "', '" + playerJoin.getPlayer().getName() + "', " + 0 + ", '" + UnitedClans.getInstance().getConfig().getString("roles.no-clan") + "')");
            }
            stmt.close();
            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}