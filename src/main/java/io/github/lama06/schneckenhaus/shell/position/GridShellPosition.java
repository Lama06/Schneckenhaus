package io.github.lama06.schneckenhaus.shell.position;

import org.bukkit.World;

/**
 * Stores a grid position as its coordinates.
 */
final class GridShellPosition extends ShellPosition {
    private final int x;
    private final int z;

    /**
     * @throws IllegalStateException if x or z is negative.
     */
    GridShellPosition(World world, int x, int z) {
        super(world);
        if (x < 0 || z < 0) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.z = z;
    }

    @Override
    public int getId() {
        int quadratSideLength = Math.max(x, z) + 1;
        if (z >= x) {
            int quadratCells = quadratSideLength * quadratSideLength;
            return quadratCells - x;
        } else {
            int smallerQuadratCells = (quadratSideLength - 1) * (quadratSideLength - 1);
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
