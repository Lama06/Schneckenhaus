package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class ListCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID uuid;
        if (args.length == 0) {
            Player player = Require.player(sender);
            if (player == null) {
                return;
            }
            uuid = player.getUniqueId();
        } else {
            uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        }

        sender.sendMessage(Component.text(t("cmd_list_load"), NamedTextColor.GREEN));
        SchneckenPlugin.INSTANCE.getWorld().getShellsByPlayer(uuid, ids -> {
            for (int id : ids) {
                sender.sendMessage(
                    Component.text(t("cmd_list_entry", id))
                        .clickEvent(ClickEvent.runCommand("/sh info " + id))
                        .hoverEvent(HoverEvent.showText(Component.text(t("cmd_list_hint_click"))))
                );
            }
            sender.sendMessage(Component.text(t("cmd_list_hint_click"), NamedTextColor.YELLOW));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
            new HelpCommand.Entry("<player>", t("cmd_list_help"))
        );
    }
}
