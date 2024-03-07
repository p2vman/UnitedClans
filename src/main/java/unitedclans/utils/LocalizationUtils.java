package unitedclans.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.langs.Langs;
import unitedclans.langs.Langs.LangEN;
import unitedclans.langs.Langs.LangRU;

import java.io.File;
import java.io.IOException;

public class LocalizationUtils {
    public static void loadLang(JavaPlugin plugin) {
        /*File lang = new File(plugin.getDataFolder() + "/lang/", "en_en.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
        if (!lang.exists()) {
            try {
                langConfig.save(lang);
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getServer().getLogger().info("[UnitedClans] Could not create language file");
                plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        for (LangEN item : LangEN.values()) {
            if (langConfig.getString(item.getPath()) == null) {
                langConfig.set(item.getPath(), item.getMsg());
            }
        }
        LangEN.setFile(langConfig);
        try {
            langConfig.save(lang);
        } catch (IOException e) {
            plugin.getServer().getLogger().info("[UnitedClans] Could not save language file");
            plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        File lang1 = new File(plugin.getDataFolder() + "/lang/", "ru_ru.yml");
        YamlConfiguration langConfig1 = YamlConfiguration.loadConfiguration(lang1);
        if (!lang1.exists()) {
            try {
                langConfig1.save(lang1);
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getServer().getLogger().info("[UnitedClans] Could not create language file");
                plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        for (LangRU item : LangRU.values()) {
            if (langConfig1.getString(item.getPath()) == null) {
                langConfig1.set(item.getPath(), item.getMsg());
            }
        }
        LangRU.setFile(langConfig1);
        try {
            langConfig1.save(lang1);
        } catch (IOException e) {
            plugin.getServer().getLogger().info("[UnitedClans] Could not save language file");
            plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }*/
    }

    void createLangFile(JavaPlugin plugin, Class Langs, String langName) {
        File lang = new File(plugin.getDataFolder() + "/lang/", langName);
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
        if (!lang.exists()) {
            try {
                langConfig.save(lang);
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getServer().getLogger().info("[UnitedClans] Could not create language file");
                plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        for (LangRU item : LangRU.values()) {
            if (langConfig.getString(item.getPath()) == null) {
                langConfig.set(item.getPath(), item.getMsg());
            }
        }
        LangRU.setFile(langConfig);
        try {
            langConfig.save(lang);
        } catch (IOException e) {
            plugin.getServer().getLogger().info("[UnitedClans] Could not save language file");
            plugin.getServer().getLogger().info("[UnitedClans] Disabling plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
}