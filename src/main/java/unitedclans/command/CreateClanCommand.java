package unitedclans.command;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.ShowClanUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class CreateClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public CreateClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length != 2) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        String clanNameInput = args[0];
        String clanColorInput = args[1];

        if (clanNameInput == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }

        if (clanNameInput.length() < 3 || clanNameInput.length() > 12) {
            return GeneralUtils.checkUtil(playerSender, language, "LENGTH_CLAN_NAME", true);
        }

        boolean valExist = false;
        String[] colorCollection = new String[] {"AQUA", "BLACK", "BLUE", "DARK_AQUA", "DARK_BLUE", "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE" ,"DARK_RED", "GOLD", "GRAY", "GREEN", "LIGHT_PURPLE", "RED", "WHITE", "YELLOW"};
        for (Object valColor:colorCollection) {
            if (Objects.equals(clanColorInput, valColor.toString())) {
                valExist = true;
            }
        }
        if (!valExist) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_COLOR_NAME", true);
        }

        int NumberClans = 1;
        List<Map<String, Object>> rsNumberClans = dbDriver.selectData("clan_id", "clans", "WHERE clan_id = (SELECT MAX(clan_id) FROM clans)");
        if (!rsNumberClans.isEmpty()) {
            NumberClans = (int) rsNumberClans.get(0).get("clan_id") + 1;
        }

        List<Map<String, Object>> rsCheckerPlayer = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
        int ClanID = (int) rsCheckerPlayer.get(0).get("clan_id");
        if (ClanID != 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_MEMBER_CLAN", true);
        }

        List<Map<String, Object>> rsCheckerClanName = dbDriver.selectData("clan_name", "clans", "WHERE clan_name = ?", clanNameInput);
        if (!rsCheckerClanName.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "CLAN_NAME_TAKEN", true);
        }

        int price = GeneralUtils.setDefaultValue(32, "clan-creation-price", 0, 64);

        if (!playerSender.getInventory().contains(Material.valueOf(UnitedClans.getInstance().getConfig().getString("server-currency")), price)) {
            return GeneralUtils.checkUtil(playerSender, language, "NOT_CURRENCY_CREATE_CLAN", true);
        }

        Map<String, Object> insertMapClans = new HashMap<>();
        insertMapClans.put("clan_id", NumberClans);
        insertMapClans.put("clan_name", clanNameInput);
        insertMapClans.put("clan_color", clanColorInput);
        insertMapClans.put("count_members", 1);
        insertMapClans.put("bank", 0);
        insertMapClans.put("kills", 0);
        dbDriver.insertData("clans", insertMapClans);
        Map<String, Object> insertMapLetters = new HashMap<>();
        insertMapLetters.put("clan_id", NumberClans);
        insertMapLetters.put("letter", "null");
        dbDriver.insertData("letters", insertMapLetters);
        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_id", NumberClans);
        updateMapPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.leader"));
        dbDriver.updateData("players", updateMapPlayers, "uuid = ?", uuid);

        GeneralUtils.removeItems(playerSender, price);

        String msgCreateClan = LocalizationUtils.langCheck(language, "SUCCESS_CREATE_CLAN");
        sender.sendMessage(msgCreateClan.replace("%clan%", clanNameInput));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " created the " + clanNameInput + " clan");

        return true;
    }
}
