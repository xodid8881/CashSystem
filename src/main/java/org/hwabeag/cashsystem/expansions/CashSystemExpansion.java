package org.hwabeag.cashsystem.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.cashsystem.CashSystem;
import org.hwabeag.cashsystem.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CashSystemExpansion extends PlaceholderExpansion {


    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    private final CashSystem plugin; //

    public CashSystemExpansion(CashSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cash";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("get")) { // %cash_get%
            String name = Objects.requireNonNull(player).getName();
            if (PlayerConfig.get(name + ".캐시") != null) {
                return String.valueOf(PlayerConfig.getInt(name + ".캐시"));
            }
            return "0";
        }
        return null; //
    }
}