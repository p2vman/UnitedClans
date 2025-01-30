package unitedclans.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.MenuClanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AbstractCommand.Command(
        name = "ucmenu",
        description = "This command allows you to open the clan menu",
        permission = "unitedclans.ucmenu",
        aliases = {
                "ucm"
        }
)
public class MenuClan extends AbstractCommand {
    public MenuClan(DatabaseDriver driver) {
        super(driver);
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length != 0) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        List<Map<String, Object>> rsPlayerClan = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
        int PlayerClanID = (int) rsPlayerClan.get(0).get("clan_id");
        if (PlayerClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        MenuClanUtils.openClanMenu(playerSender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
