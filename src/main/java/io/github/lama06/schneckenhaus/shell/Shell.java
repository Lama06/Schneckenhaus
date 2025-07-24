package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.command.InfoCommand;
import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.EnumPersistentDataType;
import io.github.lama06.schneckenhaus.data.UuidPersistentDataType;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class Shell<C extends ShellConfig> implements PersistentDataHolder {
    public static final Attribute<String> TYPE = new Attribute<>("type", PersistentDataType.STRING);
    public static final Attribute<UUID> CREATOR = new Attribute<>("creator", UuidPersistentDataType.INSTANCE);
    public static final Attribute<AccessMode> ACCESS_MODE = new Attribute<>("access_mode", new EnumPersistentDataType<>(AccessMode.class));
    public static final Attribute<List<UUID>> WHITELIST =
        new Attribute<>("whitelist", PersistentDataType.LIST.listTypeFrom(UuidPersistentDataType.INSTANCE));
    public static final Attribute<List<UUID>> BLACKLIST =
        new Attribute<>("blacklist", PersistentDataType.LIST.listTypeFrom(UuidPersistentDataType.INSTANCE));
    public static final Attribute<Boolean> DELETED = new Attribute<>("deleted", PersistentDataType.BOOLEAN);

    public static final Attribute<Integer> ITEM_ID = new Attribute<>("id", PersistentDataType.INTEGER);
    public static final Attribute<Integer> BLOCK_ID = new Attribute<>("id", PersistentDataType.INTEGER);

    protected final GridPosition position;
    protected final C config;

    protected Shell(final GridPosition position, final C config) {
        this.position = Objects.requireNonNull(position);
        this.config = config;
    }

    public abstract Map<Block, BlockData> getBlocks();

    public Map<Block, BlockData> getInitialBlocks() {
        return Map.of();
    }

    public abstract BlockArea getFloor();

    public final void place() {
        final Map<Block, BlockData> blocks = getBlocks();
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

    public List<InfoCommand.Entry> getInformation() {
        final GridPosition position = getPosition();
        final Block cornerBlock = position.getCornerBlock();
        final String creatorName = getCreator().getName();
        return List.of(
            new InfoCommand.Entry("Id", Integer.toString(position.getId())),
            new InfoCommand.Entry("Grid Position", "X: %d, Z: %d".formatted(position.getX(), position.getZ())),
            new InfoCommand.Entry("World Position", "X: %d, Z: %d".formatted(cornerBlock.getX(), cornerBlock.getZ())),
            new InfoCommand.Entry("Creator", creatorName == null ? "Unknown" : creatorName),
            new InfoCommand.Entry("Access", PlainTextComponentSerializer.plainText().serialize(ACCESS_MODE.get(this).name))
        );
    }

    public final ItemStack createItem() {
        final ItemStack item = config.createItem();
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer data = meta.getPersistentDataContainer();
        ITEM_ID.set(data, position.getId());
        item.setItemMeta(meta);
        return item;
    }

    public final void delete() {
        for (final Player player : SchneckenPlugin.INSTANCE.getWorld().getBukkit().getPlayers()) {
            final CoordinatesGridPosition playerPosition = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
            if (playerPosition == null || !playerPosition.equals(getPosition())) {
                continue;
            }
            new SchneckenPlayer(player).teleportBack();
        }
        final World world = getWorld().getBukkit();
        for (final BlockPosition blockPosition : getPosition().getArea()) {
            blockPosition.getBlock(world).setType(Material.AIR);
        }
        DELETED.set(this, true);
    }

    public final Location getSpawnLocation() {
        final Vector direction = BlockFace.SOUTH_EAST.getDirection();
        floorBlocks:
        for (final BlockPosition floorBlockPos : getFloor()) {
            final Block floorBlock = floorBlockPos.getBlock(getWorld().getBukkit());
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
        final Block floorCorner = getFloor().getLowerCorner().getBlock(getWorld().getBukkit());
        if (!floorCorner.getType().isSolid()) {
            floorCorner.setType(Material.STONE);
        }
        for (int y = 1; y <= 2; y++) {
            final Block obstructedBlock = floorCorner.getRelative(0, y, 0);
            obstructedBlock.setType(Material.AIR);
        }
        return floorCorner.getLocation().add(0.5, 1, 0.5).setDirection(direction);
    }

    public boolean isAllowedToEnter(Player player) {
        AccessMode mode = ACCESS_MODE.getOrDefault(this, AccessMode.EVERYBODY);
        return mode.check(this, player);
    }

    @Override
    public final PersistentDataContainer getPersistentDataContainer() {
        return getWorld().getShellData(position);
    }

    public final SchneckenWorld getWorld() {
        return SchneckenPlugin.INSTANCE.getWorld();
    }

    public final GridPosition getPosition() {
        return position;
    }

    public final int getId() {
        return position.getId();
    }

    public final OfflinePlayer getCreator() {
        return Bukkit.getOfflinePlayer(CREATOR.get(this));
    }
}
