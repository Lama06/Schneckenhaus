package io.github.lama06.schneckenhaus.shell.permission;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public final class ShellPermission implements ComponentLike {
    private final Connection connection = SchneckenhausPlugin.INSTANCE.getDatabaseConnection();
    private final Logger logger = SchneckenhausPlugin.INSTANCE.getSLF4JLogger();

    private final Shell shell;
    private final String attribute;
    private ShellPermissionMode mode;
    private final ShellPermissionPlayerList whitelist;
    private final ShellPermissionPlayerList blacklist;
    private final Message name;
    private final Permission bypassPermission;

    public ShellPermission(
        Shell shell,
        String attribute,
        String whitelistName,
        String blacklistName,
        Message name,
        Permission bypassPermission
    ) {
        this.shell = shell;
        this.attribute = attribute;
        whitelist = new ShellPermissionPlayerList(shell.getId(), whitelistName);
        blacklist = new ShellPermissionPlayerList(shell.getId(), blacklistName);
        this.name = name;
        this.bypassPermission = bypassPermission;
    }

    public boolean hasPermission(Player player) {
        return bypassPermission.check(player) ||
            shell.getOwners().contains(player) ||
            getMode() == ShellPermissionMode.EVERYBODY ||
            (getMode() == ShellPermissionMode.WHITELIST && whitelist.contains(player)) ||
            (getMode() == ShellPermissionMode.BLACKLIST && !blacklist.contains(player));
    }

    public ShellPermissionMode getMode() {
        if (mode != null) {
            return mode;
        }

        String sql = """
            SELECT attribute
            FROM shells
            WHERE id = ?
            """.replace("attribute", attribute);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shell.getId());
            ResultSet result = statement.executeQuery();
            result.next();
            return mode = ShellPermissionMode.valueOf(result.getString(1).toUpperCase(Locale.ROOT));
        } catch (SQLException e) {
            logger.error("failed to get permission mode: {}", shell.getId(), e);
            return ShellPermissionMode.NOBODY;
        }
    }

    public void setMode(ShellPermissionMode mode) {
        this.mode = mode;
        String sql = """
            UPDATE shells
            SET attribute = ?
            WHERE id = ?
            """.replace("attribute", attribute);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mode.name().toLowerCase(Locale.ROOT));
            statement.setInt(2, shell.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell lock type: {}", shell.getId(), e);
        }
    }

    @Override
    public Component asComponent() {
        TextComponent.Builder builder = Component.text();
        builder.append(getMode());
        ShellPermissionPlayerList players = switch (mode) {
            case WHITELIST -> whitelist;
            case BLACKLIST -> blacklist;
            case NOBODY, EVERYBODY -> null;
            case null -> null;
        };
        if (players == null) {
            return builder.build();
        }
        builder.append(Component.text(" (" + players + ")"));
        return builder.build();
    }

    public ShellPermissionPlayerList getWhitelist() {
        return whitelist;
    }

    public ShellPermissionPlayerList getBlacklist() {
        return blacklist;
    }

    public Message getName() {
        return name;
    }
}
