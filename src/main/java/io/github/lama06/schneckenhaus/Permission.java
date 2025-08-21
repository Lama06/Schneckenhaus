package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.language.Message;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionDefault;

import java.util.Locale;

public enum Permission {
    CRAFT_SHELL(PermissionDefault.TRUE),
    PLACE_SHELL(PermissionDefault.TRUE),

    ENTER_SHELL(PermissionDefault.TRUE),
    QUICKLY_ENTER_SHELL(PermissionDefault.FALSE),
    ENTER_NESTED_SHELLS(PermissionDefault.TRUE),

    ASK_FOR_ENTER_PERMISSION(PermissionDefault.TRUE),
    CHANGE_ENTER_PERMISSION(PermissionDefault.TRUE),
    BYPASS_SHELL_ENTER_PERMISSION,

    CHANGE_BUILD_PERMISSION(PermissionDefault.TRUE),
    BYPASS_SHELL_BUILD_PERMISSION,

    BYPASS_THEFT_PREVENTION,

    HOME_SHELL(PermissionDefault.FALSE),
    NEVER_HOMELESS(PermissionDefault.TRUE),

    SHELL_IN_ENDER_CHEST(PermissionDefault.TRUE),

    OPEN_OWN_SNAIL_SHELL_MENU(PermissionDefault.TRUE),
    OPEN_OTHER_SNAIL_SHELL_MENUS,
    CREATE_SNAIL_SHELL_COPIES,
    RENAME_SNAIL_SHELL(PermissionDefault.TRUE),
    EDIT_OWNERS(PermissionDefault.TRUE),
    CHANGE_SNAIL_SHELL_COLOR(PermissionDefault.TRUE),
    TOGGLE_RAINBOW_MODE(PermissionDefault.TRUE),
    UPGRADE_SNAIL_SHELL_SIZE(PermissionDefault.TRUE),
    CHANGE_SHELL_WOOD(PermissionDefault.TRUE),
    DELETE_SHELL(PermissionDefault.OP),
    PLACEMENTS_VIEW("placements.view", PermissionDefault.TRUE),
    PLACEMENTS_VIEW_POSITIONS("placements.view_positions", PermissionDefault.TRUE),
    PLACEMENTS_TELEPORT("placements.teleport", PermissionDefault.OP),

    BYPASS_ESCAPE_PREVENTION,

    COMMAND_SELECT("command.select"),
    COMMAND_SELECTION("command.list"),
    COMMAND_CREATE("command.create"),
    COMMAND_INFO("command.info"),
    COMMAND_TAG("command.tag"),
    COMMAND_ITEM("command.item"),
    COMMAND_COUNT("command.count"),
    COMMAND_DELETE("command.delete"),
    COMMAND_TP("command.tp"),
    COMMAND_HOME_TP_OWN("command.home.tp.own"),
    COMMAND_HOME_TP_OTHERS("command.home.tp.others"),
    COMMAND_HOME_MANAGE("command.home.manage"),
    COMMAND_DISCORD("command.discord"),
    COMMAND_MENU("command.menu"),
    COMMAND_LANGUAGE("command.language"),
    COMMAND_CUSTOM("command.custom"),
    COMMAND_DEBUG("command.debug", PermissionDefault.FALSE);

    public static void register() {
        for (Permission permission : Permission.values()) {
            Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(
                permission.name,
                permission.defaultValue
            ));
        }
    }

    public static void generateDocs() {
        StringBuilder builder = new StringBuilder();

        for (Permission permission : values()) {
            builder.append("- `").append(permission.name).append("` ");
            builder.append("(default: ").append(permission.defaultValue.name().toLowerCase()).append(")");
            builder.append("\n");
        }

        System.out.println(builder);
    }

    private final String name;
    private final PermissionDefault defaultValue;

    Permission(String name, PermissionDefault defaultValue) {
        this.name = "schneckenhaus." + name;
        this.defaultValue = defaultValue;
    }

    Permission(String name) {
        this(name, PermissionDefault.OP);
    }

    Permission(PermissionDefault defaultValue) {
        name = "schneckenhaus." + name().toLowerCase(Locale.ROOT);
        this.defaultValue = defaultValue;
    }

    Permission() {
        this(PermissionDefault.OP);
    }

    public String getName() {
        return name;
    }

    public boolean check(Permissible permissible) {
        return permissible.hasPermission(name);
    }

    public boolean require(Permissible permissible) {
        return require(permissible, name);
    }

    public boolean check(CommandSourceStack source) {
        return check(source.getSender());
    }

    public static boolean require(Permissible permissible, String permission) {
        if (!permissible.hasPermission(permission)) {
            if (!(permissible instanceof final CommandSender sender)) {
                return false;
            }

            sender.sendMessage(Message.ERROR_PERMISSION.asComponent(NamedTextColor.RED, permission));
            return false;
        }
        return true;
    }
}
