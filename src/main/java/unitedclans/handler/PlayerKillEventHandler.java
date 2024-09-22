package unitedclans.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class PlayerKillEventHandler implements Listener {
    private final DatabaseDriver dbDriver;

    public PlayerKillEventHandler(DatabaseDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    @EventHandler
    public void PlayerKillEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = (Player) event.getEntity().getKiller();
            Player victim = (Player) event.getEntity();

            List<Map<String, Object>> rsKiller = dbDriver.selectData("clan_id, kills", "players", "WHERE uuid = ?", killer.getUniqueId());
            int killerClanID = (int) rsKiller.get(0).get("clan_id");
            int killerKills = (int) rsKiller.get(0).get("kills");

            List<Map<String, Object>> rsVictim = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", victim.getUniqueId());
            int victimClanID = (int) rsVictim.get(0).get("clan_id");

            if (killerClanID != 0 && victimClanID != 0) {
                Map<String, Object> updateMapPlayers = new HashMap<>();
                updateMapPlayers.put("kills", killerKills + 1);
                dbDriver.updateData("players", updateMapPlayers, "uuid = ?", killer.getUniqueId());
                List<Map<String, Object>> rsClanKills = dbDriver.selectData("kills", "clans", "WHERE clan_id = ?", killerClanID);
                int clanKills = (int) rsClanKills.get(0).get("kills");
                Map<String, Object> updateMapClans = new HashMap<>();
                updateMapClans.put("kills", clanKills + 1);
                dbDriver.updateData("clans", updateMapClans, "clan_id = ?", killerClanID);
            }
        }
    }
}
