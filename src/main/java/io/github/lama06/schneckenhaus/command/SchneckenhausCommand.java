package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.command.debug.DebugCommand;
import io.github.lama06.schneckenhaus.command.select.SelectionCommand;
import io.github.lama06.schneckenhaus.command.select.SelectCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.*;

public final class SchneckenhausCommand {
    private final Map<UUID, List<Integer>> selections = new HashMap<>();
    private List<Integer> consoleSelection = List.of();

    public void register(Commands commands) {
        commands.register(
            Commands.literal("schneckenhaus")
                .then(new SelectCommand().create())
                .then(new CreateCommand().create())
                .then(new InfoCommand().create())
                .then(new SelectionCommand().create())
                .then(new MenuCommand().create())
                .then(new TeleportCommand().create())
                .then(new TagCommand().create())
                .then(new ItemCommand().create())
                .then(new CountCommand().create())
                .then(new DeleteCommand().create())
                .then(new DebugCommand().create())
                .build(),
            Set.of("sh")
        );
    }

    public List<Integer> getSelection(CommandSourceStack source) {
        if (source.getExecutor() instanceof Player player) {
            return selections.getOrDefault(player.getUniqueId(), List.of());
        }
        if (source.getSender() instanceof Player player) {
            return selections.getOrDefault(player.getUniqueId(), List.of());
        }
        return consoleSelection;
    }

    public void setSelection(CommandSourceStack source, List<Integer> selection) {
        Objects.requireNonNull(selection);
        if (source.getExecutor() instanceof Player player) {
            selections.put(player.getUniqueId(), selection);
        }
        if (source.getSender() instanceof Player player) {
            selections.put(player.getUniqueId(), selection);
        }
        consoleSelection = selection;
    }

    public Map<UUID, List<Integer>> getSelections() {
        return selections;
    }

    public List<Integer> getConsoleSelection() {
        return consoleSelection;
    }

    public void setConsoleSelection(List<Integer> consoleSelection) {
        this.consoleSelection = consoleSelection;
    }
}
