package org.hwabeag.cashsystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hwabeag.cashsystem.commands.AmountCommand;
import org.hwabeag.cashsystem.commands.CashCommand;
import org.hwabeag.cashsystem.commands.ShopCommand;
import org.hwabeag.cashsystem.config.ConfigManager;
import org.hwabeag.cashsystem.events.InvClickEvent;
import org.hwabeag.cashsystem.events.InvCloseEvent;
import org.hwabeag.cashsystem.events.JoinEvent;
import org.hwabeag.cashsystem.expansions.CashSystemExpansion;

import java.util.Objects;

public final class CashSystem extends JavaPlugin {

    private static ConfigManager configManager;

    private FileConfiguration config;

    public static CashSystem getPlugin() {
        return JavaPlugin.getPlugin(CashSystem.class);
    }

    public static void getConfigManager() {
        if (configManager == null)
            configManager = new ConfigManager();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new InvClickEvent(), this);
        getServer().getPluginManager().registerEvents(new InvCloseEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getServer().getPluginCommand("금액")).setExecutor(new AmountCommand());
        Objects.requireNonNull(getServer().getPluginCommand("캐시")).setExecutor(new CashCommand());
        Objects.requireNonNull(getServer().getPluginCommand("캐시상점")).setExecutor(new ShopCommand());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("[CashSystem] Enable");
        this.saveDefaultConfig();
        getConfigManager();
        registerCommands();
        registerEvents();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CashSystemExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("[CashSystem] Disable");
        ConfigManager.saveConfigs();
    }
}
