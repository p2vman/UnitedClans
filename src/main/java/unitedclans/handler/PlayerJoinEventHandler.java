package unitedclans.handler;

import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import unitedclans.UnitedClans;

import java.sql.*;

public class PlayerJoinEventHandler implements Listener {
    private Connection con;
    public PlayerJoinEventHandler(Connection con) {
        this.con = con;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent playerJoin) {
        UUID uuid = playerJoin.getPlayer().getUniqueId();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT UUID FROM PLAYERS WHERE UUID IS '" + uuid + "';" );
            if (!rs.next()) {
                String tablePLAYERS = "INSERT INTO PLAYERS (UUID, PlayerName, ClanID, ClanRole) " +
                        "VALUES ('" + uuid + "', '" + playerJoin.getPlayer().getName() + "', " + 0 + ", '" + UnitedClans.getInstance().getConfig().getString("roles.noclan") + "');";
                stmt.executeUpdate(tablePLAYERS);
                stmt.close();
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}