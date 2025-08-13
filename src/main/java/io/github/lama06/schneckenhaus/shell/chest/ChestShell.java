package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.sized.SizedShell;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ChestShell extends SizedShell implements ChestShellData {
    public ChestShell(int id) {
        super(id);
    }

    @Override
    protected boolean load() {
        return super.load();
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();
        for (BlockFace side : BlockFace.values()) {
            if (!side.isCartesian()) {
                continue;
            }
            addSideBlocks(side, blocks);
        }
        addDoorBlocks(blocks);
        return blocks;
    }

    @Override
    public Map<Block, BlockData> getInitialBlocks() {
        Block corner = position.getCornerBlock();
        int size = getSize();
        int y = corner.getY() + 1;
        List<Block> blocks = List.of(
                world.getBlockAt(corner.getX() + 1, y, corner.getZ() + 1),
                world.getBlockAt(corner.getX() + size, y, corner.getZ() + 1),
                world.getBlockAt(corner.getX() + 1, y, corner.getZ() + size),
                world.getBlockAt(corner.getX() + size, y, corner.getZ() + size)
        );
        return blocks.stream().collect(Collectors.toMap(Function.identity(), position -> Material.TORCH.createBlockData()));
    }

    private void addSideBlocks(BlockFace side, Map<Block, BlockData> blocks) {
        Block corner = position.getCornerBlock();
        int size = getSize();
        int staticCoordinate = switch (side) {
            case SOUTH -> corner.getZ() + size + 1;
            case NORTH -> corner.getZ();
            case EAST -> corner.getX() + size + 1;
            case WEST -> corner.getX();
            case UP -> corner.getY() + size + 1;
            case DOWN -> corner.getY();
            default -> throw new IllegalArgumentException();
        };
        int firstCoordinateStart = switch (side) {
            case SOUTH, NORTH, UP, DOWN -> corner.getX() + 1;
            case EAST, WEST -> corner.getZ() + 1;
            default -> throw new IllegalArgumentException();
        };
        int firstCoordinateEnd = firstCoordinateStart + size - 1;
        int secondCoordinateStart = switch (side) {
            case SOUTH, NORTH, EAST, WEST -> corner.getY() + 1;
            case UP, DOWN -> corner.getZ() + 1;
            default -> throw new IllegalArgumentException();
        };
        int secondCoordinateEnd = secondCoordinateStart + size - 1;
        for (
                int firstCoordinate = firstCoordinateStart;
                firstCoordinate <= firstCoordinateEnd;
                firstCoordinate++
        ) {
            for (
                    int secondCoordinate = secondCoordinateStart;
                    secondCoordinate <= secondCoordinateEnd;
                    secondCoordinate++
            ) {
                int x = switch (side) {
                    case SOUTH, NORTH, UP, DOWN -> firstCoordinate;
                    case EAST, WEST -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                int y = switch (side) {
                    case SOUTH, NORTH, EAST, WEST -> secondCoordinate;
                    case UP, DOWN -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                int z = switch (side) {
                    case WEST, EAST -> firstCoordinate;
                    case UP, DOWN -> secondCoordinate;
                    case SOUTH, NORTH -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                Block block = getWorld().getBlockAt(x, y, z);
                BlockData data;
                if (block.equals(getLowerDoorBlock()) || block.equals(getUpperDoorBlock())) {
                    continue;
                } else if (firstCoordinate == firstCoordinateStart || firstCoordinate == firstCoordinateEnd) {
                    data = Material.OAK_LOG.createBlockData();
                    Orientable orientable = (Orientable) data;
                    orientable.setAxis(switch (side) {
                        case SOUTH, NORTH, EAST, WEST -> Axis.Y;
                        case UP, DOWN -> Axis.Z;
                        default -> throw new IllegalArgumentException();
                    });
                } else if (secondCoordinate == secondCoordinateStart || secondCoordinate == secondCoordinateEnd) {
                    data = Material.OAK_LOG.createBlockData();
                    Orientable orientable = (Orientable) data;
                    orientable.setAxis(switch (side) {
                        case SOUTH, NORTH, UP, DOWN -> Axis.X;
                        case EAST, WEST -> Axis.Z;
                        default -> throw new IllegalArgumentException();
                    });
                } else {
                    data = Material.OAK_PLANKS.createBlockData();
                }
                blocks.put(block, data);
            }
        }
    }

    @Override
    public ChestShellFactory getFactory() {
        return ChestShellFactory.INSTANCE;
    }
}
