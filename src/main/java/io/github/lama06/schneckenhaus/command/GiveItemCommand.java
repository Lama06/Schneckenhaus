package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class GiveItemCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", t("cmd_give_help")),
                new HelpCommand.Entry("<id>", t("cmd_give_help_id"))
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
            player.sendMessage(Component.text(t("error_inventory_full"), NamedTextColor.RED));
        }
    }
}
