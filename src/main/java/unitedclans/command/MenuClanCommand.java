package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.MenuClanUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class MenuClanCommand implements CommandExecutor {
    private final DatabaseDriver dbDriver;

    public MenuClanCommand(DatabaseDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
}
