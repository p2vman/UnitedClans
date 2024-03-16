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

import java.sql.*;
import java.util.*;

public class InviteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public InviteClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        try {
            Statement stmt = con.createStatement();
            if (args.length != 1) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "INVALID_COMMAND", false);
            }
            String playerNameInput = args[0];
            Player playerName = plugin.getServer().getPlayer(playerNameInput);

            if (playerName == null) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "WRONG_PLAYER_NAME", true);
            }
            if (uuid == playerName.getUniqueId()) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "SEND_INVITATION_YOURSELF", true);
            }
            ResultSet rsInvitedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "'");
            if (rsInvitedPlayer.getInt("ClanID") != 0) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "PLAYER_MEMBER_CLAN", true);
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String getRoleUUID = rsSender.getString("ClanRole");
            Integer getClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "NO_RIGHTS_INVITE", true);
            }
            ResultSet rsInviteCheker = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "'");
            if (rsInviteCheker.next()) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "ALREADY_SENT_INVITATION", true);
            }
            ResultSet rsClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + getClanID);
            Integer countMembers = rsClan.getInt("CountMembers");
            String clanName = rsClan.getString("ClanName");
            if (countMembers >= 25) {
                return GeneralUtils.checkUtil(stmt, playerSender, language, "YOUR_CLAN_MAX", true);
            }

            stmt.executeUpdate("INSERT INTO INVITATIONS (UUID, PlayerName, ClanID) " + "VALUES ('" + playerName.getUniqueId() + "', '" + playerName.getName() + "', " + getClanID + ")");

            String invitationmsg = LocalizationUtils.langCheck(language, "INVITATION");
            TextComponent acceptmsg = new TextComponent(LocalizationUtils.langCheck(language, "ACCEPT"));
            acceptmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(LocalizationUtils.langCheck(language, "CLICK_INVITE"))));
            acceptmsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptclan"));
            playerName.sendMessage(invitationmsg.replace("%clan%", clanName).replace("%player%", sender.getName()));
            playerName.sendMessage(acceptmsg);
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVITATION_SENT"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            stmt.close();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate("DELETE FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "'");
                        stmt.close();
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    }
                }
            }, 1200);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}