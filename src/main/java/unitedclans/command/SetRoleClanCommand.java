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
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class SetRoleClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public SetRoleClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
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

        String playerNameInput = args[0];
        String playerRoleInput = args[1];

        if (playerNameInput == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }

        if ((playerRoleInput == null) || (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.elder"))) && (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.member")))) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_ROLE_NAME", true);
        }

        if (Objects.equals(playerSender.getName(), playerNameInput)) {
            return GeneralUtils.checkUtil(playerSender, language, "SET_ROLE_YOURSELF", true);
        }

        List<Map<String, Object>> rsSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String senderRole = (String) rsSender.get(0).get("clan_role");
        int senderClanID = (int) rsSender.get(0).get("clan_id");

        if (senderClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (!Objects.equals(senderRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_SET_ROLE", true);
        }

        List<Map<String, Object>> rsPlayerName = dbDriver.selectData("clan_id", "players", "WHERE player_name = ?", playerNameInput);
        int playerClanID = (int) rsPlayerName.get(0).get("clan_id");

        if (playerClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_NOT_MEMBER_CLAN", true);
        }

        if (senderClanID != playerClanID) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_NOT_YOUR_CLAN", true);
        }

        Map<String, Object> updateMapPlayers = new HashMap<>();
        updateMapPlayers.put("clan_role", playerRoleInput);
        dbDriver.updateData("players", updateMapPlayers, "player_name = ?", playerNameInput);

        String msgSuccessfullyChangedRole = LocalizationUtils.langCheck(language, "SUCCESSFULLY_CHANGED_ROLE");
        String setPlayerRole = null;

        if (Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
            setPlayerRole = LocalizationUtils.langCheck(language, "ELDER");
        } else if (Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.member"))) {
            setPlayerRole = LocalizationUtils.langCheck(language, "MEMBER");
        }

        playerSender.sendMessage(msgSuccessfullyChangedRole.replace("%role%", setPlayerRole));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        Player argPlayerName = plugin.getServer().getPlayer(playerNameInput);
        if (argPlayerName != null) {
            String msgYouBeenAssigned = LocalizationUtils.langCheck(language, "YOU_BEEN_ASSIGNED");
            argPlayerName.sendMessage(msgYouBeenAssigned.replace("%role%", setPlayerRole));
            argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " gave " + playerNameInput + " the role of " + setPlayerRole);

        return true;
    }
}
