package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.sized.SizedShellBuilder;

public final class ChestShellBuilder extends SizedShellBuilder implements ChestShellData {
    @Override
    public ChestShellFactory getFactory() {
        return ChestShellFactory.INSTANCE;
    }
}
