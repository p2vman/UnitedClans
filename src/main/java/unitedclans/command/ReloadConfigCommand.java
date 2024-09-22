package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

public class ReloadConfigCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public ReloadConfigCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");

        if (args.length != 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
                return false;
            } else {
                return GeneralUtils.checkUtil((Player) sender, language, "INVALID_COMMAND", false);
            }
        }

        plugin.reloadConfig();

        String newLanguage = UnitedClans.getInstance().getConfig().getString("lang");
        sender.sendMessage(LocalizationUtils.langCheck(newLanguage, "SUCCESS_RELOAD_CONFIG"));

        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        plugin.getServer().getLogger().info("[UnitedClans] The plugin config was successfully reloaded");

        return true;
    }
}
