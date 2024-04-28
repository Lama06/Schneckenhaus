package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.shell.chest.ChestShellFactory;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ShellFactories {
    private static final Map<String, ShellFactory<?>> FACTORIES = new HashMap<>();

    static {
        registerFactory(ShulkerShellFactory.INSTANCE);
        registerFactory(ChestShellFactory.INSTANCE);
    }

    public static ShellFactory<?> getByName(final String name) {
        return FACTORIES.get(name);
    }

    public static Collection<ShellFactory<?>> getFactories() {
        return FACTORIES.values();
    }

    private static void registerFactory(final ShellFactory<?> factory) {
        FACTORIES.put(factory.getName(), factory);
    }
}
