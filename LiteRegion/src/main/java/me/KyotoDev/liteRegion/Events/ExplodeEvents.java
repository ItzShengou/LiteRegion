package me.KyotoDev.liteRegion.Events;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import me.KyotoDev.liteRegion.Config.DataFolder;
import me.KyotoDev.liteRegion.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.UUID;

public class ExplodeEvents implements Listener {

    private final Main plugin;

    public ExplodeEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntityType() != EntityType.PRIMED_TNT) return;

        Location explosionLocation = e.getLocation();
        double explosionRadius = 5.0;

        for (Block block : e.blockList()) {
            Location blockLocation = block.getLocation();

            if (explosionLocation.distance(blockLocation) <= explosionRadius && ProtectionStones.isProtectBlock(block)) {
                handleRegionExplosion(blockLocation);
            }
        }
    }

    private void handleRegionExplosion(Location blockLocation) {
        List<PSRegion> regions = ProtectionStones.getPSRegions(blockLocation.getWorld(), DataFolder.getRegionId(blockLocation));
        int durability = DataFolder.getRegionDurability(blockLocation);

        if (durability <= 1) {
            ProtectionStones.removePSRegion(blockLocation.getWorld(), DataFolder.getRegionId(blockLocation));
            DataFolder.deleteRegionFile(blockLocation);
        } else {
            DataFolder.updateRegionDurability(blockLocation, durability - 1);
            updateHologram(regions, blockLocation);
        }
    }

    private void updateHologram(List<PSRegion> regions, Location blockLocation) {
        for (PSRegion region : regions) {
            UUID owner = region.getOwners().get(0);
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);

            CreateRegion.updateHologram(blockLocation, blockLocation.getBlock(), plugin.getConfig(), player.getName());
        }
    }
}