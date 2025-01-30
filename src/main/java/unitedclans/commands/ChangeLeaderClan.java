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
        name = "ucchangeleader",
        description = "This command allows you to change the clan Leader",
        permission = "unitedclans.ucchangeleader",
        aliases = {
                "uccl"
        },
        usageMessage = "/<command> <clan name> <player>"
)
public class ChangeLeaderClan extends AbstractCommand {
    public ChangeLeaderClan(DatabaseDriver driver) {
        super(driver);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length != 2) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        String inputClanName = args[0];
        String playerName = args[1];

        List<Map<String, Object>> rsSenderPlayer = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String PlayerRole = (String) rsSenderPlayer.get(0).get("clan_role");
        int PlayerClanID = (int) rsSenderPlayer.get(0).get("clan_id");

        if (PlayerClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        List<Map<String, Object>> rsSenderClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", PlayerClanID);
        if (inputClanName == null || rsSenderClan.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }
        String ClanName = (String) rsSenderClan.get(0).get("clan_name");

        if (!Objects.equals(inputClanName, ClanName)) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }

        if (playerName == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }

        List<Map<String, Object>> rsLeaderPlayer = dbDriver.selectData("clan_id", "players", "WHERE player_name = ?", playerName);
        if (rsLeaderPlayer.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }
        int LeaderPlayerClanID = (int) rsLeaderPlayer.get(0).get("clan_id");

        if (!Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NOT_LEADER", true);
        }

        if (Objects.equals(playerSender.getName(), playerName)) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_ALREADY_LEADER", true);
        }

        if (PlayerClanID != LeaderPlayerClanID) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_NOT_YOUR_CLAN", true);
        }

        Map<String, Object> updateMapFirstPlayers = new HashMap<>();
        updateMapFirstPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.elder"));
        dbDriver.updateData("players", updateMapFirstPlayers, "uuid = ?", uuid);
        Map<String, Object> updateMapSecondPlayers = new HashMap<>();
        updateMapSecondPlayers.put("clan_role", PlayerRole);
        dbDriver.updateData("players", updateMapSecondPlayers, "player_name = ?", playerName);

        String msgSuccessfullyChangeLeader = LocalizationUtils.langCheck(language, "SUCCESSFULLY_CHANGE_LEADER");
        sender.sendMessage(msgSuccessfullyChangeLeader.replace("%player%", playerName));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        List<Map<String, Object>> rsClanPlayers = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", PlayerClanID);
        String msgChangeLeader = LocalizationUtils.langCheck(language, "CHANGE_LEADER");
        for (Map<String, Object> i : rsClanPlayers) {
            String playerNameClan = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerNameClan);
            if (playerClan == null || Objects.equals(playerNameClan, playerSender.getName()) || Objects.equals(playerNameClan, playerName)) {
                continue;
            }

            playerClan.sendMessage(msgChangeLeader.replace("%old-leader%", playerSender.getName()).replace("%new-leader%", playerName));
            playerClan.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        Player argPlayerName = plugin.getServer().getPlayer(playerName);
        if (argPlayerName != null) {
            argPlayerName.sendMessage(LocalizationUtils.langCheck(language, "YOU_HAVE_BEEN_LEADER"));
            argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " passed on the role of " + UnitedClans.getInstance().getConfig().getString("roles.leader") + " of the " + ClanName + " clan to " + playerName);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("<clan name>");
        }
        if (args.length == 2) {
            Player playerSender = (Player) sender;
            UUID uuid = playerSender.getUniqueId();
            String inputPlayer = args[1].toLowerCase();
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
