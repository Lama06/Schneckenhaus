package io.github.lama06.schneckenhaus.shell.head;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellBuilder;
import org.bukkit.Bukkit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class HeadShellBuilder extends BuiltinShellBuilder implements HeadShellData {
    private UUID headOwner;

    @Override
    public HeadShellFactory getFactory() {
        return HeadShellFactory.INSTANCE;
    }

    @Override
    protected CompletableFuture<Object> prepareBuild() {
        if (headOwner == null) {
            headOwner = getCreator();
        }

        PlayerProfile profile = Bukkit.getOfflinePlayer(headOwner).getPlayerProfile();
        return profile.update().thenApplyAsync(updatedProfile -> {
            URL skin = updatedProfile.getTextures().getSkin();
            if (skin == null) {
                logger.error("failed to query player skin url: {}", profile);
                return null;
            }
            URI skinURI;
            try {
                skinURI = skin.toURI();
            } catch (URISyntaxException e) {
                logger.error("invalid skin url: {}", skin, e);
                return null;
            }

            byte[] imageData;
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder(skinURI).build();
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                imageData = response.body();
            } catch (IOException | InterruptedException e) {
                logger.error("failed to load skin: {}", skinURI, e);
                return null;
            }

            BufferedImage image;
            try {
                image = ImageIO.read(new ByteArrayInputStream(imageData));
            } catch (IOException e) {
                logger.error("failed to decode image", e);
                return null;
            }

            byte[] texture = new byte[HeadShell.TEXTURE_BYTES];
            for (HeadSide side : HeadSide.values()) {
                for (int x = 0; x < HeadShell.SIZE; x++) {
                    for (int y = 0; y < HeadShell.SIZE; y++) {
                        int rgb = image.getRGB(side.getX() * HeadShell.SIZE + x, side.getY() * HeadShell.SIZE + y);
                        byte blue = (byte) (rgb & 0xff);
                        byte green = (byte) ((rgb >>= 8) & 0xff);
                        byte red = (byte) ((rgb >> 8) & 0xff);
                        int index = side.ordinal() * HeadShell.TEXTURE_SIDE_BYTES + y * HeadShell.SIZE * HeadShell.COLOR_BYTES + x * HeadShell.COLOR_BYTES;
                        texture[index++] = red;
                        texture[index++] = green;
                        texture[index] = blue;
                    }
                }
            }

            return texture;
        });
    }

    @Override
    protected int buildDuringTransaction(Object prepared) throws SQLException {
        if (prepared == null) {
            throw new IllegalStateException("cannot build head shell");
        }
        byte[] texture = (byte[]) prepared;

        int id = super.buildDuringTransaction(prepared);

        Integer textureId = null;

        String selectTextureSql = """
            SELECT id
            FROM head_shell_textures
            WHERE texture = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(selectTextureSql)) {
            statement.setBytes(1, texture);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                textureId = result.getInt(1);
            }
        }

        if (textureId == null) {
            String insertTextureSql = """
            INSERT INTO head_shell_textures(texture)
            VALUES (?)
            RETURNING id
            """;
            try (PreparedStatement statement = connection.prepareStatement(insertTextureSql)) {
                statement.setBytes(1, texture);
                ResultSet result = statement.executeQuery();
                textureId = result.getInt(1);
            }
        }

        String insertHeadSql = """
            INSERT INTO head_shells(id, head_owner, texture)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(insertHeadSql)) {
            statement.setInt(1, id);
            statement.setString(2, headOwner.toString());
            statement.setInt(3, textureId);
            statement.executeUpdate();
        }

        return id;
    }

    @Override
    public UUID getHeadOwner() {
        return headOwner;
    }

    public void setHeadOwner(UUID headOwner) {
        this.headOwner = headOwner;
    }
}
