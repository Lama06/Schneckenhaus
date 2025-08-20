package io.github.lama06.schneckenhaus.shell;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ShellFactory extends ConstantsHolder {
    public abstract String getId();

    public abstract Message getName();

    public abstract ShellBuilder newBuilder();

    public abstract boolean getCraftingResult(ShellBuilder builder, CraftingInput input);

    public void addCommandParameters(ParameterCommandBuilder builder) {
        builder.parameter("owner", ArgumentTypes.playerProfiles());
        builder.parameter("world", ArgumentTypes.world());
        builder.parameter(Commands.literal("permission")
            .then(Commands.literal("enter")
                .then(Commands.argument("enterPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)))
            )
            .then(Commands.literal("build")
                .then(Commands.argument("buildPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)))
            )
        );
    }

    public boolean parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        if (parameters.get("owner") instanceof PlayerProfileListResolver ownerResolver) {
            Collection<PlayerProfile> owner = ownerResolver.resolve(context.getSource());
            builder.setOwner(owner.iterator().next().getId());
        }
        if (parameters.get("world") instanceof World world) {
            builder.setWorld(world);
        }
        if (parameters.get("enterPermissionMode") instanceof ShellPermissionMode enterPermissionMode) {
            builder.setEnterPermissionMode(enterPermissionMode);
        }
        if (parameters.get("buildPermissionMode") instanceof ShellPermissionMode buildPermissionMode) {
            builder.setBuildPermissionMode(buildPermissionMode);
        }
        return true;
    }

    public boolean deserializeConfig(ShellBuilder builder, Map<?, ?> config) {
        if (config.get("enter_permission_mode") instanceof String string) {
            try {
                builder.setEnterPermissionMode(ShellPermissionMode.valueOf(string.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                logger.warn("ignoring invalid enter permission mode: {}", string, e);
            }
        }
        if (config.get("build_permission_mode") instanceof String string) {
            try {
                builder.setBuildPermissionMode(ShellPermissionMode.valueOf(string.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                logger.warn("ignoring invalid build permission mode: {}", string, e);
            }
        }
        return true;
    }

    protected abstract Material getItemType(ShellData data);

    protected TextColor getItemColor(ShellData data) {
        return NamedTextColor.WHITE;
    }

    protected List<Component> getItemLore(ShellData data) {
        return List.of();
    }

    public Integer getItemAnimationDelay(ShellData data) {
        return null;
    }

    public ItemStack createItem(ShellData data) {
        ItemStack item = new ItemStack(getItemType(data));
        item.editMeta(meta -> {
            String name = data.getName();
            if (name == null) {
                meta.customName(Message.SNAIL_SHELL.asComponent(getItemColor(data)));
            } else {
                meta.customName(Component.text(name, getItemColor(data)));
            }
            meta.lore(getItemLore(data));
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "not_stackable"), PersistentDataType.INTEGER, ThreadLocalRandom.current().nextInt());
        });
        InventoryUtil.removeDefaultFormatting(item);
        return item;
    }

    public abstract Shell loadShell(int id);
}
