package me.KyotoDev.liteRegion.Events;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.event.PSCreateEvent;
import dev.espi.protectionstones.event.PSRemoveEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.KyotoDev.liteRegion.Config.DataFolder;
import me.KyotoDev.liteRegion.Config.GlowEffect;
import me.KyotoDev.liteRegion.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CreateRegion implements Listener {

    private final Main plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public CreateRegion(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreateRegion(PSCreateEvent event) {
        Location regionLocation = event.getRegion().getProtectBlock().getLocation();
        Block regionBlock = event.getRegion().getProtectBlock();
        FileConfiguration config = plugin.getConfig();


        String blockPath = "blocks." + regionBlock.getType();

        DataFolder.createRegionFile(regionLocation, config.getInt(blockPath + ".settings.durability"), event.getRegion().getId());

        if (config.getBoolean(blockPath + ".holograms.enable")) {
            createHologram(regionLocation, regionBlock, config, event.getPlayer().getName());
        }

//         TODO

        World world = regionBlock.getWorld();

        int size = 50;
        int halfSize = size / 2;

        Location corner1 = regionBlock.getLocation().add(-halfSize, 0, -halfSize);
        Location corner2 = regionBlock.getLocation().add(halfSize, 0, halfSize);

        GlowEffect.visualizeRegion(event.getPlayer(), corner1, corner2);

        if (config.getBoolean(blockPath + ".settings.create-lightning-strike")) {
            regionLocation.getWorld().strikeLightningEffect(regionLocation);
        }
    }

    @EventHandler
    public void onRemoveRegion(PSRemoveEvent event) {
        Location regionLocation = event.getRegion().getProtectBlock().getLocation();
        Block regionBlock = event.getRegion().getProtectBlock();
        FileConfiguration config = plugin.getConfig();

        String blockPath = "blocks." + regionBlock.getType();

        DataFolder.deleteRegionFile(regionLocation);

        if (config.getBoolean(blockPath + ".holograms.enable")) {
            removeHologram(regionLocation);
        }

        if (config.getBoolean(blockPath + ".settings.delete-lightning-strike")) {
            regionLocation.getWorld().strikeLightningEffect(regionLocation);
        }
    }

    public static void createHologram(Location location, Block block, FileConfiguration config, String playerName) {
        Location center = location.toCenterLocation();
        List<String> lines = config.getStringList("blocks." + block.getType() + ".holograms.lines");
        int durability = DataFolder.getRegionDurability(location);

        Hologram hologram = DHAPI.createHologram(
                "region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ(),
                center.clone().add(0, config.getDouble("blocks." + block.getType() + ".holograms.height"), 0),
                true
        );
        hologram.setDisplayRange(config.getInt("blocks." + block.getType() + ".holograms.visible-range"));

        for (String line : lines) {
            String formattedLine = line.replace("%player%", playerName)
                    .replace("%durability%", String.valueOf(durability));

            Component component = miniMessage.deserialize(formattedLine);
            String legacyText = LegacyComponentSerializer.legacySection().serialize(component);
            DHAPI.addHologramLine(hologram, legacyText);
        }
    }

    public static void updateHologram(Location location, Block block, FileConfiguration config, String playerName) {

        Hologram hologram = DHAPI.getHologram("region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ());

        List<String> lines = config.getStringList("blocks." + block.getType() + ".holograms.lines");
        int durability = DataFolder.getRegionDurability(location);

        int lineCount = 0;

        for (String line : lines) {
            String formattedLine = line.replace("%player%", playerName)
                    .replace("%durability%", String.valueOf(durability));

            Component component = miniMessage.deserialize(formattedLine);
            String legacyText = LegacyComponentSerializer.legacySection().serialize(component);
            DHAPI.setHologramLine(hologram, lineCount, legacyText);

            lineCount++;
        }

        DHAPI.updateHologram("region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ());


    }

    public static void removeHologram(Location location) {
        DHAPI.removeHologram("region_holograms_x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ());
    }
}
