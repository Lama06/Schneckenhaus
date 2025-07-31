package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.IntStream;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class TeleportOtherPlayerCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text(t("cmd_error_missing_arguments"), NamedTextColor.RED));
            return;
        }
        Player player = Require.player(sender, args[0]);
        if (player == null) {
            return;
        }
        Integer index = 1;
        if (args.length >= 2) {
            index = Require.integer(sender, args[1]);
            if (index == null) {
                return;
            }
        }
        final int indexFinal = index - 1;
        sender.sendMessage(Component.text(t("cmd_tp_other_loading"), NamedTextColor.GREEN));
        SchneckenPlugin.INSTANCE.getWorld().getShellsByPlayer(player.getUniqueId(), ids -> {
            if (ids.isEmpty()) {
                sender.sendMessage(Component.text(t("cmd_tp_other_no_shell", NamedTextColor.RED)));
                return;
            }
            int id = ids.get(Math.max(0, Math.min(ids.size() - 1, indexFinal)));
            Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
            if (shell == null) {
                return;
            }
            SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
            if (schneckenPlayer.isInside(shell.getPosition())) {
                sender.sendMessage(Component.text(t("cmd_tp_other_done", NamedTextColor.GREEN)));
                return;
            }
            schneckenPlayer.pushPreviousLocation(player.getLocation());
            player.teleport(shell.getSpawnLocation());
            sender.sendMessage(Component.text(t("cmd_tp_other_done", NamedTextColor.GREEN)));
        });
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
            new HelpCommand.Entry("<player>", "Teleport player to their first snail shell"),
            new HelpCommand.Entry("<player> <i>", "Teleport player to their i-th snail shell")
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.length == 2) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).toList();
        }
        return List.of();
    }
}
