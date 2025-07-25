package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellGlobalConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class NewShellTypeCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("<name> <template area> <ingredients>", t("cmd_new_type_help")));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 8) {
            sender.sendMessage(Component.text(t("cmd_error_missing_arguments"), NamedTextColor.RED));
            return;
        }
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (!player.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            sender.sendMessage(Component.text(t("cmd_new_type_wrong_world"), NamedTextColor.RED));
            return;
        }
        final Map<String, CustomShellGlobalConfig> customConfig = SchneckenPlugin.INSTANCE.getSchneckenConfig().custom;
        final String name = args[0];
        if (customConfig.containsKey(name)) {
            sender.sendMessage(Component.text(t("cmd_new_type_name_taken") + name, NamedTextColor.RED));
            return;
        }
        final BlockArea template = Require.blockArea(sender, Arrays.copyOfRange(args, 1, 7));
        if (template == null) {
            return;
        }
        final List<Material> ingredients = new ArrayList<>();
        for (final String ingredientArg : Arrays.copyOfRange(args, 7, args.length)) {
            final Material ingredient = Require.material(sender, ingredientArg);
            if (ingredient == null) {
                return;
            }
            ingredients.add(ingredient);
        }

        final CustomShellGlobalConfig newShell = new CustomShellGlobalConfig(true, ingredients, template);
        try {
            newShell.verify();
        } catch (final ConfigException e) {
            sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            return;
        }
        customConfig.put(name, newShell);

        SchneckenPlugin.INSTANCE.getRecipeManager().registerRecipes();
        SchneckenPlugin.INSTANCE.saveSchneckenConfig();


        Component message = Component.text()
            .append(Component.text(t("cmd_new_type_success")).color(NamedTextColor.GREEN))
            .appendNewline()
            .append(Component.text(t("cmd_new_type_hint")))
          .build();
        sender.sendMessage(message);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return List.of();
        }
        if (args.length <= 7) {
            return TabComplete.blockArea(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return TabComplete.material(args[args.length-1]);
    }
}
