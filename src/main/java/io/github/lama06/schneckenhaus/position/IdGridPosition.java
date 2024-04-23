package io.github.lama06.schneckenhaus.position;

/**
 * Stores a grid position as its id.
 */
public final class IdGridPosition extends GridPosition {
    private final int id;

    /**
     * @throws IllegalArgumentException if the id is not positive.
     */
    public IdGridPosition(final int id) {
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    @Override
    protected int getQuadratSideLength() {
        final int sqrt = (int) Math.sqrt(id);
        if (sqrt * sqrt == id) {
            return sqrt;
        }
        return sqrt + 1;
    }

    @Override
    protected boolean isAboveOrOnDiagonal() {
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
