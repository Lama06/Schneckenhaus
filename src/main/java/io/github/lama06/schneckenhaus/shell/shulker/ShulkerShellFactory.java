package io.github.lama06.schneckenhaus.shell.shulker;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellFactory;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public final class ShulkerShellFactory extends SizedShellFactory {
    public static final ShulkerShellFactory INSTANCE = new ShulkerShellFactory();

    private ShulkerShellFactory() { }

    @Override
    public String getId() {
        return "shulker";
    }

    @Override
    public Message getName() {
        return Message.SHULKER;
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

        ShulkerShellConfig config = getConfig();
        if (config.isRainbowMode() && input.remove(config.getRainbowIngredient())) {
            shulkerBuilder.setRainbow(true);
        }

        shulkerBuilder.setRainbowColors(Arrays.stream(DyeColor.values()).collect(Collectors.toSet()));

        return true;
    }

    @Override
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("color", new EnumArgumentType<>(DyeColor.class));
        builder.parameter("rainbow", BoolArgumentType.bool());
    }

    @Override
    public boolean parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        if (!super.parseCommandParameters(builder, context, parameters)) {
            return false;
        }
        ShulkerShellBuilder shulkerBuilder = (ShulkerShellBuilder) builder;
        if (parameters.get("color") instanceof DyeColor color) {
            shulkerBuilder.setColor(color);
        } else {
            shulkerBuilder.setColor(EnumUtil.getRandom(DyeColor.class));
        }
        if (parameters.get("rainbow") instanceof Boolean rainbow) {
            shulkerBuilder.setRainbow(rainbow);
        }
        shulkerBuilder.setRainbowColors(Arrays.stream(DyeColor.values()).collect(Collectors.toSet()));
        return true;
    }

    @Override
    public boolean deserializeConfig(ShellBuilder builder, Map<?, ?> config) {
        if (!super.deserializeConfig(builder, config)) {
            return false;
        }
        ShulkerShellBuilder shulkerBuilder = (ShulkerShellBuilder) builder;

        if (config.get("color") instanceof String colorName) {
            DyeColor color = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
            shulkerBuilder.setColor(color);
        } else {
            shulkerBuilder.setColor(DyeColor.WHITE);
        }

        if (config.get("rainbow") instanceof Boolean rainbow) {
            shulkerBuilder.setRainbow(rainbow);
        }

        Set<DyeColor> rainbowColors = null;
        if (config.get("rainbow_colors") instanceof List<?> rainbowColorNames) {
            rainbowColors = rainbowColorNames.stream()
                .filter(name -> name instanceof String).map(name -> (String) name)
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
        shulkerBuilder.setRainbowColors(rainbowColors);

        return true;
    }

    @Override
    public ShulkerShellConfig getConfig() {
        return SchneckenhausPlugin.INSTANCE.getPluginConfig().getShulker();
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
        if (colorList.isEmpty()) {
            return data.getColor();
        }
        int delayTicks = plugin.getPluginConfig().getShulker().getRainbowDelay();
        return colorList.get((Bukkit.getCurrentTick() / delayTicks) % colorList.size());
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
            lore.add(MiniMessage.miniMessage().deserialize("<rainbow>" + Message.RAINBOW));
        }
        return lore;
    }

    @Override
    public Integer getItemAnimationDelay(ShellData data) {
        ShulkerShellData shulkerData = (ShulkerShellData) data;
        if (!shulkerData.isRainbow()) {
            return null;
        }
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
