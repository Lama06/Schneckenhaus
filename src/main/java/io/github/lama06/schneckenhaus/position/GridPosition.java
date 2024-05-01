package io.github.lama06.schneckenhaus.position;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Represents the position of a cell in the grid of snail shells.
 */
public sealed abstract class GridPosition permits CoordinatesGridPosition, IdGridPosition {
    /**
     * The width and height of a grid cell.
     */
    public static final int CELL_SIZE = 64;

    protected GridPosition() { }

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
     * Returns the side length of the smallest quadrat that contains this id.
     * Examples:
     * <pre>
     * id -> side length
     * 1  -> 1
     * 2  -> 2
     * 3  -> 2
     * 4  -> 2
     * 5  -> 3
     * 9  -> 3
     * 10 -> 4
     * </pre>
     */
    protected abstract int getQuadratSideLength();

    /**
     * Returns if this grid position is above or on the diagonal.
     * Examples:
     * <pre>
     * 1 1 1 1 1
     * 1 1 1 1 0
     * 1 1 1 0 0
     * 1 1 0 0 0
     * 1 0 0 0 0
     * </pre>
     */
    protected abstract boolean isAboveOrOnDiagonal();

    @Override
    public final boolean equals(final Object other) {
        if (!(other instanceof final GridPosition otherPosition)) {
            return false;
        }
        return getId() == otherPosition.getId();
    }

    @Override
    public final int hashCode() {
        return getId();
    }

    /**
     * Returns the block of this grid cell with the smallest sum of its coordinates and a y-coordinate of zero.
     */
    public final Block getCornerBlock() {
        return SchneckenPlugin.INSTANCE.getWorld().getBukkit().getBlockAt(getX() * CELL_SIZE, 0, getZ() * CELL_SIZE);
    }

    public final Location getSpawnLocation() {
        return getCornerBlock().getLocation().add(2, 1, 2).setDirection(BlockFace.SOUTH.getDirection());
    }
}
