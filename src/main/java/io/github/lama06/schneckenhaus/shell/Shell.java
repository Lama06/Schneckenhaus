package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.command.InfoCommand;
import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.UuidPersistentDataType;
import io.github.lama06.schneckenhaus.position.GridPosition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class Shell implements PersistentDataHolder {
    public static final Attribute<String> TYPE = new Attribute<>("type", PersistentDataType.STRING);
    public static final Attribute<UUID> CREATOR = new Attribute<>("creator", UuidPersistentDataType.INSTANCE);

    public static final Attribute<Integer> ITEM_ID = new Attribute<>("id", PersistentDataType.INTEGER);
    public static final Attribute<Integer> BLOCK_ID = new Attribute<>("id", PersistentDataType.INTEGER);

    private final GridPosition position;
    private final ShellConfig config;

    protected Shell(final GridPosition position, final ShellConfig config) {
        this.position = Objects.requireNonNull(position);
        this.config = config;
    }

    public abstract Map<Block, BlockData> getBlocks();

    public Map<Block, BlockData> getInitialBlocks() {
        return Map.of();
    }

    protected final void addDoorBlocks(final Map<Block, BlockData> blocks) {
        final Door lowerDoorBlock = (Door) Material.SPRUCE_DOOR.createBlockData();
        lowerDoorBlock.setOpen(false);
        lowerDoorBlock.setHalf(Bisected.Half.BOTTOM);
        lowerDoorBlock.setFacing(BlockFace.NORTH);

        final Door upperDoorBlock = (Door) Material.SPRUCE_DOOR.createBlockData();
        upperDoorBlock.setOpen(false);
        upperDoorBlock.setHalf(Bisected.Half.TOP);
        upperDoorBlock.setFacing(BlockFace.NORTH);

        blocks.put(getPosition().getLowerDoorBlock(), lowerDoorBlock);
        blocks.put(getPosition().getUpperDoorBlock(), upperDoorBlock);
    }

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
        final int size = getSize();
        final String creatorName = getCreator().getName();
        return List.of(
                new InfoCommand.Entry("Id", Integer.toString(position.getId())),
                new InfoCommand.Entry("Grid Position", "X: %d, Z: %d".formatted(position.getX(), position.getZ())),
                new InfoCommand.Entry("World Position", "X: %d, Z: %d".formatted(cornerBlock.getX(), cornerBlock.getZ())),
                new InfoCommand.Entry("Size", "%dx%d".formatted(size, size)),
                new InfoCommand.Entry("Creator", creatorName == null ? "Unknown" : creatorName)
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

    @Override
    public final PersistentDataContainer getPersistentDataContainer() {
        return getWorld().getShellData(position);
    }

    public final int getSize() {
        return config.getSize();
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
