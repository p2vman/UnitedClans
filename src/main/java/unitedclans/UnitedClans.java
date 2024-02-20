package unitedclans;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.command.*;
import unitedclans.handler.PlayerJoinEventHandler;

import java.sql.*;

public final class UnitedClans extends JavaPlugin implements Listener {

    private static UnitedClans instance;
    private Connection con = null;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        try {
            con = DriverManager.getConnection("jdbc:sqlite:plugins/UnitedClans/ucdatabase.db");
            Statement stmt = con.createStatement();
            String tablePLAYERS = "CREATE TABLE IF NOT EXISTS PLAYERS (UUID TEXT NOT NULL PRIMARY KEY, PlayerName TEXT, ClanID INTEGER, ClanRole TEXT)";
            stmt.executeUpdate(tablePLAYERS);
            String tableCLANS = "CREATE TABLE IF NOT EXISTS CLANS (ClanID INTEGER NOT NULL PRIMARY KEY, ClanName TEXT)";
            stmt.executeUpdate(tableCLANS);
            String tableINVITATIONS = "CREATE TABLE IF NOT EXISTS INVITATIONS (UUID TEXT NOT NULL PRIMARY KEY, PlayerName TEXT, ClanID INTEGER)";
            stmt.executeUpdate(tableINVITATIONS);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        getServer().getLogger().info("[UnitedClans] UnitedClans is enabled");
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(con), this);
        getServer().getPluginCommand("createclan").setExecutor(new CreateClanCommand(con));
        getServer().getPluginCommand("createclan").setTabCompleter(new CreateClanTabCompleter());
        getServer().getPluginCommand("deleteclan").setExecutor(new DeleteClanCommand(this, con));
        getServer().getPluginCommand("deleteclan").setTabCompleter(new DeleteClanTabCompleter());
        getServer().getPluginCommand("inviteclan").setExecutor(new InviteClanCommand(this, con));
        getServer().getPluginCommand("inviteclan").setTabCompleter(new InviteClanTabCompleter());
        getServer().getPluginCommand("acceptclan").setExecutor(new AcceptClanCommand(this, con));
        getServer().getPluginCommand("acceptclan").setTabCompleter(new AcceptClanTabCompleter());
        getServer().getPluginCommand("kickclan").setExecutor(new KickClanCommand(this, con));
        getServer().getPluginCommand("kickclan").setTabCompleter(new KickClanTabCompleter(con));
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("[UnitedClans] UnitedClans is disabled");
        try {
            con.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static UnitedClans getInstance() {return instance;}
}