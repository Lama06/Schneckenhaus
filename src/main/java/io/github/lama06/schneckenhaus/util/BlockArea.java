package io.github.lama06.schneckenhaus.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public record BlockArea(BlockPosition position1, BlockPosition position2) implements Iterable<BlockPosition> {
    public static BlockArea fromString(final String string) {
        final String[] parts = string.split(" - ");
        if (parts.length != 2) {
            return null;
        }
        final BlockPosition position1 = BlockPosition.fromString(parts[0]);
        final BlockPosition position2 = BlockPosition.fromString(parts[1]);
        if (position1 == null || position2 == null) {
            return null;
        }
        return new BlockArea(position1, position2);
    }

    public BlockArea {
        Objects.requireNonNull(position1);
        Objects.requireNonNull(position2);
    }

    @Override
    public Iterator<BlockPosition> iterator() {
        final BlockPosition lowerCorner = getLowerCorner();
        final BlockPosition upperCorner = getUpperCorner();
        return new Iterator<>() {
            private int x = lowerCorner.x();
            private int y = lowerCorner.y();
            private int z = lowerCorner.z();

            @Override
            public boolean hasNext() {
                return x <= upperCorner.x() && y <= upperCorner.y() && z <= upperCorner.z();
            }

            @Override
            public BlockPosition next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final BlockPosition position = new BlockPosition(x, y, z);
                if (x != upperCorner.x()) {
                    x++;
                } else if (z != upperCorner.z()) {
                    x = lowerCorner.x();
                    z++;
                } else {
                    y++;
                    x = lowerCorner.x();
                    z = lowerCorner.z();
                }
                return position;
            }
        };
    }

    public boolean containsBlock(final BlockPosition position) {
        final BlockPosition lowerCorner = getLowerCorner();
        final BlockPosition upperCorner = getUpperCorner();

        final boolean x = position.x() >= lowerCorner.x() && position.x() <= upperCorner.x();
        final boolean y = position.y() >= lowerCorner.y() && position.y() <= upperCorner.y();
        final boolean z = position.z() >= lowerCorner.z() && position.z() <= upperCorner.z();

        return x && y && z;
    }

    public int getLowerX() {
        return Math.min(position1.x(), position2.x());
    }

    public int getLowerY() {
        return Math.min(position1.y(), position2.y());
    }

    public int getLowerZ() {
        return Math.min(position1.z(), position2.z());
    }

    public int getUpperX() {
        return Math.max(position1.x(), position2.x());
    }

    public int getUpperY() {
        return Math.max(position1.y(), position2.y());
    }

    public int getUpperZ() {
        return Math.max(position1.z(), position2.z());
    }

    public BlockPosition getLowerCorner() {
        return new BlockPosition(getLowerX(), getLowerY(), getLowerZ());
    }

    public BlockPosition getUpperCorner() {
        return new BlockPosition(getUpperX(), getUpperY(), getUpperZ());
    }

    public int getHeight() {
        return getUpperX() - getLowerX() + 1;
    }

    public int getWidthX() {
        return getUpperY() - getLowerY() + 1;
    }

    public int getWidthZ() {
        return getUpperZ() - getLowerZ() + 1;
    }

    @Override
    public String toString() {
        return position1 + " - " + position2;
    }
}