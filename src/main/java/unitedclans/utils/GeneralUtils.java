package unitedclans.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class GeneralUtils {
    public static boolean checkUtil (Player player, String language, String msg, Boolean value) {
        player.sendMessage(LocalizationUtils.langCheck(language, msg));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
        return value;
    }
}