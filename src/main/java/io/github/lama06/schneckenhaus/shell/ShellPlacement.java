package io.github.lama06.schneckenhaus.shell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.List;

public record ShellPlacement(Shell shell, Block block) {
    public Location getTeleportLocation() {
        for (BlockFace face : List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
            Block neighbour = block.getRelative(face);
            if (!neighbour.isEmpty() || !neighbour.getRelative(BlockFace.UP).isEmpty() || neighbour.getRelative(BlockFace.DOWN).isEmpty()) {
                continue;
            }
            return neighbour.getLocation().toCenterLocation().add(0, 1, 0).setDirection(new Vector(
                -face.getModX(),
                -1,
                -face.getModZ()
            ));
        }

        return block.getLocation().toCenterLocation().add(0, 1, 0);
    }
}
