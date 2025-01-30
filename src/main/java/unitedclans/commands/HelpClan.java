package unitedclans.commands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

@AbstractCommand.Command(
        name = "uchelp",
        description = "This command allows you to display all possible plugin commands",
        permission = "unitedclans.uchelp",
        aliases = {
                "uch"
        }
)
public class HelpClan extends AbstractCommand {
    public HelpClan(DatabaseDriver driver) {
        super(driver);
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;

        if (args.length != 0) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        if (language.equals("en_en")) {
            sender.sendMessage("§b§l⏴-------§r§e§lUnitedClans§r§b§l-------⏵§r \n" +
                    "§bCommand:§r §euchelp§r \n" +
                    "§bDescription:§r §eThis command allows you to display all possible plugin commands§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucreloadconfig§r \n" +
                    "§bDescription:§r §eThis command allows you to reload the plugin config§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucinfo§r \n" +
                    "§bDescription:§r §eThis command allows you to view information about the clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name (not necessary)>§r \n \n" +
                    "§bCommand:§r §euccreate§r \n" +
                    "§bDescription:§r §eThis command allows you to create a clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name> <color>§r \n \n" +
                    "§bCommand:§r §eucdelete§r \n" +
                    "§bDescription:§r §eThis command allows you to delete a clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name>§r \n \n" +
                    "§bCommand:§r §eucinvite§r \n" +
                    "§bDescription:§r §eThis command allows you to invite a player to a clan§r \n" +
                    "§bUsage:§r §e/<command> <player>§r \n \n" +
                    "§bCommand:§r §eucaccept§r \n" +
                    "§bDescription:§r §eThis command allows you to accept an invitation to a clan§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §euckick§r \n" +
                    "§bDescription:§r §eThis command allows you to kick a player from the clan§r \n" +
                    "§bUsage:§r §e/<command> <player>§r \n \n" +
                    "§bCommand:§r §eucleave§r \n" +
                    "§bDescription:§r §eThis command allows the player to leave the clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name>§r \n \n" +
                    "§bCommand:§r §eucsetrole§r \n" +
                    "§bDescription:§r §eThis command allows you to set the role of the player§r \n" +
                    "§bUsage:§r §e/<command> <player> <role>§r \n \n" +
                    "§bCommand:§r §eucmenu§r \n" +
                    "§bDescription:§r §eThis command allows you to open the clan menu§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucchat§r \n" +
                    "§bDescription:§r §eThis command allows you to send messages to the clan chat§r \n" +
                    "§bUsage:§r §e/<command> <message>§r \n \n" +
                    "§bCommand:§r §eucchangeleader§r \n" +
                    "§bDescription:§r §eThis command allows you to change the clan Leader§r \n" +
                    "§bUsage:§r §e/<command> <clan name> <player>§r \n \n" +
                    "§bCommand:§r §eucbankdeposit§r \n" +
                    "§bDescription:§r §eThis command allows you to deposit currency into the clan's bank account§r \n" +
                    "§bUsage:§r §e/<command> <number>§r \n \n" +
                    "§bCommand:§r §eucbankwithdraw§r \n" +
                    "§bDescription:§r §eThis command allows you to withdraw currency from a clan bank account§r \n" +
                    "§bUsage:§r §e/<command> <number>§r \n \n" +
                    "§bCommand:§r §euctop§r \n" +
                    "§bDescription:§r §eThis command allows you to open the top clans§r \n" +
                    "§bUsage:§r §e/<command> <top name>§r \n \n" +
                    "§bCommand:§r §eucletter§r \n" +
                    "§bDescription:§r §eThis command allows you to create and view a letter to the clan§r \n" +
                    "§bUsage:§r §e/<command> <letter (not necessary)>§r \n" +
                    "§b§l⏴--------------------------⏵§r");
        } else if (language.equals("ru_ru")) {
            sender.sendMessage("§b§l⏴-------§r§e§lUnitedClans§r§b§l-------⏵§r \n" +
                    "§bКоманда:§r §euchelp§r \n" +
                    "§bОписание:§r §eЭта команда позволяет отображать все возможные команды плагина§r \n" +
                    "§bИспользование:§r §e/<command>§r \n \n" +
                    "§bКоманда:§r §eucreloadconfig§r \n" +
                    "§bОписание:§r §eЭта команда позволяет перезагрузить конфигурацию плагина§r \n" +
                    "§bИспользование:§r §e/<command>§r \n \n" +
                    "§bКоманда:§r §eucinfo§r \n" +
                    "§bОписание:§r §eЭта команда позволяет просмотреть информацию о клане§r \n" +
                    "§bИспользование:§r §e/<command> <clan name (not necessary)>§r \n \n" +
                    "§bКоманда:§r §euccreate§r \n" +
                    "§bОписание:§r §eЭта команда позволяет создать клан§r \n" +
                    "§bИспользование:§r §e/<command> <clan name> <color>§r \n \n" +
                    "§bКоманда:§r §eucdelete§r \n" +
                    "§bОписание:§r §eЭта команда позволяет удалить клан§r \n" +
                    "§bИспользование:§r §e/<command> <clan name>§r \n \n" +
                    "§bКоманда:§r §eucinvite§r \n" +
                    "§bОписание:§r §eЭта команда позволяет пригласить игрока в клан§r \n" +
                    "§bИспользование:§r §e/<command> <player>§r \n \n" +
                    "§bКоманда:§r §eucaccept§r \n" +
                    "§bОписание:§r §eЭта команда позволяет принять приглашение в клан§r \n" +
                    "§bИспользование:§r §e/<command>§r \n \n" +
                    "§bКоманда:§r §euckick§r \n" +
                    "§bОписание:§r §eЭта команда позволяет выгнать игрока из клана§r \n" +
                    "§bИспользование:§r §e/<command> <player>§r \n \n" +
                    "§bКоманда:§r §eucleave§r \n" +
                    "§bОписание:§r §eЭта команда позволяет игроку выйти из клана§r \n" +
                    "§bИспользование:§r §e/<command> <clan name>§r \n \n" +
                    "§bКоманда:§r §eucsetrole§r \n" +
                    "§bОписание:§r §eЭта команда позволяет вам установить роль игрока§r \n" +
                    "§bИспользование:§r §e/<command> <player> <role>§r \n \n" +
                    "§bКоманда:§r §eucmenu§r \n" +
                    "§bОписание:§r §eЭта команда позволяет открыть меню клана§r \n" +
                    "§bИспользование:§r §e/<command>§r \n \n" +
                    "§bКоманда:§r §eucchat§r \n" +
                    "§bОписание:§r §eЭта команда позволяет отправлять сообщения в чат клана§r \n" +
                    "§bИспользование:§r §e/<command> <message>§r \n \n" +
                    "§bКоманда:§r §eucchangeleader§r \n" +
                    "§bОписание:§r §eЭта команда позволяет сменить Главу клана§r \n" +
                    "§bИспользование:§r §e/<command> <clan name> <player>§r \n \n" +
                    "§bКоманда:§r §eucbankdeposit§r \n" +
                    "§bОписание:§r §eЭта команда позволяет внести валюту на счет кланового банка§r \n" +
                    "§bИспользование:§r §e/<command> <number>§r \n \n" +
                    "§bКоманда:§r §eucbankwithdraw§r \n" +
                    "§bОписание:§r §eЭта команда позволяет вывести валюту со счета кланового банка§r \n" +
                    "§bИспользование:§r §e/<command> <number>§r \n \n" +
                    "§bКоманда:§r §euctop§r \n" +
                    "§bОписание:§r §eЭта команда позволяет открыть топ кланов§r \n" +
                    "§bИспользование:§r §e/<command> <top name>§r \n \n" +
                    "§bКоманда:§r §eucletter§r \n" +
                    "§bОписание:§r §eЭта команда позволяет создать и просмотреть письмо клану§r \n" +
                    "§bИспользование:§r §e/<command> <letter (not necessary)>§r \n" +
                    "§b§l⏴--------------------------⏵§r");
        } else {
            sender.sendMessage("§b§l⏴-------§r§e§lUnitedClans§r§b§l-------⏵§r \n" +
                    "§bCommand:§r §euchelp§r \n" +
                    "§bDescription:§r §eThis command allows you to display all possible plugin commands§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucreloadconfig§r \n" +
                    "§bDescription:§r §eThis command allows you to reload the plugin config§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucinfo§r \n" +
                    "§bDescription:§r §eThis command allows you to view information about the clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name (not necessary)>§r \n \n" +
                    "§bCommand:§r §euccreate§r \n" +
                    "§bDescription:§r §eThis command allows you to create a clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name> <color>§r \n \n" +
                    "§bCommand:§r §eucdelete§r \n" +
                    "§bDescription:§r §eThis command allows you to delete a clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name>§r \n \n" +
                    "§bCommand:§r §eucinvite§r \n" +
                    "§bDescription:§r §eThis command allows you to invite a player to a clan§r \n" +
                    "§bUsage:§r §e/<command> <player>§r \n \n" +
                    "§bCommand:§r §eucaccept§r \n" +
                    "§bDescription:§r §eThis command allows you to accept an invitation to a clan§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §euckick§r \n" +
                    "§bDescription:§r §eThis command allows you to kick a player from the clan§r \n" +
                    "§bUsage:§r §e/<command> <player>§r \n \n" +
                    "§bCommand:§r §eucleave§r \n" +
                    "§bDescription:§r §eThis command allows the player to leave the clan§r \n" +
                    "§bUsage:§r §e/<command> <clan name>§r \n \n" +
                    "§bCommand:§r §eucsetrole§r \n" +
                    "§bDescription:§r §eThis command allows you to set the role of the player§r \n" +
                    "§bUsage:§r §e/<command> <player> <role>§r \n \n" +
                    "§bCommand:§r §eucmenu§r \n" +
                    "§bDescription:§r §eThis command allows you to open the clan menu§r \n" +
                    "§bUsage:§r §e/<command>§r \n \n" +
                    "§bCommand:§r §eucchat§r \n" +
                    "§bDescription:§r §eThis command allows you to send messages to the clan chat§r \n" +
                    "§bUsage:§r §e/<command> <message>§r \n \n" +
                    "§bCommand:§r §eucchangeleader§r \n" +
                    "§bDescription:§r §eThis command allows you to change the clan Leader§r \n" +
                    "§bUsage:§r §e/<command> <clan name> <player>§r \n \n" +
                    "§bCommand:§r §eucbankdeposit§r \n" +
                    "§bDescription:§r §eThis command allows you to deposit currency into the clan's bank account§r \n" +
                    "§bUsage:§r §e/<command> <number>§r \n \n" +
                    "§bCommand:§r §eucbankwithdraw§r \n" +
                    "§bDescription:§r §eThis command allows you to withdraw currency from a clan bank account§r \n" +
                    "§bUsage:§r §e/<command> <number>§r \n \n" +
                    "§bCommand:§r §euctop§r \n" +
                    "§bDescription:§r §eThis command allows you to open the top clans§r \n" +
                    "§bUsage:§r §e/<command> <top name>§r \n \n" +
                    "§bCommand:§r §eucletter§r \n" +
                    "§bDescription:§r §eThis command allows you to create and view a letter to the clan§r \n" +
                    "§bUsage:§r §e/<command> <letter (not necessary)>§r \n" +
                    "§b§l⏴--------------------------⏵§r");
        }
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
