package org.hwabeag.cashsystem.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.hwabeag.cashsystem.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

public class InvCloseEvent implements Listener {

    FileConfiguration CashSystemConfig = ConfigManager.getConfig("cash-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        String name = p.getName();
        if (e.getView().getTitle().equals("물품설정")) {
            if (PlayerConfig.getString(name + ".설정상점") != null) {
                String shopname = PlayerConfig.getString(name + ".설정상점");
                if (CashSystemConfig.getString("캐시상점." + shopname) != null) {
                    int index = CashSystemConfig.getInt("캐시상점." + shopname + ".라인");
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int N = 0;
                    while (N <= index * 9 - 1) {
                        if (e.getInventory().getItem(N) != null) {
                            @Nullable ItemStack item = e.getInventory().getItem(N);
                            CashSystemConfig.set("캐시상점." + shopname + ".물품." + page + "." + N, item);
                            if (CashSystemConfig.getString("캐시상점." + shopname + ".금액." + page + "." + N) == null) {
                                CashSystemConfig.set("캐시상점." + shopname + ".금액." + page + "." + N, 0);
                            }
                            ConfigManager.saveConfigs();
                        } else {
                            if (CashSystemConfig.getItemStack("캐시상점." + shopname + ".물품." + page + "." + N) != null) {
                                CashSystemConfig.set("캐시상점." + shopname + ".물품." + page + "." + N, null);
                                CashSystemConfig.set("캐시상점." + shopname + ".금액." + page + "." + N, null);
                                ConfigManager.saveConfigs();
                            }
                        }
                        N += 1;
                    }
                    PlayerConfig.set(name + ".설정상점", "없음");
                    ConfigManager.saveConfigs();
                }
            }
        }
    }
}
