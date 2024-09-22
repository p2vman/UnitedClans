package unitedclans.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.*;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class InviteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public InviteClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
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

        String playerNameInput = args[0];
        Player playerName = plugin.getServer().getPlayer(playerNameInput);

        if (playerName == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }

        if (uuid == playerName.getUniqueId()) {
            return GeneralUtils.checkUtil(playerSender, language, "SEND_INVITATION_YOURSELF", true);
        }

        List<Map<String, Object>> rsSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String getRoleUUID = (String) rsSender.get(0).get("clan_role");
        int getClanID = (int) rsSender.get(0).get("clan_id");

        if (getClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        List<Map<String, Object>> rsInvitedPlayer = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", playerName.getUniqueId());
        int ClanID = (int) rsInvitedPlayer.get(0).get("clan_id");

        if (ClanID != 0) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_MEMBER_CLAN", true);
        }

        if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_INVITE", true);
        }

        List<Map<String, Object>> rsInviteChecker = dbDriver.selectData("uuid", "invitations", "WHERE uuid = ?", playerName.getUniqueId());
        if (!rsInviteChecker.isEmpty()) {
            return GeneralUtils.checkUtil(playerSender, language, "ALREADY_SENT_INVITATION", true);
        }

        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name, count_members", "clans", "WHERE clan_id = ?", getClanID);
        String clanName = (String) rsClan.get(0).get("clan_name");
        int countMembers = (int) rsClan.get(0).get("count_members");

        int max = GeneralUtils.setDefaultValue(25, "clan-max-player", 1, 100);

        if (countMembers >= max) {
            return GeneralUtils.checkUtil(playerSender, language, "YOUR_CLAN_MAX", true);
        }

        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("uuid", playerName.getUniqueId());
        insertMap.put("player_name", playerName.getName());
        insertMap.put("clan_id", getClanID);
        dbDriver.insertData("invitations", insertMap);

        String msgInvitation = LocalizationUtils.langCheck(language, "INVITATION");
        TextComponent msgAccept = new TextComponent(LocalizationUtils.langCheck(language, "ACCEPT"));
        msgAccept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(LocalizationUtils.langCheck(language, "CLICK_INVITE"))));
        msgAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ucaccept"));
        playerName.sendMessage(msgInvitation.replace("%clan%", clanName).replace("%player%", sender.getName()));
        playerName.sendMessage(msgAccept);
        sender.sendMessage(LocalizationUtils.langCheck(language, "INVITATION_SENT"));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " invited " + playerName.getName() + " to the " + clanName + " clan");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                dbDriver.deleteData("invitations", "uuid = ?", playerName.getUniqueId());
            }
        }, 1200);

        return true;
    }
}
