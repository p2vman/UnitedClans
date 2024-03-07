package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.MenuClanUtils;

import java.sql.*;
import java.util.*;

public class MenuClanCommand implements CommandExecutor {
    private Connection con;
    public MenuClanCommand(Connection con) {
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length >= 1) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsPlayerClan = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            Integer PlayerClanID = rsPlayerClan.getInt("ClanID");
            if (PlayerClanID == 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            MenuClanUtils.openClanMenu(playerSender);

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}