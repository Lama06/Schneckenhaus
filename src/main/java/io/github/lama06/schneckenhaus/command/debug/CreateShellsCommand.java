package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.Command;
import io.github.lama06.schneckenhaus.command.Require;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellConfig;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class CreateShellsCommand extends Command {
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        final int ITERATIONS = 10;
        final int COUNT_PER_ITERATION = 10;
        final RandomGenerator rnd = ThreadLocalRandom.current();
        for (int i = 0; i < ITERATIONS; i++) {
            final int finalI = i;
            Bukkit.getScheduler().runTaskLater(SchneckenPlugin.INSTANCE, () -> {
                player.sendMessage(Component.text(finalI + 1, NamedTextColor.GREEN));
                for (int j = 0; j < COUNT_PER_ITERATION; j++) {
                    final ShulkerShellFactory factory = ShulkerShellFactory.INSTANCE;
                    final int size = factory.getMinSize() + rnd.nextInt(factory.getMaxSize() - factory.getMinSize() + 1);
                    final DyeColor[] dyeColors = DyeColor.values();
                    final DyeColor color = dyeColors[rnd.nextInt(dyeColors.length)];
                    SchneckenPlugin.INSTANCE.getWorld().createShell(factory, player, new ShulkerShellConfig(size, color));
                }
            }, i);
        }
    }
}
