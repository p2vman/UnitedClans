package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.ShowClanUtils;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class AcceptClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public AcceptClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
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

        if (!UnitedClans.getInstance().invitations.containsKey(uuid)) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_INVITED", true);
        }

        int ClanID = UnitedClans.getInstance().invitations.get(uuid);

        List<Map<String, Object>> rsClan = dbDriver.selectData("count_members, clan_name", "clans", "WHERE clan_id = ?", ClanID);
        int countMembers = (int) rsClan.get(0).get("count_members");

        int max = GeneralUtils.setDefaultValue(25, "clan-max-player", 1, 100);

        if (countMembers >= max) {
            return GeneralUtils.checkUtil(playerSender, language, "THIS_CLAN_MAX", true);
        }
        String clanName = (String) rsClan.get(0).get("clan_name");

        List<Map<String, Object>> rsClanPlayers = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", ClanID);
        String msgPlayerJoined = LocalizationUtils.langCheck(language, "PLAYER_JOINED");
        for (Map<String, Object> i : rsClanPlayers) {
            String playerName = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerName);
            if (playerClan == null) {
                continue;
            }
            playerClan.sendMessage(msgPlayerJoined.replace("%player%", playerSender.getName()));
        }

        String msgJoinedClan = LocalizationUtils.langCheck(language, "SUCCESSFULLY_JOINED_CLAN");
        sender.sendMessage(msgJoinedClan.replace("%clan%", clanName));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_id", ClanID);
        updateMapPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.member"));
        updateMapPlayers.put("letter_read", 0);
        dbDriver.updateData("players", updateMapPlayers, "uuid = ?", uuid);
        Map<String, Object> updateMapClans = new HashMap<>();
        updateMapClans.put("count_members", countMembers + 1);
        dbDriver.updateData("clans", updateMapClans, "clan_id = ?", ClanID);

        UnitedClans.getInstance().invitations.remove(uuid);

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " joined the " + clanName + " clan");

        return true;
    }
}
