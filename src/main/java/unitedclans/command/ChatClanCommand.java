package unitedclans.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class ChatClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public ChatClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        if (args.length < 1) {
            return GeneralUtils.checkUtil(playerSender, language, "INVALID_COMMAND", false);
        }

        List<Map<String, Object>> rsPlayerSender = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", uuid);
        int senderClanID = (int) rsPlayerSender.get(0).get("clan_id");
        if (senderClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            message.append(" " + args[i]);
        }

        String messagePattern = UnitedClans.getInstance().getConfig().getString("clan-msg-pattern");
        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name, clan_color", "clans", "WHERE clan_id = ?", senderClanID);
        String clanName = (String) rsClan.get(0).get("clan_name");
        String clanColor = (String) rsClan.get(0).get("clan_color");

        List<Map<String, Object>> rsClanPlayers = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", senderClanID);
        for (Map<String, Object> i : rsClanPlayers) {
            String playerNameClan = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerNameClan);
            if (playerClan == null) {
                continue;
            }
            playerClan.sendMessage(messagePattern.replace("%clan%", ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET)).replace("%sender%", playerSender.getName()).replace("%message%", message));
        }

        return true;
    }
}
