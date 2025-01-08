package org.hwabeag.cashsystem.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hwabeag.cashsystem.config.ConfigManager;

import java.util.Arrays;

public class PurchaseGUI implements Listener {
    private final Inventory inv;

    FileConfiguration CashSystemConfig = ConfigManager.getConfig("cash-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    private ItemStack getCashItemData(Player p) {
        String name = p.getName();
        int page = PlayerConfig.getInt(name + ".페이지");
        String shopname = PlayerConfig.getString(name + ".구매상점");
        int slot = PlayerConfig.getInt(name + ".구매슬롯");
        ItemStack item = CashSystemConfig.getItemStack("캐시상점." + shopname + ".물품." + page + "." + slot);
        return item;
    }

    private void initItemSetting(Player p) {
        inv.setItem(4,getCashItemData(p));

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a구매하기"));
        itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 물품을 구매합니다.")));
        item.setItemMeta(itemMeta);
        inv.setItem(2,item);

        item = new ItemStack(Material.PAPER, 1);
        itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c취소하기"));
        itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 물품 구매를 취소합니다.")));
        item.setItemMeta(itemMeta);
        inv.setItem(6,item);
    }

    public PurchaseGUI(Player p) {
        String name = p.getName();
        String PlayerShop = PlayerConfig.getString(name + ".구매상점");
        inv = Bukkit.createInventory(null,9,PlayerShop + "구매도우미");
        initItemSetting(p);
    }

    public void open(Player player){
        player.openInventory(inv);
    }

}
