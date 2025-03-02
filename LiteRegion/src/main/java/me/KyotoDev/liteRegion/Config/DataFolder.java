package me.KyotoDev.liteRegion.Config;

import me.KyotoDev.liteRegion.Main;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataFolder {

    private static Main plugin;

    public DataFolder(Main plugin) {
        this.plugin = plugin;
    }

    public static void createRegionFile(Location location, int durability, String id) {
        File regionsFolder = new File(plugin.getDataFolder(), "regions");
        File regionFile = new File(regionsFolder, "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ() + ".yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(regionFile);
        config.set("durability", durability);
        config.set("id", id);

        try {
            config.save(regionFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRegionFile(Location location) {
        File regionsFolder = new File(plugin.getDataFolder(), "regions");
        File regionFile = new File(regionsFolder, "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ() + ".yml");

        if (regionFile.exists()) {
            regionFile.delete();
        }
    }

    public static void updateRegionDurability(Location location, int newDurability) {
        File regionsFolder = new File(plugin.getDataFolder(), "regions");
        File regionFile = new File(regionsFolder, "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ() + ".yml");

        if (regionFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(regionFile);
            config.set("durability", newDurability);

            try {
                config.save(regionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getRegionDurability(Location location) {
        File regionsFolder = new File(plugin.getDataFolder(), "regions");
        File regionFile = new File(regionsFolder, "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ() + ".yml");

        if (regionFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(regionFile);
            return config.getInt("durability", 1);
        }
        return -1;
    }

    public static String getRegionId(Location location) {
        File regionsFolder = new File(plugin.getDataFolder(), "regions");
        File regionFile = new File(regionsFolder, "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ() + ".yml");

        if (regionFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(regionFile);
            return config.getString("id", null);
        }
        return null;
    }
}
