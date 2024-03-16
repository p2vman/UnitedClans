package unitedclans.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.*;

public class GeneralUtils {
    public static boolean checkUtil (Statement stmt, Player player, String language, String msg, Boolean value) {
        player.sendMessage(LocalizationUtils.langCheck(language, msg));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
        try {
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return value;
    }
}