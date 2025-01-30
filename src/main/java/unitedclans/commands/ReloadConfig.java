package unitedclans.commands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

@AbstractCommand.Command(
        name = "ucreloadconfig",
        description = "This command allows you to reload the plugin config",
        permission = "unitedclans.ucreloadconfig",
        aliases = {
                "ucreload",
                "ucrc"
        }
)
public class ReloadConfig extends AbstractCommand {
    public ReloadConfig(DatabaseDriver driver) {
        super(driver);
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
