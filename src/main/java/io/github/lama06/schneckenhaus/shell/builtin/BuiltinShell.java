package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.command.InfoCommand;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BuiltinShell<C extends BuiltinShellConfig> extends Shell<C> {
    protected BuiltinShell(final GridPosition position, final C config) {
        super(position, config);
    }

    protected final Block getLowerDoorBlock() {
        return position.getCornerBlock().getRelative(1, 1, 0);
    }

    protected final Block getUpperDoorBlock() {
        return getLowerDoorBlock().getRelative(BlockFace.UP);
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

        blocks.put(getLowerDoorBlock(), lowerDoorBlock);
        blocks.put(getUpperDoorBlock(), upperDoorBlock);
        blocks.put(position.getCornerBlock().getRelative(1, 0, 1), Material.CRAFTING_TABLE.createBlockData());
    }

    @Override
    public List<InfoCommand.Entry> getInformation() {
        final List<InfoCommand.Entry> entries = new ArrayList<>(super.getInformation());

        final int size = getSize();
        entries.add(new InfoCommand.Entry("Size", "%dx%d".formatted(size, size)));

        BlockPosition pos1 = new BlockPosition(position.getCornerBlock().getRelative(1, 1, 1));
        BlockPosition pos2 = new BlockPosition(position.getCornerBlock().getRelative(size, size, size));
        entries.add(new InfoCommand.Entry("Position 1", pos1.toString()));
        entries.add(new InfoCommand.Entry("Position 2", pos2.toString()));
        entries.add(new InfoCommand.Entry("Area", pos1 + " " + pos2));

        return entries;
    }

    @Override
    public final BlockArea getFloor() {
        return new BlockArea(
                new BlockPosition(position.getCornerBlock().getRelative(1, 0, 1)),
                new BlockPosition(position.getCornerBlock().getRelative(getSize(), 0, getSize()))
        );
    }

    public final int getSize() {
        return config.getSize();
    }
}
