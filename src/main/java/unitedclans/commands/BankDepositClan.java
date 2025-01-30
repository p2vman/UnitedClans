package unitedclans.commands;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.*;

@AbstractCommand.Command(
        name = "ucbankdeposit",
        description = "This command allows you to deposit currency into the clan's bank account",
        permission = "unitedclans.ucbankdeposit",
        aliases = {
                "ucbankd",
                "ucbd"
        },
        usageMessage = "/<command> <number>"
)
public class BankDepositClan extends AbstractCommand {
    public BankDepositClan(DatabaseDriver driver) {
        super(driver);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length != 1) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        String depositInput = args[0];

        if (!GeneralUtils.checkDigits(depositInput)) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_BANK", true);
        }

        List<Map<String, Object>> rsPlayerSender = dbDriver.selectData("clan_id, donations", "players", "WHERE uuid = ?", uuid);
        int senderClanID = (int) rsPlayerSender.get(0).get("clan_id");
        int senderDonations = (int) rsPlayerSender.get(0).get("donations");
        if (senderClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        int deposit = Integer.parseInt(depositInput);
        if (deposit < 1 || deposit > 64) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_BANK", true);
        }

        if (!playerSender.getInventory().contains(Material.valueOf(UnitedClans.getInstance().getConfig().getString("server-currency")), deposit)) {
            return GeneralUtils.checkUtil(playerSender, language, "NOT_CURRENCY", true);
        }

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("donations", senderDonations + deposit);
        dbDriver.updateData("players", updateMapPlayers, "uuid = ?", uuid);
        List<Map<String, Object>> rsClanBank = dbDriver.selectData("bank", "clans", "WHERE clan_id = ?", senderClanID);
        int currentClanBank = (int) rsClanBank.get(0).get("bank");
        Map<String, Object> updateMapClans = new HashMap<>();
        updateMapClans.put("bank", currentClanBank + deposit);
        dbDriver.updateData("clans", updateMapClans, "clan_id = ?", senderClanID);

        GeneralUtils.removeItems(playerSender, deposit);

        String msgSuccessfullyDepositBank = LocalizationUtils.langCheck(language, "SUCCESSFULLY_DEPOSIT_BANK");
        sender.sendMessage(msgSuccessfullyDepositBank.replace("%value%", String.valueOf(deposit)));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", senderClanID);
        String senderClanName = (String) rsClan.get(0).get("clan_name");

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " deposited " + deposit + "$ into of the " + senderClanName + " clan bank");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("<number>");
        }
        return new ArrayList<>();
    }
}
