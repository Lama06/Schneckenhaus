package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.shell.action.ShellScreenAction;
import io.github.lama06.schneckenhaus.shell.sized.SizedShell;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public final class ShulkerShell extends SizedShell implements ShulkerShellData {
    private DyeColor color;
    private boolean rainbow;
    private final Set<DyeColor> rainbowColors = new HashSet<>();

    public ShulkerShell(int id) {
        super(id);
    }

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT color, rainbow
            FROM shulker_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }
            color = DyeColor.valueOf(result.getString("color").toUpperCase(Locale.ROOT));
            rainbow = result.getBoolean("rainbow");
        } catch (SQLException e) {
            logger.error("failed to load shulker shell data: {}", id, e);
            return false;
        }

        String colorsSql = """
            SELECT color
            FROM shulker_shell_rainbow_colors
            WHERE id = ? AND enabled
            """;
        try (PreparedStatement statement = connection.prepareStatement(colorsSql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                rainbowColors.add(DyeColor.valueOf(result.getString("color").toUpperCase(Locale.ROOT)));
            }
        } catch (SQLException e) {
            logger.error("failed to load shulker shell rainbow colors data: {}", id, e);
            return false;
        }

        return true;
    }

    @Override
    public ShulkerShellFactory getFactory() {
        return ShulkerShellFactory.INSTANCE;
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();
        addFloorBlocks(blocks);
        addWallBlocks(blocks);
        addDoorBlocks(blocks);
        addRoofBlocks(blocks);
        return blocks;
    }

    private void addFloorBlocks(Map<Block, BlockData> blocks) {
        BlockData terracotta = MaterialUtil.getColoredTerracotta(getCurrentColor()).createBlockData();
        Block cornerBlock = position.getCornerBlock();
        for (int x = cornerBlock.getX(); x <= cornerBlock.getX() + getSize() + 1; x++) {
            for (int z = cornerBlock.getZ(); z <= cornerBlock.getZ() + getSize() + 1; z++) {
                blocks.put(getWorld().getBlockAt(x, cornerBlock.getY(), z), terracotta);
            }
        }
    }

    private void addWallBlocks(Map<Block, BlockData> blocks, BlockFace side) {
        BlockData terracotta = MaterialUtil.getColoredTerracotta(getCurrentColor()).createBlockData();
        BlockData concrete = MaterialUtil.getColoredConcrete(getCurrentColor()).createBlockData();
        Block cornerBlock = position.getCornerBlock();
        for (int y = cornerBlock.getY() + 1; y <= cornerBlock.getY() + getSize(); y++) {
            int horizontalStart;
            if (side == BlockFace.EAST || side == BlockFace.WEST) {
                horizontalStart = cornerBlock.getZ();
            } else {
                horizontalStart = cornerBlock.getX();
            }
            int horizontalEnd = horizontalStart + getSize() + 1;
            for (int horizontalCoordinate = horizontalStart; horizontalCoordinate <= horizontalEnd; horizontalCoordinate++) {
                Block block;
                if (side == BlockFace.EAST || side == BlockFace.WEST) {
                    block = world.getBlockAt(
                            side == BlockFace.EAST ? cornerBlock.getX() : cornerBlock.getX() + getSize() + 1,
                            y,
                            horizontalCoordinate
                    );
                } else {
                    block = getWorld().getBlockAt(
                            horizontalCoordinate,
                            y,
                            side == BlockFace.SOUTH ? cornerBlock.getZ() : cornerBlock.getZ() + getSize() + 1
                    );
                }
                if (block.equals(getLowerDoorBlock()) || block.equals(getUpperDoorBlock())) {
                    continue;
                }
                BlockData blockData = y <= cornerBlock.getY() + 1 ? terracotta : concrete;
                blocks.put(block, blockData);
            }
        }
    }

    private void addWallBlocks(Map<Block, BlockData> blocks) {
        for (BlockFace side : List.of(BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST)) {
            addWallBlocks(blocks, side);
        }
    }

    private void addRoofBlocks(Map<Block, BlockData> blocks) {
        BlockData glass = MaterialUtil.getColoredGlass(getCurrentColor()).createBlockData();
        Block cornerBlock = position.getCornerBlock();
        int y = cornerBlock.getY() + getSize() + 1;
        for (int x = cornerBlock.getX(); x <= cornerBlock.getX() + getSize() + 1; x++) {
            for (int z = cornerBlock.getZ(); z <= cornerBlock.getZ() + getSize() + 1; z++) {
                blocks.put(getWorld().getBlockAt(x, y, z), glass);
            }
        }
    }

    @Override
    public Integer getAnimationDelay() {
        if (!rainbow) {
            return null;
        }
        return plugin.getPluginConfig().getShulker().getRainbowDelay();
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(
            Message.COLOR.asComponent(),
            Message.getDyeColor(color).asComponent(TextColor.color(color.getColor().asRGB()))
        ));
        information.add(new ShellInformation(
            Message.RAINBOW_MODE.asComponent(),
            Message.getBool(rainbow).asComponent()
        ));
        information.add(new ShellInformation(
            Message.RAINBOW_COLORS.asComponent(),
            Component.text(getRainbowColors().size()).hoverEvent(HoverEvent.showText(
                Component.text(getRainbowColors().stream()
                    .map(Message::getDyeColor)
                    .map(Message::toString)
                    .collect(Collectors.joining(", "))
                )
            ))
        ));
    }

    @Override
    protected void addShellScreenActions(Player player, List<ShellScreenAction> actions) {
        super.addShellScreenActions(player, actions);
        actions.add(new ChangeColorAction(this, player));
    }

    public DyeColor getCurrentColor() {
        return getFactory().getCurrentColor(this);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;

        String sql = """
            UPDATE shulker_shells
            SET color = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, color.name().toLowerCase(Locale.ROOT));
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shulker shell color: {}", id, e);
        }

        repair();
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;

        String sql = """
            UPDATE shulker_shells
            SET rainbow = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, rainbow);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update rainbow mode: {}", id, e);
        }

        repair();
    }

    public Set<DyeColor> getRainbowColors() {
        return Collections.unmodifiableSet(rainbowColors);
    }

    public void setRainbowColor(DyeColor color, boolean enabled) {
        if (enabled) {
            rainbowColors.add(color);
        } else {
            rainbowColors.remove(color);
        }

        String sql = """
            INSERT INTO shulker_shell_rainbow_colors(id, color, enabled)
            VALUES (?, ?, ?)
            ON CONFLICT (id, color) DO UPDATE SET enabled = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, color.name().toLowerCase(Locale.ROOT));
            statement.setBoolean(3, enabled);
            statement.setBoolean(4, enabled);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shulker shell rainbow colors: {}", id, e);
        }

        repair();
    }

}
