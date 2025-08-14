package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

import java.util.Map;

public abstract class BuiltinShell extends Shell implements BuiltinShellData {
    protected BuiltinShell(int id) {
        super(id);
    }

    protected final Block getLowerDoorBlock() {
        return position.getCornerBlock().getRelative(1, 1, 0);
    }

    protected final Block getUpperDoorBlock() {
        return getLowerDoorBlock().getRelative(BlockFace.UP);
    }

    protected final Block getMenuBlock() {
        return position.getCornerBlock().getRelative(1, 0, 1);
    }

    protected final void addDoorBlocks(Map<Block, BlockData> blocks) {
        Door lowerDoorBlock = (Door) Material.SPRUCE_DOOR.createBlockData();
        lowerDoorBlock.setOpen(false);
        lowerDoorBlock.setHalf(Bisected.Half.BOTTOM);
        lowerDoorBlock.setFacing(BlockFace.NORTH);

        Door upperDoorBlock = (Door) Material.SPRUCE_DOOR.createBlockData();
        upperDoorBlock.setOpen(false);
        upperDoorBlock.setHalf(Bisected.Half.TOP);
        upperDoorBlock.setFacing(BlockFace.NORTH);

        blocks.put(getLowerDoorBlock(), lowerDoorBlock);
        blocks.put(getUpperDoorBlock(), upperDoorBlock);
        blocks.put(getMenuBlock(), Material.CRAFTING_TABLE.createBlockData());
    }

    protected final void addCornerTorches(Map<Block, BlockData> blocks, int size) {
        Block corner = position.getCornerBlock();
        BlockData torch = Material.TORCH.createBlockData();
        blocks.put(corner.getRelative(1, 1, 1), torch);
        blocks.put(corner.getRelative(size, 1, 1), torch);
        blocks.put(corner.getRelative(1, 1, size), torch);
        blocks.put(corner.getRelative(size, 1, size), torch);
    }

    @Override
    public boolean isDoorBlock(Block block) {
        return getLowerDoorBlock().equals(block) || getUpperDoorBlock().equals(block);
    }

    @Override
    public boolean isMenuBlock(Block block) {
        return getMenuBlock().equals(block);
    }
}
