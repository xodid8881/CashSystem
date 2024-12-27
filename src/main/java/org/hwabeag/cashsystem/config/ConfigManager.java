package org.hwabeag.cashsystem.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.cashsystem.CashSystem;

import java.util.HashMap;

public class ConfigManager {
    private static final CashSystem plugin = CashSystem.getPlugin();

    private static final HashMap<String, ConfigMaker> configSet = new HashMap<>();

    public ConfigManager() {
        String path = plugin.getDataFolder().getAbsolutePath();
        configSet.put("cash-system", new ConfigMaker(path, "CashSystem.yml"));
        configSet.put("player", new ConfigMaker(path, "Player.yml"));
        loadSettings();
        saveConfigs();
    }

    public static void reloadConfigs() {
        for (String key : configSet.keySet()){
            plugin.getLogger().info(key);
            configSet.get(key).reloadConfig();
        }
    }

    public static void saveConfigs(){
        for (String key : configSet.keySet())
            configSet.get(key).saveConfig();
    }

    public static FileConfiguration getConfig(String fileName) {
        return configSet.get(fileName).getConfig();
    }

    public static void loadSettings(){
        FileConfiguration StyleSystemConfig = getConfig("cash-system");
        StyleSystemConfig.options().copyDefaults(true);
        StyleSystemConfig.addDefault("cash-system.prefix", "&a&l[CashSystem]&7");
    }
}