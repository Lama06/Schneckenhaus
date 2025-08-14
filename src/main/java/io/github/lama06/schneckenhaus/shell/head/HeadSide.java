package io.github.lama06.schneckenhaus.shell.head;

import org.bukkit.block.Block;

public enum HeadSide {
    TOP(1, 0) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(skinX + 1, HeadShell.SIZE + 1, skinY + 1);
        }
    },
    BOTTOM(2, 0) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(skinX + 1, 0, skinY + 1);
        }
    },
    LEFT(0, 1) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(0, HeadShell.SIZE - skinY, skinX + 1);
        }
    },
    FRONT(1, 1) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(skinX + 1, HeadShell.SIZE - skinY, HeadShell.SIZE + 1);
        }
    },
    RIGHT(2, 1) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(HeadShell.SIZE + 1, HeadShell.SIZE - skinY, HeadShell.SIZE - skinX);
        }
    },
    BACK(3, 1) {
        @Override
        public Block getBlock(Block corner, int skinX, int skinY) {
            return corner.getRelative(HeadShell.SIZE - skinX, HeadShell.SIZE - skinY, 0);
        }
    };

    private final int x;
    private final int y;

    HeadSide(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract Block getBlock(Block corner, int skinX, int skinY);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
