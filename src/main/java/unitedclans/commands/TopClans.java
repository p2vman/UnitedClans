package unitedclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AbstractCommand.Command(
        name = "uctop",
        description = "This command allows you to open the top clans",
        permission = "unitedclans.uctop",
        aliases = {
                "uct"
        },
        usageMessage = "/<command> <top name>"
)
public class TopClans extends AbstractCommand {
    public TopClans(DatabaseDriver driver) {
        super(driver);
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;

        if (args.length != 1) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        String topNameInput = args[0];

        if (topNameInput == null || !topNameInput.equals("kills") && !topNameInput.equals("money")) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_TOP", true);
        }

        if (topNameInput.equals("kills")) {
            List<Map<String, Object>> rsKills = dbDriver.selectData("clan_name, clan_color, kills", "clans", "ORDER BY kills DESC LIMIT ?", 10);
            String msgTop = LocalizationUtils.langCheck(language, "TITLE_KILLS_TOP") + "\n";
            if (rsKills.isEmpty()) {
                msgTop = msgTop + LocalizationUtils.langCheck(language, "TITLE_EMPTY_TOP") + "\n";
            } else {
                for (int i = 0; i < rsKills.size(); i++) {
                    String clanName = (String) rsKills.get(i).get("clan_name");
                    String clanColor = (String) rsKills.get(i).get("clan_color");
                    int kills = (int) rsKills.get(i).get("kills");
                    msgTop = msgTop + ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET) + " - " + kills + "\uD83D\uDDE1" + "\n";
                }
            }
            msgTop = msgTop + LocalizationUtils.langCheck(language, "TITLE_END");
            sender.sendMessage(msgTop);
        } else if (topNameInput.equals("money")) {
            List<Map<String, Object>> rsMoney = dbDriver.selectData("clan_name, clan_color, bank", "clans", "ORDER BY bank DESC LIMIT ?", 10);
            String msgTop = LocalizationUtils.langCheck(language, "TITLE_MONEY_TOP") + "\n";
            if (rsMoney.isEmpty()) {
                msgTop = msgTop + LocalizationUtils.langCheck(language, "TITLE_EMPTY_TOP") + "\n";
            } else {
                for (int i = 0; i < rsMoney.size(); i++) {
                    String clanName = (String) rsMoney.get(i).get("clan_name");
                    String clanColor = (String) rsMoney.get(i).get("clan_color");
                    int bank = (int) rsMoney.get(i).get("bank");
                    msgTop = msgTop + ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET) + " - " + bank + "$" + "\n";
                }
            }
            msgTop = msgTop + LocalizationUtils.langCheck(language, "TITLE_END");
            sender.sendMessage(msgTop);
        }
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            String inputTop = args[0].toLowerCase();
            ArrayList<String> topList = new ArrayList<>();
            topList.add("kills");
            topList.add("money");
            List<String> tops = null;
            for (String top : topList) {
                if (top.toLowerCase().startsWith(inputTop)) {
                    if (tops == null) {
                        tops = new ArrayList<>();
                    }
                    tops.add(top);
                }
            }
            if (tops != null) {
                Collections.sort(tops);
            }
            return tops;
        }
        return new ArrayList<>();
    }
}
