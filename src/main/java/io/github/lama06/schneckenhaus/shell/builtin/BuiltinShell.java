package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

import java.util.Map;
import java.util.Set;

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

    protected final Set<Block> getCornerTorchBlocks() {
        BlockArea area = getArea();
        Block corner = area.getLowerCorner().getBlock(world);
        return Set.of(
            corner.getRelative(1, 1, 1),
            corner.getRelative(area.getWidthX() - 2, 1, 1),
            corner.getRelative(1, 1, area.getWidthZ() - 2),
            corner.getRelative(area.getWidthX() - 2, 1, area.getWidthZ() - 2)
        );
    }

    protected final void addCornerTorches(Map<Block, BlockData> blocks) {
        BlockData torch = Material.TORCH.createBlockData();
        for (Block block : getCornerTorchBlocks()) {
            blocks.put(block, torch);
        }
    }

    @Override
    public boolean isExitBlock(Block block) {
        return getLowerDoorBlock().equals(block) || getUpperDoorBlock().equals(block);
    }

    @Override
    public boolean isMenuBlock(Block block) {
        return getMenuBlock().equals(block);
    }
}
