package me.KyotoDev.liteRegion.Config;

import me.KyotoDev.liteRegion.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GlowEffect {
    public static void visualizeRegion(Player player, Location corner1, Location corner2) {
        World world = corner1.getWorld();
        if (world == null) return;

        Vector min = Vector.getMinimum(corner1.toVector(), corner2.toVector());
        Vector max = Vector.getMaximum(corner1.toVector(), corner2.toVector());

        Vector ymax = Vector.getMaximum(corner1.toVector(), corner2.toVector());

        // Отрисовка куба из партиклов
        drawCube(player, min, max);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (double x = min.getX(); x <= max.getX(); x += 0.5) {
                    for (double z = min.getZ(); z <= max.getZ(); z += 0.5) {
                        if (x == min.getX() || x == max.getX() || z == min.getZ() || z == max.getZ()) {
                            Location particleLocation = new Location(world, x, ymax.getY(), z);
                            player.spawnParticle(Particle.CLOUD, particleLocation, 1);
                        }
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
    }

    private static void drawCube(Player player, Vector min, Vector max) {
        World world = player.getWorld();

        // Отрисовка нижней грани
        drawLine(player, new Vector(min.getX(), min.getY(), min.getZ()), new Vector(max.getX(), min.getY(), min.getZ())); // Нижняя линия X
        drawLine(player, new Vector(min.getX(), min.getY(), max.getZ()), new Vector(max.getX(), max.getY(), max.getZ())); // Верхняя линия X
        drawLine(player, new Vector(min.getX(), min.getY(), min.getZ()), new Vector(min.getX(), min.getY(), max.getZ())); // Левая линия Z
        drawLine(player, new Vector(max.getX(), max.getY(), min.getZ()), new Vector(max.getX(), min.getY(), max.getZ())); // Правая линия Z

        // Отрисовка верхней грани
        drawLine(player, new Vector(min.getX(), min.getY(), min.getZ()), new Vector(max.getX(), max.getY(), min.getZ())); // Нижняя линия X
        drawLine(player, new Vector(min.getX(), min.getY(), max.getZ()), new Vector(max.getX(), max.getY(), max.getZ())); // Верхняя линия X
        drawLine(player, new Vector(min.getX(), min.getY(), min.getZ()), new Vector(min.getX(), max.getY(), max.getZ())); // Левая линия Z
        drawLine(player, new Vector(max.getX(), max.getY(), min.getZ()), new Vector(max.getX(), max.getY(), max.getZ())); // Правая линия Z

        // Отрисовка вертикальных ребер
        drawLine(player, new Vector(min.getX(), min.getY(), min.getZ()), new Vector(min.getX(), max.getY(), min.getZ())); // Левое переднее ребро
        drawLine(player, new Vector(max.getX(), max.getY(), min.getZ()), new Vector(max.getX(), max.getY(), min.getZ())); // Правое переднее ребро
        drawLine(player, new Vector(min.getX(), min.getY(), max.getZ()), new Vector(min.getX(), max.getY(), max.getZ())); // Левое заднее ребро
        drawLine(player, new Vector(max.getX(), max.getY(), max.getZ()), new Vector(max.getX(), max.getY(), max.getZ())); // Правое заднее ребро
    }

    private static void drawLine(Player player, Vector start, Vector end) {
        World world = player.getWorld();
        Vector direction = end.clone().subtract(start).normalize();
        double distance = start.distance(end);

        for (double i = 0; i <= distance; i += 0.5) {
            Vector point = start.clone().add(direction.clone().multiply(i));
            Location particleLocation = new Location(world, point.getX(), point.getY(), point.getZ());
            player.spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.RED, 100));
        }
    }
}

