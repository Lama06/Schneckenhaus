package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.position.Position;
import io.github.lama06.schneckenhaus.screen.InputScreen;
import io.github.lama06.schneckenhaus.screen.PermissionScreen;
import io.github.lama06.schneckenhaus.screen.PlayerListEditScreen;
import io.github.lama06.schneckenhaus.screen.ShellScreen;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermission;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionPlayerList;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class Shell implements ShellData {
    public static String ITEM_ID = "id";

    protected final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
    protected final SchneckenhausConfig config = plugin.getPluginConfig();
    protected final Connection connection = plugin.getDBConnection();
    protected final Logger logger = plugin.getSLF4JLogger();

    protected final int id;
    protected World world;
    protected Position position;
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
            position = Position.id(world, result.getInt("position"));
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

    public abstract BlockArea getFloor();

    public final void place() {
        Map<Block, BlockData> blocks = getBlocks();
        for (final Block block : blocks.keySet()) {
            if (block.getBlockData().equals(blocks.get(block))) {
                continue;
            }
            block.setBlockData(blocks.get(block));
        }
    }

    public final void placeInitially() {
        place();
        final Map<Block, BlockData> initialBlocks = getInitialBlocks();
        for (final Block block : initialBlocks.keySet()) {
            block.setBlockData(initialBlocks.get(block));
        }
    }

    public final Location getSpawnLocation() {
        Vector direction = BlockFace.SOUTH_EAST.getDirection();
        floorBlocks:
        for (BlockPosition floorBlockPos : getFloor()) {
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
        Block floorCorner = getFloor().getLowerCorner().getBlock(getWorld());
        if (!floorCorner.getType().isSolid()) {
            floorCorner.setType(Material.STONE);
        }
        for (int y = 1; y <= 2; y++) {
            Block obstructedBlock = floorCorner.getRelative(0, y, 0);
            obstructedBlock.setType(Material.AIR);
        }
        return floorCorner.getLocation().add(0.5, 1, 0.5).setDirection(direction);
    }

    public boolean isDoorBlock(Block block) {
        return block != null && Tag.DOORS.isTagged(block.getType()) && getBlocks().containsKey(block);
    }

    public boolean isMenuBlock(Block block) {
        return block.equals(position.getCornerBlock().getRelative(1, 0, 1));
    }

    public final ItemStack createItem() {
        ItemStack item = getFactory().createItem(this);
        item.editMeta(meta -> {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "id"), PersistentDataType.INTEGER, id);
        });
        return item;
    }

    protected void addInformation(List<ShellInformation> information) {
        information.add(new ShellInformation(Message.ID.toComponent(), Component.text(id)));
        information.add(new ShellInformation(Message.TYPE.toComponent(), Component.text(getFactory().getName()))); // TODO translatable

        information.add(new ShellInformation(
            Message.GRID_POSITION.toComponent(),
            Component.text(position.getX() + " " + position.getZ())
        ));
        information.add(new ShellInformation(
            Message.WORLD.toComponent(),
            Component.text(world.getName())
        ));
        BlockArea area = getArea();
        information.add(new ShellInformation(
            Message.POSITION_1.toComponent(),
            Component.text(area.position1().toString())
        ));
        information.add(new ShellInformation(
            Message.POSITION_2.toComponent(),
            Component.text(area.position2().toString())
        ));

        information.add(new ShellInformation(
            Message.CREATOR.toComponent(),
            Component.text(Objects.requireNonNullElse(Bukkit.getOfflinePlayer(creator).getName(), Message.UNKNOWN_PLAYER.toString()))
        ));
        information.add(new ShellInformation(
            Message.CREATION_TIME.toComponent(),
            Component.text(creationTime.toString())
        ));

        information.add(new ShellInformation(Message.OWNERS.toComponent(), Component.text(owners.toString())));
        information.add(new ShellInformation(Message.ENTER_PERMISSION.toComponent(), enterPermission.toComponent()));
        information.add(new ShellInformation(Message.BUILD_PERMISSION.toComponent(), buildPermission.toComponent()));
        information.add(new ShellInformation(Message.ACCESS_COUNT.toComponent(), Component.text(getTotalAccessCount())));
    }

    public final List<ShellInformation> getInformation() {
        List<ShellInformation> information = new ArrayList<>();
        addInformation(information);
        return information;
    }

    protected void addMenuActions(Player player, List<ShellMenuAction> actions) {
        actions.add(new ShellMenuAction() {
            @Override
            public ItemStack getItem() {
                // item without id to prevent the animation system from messing with it
                ItemStack item = getFactory().createItem(Shell.this);
                item.editMeta(meta -> {
                    List<Component> lore = new ArrayList<>(meta.lore());
                    if (Permission.CREATE_SNAIL_SHELL_COPIES.check(player)) {
                        lore.add(Message.CLICK_TO_CREATE_SHELL_COPY.toComponent(NamedTextColor.YELLOW));
                    }
                    meta.lore(lore);
                });
                return item;
            }

            @Override
            public Integer getItemAnimationDelay() {
                return getFactory().getItemAnimationDelay(Shell.this);
            }

            @Override
            public void onClick() {
                if (!Permission.CREATE_SNAIL_SHELL_COPIES.check(player)) {
                    return;
                }
                player.give(createItem());
            }
        });

        // Name
        actions.add(new ShellMenuAction(
            name == null ? Message.NAME_NOT_SET.toComponent() : Message.SNAIL_SHELL_NAME.toComponent(name),
            Material.NAME_TAG,
            Permission.RENAME_SNAIL_SHELL.check(player) ? Message.CLICK_TO_CHANGE_NAME.toComponent(NamedTextColor.YELLOW) : null
        ) {
            @Override
            public void onClick() {
                if (!Permission.RENAME_SNAIL_SHELL.check(player)) {
                    return;
                }
                new InputScreen(
                    player,
                    Message.RENAME_SHELL_TITLE.toComponent(NamedTextColor.YELLOW),
                    name == null ? "" : name,
                    newName -> {
                        setName(newName);
                        player.sendMessage(Message.RENAME_SHELL_SUCCESS.toComponent(NamedTextColor.GREEN, newName));
                    },
                    () -> { }
                ).open();
            }
        });

        // Owners
        actions.add(new ShellMenuAction() {
            private static final int ANIMATION_DELAY = 20;

            private final List<UUID> ownerUuids = owners.get().stream().sorted().toList();

            @Override
            public ItemStack getItem() {
                if (!Permission.EDIT_OWNERS.check(player)) {
                    return null;
                }

                if (ownerUuids.isEmpty()) {
                    return null;
                }
                int currentlyDisplayOwnerIndex = (Bukkit.getCurrentTick() / ANIMATION_DELAY) % ownerUuids.size();
                UUID currentlyDisplayedOwnerUuid = ownerUuids.get(currentlyDisplayOwnerIndex);
                OfflinePlayer currentlyDisplayedOwner = Bukkit.getOfflinePlayer(currentlyDisplayedOwnerUuid);
                String currentlyDisplayedOwnerName = Objects.requireNonNullElse(
                    currentlyDisplayedOwner.getName(),
                    currentlyDisplayedOwner.getUniqueId().toString()
                );

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                head.editMeta(SkullMeta.class, meta -> {
                    meta.customName(Message.OWNERS.toComponent(NamedTextColor.YELLOW));
                    meta.lore(List.of(
                        Component.text(currentlyDisplayedOwnerName),
                        Message.CLICK_TO_EDIT.toComponent(NamedTextColor.YELLOW)
                    ));
                    meta.setOwningPlayer(currentlyDisplayedOwner);
                });
                return head;
            }

            @Override
            public Integer getItemAnimationDelay() {
                if (ownerUuids.size() <= 1) {
                    return null;
                }
                return ANIMATION_DELAY;
            }

            @Override
            public void onClick() {
                new PlayerListEditScreen(
                    player,
                    Message.OWNERS.toComponent(NamedTextColor.YELLOW),
                    owners.get(),
                    owners::set,
                    () -> new ShellScreen(Shell.this, player).open()
                ).open();
            }
        });

        // Enter Permissions
        actions.add(new ShellMenuAction(
            Message.ENTER_PERMISSION.toComponent(NamedTextColor.YELLOW),
            Material.OAK_DOOR,
            Message.CLICK_TO_EDIT.toComponent(NamedTextColor.YELLOW)
        ) {
            @Override
            public ItemStack getItem() {
                if (!Permission.CHANGE_ENTER_PERMISSION.check(player)) {
                    return null;
                }
                return super.getItem();
            }

            @Override
            public void onClick() {
                new PermissionScreen(player, enterPermission).open();
            }
        });

        // Build Permissions
        actions.add(new ShellMenuAction(
            Message.BUILD_PERMISSION.toComponent(NamedTextColor.YELLOW),
            Material.GOLDEN_PICKAXE,
            Message.CLICK_TO_EDIT.toComponent(NamedTextColor.YELLOW)
        ) {
            @Override
            public ItemStack getItem() {
                if (!Permission.CHANGE_BUILD_PERMISSION.check(player)) {
                    return null;
                }
                return super.getItem();
            }

            @Override
            public void onClick() {
                new PermissionScreen(player, enterPermission).open();
            }
        });
    }

    public final List<ShellMenuAction> getShellMenuActions(Player player) {
        List<ShellMenuAction> actions = new ArrayList<>();
        addMenuActions(player, actions);
        return actions;
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

    public Position getPosition() {
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
            FROM homes
            WHERE shell_id = ?
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
