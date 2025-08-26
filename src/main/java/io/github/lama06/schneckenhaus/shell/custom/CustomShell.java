package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class CustomShell extends Shell implements CustomShellData {
    public static final String IMPORT_DIRECTORY = "import";
    public static final String EXPORT_DIRECTORY = "export";
    public static final String FILE_EXTENSION = ".haus";

    private String template;
    private CustomShellConfig config;
    private World templateWorld;

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
            result.next();
            template = result.getString(1);
        } catch (SQLException e) {
            logger.error("failed to load custom shell data: {}", id, e);
            return false;
        }

        config = plugin.getPluginConfig().getCustom().get(template);
        if (config == null) {
            logger.error("failed to find custom shell type config: {}", template, new RuntimeException());
            return false;
        }

        templateWorld = Bukkit.getWorld(config.getTemplateWorld());
        if (templateWorld == null) {
            logger.error("failed to find custom shell type template world: {}", config.getTemplateWorld());
            return false;
        }

        return true;
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();

        for (BlockPosition templatePosition : config.getTemplatePosition()) {
            Block templateBlock = templatePosition.getBlock(templateWorld);
            BlockPosition instancePosition = templatePosToInstancePos(templatePosition);
            Block instanceBlock = instancePosition.getBlock(world);

            if (templateBlock.isEmpty()) {
                continue;
            }

            blocks.put(instanceBlock, templateBlock.getBlockData());
        }

        return blocks;
    }

    @Override
    public Set<Material> getBlockRestrictionsOverride(Block block) {
        BlockPosition templatePosition = new BlockPosition(block)
            .subtract(position.getCornerBlockPosition())
            .add(config.getTemplatePosition().getLowerCorner());
        Set<Material> restrictionsOverride = config.getBlockRestrictions().getOrDefault(templatePosition, null);
        if (restrictionsOverride == null && config.isProtectAir() && templatePosition.getBlock(Bukkit.getWorld(config.getTemplateWorld())).isEmpty()) {
            return Set.of(Material.AIR, Material.VOID_AIR, Material.CAVE_AIR);
        }
        return restrictionsOverride;
    }

    @Override
    public BlockArea getArea() {
        BlockArea template = config.getTemplatePosition();
        int widthX = template.getWidthX();
        int height = template.getHeight();
        int widthZ = template.getWidthZ();
        return new BlockArea(
            position.getCornerBlock(),
            position.getCornerBlock().getRelative(widthX - 1, height - 1, widthZ - 1)
        );
    }

    @Override
    public Location getSpawnLocation() {
        Location spawnPositionConfig = config.getSpawnPosition();
        if (spawnPositionConfig == null) {
            return super.getSpawnLocation();
        }
        Location spawnLocation = spawnPositionConfig.clone();
        spawnLocation.setWorld(world);
        return spawnLocation.subtract(config.getTemplatePosition().getLowerCorner().toVector())
            .add(position.getCornerBlockPosition().toVector());
    }

    @Override
    public boolean isExitBlock(Block block) {
        Set<BlockPosition> exitBlocks = config.getExitBlocks().stream().map(this::templatePosToInstancePos).collect(Collectors.toSet());
        if (exitBlocks.isEmpty()) {
            return block != null && Tag.DOORS.isTagged(block.getType()) && getBlocks().containsKey(block);
        }
        return exitBlocks.contains(new BlockPosition(block));
    }

    @Override
    public boolean isMenuBlock(Block block) {
        BlockPosition menuBlock = config.getMenuBlock();
        if (menuBlock == null) {
            return block != null && block.getType() == Material.CRAFTING_TABLE && getBlocks().containsKey(block);
        }
        return templatePosToInstancePos(menuBlock).getBlock(world).equals(block);
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(
            Message.TEMPLATE.asComponent(),
            Component.text(template)
        ));
    }

    private BlockPosition templatePosToInstancePos(BlockPosition position) {
        return position.subtract(config.getTemplatePosition().getLowerCorner()).add(this.position.getCornerBlockPosition());
    }

    public String getTemplate() {
        return template;
    }
}
