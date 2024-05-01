package io.github.lama06.schneckenhaus.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public record BlockPosition(int x, int y, int z) {
    public static BlockPosition fromString(final String string) {
        final String[] parts = string.strip().split(" ");
        if (parts.length != 3) {
            return null;
        }
        final int x, y, z;
        try {
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
            z = Integer.parseInt(parts[2]);
        } catch (final NumberFormatException e) {
            return null;
        }
        return new BlockPosition(x, y, z);
    }

    public BlockPosition(final Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public BlockPosition(final Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockPosition subtract(final BlockPosition other) {
        return new BlockPosition(x - other.x(), y - other.y(), z - other.z());
    }

    public Block getBlock(final World world) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public String toString() {
        return "%d %d %d".formatted(x, y, z);
    }
}
