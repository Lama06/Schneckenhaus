package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.language.Message;

public enum ShellCreationType {
    COMMAND(Message.COMMAND),
    CRAFTING(Message.CRAFTING),
    HOME(Message.HOME);

    private final Message message;

    ShellCreationType(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
