package unitedclans;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.command.*;
import unitedclans.handler.*;
import unitedclans.langs.DefaultConfig;
import unitedclans.utils.LocalizationUtils;

import java.sql.*;

public final class UnitedClans extends JavaPlugin implements Listener {

    private static UnitedClans instance;
    private Connection con = null;
    @Override
    public void onEnable() {
        instance = this;
        DefaultConfig.createDefaultConfigFile(this);
        LocalizationUtils.loadLang(this);

        try {
            con = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder() + "/ucdatabase.db");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PLAYERS (UUID TEXT NOT NULL PRIMARY KEY, PlayerName TEXT, ClanID INTEGER, ClanRole TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CLANS (ClanID INTEGER NOT NULL PRIMARY KEY, ClanName TEXT, ClanColor TEXT, CountMembers INTEGER)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS INVITATIONS (UUID TEXT NOT NULL PRIMARY KEY, PlayerName TEXT, ClanID INTEGER)");
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        getServer().getLogger().info("[UnitedClans] UnitedClans is enabled");
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(this, con), this);
        getServer().getPluginManager().registerEvents(new ClanMenuInventoryHandler(this, con), this);
        getServer().getPluginCommand("createclan").setExecutor(new CreateClanCommand(this, con));
        getServer().getPluginCommand("createclan").setTabCompleter(new CreateClanTabCompleter());
        getServer().getPluginCommand("deleteclan").setExecutor(new DeleteClanCommand(this, con));
        getServer().getPluginCommand("deleteclan").setTabCompleter(new DeleteClanTabCompleter());
        getServer().getPluginCommand("inviteclan").setExecutor(new InviteClanCommand(this, con));
        getServer().getPluginCommand("inviteclan").setTabCompleter(new InviteClanTabCompleter());
        getServer().getPluginCommand("acceptclan").setExecutor(new AcceptClanCommand(this, con));
        getServer().getPluginCommand("acceptclan").setTabCompleter(new AcceptClanTabCompleter());
        getServer().getPluginCommand("kickclan").setExecutor(new KickClanCommand(this, con));
        getServer().getPluginCommand("kickclan").setTabCompleter(new KickClanTabCompleter(con));
        getServer().getPluginCommand("leaveclan").setExecutor(new LeaveClanCommand(this, con));
        getServer().getPluginCommand("leaveclan").setTabCompleter(new LeaveClanTabCompleter());
        getServer().getPluginCommand("setroleclan").setExecutor(new SetRoleClanCommand(this, con));
        getServer().getPluginCommand("setroleclan").setTabCompleter(new SetRoleClanTabCompleter(con));
        getServer().getPluginCommand("menuclan").setExecutor(new MenuClanCommand(con));
        getServer().getPluginCommand("menuclan").setTabCompleter(new MenuClanTabCompleter());
        getServer().getPluginCommand("chatclan").setExecutor(new ChatClanCommand(this, con));
        getServer().getPluginCommand("chatclan").setTabCompleter(new ChatClanTabCompleter());
        getServer().getPluginCommand("changeleaderclan").setExecutor(new ChangeLeaderClanCommand(this, con));
        getServer().getPluginCommand("changeleaderclan").setTabCompleter(new ChangeLeaderClanTabCompleter(con));
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