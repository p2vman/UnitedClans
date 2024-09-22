package unitedclans.command;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class BankWithdrawClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public BankWithdrawClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length != 1) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        String withdrawInput = args[0];

        if (!GeneralUtils.checkDigits(withdrawInput)) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_BANK", true);
        }

        List<Map<String, Object>> rsPlayerSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String senderRole = (String) rsPlayerSender.get(0).get("clan_role");
        int senderClanID = (int) rsPlayerSender.get(0).get("clan_id");
        if (senderClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (Objects.equals(senderRole, UnitedClans.getInstance().getConfig().getString("roles.member"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_BANK", true);
        }

        int withdraw = Integer.parseInt(withdrawInput);
        if (withdraw < 1 || withdraw > 64) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_BANK", true);
        }

        List<Map<String, Object>> rsBank = dbDriver.selectData("clan_name, bank", "clans", "WHERE clan_id = ?", senderClanID);
        String senderClanName = (String) rsBank.get(0).get("clan_name");
        int bankAccount = (int) rsBank.get(0).get("bank");
        if (bankAccount - withdraw < 0) {
            String msgEmptyBank = LocalizationUtils.langCheck(language, "EMPTY_BANK");
            sender.sendMessage(msgEmptyBank.replace("%value%", String.valueOf(bankAccount)));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }

        int emptySlots = 0;
        for (int slot = 0; slot < 36; slot++) {
            if (playerSender.getInventory().getItem(slot) == null) {
                emptySlots++;
            }
        }

        if (emptySlots == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "INVENTORY_FULL", true);
        }

        Map<String, Object> updateMapClans = new HashMap<>();
        updateMapClans.put("bank", bankAccount - withdraw);
        dbDriver.updateData("clans", updateMapClans, "clan_id = ?", senderClanID);

        for (int i = 0; i < withdraw; i++) {
            playerSender.getInventory().addItem(new ItemStack(Material.valueOf(UnitedClans.getInstance().getConfig().getString("server-currency"))));
        }

        String msgSuccessfullyWithdrawBank = LocalizationUtils.langCheck(language, "SUCCESSFULLY_WITHDRAW_BANK");
        sender.sendMessage(msgSuccessfullyWithdrawBank.replace("%value%", String.valueOf(withdraw)));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " withdrew " + withdraw + "$ from the " + senderClanName + " clan bank");

        return true;
    }
}
