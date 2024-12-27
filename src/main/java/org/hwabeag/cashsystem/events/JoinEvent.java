package org.hwabeag.cashsystem.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hwabeag.cashsystem.config.ConfigManager;

public class JoinEvent implements Listener {
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (PlayerConfig.getString(name + ".캐시") == null) {
            PlayerConfig.addDefault(name, "");
            PlayerConfig.set(name + ".캐시", 0);
            PlayerConfig.set(name + ".페이지", 0);
            PlayerConfig.set(name + ".구매상점", "없음");
            PlayerConfig.set(name + ".구매슬롯", 0);
            PlayerConfig.set(name + ".설정상점", "없음");
            PlayerConfig.set(name + ".금액설정", false);
            PlayerConfig.set(name + ".설정슬롯", 0);
            ConfigManager.saveConfigs();
        }
    }
}
