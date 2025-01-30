package unitedclans.commands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.ShowClanUtils;

import java.util.*;


@AbstractCommand.Command(
        name = "uckick",
        description = "This command allows you to kick a player from the clan",
        permission = "unitedclans.uckick",
        aliases = {
                "uck"
        },
        usageMessage = "/<command> <player>"
)
public class KickClan extends AbstractCommand {
    public KickClan(DatabaseDriver driver) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            Player playerSender = (Player) sender;
            UUID uuid = playerSender.getUniqueId();
            String inputPlayer = args[0].toLowerCase();
            List<Map<String, Object>> rsSender = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
            int ClanID = (int) rsSender.get(0).get("clan_id");

            if (ClanID == 0) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> rsPlayerClan = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", ClanID);
            ArrayList<String> onlinePlayers = new ArrayList<>();
            for (Map<String, Object> i : rsPlayerClan) {
                String playerName = (String) i.get("player_name");
                onlinePlayers.add(playerName);
            }

            List<String> onlinePlayerName = null;
            for (String onlinePlayer : onlinePlayers) {
                if (onlinePlayer.toString().toLowerCase().startsWith(inputPlayer)) {
                    if (onlinePlayerName == null) {
                        onlinePlayerName = new ArrayList<>();
                    }
                    onlinePlayerName.add(onlinePlayer);
                }
            }

            if (onlinePlayerName != null) {
                Collections.sort(onlinePlayerName);
            }

            return onlinePlayerName;
        }
        return new ArrayList<>();
    }
}
