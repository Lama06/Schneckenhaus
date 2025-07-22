package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class GiveItemCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", "Gives you an item to teleport to the snail shell at your location"),
                new HelpCommand.Entry("<id>", "Gives you an item to teleport to the specified snail shell")
        );
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        final Shell<?> shell = Require.shell(sender, args.length == 1 ? args[0] : null);
        if (shell == null) {
            return;
        }
        if (!player.getInventory().addItem(shell.createItem()).isEmpty()) {
            player.sendMessage(Component.text("Your inventory is full", NamedTextColor.RED));
        }
    }
}
