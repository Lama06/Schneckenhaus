package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.command.InfoCommand;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
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

    @Override
    public List<InfoCommand.Entry> getInformation() {
        final List<InfoCommand.Entry> entries = new ArrayList<>(super.getInformation());
        final int size = getSize();
        entries.add(new InfoCommand.Entry("Size", "%dx%d".formatted(size, size)));
        return entries;
    }

    public final int getSize() {
        return config.getSize();
    }
}
