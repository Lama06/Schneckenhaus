package io.github.lama06.schneckenhaus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class Permissions {
    public static final String CRAFT = "schneckenhaus.craft";
    public static final String ENTER = "schneckenhaus.enter";
    public static final String BYPASS_THEFT_PREVENTION = "schneckenhaus.bypass_theft_prevention";
    public static final String CREATE_SHELL_COPIES = "schneckenhaus.create_shell_copies";

    public static boolean require(final Permissible permissible, final String permission) {
        if (!permissible.hasPermission(permission)) {
            if (!(permissible instanceof final CommandSender sender)) {
                return false;
            }

            sender.sendMessage(Component.text(t("error_permission", permission), NamedTextColor.RED));
            return false;
        }
        return true;
    }

    private Permissions() { }
}
