package unitedclans.command;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class InfoClanCommand implements CommandExecutor {
    private final DatabaseDriver dbDriver;

    public InfoClanCommand(DatabaseDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        List<Map<String, Object>> rsSender = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
        int senderClanID = (int) rsSender.get(0).get("clan_id");

        if (args.length == 0) {
            if (senderClanID == 0) {
                return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
            }

            List<Map<String, Object>> rsSenderClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", senderClanID);
            String senderClanName = (String) rsSenderClan.get(0).get("clan_name");

            return showInfo(playerSender, senderClanName, language);
        }

        if (args.length == 1) {
            String clanNameInput = args[0];

            if (!playerSender.isOp()) {
                return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_USE_COMMAND", true);
            }

            return showInfo(playerSender, clanNameInput, language);
        }

        return true;
    }

    public boolean showInfo(Player player, String inputClanName, String language) {
        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_id, clan_name, clan_color, count_members, bank, kills", "clans", "WHERE clan_name = ?", inputClanName);
        if (rsClan.isEmpty()) {
            return GeneralUtils.checkUtil(player, language, "CLAN_NOT_EXIST", true);
        }
        int clanID = (int) rsClan.get(0).get("clan_id");
        String clanName = (String) rsClan.get(0).get("clan_name");
        String clanColor = (String) rsClan.get(0).get("clan_color");
        int countMembers = (int) rsClan.get(0).get("count_members");
        int bank = (int) rsClan.get(0).get("bank");
        int kills = (int) rsClan.get(0).get("kills");

        List<Map<String, Object>> rsLeader = dbDriver.selectData("player_name", "players", "WHERE clan_id = ? AND clan_role = ?", clanID, UnitedClans.getInstance().getConfig().getString("roles.leader"));
        String leaderName = (String) rsLeader.get(0).get("player_name");

        String msgInfo = LocalizationUtils.langCheck(language, "INFO_PATTERN");

        player.sendMessage(msgInfo.replace("%clan%", ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET)).replace("%leader%", leaderName).replace("%members%", String.valueOf(countMembers)).replace("%kills%", String.valueOf(kills)).replace("%bank%", String.valueOf(bank)));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        return true;
    }
}
