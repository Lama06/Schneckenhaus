package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.command.MultiplexerCommand;

public final class DebugCommand extends MultiplexerCommand {
    public DebugCommand() {
        addSubCommand("createShells", new CreateShellsCommand());
        addSubCommand("viewData", new ViewDataCommand());
        addSubCommand("craft", new CraftCommand());
    }
}
