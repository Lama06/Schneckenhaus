package io.github.lama06.schneckenhaus.shell.head;

import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellData;

import java.util.UUID;

public interface HeadShellData extends BuiltinShellData {
    UUID getHeadOwner();
}
