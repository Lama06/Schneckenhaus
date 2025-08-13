package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomShell extends Shell implements CustomShellData {
    private String template;
    private GlobalCustomShellConfig config;

    public CustomShell(int id) {
        super(id);
    }

    @Override
    public ShellFactory getFactory() {
        return CustomShellFactory.INSTANCE;
    }

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT template
            FROM custom_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }
            template = result.getString(1);
        } catch (SQLException e) {
            logger.error("failed to load custom shell data: {}", id, e);
            return false;
        }

        config = plugin.getPluginConfig().getCustom().get(template);
        return config != null;
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();

        BlockPosition templateCorner = config.getTemplatePosition().getLowerCorner();
        Block targetCorner = getPosition().getCornerBlock();
        for (BlockPosition templatePosition : config.getTemplatePosition()) {
            Block templateBlock = templatePosition.getBlock(config.getTemplateWorld());
            if (templateBlock.isEmpty()) {
                continue;
            }
            BlockPosition templatePositionRelative = templatePosition.subtract(templateCorner);
            Block targetBlock = targetCorner.getRelative(
                    templatePositionRelative.x(),
                    templatePositionRelative.y(),
                    templatePositionRelative.z()
            );
            blocks.put(targetBlock, templateBlock.getBlockData());
        }
        return blocks;
    }

    @Override
    public BlockArea getFloor() {
        final BlockArea template = config.getTemplatePosition();
        final Block cornerBlock = position.getCornerBlock();
        return new BlockArea(
                new BlockPosition(cornerBlock.getRelative(1, 0, 1)),
                new BlockPosition(cornerBlock.getRelative(template.getWidthX() - 2, 0, template.getWidthZ() - 2))
        );
    }

    @Override
    public BlockArea getArea() {
        BlockArea template = config.getTemplatePosition();
        int widthX = template.getWidthX();
        int height = template.getHeight();
        int widthZ = template.getWidthZ();
        return new BlockArea(
            position.getCornerBlock(),
            position.getCornerBlock().getRelative(widthX, height, widthZ)
        );
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(
            Message.TEMPLATE.asComponent(),
            Component.text(template)
        ));
    }

    public String getTemplate() {
        return template;
    }
}
