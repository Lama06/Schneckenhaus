package io.github.lama06.schneckenhaus.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Objects;

public interface ShellSelector {
    List<Shell> resolve(CommandSourceStack source) throws CommandSyntaxException;

    enum Here implements ShellSelector {
        INSTANCE;

        private static final SimpleCommandExceptionType NOT_AT_SHELL =
            new SimpleCommandExceptionType(Message.SELECTOR_ERROR_NOT_AT_SHELL::toString);

        @Override
        public List<Shell> resolve(CommandSourceStack source) throws CommandSyntaxException {
            Entity executor = source.getExecutor();
            if (executor == null) {
                throw NOT_AT_SHELL.create();
            }
            Shell shell = SchneckenhausPlugin.INSTANCE.getShellManager().getShellAt(executor);
            if (shell == null) {
                throw NOT_AT_SHELL.create();
            }
            return List.of(shell);
        }
    }

    enum Selection implements ShellSelector {
        INSTANCE;

        private static final SimpleCommandExceptionType EMPTY_SELECTION =
            new SimpleCommandExceptionType(Message.SELECTOR_ERROR_EMPTY_SELECTION::toString);

        @Override
        public List<Shell> resolve(CommandSourceStack source) throws CommandSyntaxException {
            SchneckenhausPlugin plugin = SchneckenhausPlugin.INSTANCE;
            List<Integer> selection = plugin.getCommand().getSelection(source);
            List<Shell> shells = selection.stream()
                .map(plugin.getShellManager()::getShell)
                .filter(Objects::nonNull)
                .toList();
            if (shells.isEmpty()) {
                throw EMPTY_SELECTION.create();
            }
            return shells;
        }
    }

    record Id(int id) implements ShellSelector {
        private static final DynamicCommandExceptionType INVALID_ID =
            new DynamicCommandExceptionType(selector -> () -> Message.SELECTOR_ERROR_INVALID_ID.toString(selector));

        @Override
        public List<Shell> resolve(CommandSourceStack source) throws CommandSyntaxException {
            Shell shell = SchneckenhausPlugin.INSTANCE.getShellManager().getShell(id);
            if (shell == null) {
                throw INVALID_ID.create(id);
            }
            return List.of(shell);
        }
    }
}
