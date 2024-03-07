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
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String playerNameInput = args[0];
        Player playerName = plugin.getServer().getPlayer(playerNameInput);
        if (playerName == null) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "WRONG_PLAYER_NAME"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if (uuid == playerName.getUniqueId()) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "SEND_INVITATION_YOURSELF"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsInvitedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "'");
            if (rsInvitedPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "PLAYER_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            String getRoleUUID = rsSender.getString("ClanRole");
            Integer getClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "NO_RIGHTS_INVITE"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsInviteCheker = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "'");
            if (rsInviteCheker.next()) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "ALREADY_SENT_INVITATION"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + getClanID);
            Integer countMembers = rsClan.getInt("CountMembers");
            String clanName = rsClan.getString("ClanName");
            if (countMembers >= 25) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOUR_CLAN_MAX"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("INSERT INTO INVITATIONS (UUID, PlayerName, ClanID) " + "VALUES ('" + playerName.getUniqueId() + "', '" + playerName.getName() + "', " + getClanID + ")");

            String invitationmsg = LocalizationUtils.langCheck(language, "INVITATION");
            TextComponent acceptmsg = new TextComponent(LocalizationUtils.langCheck(language, "ACCEPT"));
            acceptmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(LocalizationUtils.langCheck(language, "CLICK_INVITE"))));
            acceptmsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptclan"));
            playerName.sendMessage(invitationmsg.replace("%clan%", clanName).replace("%player%", sender.getName()));
            playerName.sendMessage(acceptmsg);
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVITATION_SENT"));
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