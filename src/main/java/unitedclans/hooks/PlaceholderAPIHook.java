package unitedclans.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public PlaceholderAPIHook(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "unitedclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");

        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (params.equals("clanName")) {
                List<Map<String, Object>> rsClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int clanID = (int) rsClanID.get(0).get("clan_id");

                if (clanID != 0) {
                    List<Map<String, Object>> rsClanName = dbDriver.selectData("clan_name, clan_color", "clans", "WHERE clan_id = ?", clanID);
                    String clanName = (String) rsClanName.get(0).get("clan_name");
                    String clanColor = (String) rsClanName.get(0).get("clan_color");

                    return ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET) + " ";
                }

                return "";
            } else if (params.equals("clanCountMembers")) {
                List<Map<String, Object>> rsClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int clanID = (int) rsClanID.get(0).get("clan_id");

                if (clanID != 0) {
                    List<Map<String, Object>> rsClanCountMembers = dbDriver.selectData("count_members", "clans", "WHERE clan_id = ?", clanID);
                    int countMembers = (int) rsClanCountMembers.get(0).get("count_members");

                    return ChatColor.YELLOW + String.valueOf(countMembers) + "☠ " + ChatColor.RESET;
                }

                return "";
            } else if (params.equals("clanKills")) {
                List<Map<String, Object>> rsClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int clanID = (int) rsClanID.get(0).get("clan_id");

                if (clanID != 0) {
                    List<Map<String, Object>> rsClanKills = dbDriver.selectData("kills", "clans", "WHERE clan_id = ?", clanID);
                    int clanKills = (int) rsClanKills.get(0).get("kills");

                    return ChatColor.DARK_RED + String.valueOf(clanKills) + "\uD83D\uDDE1 " + ChatColor.RESET;
                }

                return "";
            } else if (params.equals("clanBank")) {
                List<Map<String, Object>> rsClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int clanID = (int) rsClanID.get(0).get("clan_id");

                if (clanID != 0) {
                    List<Map<String, Object>> rsClanBank = dbDriver.selectData("bank", "clans", "WHERE clan_id = ?", clanID);
                    int clanBank = (int) rsClanBank.get(0).get("bank");

                    return ChatColor.DARK_GREEN + String.valueOf(clanBank) + "$ " + ChatColor.RESET;
                }

                return "";
            } else if (params.equals("playerRole")) {
                List<Map<String, Object>> rsPlayerRole = dbDriver.selectData("clan_role", "players", "WHERE uuid = ?", player.getUniqueId());
                String playerRole = (String) rsPlayerRole.get(0).get("clan_role");

                if (!playerRole.equals(UnitedClans.getInstance().getConfig().getString("roles.no-clan"))) {
                    String color = "WHITE";
                    String role = "";
                    if (playerRole.equals(UnitedClans.getInstance().getConfig().getString("roles.member"))) {
                        color = "GREEN";
                        role = LocalizationUtils.langCheck(language, "MEMBER");
                    } else if (playerRole.equals(UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                        color = "GOLD";
                        role = LocalizationUtils.langCheck(language, "ELDER");
                    } else if (playerRole.equals(UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                        color = "DARK_PURPLE";
                        role = LocalizationUtils.langCheck(language, "LEADER");
                    }
                    return ChatColor.valueOf(color) + role + ChatColor.RESET;
                }

                return "";
            } else if (params.equals("playerKills")) {
                List<Map<String, Object>> rsPlayerKills = dbDriver.selectData("kills", "players", "WHERE uuid = ?", player.getUniqueId());
                int playerKills = (int) rsPlayerKills.get(0).get("kills");

                return ChatColor.DARK_RED + String.valueOf(playerKills) + "\uD83D\uDDE1 " + ChatColor.RESET;
            } else if (params.equals("playerDonations")) {
                List<Map<String, Object>> rsPlayerDonations = dbDriver.selectData("donations", "players", "WHERE uuid = ?", player.getUniqueId());
                int playerDonations = (int) rsPlayerDonations.get(0).get("donations");

                return ChatColor.DARK_GREEN + String.valueOf(playerDonations) + "$ " + ChatColor.RESET;
            }
        }

        return null;
    }
}
