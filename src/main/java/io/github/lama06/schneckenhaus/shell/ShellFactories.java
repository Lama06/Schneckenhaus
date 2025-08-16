package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.shell.chest.ChestShellFactory;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellFactory;
import io.github.lama06.schneckenhaus.shell.head.HeadShellFactory;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ShellFactories {
    private static final Map<String, ShellFactory> FACTORIES = new HashMap<>();

    static {
        registerFactory(ShulkerShellFactory.INSTANCE);
        registerFactory(ChestShellFactory.INSTANCE);
        registerFactory(HeadShellFactory.INSTANCE);
        registerFactory(CustomShellFactory.INSTANCE);
    }

    private static void registerFactory(ShellFactory factory) {
        FACTORIES.put(factory.getId(), factory);
    }

    public static ShellFactory getByName(String name) {
        return FACTORIES.get(name);
    }

    public static Collection<ShellFactory> getFactories() {
        return FACTORIES.values();
    }
}
