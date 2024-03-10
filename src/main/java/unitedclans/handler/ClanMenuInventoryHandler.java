package unitedclans.handler;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.MenuClanUtils;
import unitedclans.utils.ShowClanUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class ClanMenuInventoryHandler implements Listener {
    private final JavaPlugin plugin;
    private Connection con;
    private String selectedPlayerName;
    public ClanMenuInventoryHandler(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player player = (Player) event.getWhoClicked();

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CLAN_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                MenuClanUtils.openMembersMenu(player, con);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.GOLD_BLOCK) {

            } else if (event.getCurrentItem().getType() == Material.BEACON) {

            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBERS_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                selectedPlayerName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                MenuClanUtils.openMemberMenu(player, con, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
        }
        else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBER_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {

            } else if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                tryCatch:
                try {
                    Statement stmt = con.createStatement();
                    ResultSet rsSetRolePlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + selectedPlayerName + "'");
                    String SetRolePlayerRole = rsSetRolePlayer.getString("ClanRole");
                    if (Objects.equals(player.getName(), selectedPlayerName)) {
                        player.sendMessage(LocalizationUtils.langCheck(language, "SET_ROLE_YOURSELF"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                        break tryCatch;
                    }
                    ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + player.getUniqueId() + "'");
                    String getRoleUUID = rsSender.getString("ClanRole");
                    if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                        player.sendMessage(LocalizationUtils.langCheck(language, "NO_RIGHTS_SET_ROLE"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                        break tryCatch;
                    }

                    String setRole = null;
                    String setPlayerRole = null;
                    if (Objects.equals(SetRolePlayerRole, UnitedClans.getInstance().getConfig().getString("roles.member"))) {
                        setRole = UnitedClans.getInstance().getConfig().getString("roles.elder");
                        setPlayerRole = LocalizationUtils.langCheck(language, "ELDER");
                    } else if (Objects.equals(SetRolePlayerRole, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                        setRole = UnitedClans.getInstance().getConfig().getString("roles.member");
                        setPlayerRole = LocalizationUtils.langCheck(language, "MEMBER");
                    }
                    stmt.executeUpdate("UPDATE PLAYERS SET ClanRole = '" + setRole + "' WHERE PlayerName IS '" + selectedPlayerName + "'");

                    String successfullychangedrolemsg = LocalizationUtils.langCheck(language, "SUCCESSFULLY_CHANGED_ROLE");
                    player.sendMessage(successfullychangedrolemsg.replace("%role%", setPlayerRole));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    Player argPlayerName = plugin.getServer().getPlayer(selectedPlayerName);
                    if (argPlayerName != null) {
                        String youbeenassignedmsg = LocalizationUtils.langCheck(language, "YOU_BEEN_ASSIGNED");
                        argPlayerName.sendMessage(youbeenassignedmsg.replace("%role%", setPlayerRole));
                        argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    stmt.close();

                    player.closeInventory();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            } else if (event.getCurrentItem().getType() == Material.BARRIER) {
                tryCatch:
                try {
                    Statement stmt = con.createStatement();
                    ResultSet rsKickedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + selectedPlayerName + "'");
                    String KickedPlayerRole = rsKickedPlayer.getString("ClanRole");
                    Integer KickedPlayerClanID = rsKickedPlayer.getInt("ClanID");
                    if (Objects.equals(player.getName(), selectedPlayerName)) {
                        player.sendMessage(LocalizationUtils.langCheck(language, "KICK_YOURSELF"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                        break tryCatch;
                    }
                    ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + player.getUniqueId() + "'");
                    String getRoleUUID = rsSender.getString("ClanRole");
                    Integer getClanID = rsSender.getInt("ClanID");
                    if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                        player.sendMessage(LocalizationUtils.langCheck(language, "NO_RIGHTS_KICK"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                        break tryCatch;
                    }
                    if (Objects.equals(KickedPlayerRole, getRoleUUID) || Objects.equals(KickedPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                        player.sendMessage(LocalizationUtils.langCheck(language, "ROLE_IS_HIGHER"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                        break tryCatch;
                    }

                    stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.no-clan") + "' WHERE PlayerName IS '" + selectedPlayerName + "'");
                    stmt.executeUpdate("UPDATE CLANS SET CountMembers = CountMembers - 1 WHERE ClanID IS " + getClanID);

                    ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + getClanID);
                    String playerkickedmsg = LocalizationUtils.langCheck(language, "PLAYER_WAS_KICKED");
                    while (rsClanPlayers.next()) {
                        String playerNameClan = rsClanPlayers.getString("PlayerName");
                        Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                        playerClan.sendMessage(playerkickedmsg.replace("%player%", selectedPlayerName));
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    Player argPlayerName = plugin.getServer().getPlayer(selectedPlayerName);
                    if (argPlayerName != null) {
                        ResultSet rsClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + KickedPlayerClanID);
                        String KickedPlayerClanName = rsClanName.getString("ClanName");
                        String youwaskickedmsg = LocalizationUtils.langCheck(language, "YOU_WAS_KICKED");
                        argPlayerName.sendMessage(youwaskickedmsg.replace("%clan%", KickedPlayerClanName));
                        argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    stmt.close();

                    ShowClanUtils.showClan(plugin, con);
                    player.closeInventory();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openMembersMenu(player, con);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
        }
        event.setCancelled(true);
    }
}