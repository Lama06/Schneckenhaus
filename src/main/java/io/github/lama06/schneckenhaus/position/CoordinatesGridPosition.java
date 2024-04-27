package io.github.lama06.schneckenhaus.position;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Location;

/**
 * Stores a grid position as its coordinates.
 */
public final class CoordinatesGridPosition extends GridPosition {
    /**
     * Returns the grid position that contains the given coordinates or null if there is none.
     */
    public static CoordinatesGridPosition fromWorldPosition(final double x, final double z) {
        final int cellX = (int) x / CELL_SIZE;
        final int cellZ = (int) z / CELL_SIZE;
        if (cellX < 0 || cellZ < 0) {
            return null;
        }
        return new CoordinatesGridPosition(cellX, cellZ);
    }

    /**
     * Returns null if the given location has a world that isn't the snail shells world.
     * Otherwise, returns the grid position that contains the given location or null if there is none.
     */
    public static CoordinatesGridPosition fromWorldPosition(final Location location) {
        if (location.getWorld() != null && !location.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return null;
        }
        return fromWorldPosition(location.getX(), location.getZ());
    }

    private final int x;
    private final int z;

    /**
     * @throws IllegalStateException if x or z is negative.
     */
    public CoordinatesGridPosition(final int x, final int z) {
        if (x < 0 || z < 0) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.z = z;
    }

    @Override
    protected int getQuadratSideLength() {
        return Math.max(x, z) + 1;
    }

    @Override
    protected boolean isAboveOrOnDiagonal() {
        return z >= x;
    }

    @Override
    public int getId() {
        final int quadratSideLength = getQuadratSideLength();
        if (isAboveOrOnDiagonal()) {
            final int quadratCells = quadratSideLength * quadratSideLength;
            return quadratCells - x;
        } else {
            final int smallerQuadratCells = (quadratSideLength - 1) * (quadratSideLength - 1);
            return smallerQuadratCells + 1 + z;
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }
}
