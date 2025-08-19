package io.github.lama06.schneckenhaus.shell.position;

import org.bukkit.World;

/**
 * Stores a grid position as its id.
 */
final class IdShellPosition extends ShellPosition {
    private final int id;

    /**
     * @throws IllegalArgumentException if the id is not positive.
     */
    IdShellPosition(World world, int id) {
        super(world);
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    private int getQuadratSideLength() {
        final int sqrt = (int) Math.sqrt(id);
        if (sqrt * sqrt == id) {
            return sqrt;
        }
        return sqrt + 1;
    }

    private boolean isAboveOrOnDiagonal() {
        final int quadratSideLength = getQuadratSideLength();
        final int quadratArea = quadratSideLength * quadratSideLength;
        final int firstId = quadratArea - (quadratSideLength - 1);
        return firstId <= id;
    }

    @Override
    public int getX() {
        final int quadratSideLength = getQuadratSideLength();
        if (isAboveOrOnDiagonal()) {
            return quadratSideLength * quadratSideLength - id;
        } else {
            return quadratSideLength - 1;
        }
    }

    @Override
    public int getZ() {
        final int quadratSideLength = getQuadratSideLength();
        if (isAboveOrOnDiagonal()) {
            return quadratSideLength - 1;
        } else {
            return id - (quadratSideLength - 1) * (quadratSideLength - 1) - 1;
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
