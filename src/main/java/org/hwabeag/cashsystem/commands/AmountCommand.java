package org.hwabeag.cashsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.cashsystem.CashSystem;
import org.hwabeag.cashsystem.config.ConfigManager;
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class AmountCommand implements CommandExecutor {

    FileConfiguration CashSystemConfig = ConfigManager.getConfig("cash-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(CashSystemConfig.getString("cash-system.prefix")));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getConsoleSender().sendMessage(Prefix + " 인게임에서 사용할 수 있습니다.");
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage(Prefix + " 당신은 권한이 없습니다.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Prefix + " /금액 [정도] - 금액을 설정합니다.");
            return true;
        }
        String name = player.getName();
        if (PlayerConfig.getBoolean(name + ".금액설정")) {
            int page = PlayerConfig.getInt(name + ".페이지");
            String shopname = PlayerConfig.getString(name + ".설정상점");
            int slot = PlayerConfig.getInt(name + ".설정슬롯");
            CashSystemConfig.set("캐시상점." + shopname + ".금액." + page + "." + slot, Integer.parseInt(args[0]));
            PlayerConfig.set(name + ".금액설정", false);
            PlayerConfig.set(name + ".설정슬롯", 0);
            ConfigManager.saveConfigs();
            player.sendMessage(Prefix + " 해당 구매 금액을 " + args[0] + "원으로 설정했습니다.");
            getServer().getScheduler().scheduleSyncDelayedTask(CashSystem.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    CashShopAmountSettingGUI inv = new CashShopAmountSettingGUI(player);
                    inv.open(player);
                }
            }, 20 * 2);
            return true;
        }
        return false;
    }
}