package me.KyotoDev.liteRegion;

import me.KyotoDev.liteRegion.Config.DataFolder;
import me.KyotoDev.liteRegion.Events.ClickMenu;
import me.KyotoDev.liteRegion.Events.CreateRegion;
import me.KyotoDev.liteRegion.Events.ExplodeEvents;
import me.KyotoDev.liteRegion.Inventores.EffectsInventory;
import me.KyotoDev.liteRegion.Inventores.MainInventory;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.io.File;

public final class Main extends JavaPlugin {

    private static Main instance;


    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();

        File dataFolder = new File(getDataFolder(), "regions");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        instance = this;

        new DataFolder(this);

        getLogger().info("Плагин запустился! Ура");

        getServer().getPluginManager().registerEvents(new CreateRegion(this), this);
        getServer().getPluginManager().registerEvents(new ExplodeEvents(this), this);
        getServer().getPluginManager().registerEvents(new ClickMenu(this), this);
        getServer().getPluginManager().registerEvents(new MainInventory(this), this);
        getServer().getPluginManager().registerEvents(new EffectsInventory(this), this);
    }

    public static String getColorPicker(Main plugin, String path) {
        String message;
        message = plugin.getConfig().getString(path, "Сообщение " + path + " не найдено!");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replaceColor(Main plugin, String path) {
        return ChatColor.translateAlternateColorCodes('&', path);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин остановлен, о нет!");
    }

    public static Main getInstance() {
        return instance;
    }

}
