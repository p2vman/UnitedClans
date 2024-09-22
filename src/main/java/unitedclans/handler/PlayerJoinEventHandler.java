package unitedclans.handler;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.ShowClanUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class PlayerJoinEventHandler implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public PlayerJoinEventHandler(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        UUID uuid = event.getPlayer().getUniqueId();

        List<Map<String, Object>> rsPlayer = dbDriver.selectData("uuid", "players", "WHERE uuid = ?", uuid);
        if (rsPlayer.isEmpty()) {
            Map<String, Object> insertMap = new HashMap<>();
            insertMap.put("uuid", uuid);
            insertMap.put("player_name", event.getPlayer().getName());
            insertMap.put("clan_id", 0);
            insertMap.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.no-clan"));
            insertMap.put("kills", 0);
            insertMap.put("donations", 0);
            insertMap.put("letter_read", 0);
            dbDriver.insertData("players", insertMap);
        }

        List<Map<String, Object>> rsLetter = dbDriver.selectData("letter_read", "players", "WHERE uuid = ?", uuid);
        int valueLetterRead = (int) rsLetter.get(0).get("letter_read");
        if (valueLetterRead == 1) {
            event.getPlayer().sendMessage(LocalizationUtils.langCheck(language, "UNREAD_LETTER_MESSAGE"));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        ShowClanUtils.showClan(plugin, dbDriver);
    }
}
