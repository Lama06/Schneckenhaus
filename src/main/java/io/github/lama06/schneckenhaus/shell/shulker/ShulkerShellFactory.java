package io.github.lama06.schneckenhaus.shell.shulker;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.CommandParameter;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellFactory;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class ShulkerShellFactory extends SizedShellFactory {
    public static final ShulkerShellFactory INSTANCE = new ShulkerShellFactory();

    private ShulkerShellFactory() { }

    @Override
    public String getName() {
        return "shulker";
    }

    @Override
    public int getMinSize() {
        return 2;
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }

        ShulkerShellBuilder shulkerBuilder = (ShulkerShellBuilder) builder;

        DyeColor color = null;
        for (DyeColor possibleColor : DyeColor.values()) {
            if (input.remove(MaterialUtil.getColoredShulkerBox(possibleColor))) {
                color = possibleColor;
                break;
            }
        }
        if (color == null && input.remove(Material.SHULKER_BOX)) {
            color = DyeColor.PINK;
        }
        if (color == null) {
            return false;
        }
        shulkerBuilder.setColor(color);

        GlobalShulkerShellConfig config = getGlobalConfig();
        if (config.isRainbowMode() && input.remove(config.getRainbowIngredient())) {
            shulkerBuilder.setRainbow(true);
        }

        shulkerBuilder.setRainbowColors(Arrays.stream(DyeColor.values()).collect(Collectors.toSet()));

        return true;
    }

    @Override
    protected void addCommandParameters(List<CommandParameter> parameters) {
        super.addCommandParameters(parameters);
        parameters.add(CommandParameter.optional("color", new EnumArgumentType<>(DyeColor.class)));
        parameters.add(CommandParameter.optional("rainbow", BoolArgumentType.bool()));
    }

    @Override
    public ShulkerShellBuilder parseCommandParameters(
        CommandContext<CommandSourceStack> context,
        Map<String, ?> parameters
    ) throws CommandSyntaxException {
        ShulkerShellBuilder builder = (ShulkerShellBuilder) super.parseCommandParameters(context, parameters);
        if (parameters.get("color") instanceof DyeColor color) {
            builder.setColor(color);
        } else {
            DyeColor[] colors = DyeColor.values();
            builder.setColor(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
        }
        if (parameters.get("rainbow") instanceof Boolean bool) {
            builder.setRainbow(bool);
        }
        builder.setRainbowColors(Arrays.stream(DyeColor.values()).collect(Collectors.toSet()));
        return builder;
    }

    @Override
    public ShulkerShellBuilder deserializeConfig(Map<?, ?> config) {
        ShulkerShellBuilder builder = (ShulkerShellBuilder) super.deserializeConfig(config);

        if (config.get("color") instanceof String colorName) {
            DyeColor color = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
            builder.setColor(color);
        } else {
            builder.setColor(DyeColor.WHITE);
        }

        if (config.get("rainbow") instanceof Boolean rainbow) {
            builder.setRainbow(rainbow);
        }

        Set<DyeColor> rainbowColors = null;
        if (config.get("rainbow_colors") instanceof List<?> rainbowColorNames) {
            rainbowColors = rainbowColorNames.stream()
                .filter(name -> name instanceof String)
                .map(name -> (String) name)
                .map(name -> {
                    try {
                        return DyeColor.valueOf(name.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
        if (rainbowColors == null || rainbowColors.isEmpty()) {
            rainbowColors = Arrays.stream(DyeColor.values()).collect(Collectors.toSet());
        }
        builder.setRainbowColors(rainbowColors);

        return builder;
    }

    @Override
    public GlobalShulkerShellConfig getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getPluginConfig().getShulker();
    }

    @Override
    public ShulkerShellBuilder newBuilder() {
        return new ShulkerShellBuilder();
    }

    public DyeColor getCurrentColor(ShulkerShellData data) {
        if (!data.isRainbow()) {
            return data.getColor();
        }
        List<DyeColor> colorList = data.getRainbowColors().stream().sorted().toList();
        return colorList.get((Bukkit.getCurrentTick() / plugin.getPluginConfig().getShulker().getRainbowDelay()) % colorList.size());
    }

    @Override
    protected Material getItemType(ShellData data) {
        return MaterialUtil.getColoredShulkerBox(getCurrentColor((ShulkerShellData) data));
    }

    @Override
    protected TextColor getItemColor(ShellData data) {
        return TextColor.color(getCurrentColor((ShulkerShellData) data).getColor().asRGB());
    }

    @Override
    protected List<Component> getItemLore(ShellData data) {
        ShulkerShellData shulkerData = (ShulkerShellData) data;
        List<Component> lore = new ArrayList<>(super.getItemLore(data));
        if (shulkerData.isRainbow()) {
            lore.add(MiniMessage.miniMessage().deserialize("<rainbow>Rainbow"));
        }
        return lore;
    }

    @Override
    public Integer getItemAnimationDelay(ShellData data) {
        return plugin.getPluginConfig().getShulker().getRainbowDelay();
    }

    @Override
    public ShulkerShell loadShell(int id) {
        ShulkerShell shell = new ShulkerShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}
