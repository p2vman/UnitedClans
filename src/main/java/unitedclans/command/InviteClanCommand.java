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
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String playerNameInput = args[0];
        Player playerName = plugin.getServer().getPlayer(playerNameInput);
        if (playerName == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if (uuid == playerName.getUniqueId()) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.sendinvitationyourself"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsInvitedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsInvitedPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playermemberclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String getRoleUUID = rsSender.getString("ClanRole");
            Integer getClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.norightsinvite"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsInviteCheker = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsInviteCheker.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.alreadysentinvitation"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("INSERT INTO INVITATIONS (UUID, PlayerName, ClanID) " + "VALUES ('" + playerName.getUniqueId() + "', '" + playerName.getName() + "', " + getClanID + ");");

            ResultSet rsClanName = stmt.executeQuery("SELECT ClanName FROM CLANS WHERE ClanID IS " + getClanID + ";");
            String invitationmsg = UnitedClans.getInstance().getConfig().getString("messages.invitation");
            TextComponent acceptmsg = new TextComponent(UnitedClans.getInstance().getConfig().getString("messages.accept"));
            acceptmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(UnitedClans.getInstance().getConfig().getString("messages.clickinvite"))));
            acceptmsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptclan"));
            playerName.sendMessage(invitationmsg.replace("%clan%",rsClanName.getString("ClanName")).replace("%player%",sender.getName()));
            playerName.sendMessage(acceptmsg);
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invitationsent"));
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

