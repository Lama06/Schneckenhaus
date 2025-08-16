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
    QUICKLY_ENTER_SHELL(PermissionDefault.TRUE),
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

    BYPASS_ESCAPE_PREVENTION,

    COMMAND_SELECT("command.select"),
    COMMAND_LIST("command.list"),
    COMMAND_CREATE("command.create"),
    COMMAND_INFO("command.info"),
    COMMAND_DEBUG("command.debug", PermissionDefault.FALSE),
    COMMAND_TP("command.tp");

    public static void register() {
        for (Permission permission : Permission.values()) {
            Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(
                permission.name,
                permission.defaultValue
            ));
        }
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
