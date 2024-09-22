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

public class KickClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public KickClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
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

        String playerName = args[0];

        if (playerName == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }

        List<Map<String, Object>> rsKickedPlayer = dbDriver.selectData("clan_role, clan_id", "players", "WHERE player_name = ?", playerName);
        if (rsKickedPlayer.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }
        String KickedPlayerRole = (String) rsKickedPlayer.get(0).get("clan_role");
        int KickedPlayerClanID = (int) rsKickedPlayer.get(0).get("clan_id");

        if (Objects.equals(playerSender.getName(), playerName)) {
            return GeneralUtils.checkUtil(playerSender, language, "KICK_YOURSELF", true);
        }

        List<Map<String, Object>> rsSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String getRoleUUID = (String) rsSender.get(0).get("clan_role");
        int getClanID = (int) rsSender.get(0).get("clan_id");
        if (getClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (KickedPlayerClanID != getClanID) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_NOT_YOUR_CLAN", true);
        }

        if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_KICK", true);
        }

        if (Objects.equals(KickedPlayerRole, getRoleUUID) || Objects.equals(KickedPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "ROLE_IS_HIGHER", true);
        }

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_id", 0);
        updateMapPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.no-clan"));
        updateMapPlayers.put("kills", 0);
        updateMapPlayers.put("donations", 0);
        updateMapPlayers.put("letter_read", 0);
        dbDriver.updateData("players", updateMapPlayers, "player_name = ?", playerName);
        List<Map<String, Object>> rsClanCountMembers = dbDriver.selectData("count_members", "clans", "WHERE clan_id = ?", getClanID);
        int currentCountMembers = (int) rsClanCountMembers.get(0).get("count_members");
        Map<String, Object> updateMapClans = new HashMap<>();
        updateMapClans.put("count_members", currentCountMembers - 1);
        dbDriver.updateData("clans", updateMapClans, "clan_id = ?", getClanID);

        List<Map<String, Object>> rsClanPlayers = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", getClanID);
        String playerkickedmsg = LocalizationUtils.langCheck(language, "PLAYER_WAS_KICKED");
        for (Map<String, Object> i : rsClanPlayers) {
            String playerNameClan = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerNameClan);
            if (playerClan == null) {
                continue;
            }
            playerClan.sendMessage(playerkickedmsg.replace("%player%", playerName));
        }
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        Player argPlayerName = plugin.getServer().getPlayer(playerName);

        List<Map<String, Object>> rsClanName = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", KickedPlayerClanID);
        String KickedPlayerClanName = (String) rsClanName.get(0).get("clan_name");
        if (argPlayerName != null) {
            String msgYouWasKicked = LocalizationUtils.langCheck(language, "YOU_WAS_KICKED");
            argPlayerName.sendMessage(msgYouWasKicked.replace("%clan%", KickedPlayerClanName));
            argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " kicked " + playerName + " from the " + KickedPlayerClanName + " clan");

        return true;
    }
}
