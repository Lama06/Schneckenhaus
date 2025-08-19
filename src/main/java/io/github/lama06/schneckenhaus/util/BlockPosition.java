package io.github.lama06.schneckenhaus.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public record BlockPosition(int x, int y, int z) {
    public static BlockPosition fromString(String string) {
        String[] parts = string.strip().split(" ");
        if (parts.length != 3) {
            return null;
        }
        int x, y, z;
        try {
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
            z = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
        return new BlockPosition(x, y, z);
    }

    public BlockPosition(Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public BlockPosition(io.papermc.paper.math.BlockPosition paper) {
        this(paper.blockX(), paper.blockY(), paper.blockZ());
    }

    public BlockPosition(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockPosition subtract(BlockPosition other) {
        return new BlockPosition(x - other.x(), y - other.y(), z - other.z());
    }

    public BlockPosition subtract(int x, int y, int z) {
        return new BlockPosition(this.x - x, this.y - y, this.z - z);
    }

    public BlockPosition add(BlockPosition other) {
        return new BlockPosition(x + other.x(), y + other.y(), z + other.z());
    }

    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(this.x + x, this.y + y, this.z + z);
    }

    public Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    @Override
    public String toString() {
        return "%d %d %d".formatted(x, y, z);
    }
}
