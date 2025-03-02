package me.KyotoDev.liteRegion.Events;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import me.KyotoDev.liteRegion.Config.DataFolder;
import me.KyotoDev.liteRegion.Inventores.MainInventory;
import me.KyotoDev.liteRegion.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClickMenu implements Listener {


    private final Main plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();


    public ClickMenu(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        Location blockLocation = block.getLocation();
        PSRegion regionInput = PSRegion.fromLocation(blockLocation);


        if (ProtectionStones.isProtectBlock(block)) {
            List<PSRegion> regions = ProtectionStones.getPSRegions(blockLocation.getWorld(), DataFolder.getRegionId(blockLocation));

            if (!regions.isEmpty()) {
                for (PSRegion region : regions) {
                    ArrayList<UUID> owner = region.getOwners();

                    for (UUID uuid : owner) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null) {
                            if (player == p) {

                                MainInventory.openMainRGInventory(p, regionInput);

                                break;
                            }
                        }

                    }
                }
            }
        }


    }
}
