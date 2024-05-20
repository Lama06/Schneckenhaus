package io.github.lama06.schneckenhaus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public final class Permissions {
    public static final String CRAFT = "schneckenhaus.craft";
    public static final String ENTER = "schneckenhaus.enter";

    public static boolean require(final Permissible permissible, final String permission) {
        if (!permissible.hasPermission(permission)) {
            if (!(permissible instanceof final CommandSender sender)) {
                return false;
            }
            sender.spigot().sendMessage(new ComponentBuilder(
                    "You are not allowed to do this. Missing permission: " + permission
            ).color(ChatColor.RED).build());
            return false;
        }
        return true;
    }

    private Permissions() { }
}
