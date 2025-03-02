package me.KyotoDev.liteRegion.Inventores;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import me.KyotoDev.liteRegion.Config.DataFolder;
import me.KyotoDev.liteRegion.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainInventory implements Listener {

    private static Main plugin;
    public static HashMap<UUID, PSRegion> playerRegions = new HashMap<>();

    public MainInventory(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        String titleConfig = Main.getColorPicker(plugin, "inv.main.title");


        if (title.equalsIgnoreCase(titleConfig.toString())) {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();
            ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String active = clickedItem.getItemMeta().getLocalizedName();
            if (active != null) {
                switch (active) {
                    case "ShopRegion":
                        e.getInventory().close();
                        EffectsInventory.openShopRGInventory(p);
                        break;
                    default:
                        break;
                }
            } else {
                p.sendMessage("Локализованное имя не установлено.");
            }
        }

    }

    public static void openMainRGInventory(Player player, PSRegion region) {
        String title = Main.getColorPicker(plugin, "inv.main.title");
        Inventory menu = Bukkit.createInventory(null, 45, title);

        playerRegions.put(player.getUniqueId(), region);

        uploadItems(menu, "inv.main.items", player.getUniqueId());

        player.openInventory(menu);
    }


    private static void uploadItems(Inventory inventory, String path, UUID uuid) {

        FileConfiguration config = plugin.getConfig();

        if (!config.isConfigurationSection(path)) {
            return;
        }

        for (String key : config.getConfigurationSection(path).getKeys(false)) {

            Material material = Material.matchMaterial(key);
            if (material == null) {
                continue;
            }


            String itemPath = path + "." + key;
            List<Integer> slots = config.getIntegerList(itemPath + ".slots");
            String nameConfig = Main.getColorPicker(plugin, itemPath + ".name");
            List<String> loreConfig = config.getStringList(itemPath + ".lore");
            String active = config.getString(itemPath + ".active");
            Boolean regionInfo = config.getBoolean(itemPath + ".regionInfo");

            if (slots.isEmpty()) {
                continue;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {

                if (nameConfig != null) {
                    meta.setDisplayName(nameConfig);
                }

                if (loreConfig != null && !loreConfig.isEmpty()) {

                    List<String> lore = new ArrayList<>();

                    if (regionInfo != null && regionInfo) {

                        PSRegion region = playerRegions.get(uuid);
                        OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(region.getOwners().get(0));

                        for (String line : loreConfig) {
                            lore.add(Main.replaceColor(plugin, line)
                                    .replace("%x%", region.getProtectBlock().getX() + "")
                                    .replace("%y%", region.getProtectBlock().getY() + "")
                                    .replace("%z%", region.getProtectBlock().getZ() + "")
                                    .replace("%owner%", offlineOwner.getName() + "")
                                    .replace("%durability%", DataFolder.getRegionDurability(region.getProtectBlock().getLocation()) + "")
                            );
                        }
                    } else {
                        for (String line : loreConfig) {
                            lore.add(Main.replaceColor(plugin, line));
                        }
                    }

                    meta.setLore(lore);

                }

                if (active != null) {
                    meta.setLocalizedName(active);
                }


                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }

            for (int slot : slots) {
                if (slot >= 0 && slot < inventory.getSize()) {
                    inventory.setItem(slot, item);
                } else {
                    System.out.println("Ошибка: слот " + slot + " выходит за пределы инвентаря.");
                }
            }
        }
    }

}
