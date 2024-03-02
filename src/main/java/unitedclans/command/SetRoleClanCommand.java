package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;

import java.sql.*;
import java.util.*;

public class SetRoleClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public SetRoleClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        String playerNameInput = args[0];
        String playerRoleInput = args[1];
        if (args.length <= 0 || args.length >= 3) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        if (playerNameInput == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if ((playerRoleInput == null) || (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.elder"))) && (!Objects.equals(playerRoleInput, UnitedClans.getInstance().getConfig().getString("roles.member")))) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongrolename"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        if (Objects.equals(playerSender.getName(), playerNameInput)) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.setroleyourself"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String senderRole = rsSender.getString("ClanRole");
            Integer senderClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(senderRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.norightssetrole"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsPlayerName = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + playerNameInput + "';");
            Integer playerClanID = rsPlayerName.getInt("ClanID");
            if (playerClanID == 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playernotmemberclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (senderClanID != playerClanID) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playernotyourclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanRole = '" + playerRoleInput + "' WHERE PlayerName IS '" + playerNameInput + "';");

            String successfullychangedrolemsg = UnitedClans.getInstance().getConfig().getString("messages.successfullychangedrole");
            playerSender.sendMessage(successfullychangedrolemsg.replace("%role%", playerRoleInput));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            Player argPlayerName = plugin.getServer().getPlayer(playerNameInput);
            if (argPlayerName != null) {
                String youbeenassignedmsg = UnitedClans.getInstance().getConfig().getString("messages.youbeenassigned");
                argPlayerName.sendMessage(youbeenassignedmsg.replace("%role%", playerRoleInput));
                argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}