package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.screen.ChestShellWoodScreen;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.shell.ShellMenuAction;
import io.github.lama06.schneckenhaus.shell.sized.SizedShell;
import io.github.lama06.schneckenhaus.util.WoodType;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ChestShell extends SizedShell implements ChestShellData {
    private WoodType wood;

    public ChestShell(int id) {
        super(id);
    }

    @Override
    public ChestShellFactory getFactory() {
        return ChestShellFactory.INSTANCE;
    }

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT wood
            FROM chest_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            wood = WoodType.valueOf(result.getString(1).toUpperCase(Locale.ROOT));
            return true;
        } catch (SQLException e) {
            logger.error("failed to insert chest shell data", e);
            return false;
        }
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
        Map<Block, BlockData> blocks = new HashMap<>();
        addCornerTorches(blocks, getSize());
        return blocks;
    }

    private void addSideBlocks(BlockFace side, Map<Block, BlockData> blocks) {
        Material log = wood.getLog();
        Material planks = wood.getPlanks();

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
                    data = log.createBlockData();
                    Orientable orientable = (Orientable) data;
                    orientable.setAxis(switch (side) {
                        case SOUTH, NORTH, EAST, WEST -> Axis.Y;
                        case UP, DOWN -> Axis.Z;
                        default -> throw new IllegalArgumentException();
                    });
                } else if (secondCoordinate == secondCoordinateStart || secondCoordinate == secondCoordinateEnd) {
                    data = log.createBlockData();
                    Orientable orientable = (Orientable) data;
                    orientable.setAxis(switch (side) {
                        case SOUTH, NORTH, UP, DOWN -> Axis.X;
                        case EAST, WEST -> Axis.Z;
                        default -> throw new IllegalArgumentException();
                    });
                } else {
                    data = planks.createBlockData();
                }
                blocks.put(block, data);
            }
        }
    }

    @Override
    protected void addMenuActions(Player player, List<ShellMenuAction> actions) {
        super.addMenuActions(player, actions);
        actions.add(new ShellMenuAction() {
            @Override
            public ItemStack getItem() {
                if (!Permission.CHANGE_SHELL_WOOD.check(player)) {
                    return null;
                }

                ItemStack item = new ItemStack(wood.getSapling());
                item.editMeta(meta -> {
                    meta.customName(Message.WOOD.asComponent());
                    meta.lore(List.of(Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)));
                });
                return item;
            }

            @Override
            public void onClick() {
                new ChestShellWoodScreen(ChestShell.this, player).open();
            }
        });
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(Message.WOOD.asComponent(), wood.getMessage().asComponent()));
    }

    public WoodType getWood() {
        return wood;
    }

    public void setWood(WoodType wood) {
        this.wood = wood;
        String sql = """
            UPDATE chest_shells
            SET wood = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, wood.name().toLowerCase(Locale.ROOT));
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update chest shell wood type: {}", id, e);
        }
        place();
    }
}
