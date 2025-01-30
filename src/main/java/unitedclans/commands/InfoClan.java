package unitedclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.*;


@AbstractCommand.Command(
        name = "ucinfo",
        description = "This command allows you to view information about the clan",
        permission = "unitedclans.ucinfo",
        aliases = {
                "uci"
        },
        usageMessage = "/<command> <clan name (not necessary)>"
)
public class InfoClan extends AbstractCommand {
    public InfoClan(DatabaseDriver driver) {
        super(driver);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            try {
                Player playerSender = (Player) sender;
                if (!playerSender.isOp()) {
                    return new ArrayList<>();
                }

                String inputClan = args[0].toLowerCase();
                List<Map<String, Object>> rsClans = dbDriver.selectData("clan_name", "clans", null);

                ArrayList<String> clans = new ArrayList<>();
                for (Map<String, Object> i : rsClans) {
                    String clanName = (String) i.get("clan_name");
                    clans.add(clanName);
                }

                List<String> clanNames = null;
                for (String clan : clans) {
                    if (clan.toString().toLowerCase().startsWith(inputClan)) {
                        if (clanNames == null) {
                            clanNames = new ArrayList<>();
                        }
                        clanNames.add(clan);
                    }
                }

                if (clanNames != null) {
                    Collections.sort(clanNames);
                }

                return clanNames;
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}
