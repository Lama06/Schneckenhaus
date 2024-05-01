package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.InfoCommand;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomShell extends Shell<CustomShellConfig> {
    public CustomShell(final GridPosition position, final CustomShellConfig config) {
        super(position, config);
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        final Map<Block, BlockData> blocks = new HashMap<>();
        final ConfigurationSection globalConfig = config.getGlobalConfig();
        final BlockArea template = BlockArea.fromString(globalConfig.getString("template"));
        final BlockPosition templateCorner = template.getLowerCorner();
        final Block targetCorner = getPosition().getCornerBlock();
        for (final BlockPosition templatePosition : template) {
            final Block templateBlock = templatePosition.getBlock(SchneckenPlugin.INSTANCE.getWorld().getBukkit());
            if (templateBlock.isEmpty()) {
                continue;
            }
            final BlockPosition templatePositionRelative = templatePosition.subtract(templateCorner);
            final Block targetBlock = targetCorner.getRelative(
                    templatePositionRelative.x(),
                    templatePositionRelative.y(),
                    templatePositionRelative.z()
            );
            blocks.put(targetBlock, templateBlock.getBlockData());
        }
        return blocks;
    }

    @Override
    public List<InfoCommand.Entry> getInformation() {
        final List<InfoCommand.Entry> information = new ArrayList<>(super.getInformation());
        information.add(new InfoCommand.Entry("Template", config.getName()));
        return information;
    }
}
