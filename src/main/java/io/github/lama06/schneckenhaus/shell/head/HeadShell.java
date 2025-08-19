package io.github.lama06.schneckenhaus.shell.head;

import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.ColorUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HeadShell extends BuiltinShell implements HeadShellData {
    public static final int SIZE = 8;
    public static final int COLOR_BYTES = 3;
    public static final int TEXTURE_SIDE_BYTES = SIZE * SIZE * COLOR_BYTES;
    public static final int TEXTURE_BYTES = 6 * TEXTURE_SIDE_BYTES;

    private UUID headOwner;
    private Map<HeadSide, Material[][]> texture;

    public HeadShell(int id) {
        super(id);
    }

    @Override
    public HeadShellFactory getFactory() {
        return HeadShellFactory.INSTANCE;
    }

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT head_owner
            FROM head_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }
            headOwner = UUID.fromString(result.getString(1));
            return true;
        } catch (SQLException e) {
            logger.error("failed to load head shell: {}", id, e);
            return true;
        }
    }

    @Override
    public Map<Block, BlockData> getBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();
        for (HeadSide side : HeadSide.values()) {
            addSide(blocks,side);
        }
        addDoorBlocks(blocks);
        addDoorBlocks(blocks);
        return blocks;
    }

    private void addSide(Map<Block, BlockData> blocks, HeadSide side) {
        Material[][] materials = getTexture().get(side);
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                blocks.put(side.getBlock(getPosition().getCornerBlock(), x, y), materials[x][y].createBlockData());
            }
        }
    }

    @Override
    public Map<Block, BlockData> getInitialBlocks() {
        Map<Block, BlockData> blocks = new HashMap<>();
        addCornerTorches(blocks, SIZE);
        return blocks;
    }

    @Override
    public BlockArea getArea() {
        Block corner = getPosition().getCornerBlock();
        return new BlockArea(corner, corner.getRelative(SIZE + 1, SIZE + 1, SIZE + 1));
    }

    @Override
    public UUID getHeadOwner() {
        return headOwner;
    }

    public Map<HeadSide, Material[][]> getTexture() {
        if (texture != null) {
            return texture;
        }

        byte[] textureBytes;

        String sql = """
            SELECT head_shell_textures.texture
            FROM head_shells JOIN head_shell_textures ON head_shells.texture = head_shell_textures.id
            WHERE head_shells.id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            textureBytes = result.getBytes(1);
        } catch (SQLException e) {
            logger.error("failed to query head shell texture: {}", id, e);
            return null;
        }

        texture = new EnumMap<>(HeadSide.class);
        for (HeadSide side : HeadSide.values()) {
            Material[][] sideTexture = new Material[SIZE][SIZE];
            texture.put(side, sideTexture);
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    int i = side.ordinal() * TEXTURE_SIDE_BYTES + y * SIZE * COLOR_BYTES + x * COLOR_BYTES;
                    sideTexture[x][y] = ColorUtil.getMatchingBlockType(Color.fromRGB(
                        Byte.toUnsignedInt(textureBytes[i]),
                        Byte.toUnsignedInt(textureBytes[i + 1]),
                        Byte.toUnsignedInt(textureBytes[i + 2])
                    ));
                }
            }
        }

        return texture;
    }
}
