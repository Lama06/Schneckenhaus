package io.github.lama06.schneckenhaus.position;

import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the position of a cell in the grid of snail shells.
 */
public sealed abstract class Position permits GridPosition, IdPosition {
    public static final int CELL_SIZE_CHUNKS = 4;

    /**
     * The width and height of a grid cell.
     */
    public static final int CELL_SIZE = 16 * CELL_SIZE_CHUNKS;

    public static Position id(World world, int id) {
        return new IdPosition(world, id);
    }

    public static Position grid(World world, int x, int y) {
        return new GridPosition(world, x, y);
    }

    public static Position location(World world, double x, double z) {
        int cellX = (int) x / CELL_SIZE;
        int cellZ = (int) z / CELL_SIZE;
        if (cellX < 0 || cellZ < 0) {
            return null;
        }
        return new GridPosition(world, cellX, cellZ);
    }

    public static Position location(Location location) {
        return location(location.getWorld(), location.getX(), location.getZ());
    }

    public static Position block(Block block) {
        if (block == null) {
            return null;
        }
        int cellX = block.getX() / CELL_SIZE;
        int cellZ = block.getZ() / CELL_SIZE;
        if (cellX < 0 || cellZ < 0) {
            return null;
        }
        return new GridPosition(block.getWorld(), cellX, cellZ);
    }

    protected final World world;

    protected Position(World world) {
        this.world = world;
    }

    /**
     * Returns the one-indexed id of this position. Ids are laid out like this:
     * <pre>
     * 25 24 23 22 21
     * 16 15 14 13 20
     * 9  8  7  12 19
     * 4  3  6  11 18
     * 1  2  5  10 17
     * </pre>
     */
    public abstract int getId();

    /**
     * Returns the zero-indexed x-coordinate of this position.
     * <pre>
     * 0 1 2 3 4
     * 0 1 2 3 4
     * 0 1 2 3 4
     * 0 1 2 3 4
     * 0 1 2 3 4
     * </pre>
     */
    public abstract int getX();

    /**
     * Returns the zero-indexed z-coordinate of this position.
     * <pre>
     * 4 4 4 4 4
     * 3 3 3 3 3
     * 2 2 2 2 2
     * 1 1 1 1 1
     * 0 0 0 0 0
     * </pre>
     */
    public abstract int getZ();

    /**
     * Returns the block of this grid cell with the smallest sum of its coordinates and a y-coordinate of zero.
     */
    public final Block getCornerBlock() {
        return world.getBlockAt(getX() * CELL_SIZE, 0, getZ() * CELL_SIZE);
    }

    public final BlockArea getArea() {
        return new BlockArea(
            new BlockPosition(getX() * CELL_SIZE, world.getMinHeight(), getZ() * CELL_SIZE),
            new BlockPosition((getX() + 1) * CELL_SIZE - 1, world.getMaxHeight(), (getZ() + 1) * CELL_SIZE - 1)
        );
    }

    public List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        for (int x = 0; x < CELL_SIZE_CHUNKS; x++) {
            for (int z = 0; z < CELL_SIZE_CHUNKS; z++) {
                chunks.add(world.getChunkAt(
                    getX() * CELL_SIZE_CHUNKS + x,
                    getZ() * CELL_SIZE_CHUNKS + z
                ));
            }
        }
        return chunks;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public final boolean equals(final Object other) {
        if (!(other instanceof final Position otherPosition)) {
            return false;
        }
        return getId() == otherPosition.getId();
    }

    @Override
    public final int hashCode() {
        return getId();
    }
}
