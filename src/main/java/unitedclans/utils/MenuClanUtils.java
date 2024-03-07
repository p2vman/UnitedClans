package unitedclans.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MenuClanUtils {
    public static void openClanMenu(Player player) {
        Inventory clanMenuGUI = Bukkit.createInventory(player, 27, ChatColor.BOLD + "Clan menu");

        ItemStack clanBank = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta clanBank_meta = clanBank.getItemMeta();
        clanBank_meta.setDisplayName(ChatColor.RESET + (ChatColor.YELLOW + "Clan bank"));
        ArrayList<String> clanBank_lore = new ArrayList<>();
        clanBank_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Opens the clan bank menu"));
        clanBank_meta.setLore(clanBank_lore);
        clanBank.setItemMeta(clanBank_meta);
        clanMenuGUI.setItem(10, clanBank);

        ItemStack players = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta players_meta = players.getItemMeta();
        players_meta.setDisplayName(ChatColor.RESET + (ChatColor.GREEN + "Clan members"));
        ArrayList<String> players_lore = new ArrayList<>();
        players_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Opens list of clan members"));
        players_meta.setLore(players_lore);
        players.setItemMeta(players_meta);
        clanMenuGUI.setItem(13, players);

        ItemStack topClans = new ItemStack(Material.BEACON, 1);
        ItemMeta topClans_meta = topClans.getItemMeta();
        topClans_meta.setDisplayName(ChatColor.RESET + (ChatColor.AQUA + "Top clans"));
        ArrayList<String> topClans_lore = new ArrayList<>();
        topClans_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Opens list of the best clans"));
        topClans_meta.setLore(topClans_lore);
        topClans.setItemMeta(topClans_meta);
        clanMenuGUI.setItem(16, topClans);

        player.openInventory(clanMenuGUI);
    }

    public static void openMembersMenu(Player player, Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rsPlayerClan = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + player.getUniqueId() + "'");
            Integer PlayerClanID = rsPlayerClan.getInt("ClanID");
            ResultSet rsPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + PlayerClanID);
            ArrayList<String> PlayerList = new ArrayList<>();
            ArrayList<String> RoleList = new ArrayList<>();
            while (rsPlayers.next()) {
                String getPlayersName = rsPlayers.getString("PlayerName");
                PlayerList.add(getPlayersName);
                String getPlayersRole = rsPlayers.getString("ClanRole");
                RoleList.add(getPlayersRole);
            }

            Inventory membersMenuGUI = Bukkit.createInventory(player, 36, ChatColor.BOLD + "Members menu");
            for (int i = 0; i < PlayerList.size(); i++) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                meta.setDisplayName(ChatColor.RESET + (ChatColor.WHITE + PlayerList.get(i)));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.ITALIC + (ChatColor.DARK_PURPLE + RoleList.get(i)));
                meta.setLore(lore);
                meta.setOwner(PlayerList.get(i));
                playerHead.setItemMeta(meta);
                membersMenuGUI.addItem(playerHead);
            }

            ItemStack BackToMenu = new ItemStack(Material.STRUCTURE_VOID, 1);
            ItemMeta BackToMenu_meta = BackToMenu.getItemMeta();
            BackToMenu_meta.setDisplayName(ChatColor.RESET + (ChatColor.AQUA + "Back to menu"));
            ArrayList<String> BackToMenu_lore = new ArrayList<>();
            BackToMenu_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Opens main clan menu"));
            BackToMenu_meta.setLore(BackToMenu_lore);
            BackToMenu.setItemMeta(BackToMenu_meta);
            for (int n = 27; n <= 35; n++) {
                membersMenuGUI.setItem(n, BackToMenu);
            }

            player.openInventory(membersMenuGUI);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void openMemberMenu(Player player, Connection con, String selectedPlayerName) {
        Inventory memberMenuGUI = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Member");

        ItemStack playerSelected = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerSelected_meta = (SkullMeta) playerSelected.getItemMeta();
        playerSelected_meta.setDisplayName(ChatColor.RESET + (ChatColor.WHITE + selectedPlayerName));
        ArrayList<String> playerSelected_lore = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rsplayerSelectedRole = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + selectedPlayerName + "'");
            String playerSelectedRole = rsplayerSelectedRole.getString("ClanRole");
            playerSelected_lore.add(ChatColor.ITALIC + (ChatColor.DARK_PURPLE + playerSelectedRole));
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        playerSelected_meta.setLore(playerSelected_lore);
        playerSelected_meta.setOwner(selectedPlayerName);
        playerSelected.setItemMeta(playerSelected_meta);
        memberMenuGUI.setItem(1, playerSelected);

        ItemStack changeRole = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta changeRole_meta = changeRole.getItemMeta();
        changeRole_meta.setDisplayName(ChatColor.RESET + (ChatColor.GOLD + "Change role"));
        ArrayList<String> changeRole_lore = new ArrayList<>();
        changeRole_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Changes role"));
        changeRole_meta.setLore(changeRole_lore);
        changeRole.setItemMeta(changeRole_meta);
        memberMenuGUI.setItem(3, changeRole);

        ItemStack kickPlayer = new ItemStack(Material.BARRIER, 1);
        ItemMeta kickPlayer_meta = kickPlayer.getItemMeta();
        kickPlayer_meta.setDisplayName(ChatColor.RESET + (ChatColor.AQUA + "Kick member"));
        ArrayList<String> kickPlayer_lore = new ArrayList<>();
        kickPlayer_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Kicks member out of the clan"));
        kickPlayer_meta.setLore(kickPlayer_lore);
        kickPlayer.setItemMeta(kickPlayer_meta);
        memberMenuGUI.setItem(5, kickPlayer);

        ItemStack BackToMenu = new ItemStack(Material.STRUCTURE_VOID, 1);
        ItemMeta BackToMenu_meta = BackToMenu.getItemMeta();
        BackToMenu_meta.setDisplayName(ChatColor.RESET + (ChatColor.AQUA + "Back to menu"));
        ArrayList<String> BackToMenu_lore = new ArrayList<>();
        BackToMenu_lore.add(ChatColor.ITALIC + (ChatColor.GRAY + "Opens main clan menu"));
        BackToMenu_meta.setLore(BackToMenu_lore);
        BackToMenu.setItemMeta(BackToMenu_meta);
        memberMenuGUI.setItem(7, BackToMenu);

        player.openInventory(memberMenuGUI);
    }
}