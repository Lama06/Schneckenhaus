package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.command.SchneckenhausCommand;
import io.github.lama06.schneckenhaus.config.ConfigManager;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import io.github.lama06.schneckenhaus.language.Language;
import io.github.lama06.schneckenhaus.language.Translator;
import io.github.lama06.schneckenhaus.shell.ShellManager;
import io.github.lama06.schneckenhaus.systems.Systems;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.logging.Level;

public final class SchneckenhausPlugin extends JavaPlugin implements Listener {
    private static final int BSTATS_ID = 21674;
    public static SchneckenhausPlugin INSTANCE;

    private ConfigManager config;
    private Translator translator;
    private DatabaseManager database;
    private WorldManager worlds;
    private ShellManager shellManager;
    private SchneckenhausCommand command;


    public SchneckenhausPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        try {
            Files.createDirectories(getDataPath());

            config = new ConfigManager();
            if (!config.load()) {
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            database = new DatabaseManager();
            if (!database.connect()) {
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            translator = new Translator();
            translator.loadConfig();

            worlds = new WorldManager();
            worlds.load();

            shellManager = new ShellManager();

            Permission.register();

            Systems.start();

            command = new SchneckenhausCommand();
            getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> command.register(event.registrar()));

            try {
                startBstats();
            } catch (RuntimeException exception) {
                getLogger().log(Level.WARNING, "Failed to start bStats", exception);
            }

            Bukkit.getPluginManager().registerEvents(this, this);
        } catch (RuntimeException | IOException e) {
            getSLF4JLogger().error("failed to enable Schneckenhaus plugin", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void startBstats() {
        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("custom_shell_types", () -> getPluginConfig().getCustom().isEmpty() ? "no" : "yes"));
        metrics.addCustomChart(new SingleLineChart("shells", shellManager::getTotalShellCount));
        metrics.addCustomChart(new SimplePie("language", () -> {
            Language language = getTranslator().getLanguage();
            if (language == null) {
                return "Default";
            }
            return language.getName();
        }));
    }

    public SchneckenhausConfig getPluginConfig() {
        return config.getConfig();
    }

    public ShellManager getShellManager() {
        return shellManager;
    }

    public Translator getTranslator() {
        return translator;
    }

    public Connection getDBConnection() {
        return database.getConnection();
    }

    public SchneckenhausCommand getCommand() {
        return command;
    }
}
