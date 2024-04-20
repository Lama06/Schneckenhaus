package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.util.EnumPersistentDataType;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import io.github.lama06.schneckenhaus.util.UuidPersistentDataType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a snail shell in the world of snail shells.
 * Before accessing its attributes like its size, one must verify that this snail shell exists.
 */
public final class SnailShell {
    public static final int MIN_SIZE = 2;
    public static final int MAX_SIZE = 32 - 2; // Minus two because of the walls on each side.

    private final World world;
    private final GridPosition position;

    public SnailShell(final GridPosition position) {
        world = SchneckenPlugin.INSTANCE.getWorld();
        this.position = Objects.requireNonNull(position);
    }

    public boolean exists() {
        return position.getId() < SchneckenPlugin.INSTANCE.getNextSnailShellId();
    }

    /**
     * Places the blocks of the floor, walls, roof and the door of this snail shell.
     * If these blocks are already present, this method does nothing.
     */
    public void placeBlocks() {
        placeFloor();
        placeWall();
        placeRoof();
        placeDoor();
    }

    /**
     * Sets up the snail shell initially.
     */
    public void create(final int size, final DyeColor color, final Player creator) {
        final PersistentDataContainer data = getData();
        data.set(Data.SNAIL_SHELL_SIZE, PersistentDataType.INTEGER, size);
        data.set(Data.SNAIL_SHELL_CREATOR, UuidPersistentDataType.INSTANCE, creator.getUniqueId());
        data.set(Data.SNAIL_SHELL_COLOR, EnumPersistentDataType.DYE_COLOR, color);
        placeBlocks();
    }

    /**
     * Returns a list of all blocks this snail shell consists of.
     * This list includes the floor, walls, roof and the door.
     * This list doesn't contain blocks added by the player.
     */
    public Set<Block> getBlocks() {
        final Set<Block> blocks = new HashSet<>();
        blocks.addAll(getFloorBlocks());
        blocks.addAll(getWallBlocks());
        blocks.addAll(getRoofBlocks());
        blocks.add(position.getLowerDoorBlock());
        blocks.add(position.getUpperDoorBlock());
        return blocks;
    }

    public int getSize() {
        return getData().get(Data.SNAIL_SHELL_SIZE, PersistentDataType.INTEGER);
    }

    public DyeColor getColor() {
        return getData().get(Data.SNAIL_SHELL_COLOR, EnumPersistentDataType.DYE_COLOR);
    }

    public OfflinePlayer getCreator() {
        return Bukkit.getOfflinePlayer(getData().get(Data.SNAIL_SHELL_CREATOR, UuidPersistentDataType.INSTANCE));
    }

    public GridPosition getPosition() {
        return position;
    }

    private PersistentDataContainer getData() {
        return position.getCornerBlock().getChunk().getPersistentDataContainer();
    }

    private Set<Block> getFloorBlocks() {
        final Set<Block> blocks = new HashSet<>();
        final int size = getSize();
        final Block cornerBlock = position.getCornerBlock();
        for (int x = cornerBlock.getX(); x <= cornerBlock.getX() + size + 1; x++) {
            for (int z = cornerBlock.getZ(); z <= cornerBlock.getZ() + size + 1; z++) {
                blocks.add(world.getBlockAt(x, cornerBlock.getY(), z));
            }
        }
        return blocks;
    }

    private void placeFloor() {
        final Material terracotta = MaterialUtil.getColoredTerracotta(getColor());
        for (final Block floorBlock : getFloorBlocks()) {
            if (floorBlock.getType() == terracotta) {
                continue;
            }
            floorBlock.setType(terracotta);
        }
    }

    private Set<Block> getWallBlocks(final BlockFace side) {
        final Set<Block> blocks = new HashSet<>();
        final int size = getSize();
        final Block cornerBlock = position.getCornerBlock();
        for (int y = cornerBlock.getY() + 1; y <= cornerBlock.getY() + size; y++) {
            final int horizontalStart;
            if (side == BlockFace.EAST || side == BlockFace.WEST) {
                horizontalStart = cornerBlock.getZ();
            } else {
                horizontalStart = cornerBlock.getX();
            }
            final int horizontalEnd = horizontalStart + size + 1;
            for (int horizontalCoordinate = horizontalStart; horizontalCoordinate <= horizontalEnd; horizontalCoordinate++) {
                final Block block;
                if (side == BlockFace.EAST || side == BlockFace.WEST) {
                    block = world.getBlockAt(
                            side == BlockFace.EAST ? cornerBlock.getX() : cornerBlock.getX() + size + 1,
                            y,
                            horizontalCoordinate
                    );
                } else {
                    block = world.getBlockAt(
                            horizontalCoordinate,
                            y,
                            side == BlockFace.SOUTH ? cornerBlock.getZ() : cornerBlock.getZ() + size + 1
                    );
                }
                if (block.equals(position.getLowerDoorBlock()) || block.equals(position.getUpperDoorBlock())) {
                    continue;
                }
                blocks.add(block);
            }
        }
        return blocks;
    }

    private Set<Block> getWallBlocks() {
        Set<Block> blocks = new HashSet<>();
        for (final BlockFace side : List.of(BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST)) {
            blocks.addAll(getWallBlocks(side));
        }
        return blocks;
    }

    private void placeWall() {
        final Block cornerBlock = position.getCornerBlock();
        final DyeColor color = getColor();
        final Material terracotta = MaterialUtil.getColoredTerracotta(color);
        final Material concrete = MaterialUtil.getColoredConcrete(color);
        for (final Block wallBlock : getWallBlocks()) {
            final Material material = wallBlock.getY() <= cornerBlock.getY() + 1 ? terracotta : concrete;
            if (wallBlock.getType() == material) {
                continue;
            }
            wallBlock.setType(material);
        }
    }

    private Set<Block> getRoofBlocks() {
        Set<Block> blocks = new HashSet<>();
        final int size = getSize();
        final Block cornerBlock = position.getCornerBlock();
        final int y = cornerBlock.getY() + size + 1;
        for (int x = cornerBlock.getX(); x <= cornerBlock.getX() + size + 1; x++) {
            for (int z = cornerBlock.getZ(); z <= cornerBlock.getZ() + size + 1; z++) {
                blocks.add(world.getBlockAt(x, y, z));
            }
        }
        return blocks;
    }

    private void placeRoof() {
        final Material glass = MaterialUtil.getColoredGlass(getColor());
        for (final Block roofBlock : getRoofBlocks()) {
            if (roofBlock.getType() == glass) {
                continue;
            }
            roofBlock.setType(glass);
        }
    }

    private void placeDoor() {
        final Door lowerDoorBlockData = (Door) Material.SPRUCE_DOOR.createBlockData();
        lowerDoorBlockData.setOpen(false);
        lowerDoorBlockData.setHalf(Bisected.Half.BOTTOM);
        lowerDoorBlockData.setFacing(BlockFace.NORTH);

        final Door upperDoorBlockData = (Door) Material.SPRUCE_DOOR.createBlockData();
        upperDoorBlockData.setOpen(false);
        upperDoorBlockData.setHalf(Bisected.Half.TOP);
        upperDoorBlockData.setFacing(BlockFace.NORTH);

        final Block lowerDoorBlock = position.getLowerDoorBlock();
        final Block upperDoorBlock = position.getUpperDoorBlock();
        if (!lowerDoorBlock.getBlockData().equals(lowerDoorBlockData)) {
            lowerDoorBlock.setBlockData(lowerDoorBlockData, false);
        }
        if (!upperDoorBlock.getBlockData().equals(upperDoorBlockData)) {
            upperDoorBlock.setBlockData(upperDoorBlockData, false);
        }
    }
}
