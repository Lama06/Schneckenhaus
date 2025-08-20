package io.github.lama06.schneckenhaus.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public record BlockArea(BlockPosition position1, BlockPosition position2) implements Iterable<BlockPosition> {
    public static BlockArea fromString(String string) {
        String[] parts = string.split(" ");
        if (parts.length != 6) {
            return null;
        }
        BlockPosition position1 = BlockPosition.fromString(String.join(" ", Arrays.copyOfRange(parts, 0, 3)));
        BlockPosition position2 = BlockPosition.fromString(String.join(" ", Arrays.copyOfRange(parts, 3, 6)));
        if (position1 == null || position2 == null) {
            return null;
        }
        return new BlockArea(position1, position2);
    }

    public BlockArea {
        Objects.requireNonNull(position1);
        Objects.requireNonNull(position2);
    }

    public BlockArea(Block block1, Block block2) {
        this(new BlockPosition(block1), new BlockPosition(block2));
    }

    @Override
    public Iterator<BlockPosition> iterator() {
        BlockPosition lowerCorner = getLowerCorner();
        BlockPosition upperCorner = getUpperCorner();
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
                BlockPosition position = new BlockPosition(x, y, z);
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

    public boolean containsBlock(BlockPosition position) {
        BlockPosition lowerCorner = getLowerCorner();
        BlockPosition upperCorner = getUpperCorner();

        boolean x = position.x() >= lowerCorner.x() && position.x() <= upperCorner.x();
        boolean y = position.y() >= lowerCorner.y() && position.y() <= upperCorner.y();
        boolean z = position.z() >= lowerCorner.z() && position.z() <= upperCorner.z();

        return x && y && z;
    }

    public boolean contains(Location location) {
        return getLowerX() <= location.getX() && location.getX() <= getUpperX() &&
            getLowerY() <= location.getY() && location.getY() <= getUpperY() &&
            getLowerZ() <= location.getZ() && location.getZ() <= getUpperZ();
    }

    public BlockArea getLayer(int index) {
        if (index < 0 || index >= getHeight()) {
            throw new IndexOutOfBoundsException(index);
        }
        int lowerY = getLowerY();
        return new BlockArea(
            new BlockPosition(position1.x(), lowerY + index, position1.z()),
            new BlockPosition(position2.x(), lowerY + index, position2.z())
        );
    }

    public BlockArea shrink(int shrinkX, int shrinkY, int shrinkZ) {
        BlockPosition lowerCorner = getLowerCorner();
        BlockPosition upperCorner = getUpperCorner();
        return new BlockArea(
            lowerCorner.add(shrinkX, shrinkY, shrinkZ),
            upperCorner.subtract(shrinkX, shrinkY, shrinkZ)
        );
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
        return getUpperY() - getLowerY() + 1;
    }

    public int getWidthX() {
        return getUpperX() - getLowerX() + 1;
    }

    public int getWidthZ() {
        return getUpperZ() - getLowerZ() + 1;
    }

    @Override
    public String toString() {
        return position1 + " " + position2;
    }
}