package org.hwabeag.cashsystem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.hwabeag.cashsystem.CashSystem;
import org.hwabeag.cashsystem.config.ConfigManager;
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI;
import org.hwabeag.cashsystem.inventorys.CashShopGUI;
import org.hwabeag.cashsystem.inventorys.CashShopItemSettingGUI;
import org.hwabeag.cashsystem.inventorys.PurchaseGUI;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class InvClickEvent implements Listener {
    FileConfiguration CashSystemConfig = ConfigManager.getConfig("cash-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(CashSystemConfig.getString("cash-system.prefix")));

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();
            String name = player.getName();
            String PlayerShop = PlayerConfig.getString(name + ".설정상점");
            if (e.getView().getTitle().equals(PlayerShop + "물품설정")) {
                String clickitem = e.getCurrentItem().getItemMeta().getDisplayName();
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a이전 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page - 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopItemSettingGUI inv = new CashShopItemSettingGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a다음 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page + 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopItemSettingGUI inv = new CashShopItemSettingGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                return;
            }
            if (e.getView().getTitle().equals(PlayerShop + "금액설정")) {
                String clickitem = e.getCurrentItem().getItemMeta().getDisplayName();
                e.setCancelled(true);
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a이전 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page - 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopAmountSettingGUI inv = new CashShopAmountSettingGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a다음 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page + 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopAmountSettingGUI inv = new CashShopAmountSettingGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                int Slot = e.getSlot();
                PlayerConfig.set(name + ".금액설정", true);
                PlayerConfig.set(name + ".설정슬롯", Slot);
                ConfigManager.saveConfigs();
                player.closeInventory();
                player.sendMessage(Prefix + " 금액을 /금액 명령어를 이용해서 설정해주세요.");
                return;
            }
            PlayerShop = PlayerConfig.getString(name + ".구매상점");
            if (e.getView().getTitle().equals(PlayerShop + "캐시상점")) {
                String clickitem = e.getCurrentItem().getItemMeta().getDisplayName();
                e.setCancelled(true);
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a이전 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page - 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopGUI inv = new CashShopGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a다음 페이지"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    int page = PlayerConfig.getInt(name + ".페이지");
                    int plus = page + 1;
                    PlayerConfig.set(name + ".페이지", plus);
                    ConfigManager.saveConfigs();
                    getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            CashShopGUI inv = new CashShopGUI(player);
                            inv.open(player);
                        }
                    }, 20);
                }
                int Slot = e.getSlot();
                PlayerConfig.getString(name + ".구매상점");
                PlayerConfig.set(name + ".구매슬롯", Slot);
                ConfigManager.saveConfigs();
                e.getInventory().clear();
                player.closeInventory();
                getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        PurchaseGUI inv = new PurchaseGUI(player);
                        inv.open(player);
                    }
                }, 20);
                return;
            }
            if (e.getView().getTitle().equals(PlayerShop + "구매도우미")) {
                String clickitem = e.getCurrentItem().getItemMeta().getDisplayName();
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&a구매하기"))) {
                    int page = PlayerConfig.getInt(name + ".페이지");
                    String shopname = PlayerConfig.getString(name + ".구매상점");
                    int slot = PlayerConfig.getInt(name + ".구매슬롯");
                    ItemStack item = CashSystemConfig.getItemStack("캐시상점." + shopname + ".물품." + page + "." + slot);
                    int amount = CashSystemConfig.getInt("캐시상점." + shopname + ".금액." + page + "." + slot);
                    int cash = PlayerConfig.getInt(name + ".캐시");
                    if (amount == 0){
                        e.getInventory().clear();
                        player.closeInventory();
                        player.sendMessage(Prefix + " 해당 물품은 구매할 수 없습니다.");
                        return;
                    }
                    if (cash < amount) {
                        e.getInventory().clear();
                        player.closeInventory();
                        player.sendMessage(Prefix + " 구매하기에 캐시가 부족합니다.");
                        player.sendMessage(Prefix + " 보유 캐시:" + cash + "원");
                        return;
                    }
                    e.getInventory().clear();
                    player.closeInventory();
                    PlayerConfig.set(name + ".캐시", cash - amount);
                    ConfigManager.saveConfigs();
                    player.sendMessage(Prefix + " 정상적으로 물품을 구매했습니다.");
                    player.sendMessage(Prefix + " 보유 캐시:" + cash + "원");
                    player.getInventory().addItem(item);
                }
                if (clickitem.equals(ChatColor.translateAlternateColorCodes('&', "&c취소하기"))) {
                    e.getInventory().clear();
                    player.closeInventory();
                    player.sendMessage(Prefix + " 작업을 취소했습니다.");
                }
            }
        }
    }
}
