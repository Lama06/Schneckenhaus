package io.github.lama06.schneckenhaus.shell;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.CommandParameter;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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
import org.slf4j.Logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ShellFactory {
    protected final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
    protected final Connection connection = plugin.getDBConnection();
    protected final Logger logger = plugin.getSLF4JLogger();
    protected final SchneckenhausConfig config = plugin.getPluginConfig();

    public abstract String getName();

    public abstract ShellBuilder newBuilder();

    public abstract boolean getCraftingResult(ShellBuilder builder, CraftingInput input);

    protected void addCommandParameters(List<CommandParameter> parameters) {
        parameters.add(CommandParameter.optional("owner", ArgumentTypes.playerProfiles()));
        parameters.add(CommandParameter.optional("world", ArgumentTypes.world()));
        parameters.add(CommandParameter.optional("enterPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)));
        parameters.add(CommandParameter.optional("buildPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)));
    }

    public final List<CommandParameter> getCommandParameters() {
        List<CommandParameter> parameters = new ArrayList<>();
        addCommandParameters(parameters);
        return parameters;
    }

    public ShellBuilder parseCommandParameters(
        CommandContext<CommandSourceStack> context,
        Map<String, ?> parameters
    ) throws CommandSyntaxException {
        ShellBuilder builder = newBuilder();
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
        return builder;
    }

    public ShellBuilder deserializeConfig(Map<?, ?> config) {
        ShellBuilder builder = newBuilder();
        builder.setEnterPermissionMode(ShellPermissionMode.EVERYBODY);
        builder.setBuildPermissionMode(ShellPermissionMode.EVERYBODY);
        return builder;
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
