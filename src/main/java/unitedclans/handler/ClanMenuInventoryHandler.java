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

import java.sql.*;

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
            event.setCancelled(true);
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
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBER_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {

            } else if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                MenuClanUtils.openChangeRoleMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.BARRIER) {
                plugin.getServer().dispatchCommand(player, "kickclan " + selectedPlayerName);
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openMembersMenu(player, con);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CHANGE_ROLE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.STICK) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.member"));
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.BLAZE_ROD) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.elder"));
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openMemberMenu(player, con, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        }
    }
}