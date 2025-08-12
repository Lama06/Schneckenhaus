package io.github.lama06.schneckenhaus.shell;

import java.util.UUID;

public interface ShellData {
    String getName();

    ShellCreationType getCreationType();

    UUID getCreator();
}
