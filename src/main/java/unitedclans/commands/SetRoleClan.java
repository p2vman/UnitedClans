package unitedclans.commands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.*;

@AbstractCommand.Command(
        name = "ucsetrole",
        description = "This command allows you to set the role of the player",
        permission = "unitedclans.ucsetrole",
        aliases = {
                "ucsr"
        },
        usageMessage = "/<command> <player> <role>"
)
public class SetRoleClan extends AbstractCommand {
    public SetRoleClan(DatabaseDriver driver) {
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
        } else if (args.length == 2) {
            String inputRole = args[1].toLowerCase();
            ArrayList<String> rolesList = new ArrayList<>();
            rolesList.add(UnitedClans.getInstance().getConfig().getString("roles.elder"));
            rolesList.add(UnitedClans.getInstance().getConfig().getString("roles.member"));
            List<String> roles = null;
            for (String role : rolesList) {
                if (role.toString().toLowerCase().startsWith(inputRole)) {
                    if (roles == null) {
                        roles = new ArrayList<>();
                    }
                    roles.add(role);
                }
            }
            if (roles != null) {
                Collections.sort(roles);
            }
            return roles;
        }
        return new ArrayList<>();
    }
}
