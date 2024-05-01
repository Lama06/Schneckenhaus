package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ChestShell extends BuiltinShell<ChestShellConfig> {
    public ChestShell(final GridPosition position, final ChestShellConfig config) {
        super(position, config);
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        final Map<Block, BlockData> blocks = new HashMap<>();
        for (final BlockFace side : BlockFace.values()) {
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
        final World world = getWorld().getBukkit();
        final Block corner = getPosition().getCornerBlock();
        final int size = getSize();
        final int y = corner.getY() + 1;
        final List<Block> blocks = List.of(
                world.getBlockAt(corner.getX() + 1, y, corner.getZ() + 1),
                world.getBlockAt(corner.getX() + size, y, corner.getZ() + 1),
                world.getBlockAt(corner.getX() + 1, y, corner.getZ() + size),
                world.getBlockAt(corner.getX() + size, y, corner.getZ() + size)
        );
        return blocks.stream().collect(Collectors.toMap(Function.identity(), position -> Material.TORCH.createBlockData()));
    }

    private void addSideBlocks(final BlockFace side, final Map<Block, BlockData> blocks) {
        final GridPosition position = getPosition();
        final Block corner = position.getCornerBlock();
        final int size = getSize();
        final int staticCoordinate = switch (side) {
            case SOUTH -> corner.getZ() + size + 1;
            case NORTH -> corner.getZ();
            case EAST -> corner.getX() + size + 1;
            case WEST -> corner.getX();
            case UP -> corner.getY() + size + 1;
            case DOWN -> corner.getY();
            default -> throw new IllegalArgumentException();
        };
        final int firstCoordinateStart = switch (side) {
            case SOUTH, NORTH, UP, DOWN -> corner.getX() + 1;
            case EAST, WEST -> corner.getZ() + 1;
            default -> throw new IllegalArgumentException();
        };
        final int firstCoordinateEnd = firstCoordinateStart + size - 1;
        final int secondCoordinateStart = switch (side) {
            case SOUTH, NORTH, EAST, WEST -> corner.getY() + 1;
            case UP, DOWN -> corner.getZ() + 1;
            default -> throw new IllegalArgumentException();
        };
        final int secondCoordinateEnd = secondCoordinateStart + size - 1;
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
                final int x = switch (side) {
                    case SOUTH, NORTH, UP, DOWN -> firstCoordinate;
                    case EAST, WEST -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                final int y = switch (side) {
                    case SOUTH, NORTH, EAST, WEST -> secondCoordinate;
                    case UP, DOWN -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                final int z = switch (side) {
                    case WEST, EAST -> firstCoordinate;
                    case UP, DOWN -> secondCoordinate;
                    case SOUTH, NORTH -> staticCoordinate;
                    default -> throw new IllegalArgumentException();
                };
                final Block block = getWorld().getBukkit().getBlockAt(x, y, z);
                final BlockData data;
                if (block.equals(position.getLowerDoorBlock()) || block.equals(position.getUpperDoorBlock())) {
                    continue;
                } else if (firstCoordinate == firstCoordinateStart || firstCoordinate == firstCoordinateEnd) {
                    data = Material.OAK_LOG.createBlockData();
                    final Orientable orientable = (Orientable) data;
                    orientable.setAxis(switch (side) {
                        case SOUTH, NORTH, EAST, WEST -> Axis.Y;
                        case UP, DOWN -> Axis.Z;
                        default -> throw new IllegalArgumentException();
                    });
                } else if (secondCoordinate == secondCoordinateStart || secondCoordinate == secondCoordinateEnd) {
                    data = Material.OAK_LOG.createBlockData();
                    final Orientable orientable = (Orientable) data;
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
}
