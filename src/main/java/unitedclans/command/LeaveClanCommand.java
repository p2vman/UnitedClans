package unitedclans.command;

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

public class LeaveClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public LeaveClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
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

        String clanNameInput = args[0];

        if (clanNameInput == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }

        List<Map<String, Object>> rsLeavingPlayer = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String LeavingPlayerRole = (String) rsLeavingPlayer.get(0).get("clan_role");
        int LeavingPlayerClanID = (int) rsLeavingPlayer.get(0).get("clan_id");

        List<Map<String, Object>> rsgetClan = dbDriver.selectData("clan_name, clan_id", "clans", "WHERE clan_name = ?", clanNameInput);
        if (rsgetClan.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }
        String getClanName = (String) rsgetClan.get(0).get("clan_name");
        int getClanID = (int) rsgetClan.get(0).get("clan_id");

        if (LeavingPlayerClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (getClanID != LeavingPlayerClanID) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_THIS_CLAN", true);
        }

        if (Objects.equals(LeavingPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_LEADER", true);
        }

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_id", 0);
        updateMapPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.no-clan"));
        updateMapPlayers.put("kills", 0);
        updateMapPlayers.put("donations", 0);
        updateMapPlayers.put("letter_read", 0);
        dbDriver.updateData("players", updateMapPlayers, "uuid = ?", uuid);
        List<Map<String, Object>> rsClanCountMembers = dbDriver.selectData("count_members", "clans", "WHERE clan_id = ?", LeavingPlayerClanID);
        int currentCountMembers = (int) rsClanCountMembers.get(0).get("count_members");
        Map<String, Object> updateMapClans = new HashMap<>();
        updateMapClans.put("count_members", currentCountMembers - 1);
        dbDriver.updateData("clans", updateMapClans, "clan_id = ?", LeavingPlayerClanID);

        List<Map<String, Object>> rsClanPlayers = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", LeavingPlayerClanID);
        String msgPlayerLeave = LocalizationUtils.langCheck(language, "PLAYER_LEAVE");
        for (Map<String, Object> i : rsClanPlayers) {
            String playerNameClan = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerNameClan);
            if (playerClan == null || playerClan == playerSender) {
                continue;
            }
            playerClan.sendMessage(msgPlayerLeave.replace("%player%", playerSender.getName()));
        }

        List<Map<String, Object>> rsClanName = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", LeavingPlayerClanID);
        String LeavingPlayerClanName = (String) rsClanName.get(0).get("clan_name");
        String msgSuccessfullyLeft = LocalizationUtils.langCheck(language, "SUCCESSFULLY_LEFT");
        playerSender.sendMessage(msgSuccessfullyLeft.replace("%clan%", LeavingPlayerClanName));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " left the " + getClanName + " clan");

        return true;
    }
}
