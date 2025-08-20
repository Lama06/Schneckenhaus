package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.action.*;
import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermission;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionPlayerList;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class Shell extends ConstantsHolder implements ShellData {
    public static String ITEM_ID = "id";

    protected final int id;
    protected World world;
    protected ShellPosition position;
    protected UUID creator;

    protected ShellCreationType creationType;
    protected Date creationTime;

    protected String name;

    protected final ShellPermissionPlayerList owners;
    protected final ShellPermission enterPermission;
    protected final ShellPermission buildPermission;

    protected UUID homeOwner;

    protected Shell(int id) {
        this.id = id;
        owners = new ShellPermissionPlayerList(id, "owner");
        enterPermission = new ShellPermission(
            this,
            "enter_permission_mode",
            "enter_whitelist",
            "enter_blacklist",
            Message.ENTER_PERMISSION,
            Permission.BYPASS_SHELL_ENTER_PERMISSION
        );
        buildPermission = new ShellPermission(
            this,
            "build_permission_mode",
            "build_whitelist",
            "build_blacklist",
            Message.BUILD_PERMISSION,
            Permission.BYPASS_SHELL_BUILD_PERMISSION
        );
    }

    public abstract ShellFactory getFactory();

    protected boolean load() {
        String sql = """
            SELECT world, position, creator, creation_type, creation_time, name
            FROM shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }

            world = Bukkit.getWorld(result.getString("world"));
            if (world == null || !config.getWorlds().containsKey(world.getName())) {
                return false;
            }
            position = ShellPosition.id(world, result.getInt("position"));
            String creatorUuid = result.getString("creator");
            if (creatorUuid != null) {
                creator = UUID.fromString(creatorUuid);
            }
            creationType = ShellCreationType.valueOf(result.getString("creation_type").toUpperCase(Locale.ROOT));
            creationTime = result.getTimestamp("creation_time");
            name = result.getString("name");
            return true;
        } catch (SQLException e) {
            logger.error("failed to load shell data: {}", id, e);
            return false;
        }
    }

    public abstract Map<Block, BlockData> getBlocks();

    public Map<Block, BlockData> getInitialBlocks() {
        return Map.of();
    }

    public Integer getAnimationDelay() {
        return null;
    }

    public abstract BlockArea getArea();

    public final void place() {
        Map<Block, BlockData> blocks = getBlocks();
        for (Block block : blocks.keySet()) {
            if (block.getBlockData().equals(blocks.get(block))) {
                continue;
            }
            block.setBlockData(blocks.get(block));
        }
    }

    public final void placeInitially() {
        place();
        Map<Block, BlockData> initialBlocks = getInitialBlocks();
        for (Block block : initialBlocks.keySet()) {
            block.setBlockData(initialBlocks.get(block));
        }
    }

    public Location getSpawnLocation() {
        Vector direction = BlockFace.SOUTH_EAST.getDirection();
        BlockArea floor = getArea().getLayer(0).shrink(1, 0, 1);
        floorBlocks:
        for (BlockPosition floorBlockPos : floor) {
            Block floorBlock = floorBlockPos.getBlock(getWorld());
            if (!floorBlock.getType().isSolid()) {
                continue;
            }
            for (int y = 1; y <= 2; y++) {
                if (floorBlock.getRelative(0, y, 0).getType().isSolid()) {
                    continue floorBlocks;
                }
            }
            return floorBlock.getLocation().add(0.5, 1, 0.5).setDirection(direction);
        }
        Block floorCorner = floor.getLowerCorner().getBlock(getWorld());
        if (!floorCorner.getType().isSolid()) {
            floorCorner.setType(Material.STONE);
        }
        for (int y = 1; y <= 2; y++) {
            Block obstructedBlock = floorCorner.getRelative(0, y, 0);
            obstructedBlock.setType(Material.AIR);
        }
        return floorCorner.getLocation().add(0.5, 1, 0.5).setDirection(direction);
    }

    public abstract boolean isExitBlock(Block block);

    public abstract boolean isMenuBlock(Block block);

    public final ItemStack createItem(boolean storeId) {
        ItemStack item = getFactory().createItem(this);
        item.editMeta(meta -> {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if (storeId) {
                data.set(new NamespacedKey(plugin, "id"), PersistentDataType.INTEGER, id);
            }
        });
        return item;
    }

    public final ItemStack createItem() {
        return createItem(true);
    }

    protected void addInformation(List<ShellInformation> information) {
        information.add(new ShellInformation(Message.ID.asComponent(), Component.text(id)));
        information.add(new ShellInformation(Message.TAGS.asComponent(), Component.text(getTags().toString())));

        information.add(new ShellInformation(Message.TYPE.asComponent(), getFactory().getName().asComponent()));

        information.add(new ShellInformation(
            Message.GRID_POSITION.asComponent(),
            Component.text(position.getX() + " " + position.getZ())
        ));
        information.add(new ShellInformation(
            Message.WORLD.asComponent(),
            Component.text(world.getName())
        ));
        BlockArea area = getArea();
        information.add(new ShellInformation(
            Message.POSITION_1.asComponent(),
            Component.text(area.position1().toString())
        ));
        information.add(new ShellInformation(
            Message.POSITION_2.asComponent(),
            Component.text(area.position2().toString())
        ));
        information.add(new ShellInformation(Message.AREA.asComponent(), Component.text(area.toString())));

        information.add(new ShellInformation(
            Message.CREATOR.asComponent(),
            Component.text(Objects.requireNonNullElse(Bukkit.getOfflinePlayer(creator).getName(), Message.UNKNOWN_PLAYER.toString()))
        ));
        information.add(new ShellInformation(
            Message.CREATION_TIME.asComponent(),
            Component.text(creationTime.toString())
        ));

        information.add(new ShellInformation(Message.OWNERS.asComponent(), Component.text(owners.toString())));
        information.add(new ShellInformation(Message.ENTER_PERMISSION.asComponent(), enterPermission.toComponent()));
        information.add(new ShellInformation(Message.BUILD_PERMISSION.asComponent(), buildPermission.toComponent()));
        information.add(new ShellInformation(Message.ACCESS_COUNT.asComponent(), Component.text(getTotalAccessCount())));
    }

    public final List<ShellInformation> getInformation() {
        List<ShellInformation> information = new ArrayList<>();
        addInformation(information);
        return information;
    }

    protected void addShellScreenActions(Player player, List<ShellScreenAction> actions) {
        actions.add(new CopyAction(this, player));
        actions.add(new EditNameAction(this, player));
        actions.add(new EditOwnersAction(this, player));
        actions.add(new EditPermissionAction(
            this,
            player,
            enterPermission,
            Permission.CHANGE_ENTER_PERMISSION,
            Material.OAK_DOOR,
            Message.ENTER_PERMISSION
        ));
        actions.add(new EditPermissionAction(
            this,
            player,
            buildPermission,
            Permission.CHANGE_BUILD_PERMISSION,
            Material.GOLDEN_PICKAXE,
            Message.BUILD_PERMISSION
        ));
        actions.add(new ShowPlacementsAction(this, player));
        actions.add(new DeleteAction(this, player));
    }

    public final List<ShellScreenAction> getShellScreenActions(Player player) {
        List<ShellScreenAction> actions = new ArrayList<>();
        addShellScreenActions(player, actions);
        return actions;
    }

    public final void delete() {
        for (Player player : getWorld().getPlayers()) {
            if (!getPosition().equals(ShellPosition.location(player.getLocation()))) {
                continue;
            }
            if (player.isFlying()) {
                continue;
            }
            new SchneckenhausPlayer(player).leave();
        }
        for (BlockPosition position : getArea()) {
            position.getBlock(getWorld()).setType(Material.AIR);
        }
        for (ShellPlacement placement : plugin.getShellManager().getShellPlacements(this)) {
            placement.block().setType(Material.AIR);
        }

        try {
            plugin.getDatabase().executeTransaction(() -> {
                String deleteSql = """
                    DELETE FROM shells
                    WHERE id = ?
                    """;
                try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }

                String insertUnusedPositionSql = """
                    INSERT INTO unused_shell_positions(world, position)
                    VALUES (?, ?)
                    """;
                try (PreparedStatement statement = connection.prepareStatement(insertUnusedPositionSql)) {
                    statement.setString(1, world.getName());
                    statement.setInt(2, position.getId());
                    statement.executeUpdate();
                }

                return null;
            });
        } catch (Exception e) {
            logger.error("failed to delete snail shell {}", id, e);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Shell that && id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public ShellPosition getPosition() {
        return position;
    }

    public UUID getCreator() {
        return creator;
    }

    @Override
    public ShellCreationType getCreationType() {
        return creationType;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        String sql = """
            UPDATE shells
            SET name = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell name: {}", id, e);
        }
    }

    public ShellPermissionPlayerList getOwners() {
        return owners;
    }

    public ShellPermission getEnterPermission() {
        return enterPermission;
    }

    public ShellPermission getBuildPermission() {
        return buildPermission;
    }

    public UUID getHomeOwner() {
        if (homeOwner != null) {
            return homeOwner;
        }
        String sql = """
            SELECT player
            FROM home_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql))  {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            return homeOwner = UUID.fromString(result.getString(1));
        } catch (SQLException e) {
            logger.error("failed to query snail shell home owner: {}", id, e);
            return null;
        }
    }

    public int getTotalAccessCount() {
        String sql = """
            SELECT SUM(amount)
            FROM shell_access_statistics
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            logger.error("failed to query join statistics: {}", id, e);
            return 0;
        }
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<>();
        String sql = """
            SELECT tag
            FROM shell_tags
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                tags.add(result.getString("tag"));
            }
            return tags;
        } catch (SQLException e) {
            logger.error("failed to query tags of shell {}", id, e);
            return List.of();
        }
    }

    public boolean hasTag(String tag) {
        String sql = """
            SELECT 1
            FROM shell_tags
            WHERE id = ? AND tag = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, tag);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            logger.error("failed to query if shell {} has tag {}", id, tag, e);
            return false;
        }
    }

    public void addTag(String tag) {
        String sql = """
            INSERT INTO shell_tags(id, tag)
            VALUES (?, ?)
            ON CONFLICT (id, tag) DO NOTHING
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, tag);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to add tag {} to {}", tag, id, e);
        }
    }

    public void removeTag(String tag) {
        String sql = """
            DELETE FROM shell_tags
            WHERE id = ? AND tag = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, tag);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to remove tag {} from shell {}", tag, id, e);
        }
    }

    public void clearTags() {
        String sql = """
            DELETE FROM shell_tags
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to clear tags of shell {}", id, e);
        }
    }
}
