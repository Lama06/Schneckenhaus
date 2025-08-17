package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.shell.ShellScreenAction;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class SizedShell extends BuiltinShell implements SizedShellData {
    protected SizedShell(int id) {
        super(id);
    }

    private int size;

    @Override
    public abstract SizedShellFactory getFactory();

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT size
            FROM sized_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }

            size = result.getInt("size");
            return true;
        } catch (SQLException e) {
            logger.error("failed to load shell: {}", id, e);
            return false;
        }
    }

    @Override
    public final BlockArea getFloor() {
        return new BlockArea(
            position.getCornerBlock().getRelative(1, 0, 1),
            position.getCornerBlock().getRelative(getSize(), 0, getSize())
        );
    }

    @Override
    public BlockArea getArea() {
        return new BlockArea(
            position.getCornerBlock(), position.getCornerBlock().getRelative(size + 1, size + 1, size + 1)
        );
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(Message.SIZE.asComponent(), Component.text(size)));
    }

    @Override
    protected void addShellScreenActions(Player player, List<ShellScreenAction> actions) {
        super.addShellScreenActions(player, actions);

        actions.add(new ShellScreenAction() {
            private static final int ANIMATION_DELAY = 20;

            private static final List<Material> ICONS = List.of(
                Material.SMALL_AMETHYST_BUD,
                Material.MEDIUM_AMETHYST_BUD,
                Material.LARGE_AMETHYST_BUD,
                Material.AMETHYST_CLUSTER
            );

            private int newSize;

            @Override
            public ItemStack getItem() {
                GlobalSizedShellConfig config = getFactory().getGlobalConfig();

                int maxSize = Math.max(getFactory().getMaxSize(), config.getMaxUpgradeSize());
                if (size >= maxSize) {
                    return null;
                }
                newSize = Math.min(maxSize, size + config.getSizePerUpgradeIngredient());

                boolean canAfford = config.getUpgradeIngredient().canRemoveFrom(player.getInventory());

                Material icon = ICONS.get((Bukkit.getCurrentTick() / ANIMATION_DELAY) % ICONS.size());
                ItemStack item = new ItemStack(icon);
                item.editMeta(meta -> {
                    meta.customName(Message.SIZE_UPGRADE.asComponent(NamedTextColor.YELLOW));
                    meta.lore(List.of(
                        Message.CURRENT_SIZE.asComponent().append(Component.text(": " + size)),
                        Message.SIZE_AFTER_UPGRADE.asComponent().append(Component.text(": " + newSize)),
                        Message.COST.asComponent(canAfford ? NamedTextColor.GREEN : NamedTextColor.RED)
                            .append(Component.text(": "))
                            .append(config.getUpgradeIngredient())
                    ));
                });

                return item;
            }

            @Override
            public Integer getItemAnimationDelay() {
                return ANIMATION_DELAY;
            }

            @Override
            public void onClick() {
                if (!getFactory().getGlobalConfig().getUpgradeIngredient().removeFrom(player.getInventory())) {
                    player.sendMessage(Message.ERROR_NOT_AFFORDABLE.asComponent(NamedTextColor.RED));
                    return;
                }
                setSize(newSize);
                player.sendMessage(Message.SIZE_UPGRADE_SUCCESS);
            }
        });
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        Map<Block, BlockData> oldBlocks = getBlocks();

        String sql = """
            UPDATE sized_shells
            SET size = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, size);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell size: {}", id, e);
            return;
        }

        this.size = size;

        Map<Block, BlockData> newBlocks = getBlocks();
        for (Block block : oldBlocks.keySet()) {
            if (newBlocks.containsKey(block)) {
                continue;
            }
            block.setType(Material.AIR);
        }
        place();
    }
}
