package unitedclans.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;

import java.util.*;


@AbstractCommand.Command(
        name = "ucinvite",
        description = "This command allows you to invite a player to a clan",
        permission = "unitedclans.ucinvite",
        aliases = {
                "ucinv"
        },
        usageMessage = "/<command> <player>"
)
public class InviteClan extends AbstractCommand {
    public InviteClan(DatabaseDriver driver) {
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

        String playerNameInput = args[0];
        Player invitedPlayer = plugin.getServer().getPlayer(playerNameInput);

        if (invitedPlayer == null) {
            return GeneralUtils.checkUtil(playerSender, language, "WRONG_PLAYER_NAME", true);
        }

        if (uuid == invitedPlayer.getUniqueId()) {
            return GeneralUtils.checkUtil(playerSender, language, "SEND_INVITATION_YOURSELF", true);
        }

        List<Map<String, Object>> rsSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String getRoleUUID = (String) rsSender.get(0).get("clan_role");
        int getClanID = (int) rsSender.get(0).get("clan_id");

        if (getClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        List<Map<String, Object>> rsInvitedPlayer = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", invitedPlayer.getUniqueId());
        int ClanID = (int) rsInvitedPlayer.get(0).get("clan_id");

        if (ClanID != 0) {
            return GeneralUtils.checkUtil(playerSender, language, "PLAYER_MEMBER_CLAN", true);
        }

        if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_INVITE", true);
        }

        if (UnitedClans.getInstance().invitations.containsKey(invitedPlayer.getUniqueId())) {
            return GeneralUtils.checkUtil(playerSender, language, "ALREADY_SENT_INVITATION", true);
        }

        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name, count_members", "clans", "WHERE clan_id = ?", getClanID);
        String clanName = (String) rsClan.get(0).get("clan_name");
        int countMembers = (int) rsClan.get(0).get("count_members");

        int max = GeneralUtils.setDefaultValue(25, "clan-max-player", 1, 100);

        if (countMembers >= max) {
            return GeneralUtils.checkUtil(playerSender, language, "YOUR_CLAN_MAX", true);
        }

        UnitedClans.getInstance().invitations.put(invitedPlayer.getUniqueId(), getClanID);

        String msgInvitation = LocalizationUtils.langCheck(language, "INVITATION");
        TextComponent msgAccept = new TextComponent(LocalizationUtils.langCheck(language, "ACCEPT"));
        msgAccept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(LocalizationUtils.langCheck(language, "CLICK_INVITE"))));
        msgAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ucaccept"));
        invitedPlayer.sendMessage(msgInvitation.replace("%clan%", clanName).replace("%player%", sender.getName()));
        invitedPlayer.sendMessage(msgAccept);
        sender.sendMessage(LocalizationUtils.langCheck(language, "INVITATION_SENT"));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " invited " + invitedPlayer.getName() + " to the " + clanName + " clan");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                UnitedClans.getInstance().invitations.remove(invitedPlayer.getUniqueId());
            }
        }, 1200);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            String inputPlayer = args[0].toLowerCase();
            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            List<String> onlinePlayerName = null;
            for (Player onlinePlayer : onlinePlayers) {
                if (onlinePlayer.toString().toLowerCase().startsWith(inputPlayer)) {
                    if (onlinePlayerName == null) {
                        onlinePlayerName = new ArrayList<>();
                    }
                    String name = onlinePlayer.getName();
                    onlinePlayerName.add(name);
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
