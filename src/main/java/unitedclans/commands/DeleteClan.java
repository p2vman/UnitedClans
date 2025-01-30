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
        name = "ucdelete",
        description = "This command allows you to delete a clan",
        permission = "unitedclans.ucdelete",
        aliases = {
                "ucd"
        },
        usageMessage = "/<command> <clan name>"
)
public class DeleteClan extends AbstractCommand {
    public DeleteClan(DatabaseDriver driver) {
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

        String clanNameInput = args[0];

        if (clanNameInput == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }

        List<Map<String, Object>> rsgetClan = dbDriver.selectData("clan_name, clan_id", "clans", "WHERE clan_name = ?", clanNameInput);
        if (rsgetClan.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_CLAN_NAME", true);
        }
        String getClanName = (String) rsgetClan.get(0).get("clan_name");
        int getClanID = (int) rsgetClan.get(0).get("clan_id");

        List<Map<String, Object>> rsgetClanPlayer = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String getLeaderUUID = (String) rsgetClanPlayer.get(0).get("clan_role");
        int getClanIDPlayer = (int) rsgetClanPlayer.get(0).get("clan_id");

        if (getClanIDPlayer == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (getClanID != getClanIDPlayer) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_THIS_CLAN", true);
        }

        if (!Objects.equals(getLeaderUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NOT_LEADER", true);
        }

        List<Map<String, Object>> rsPlayerClan = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", getClanIDPlayer);
        for (Map<String, Object> i : rsPlayerClan) {
            String playerName = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerName);
            if (playerClan == null || playerClan == playerSender) {
                continue;
            }

            playerClan.sendMessage(LocalizationUtils.langCheck(language, "LEADER_DELETE_CLAN"));
            playerClan.playSound(playerClan.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_id", 0);
        updateMapPlayers.put("clan_role", UnitedClans.getInstance().getConfig().getString("roles.no-clan"));
        updateMapPlayers.put("kills", 0);
        updateMapPlayers.put("donations", 0);
        updateMapPlayers.put("letter_read", 0);
        dbDriver.updateData("players", updateMapPlayers, "clan_id = ?", getClanID);
        dbDriver.deleteData("clans", "clan_id = ?", getClanID);
        dbDriver.deleteData("letters", "clan_id = ?", getClanID);

        String msgDeleteClan = LocalizationUtils.langCheck(language, "SUCCESS_DELETE_CLAN");
        sender.sendMessage(msgDeleteClan.replace("%clan%", getClanName));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        ShowClanUtils.showClan(plugin, dbDriver);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " deleted the " + clanNameInput + " clan");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("<clan name>");
        }
        return new ArrayList<>();
    }
}
