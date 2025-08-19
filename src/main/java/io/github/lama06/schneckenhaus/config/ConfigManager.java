package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public final class ConfigManager {
    private static final String FILE = "config.yml";
    private static final String HEADER = """
        # Schneckenhaus Plugin
        # Spigot: https://www.spigotmc.org/resources/schneckenhaus.116315/
        # GitHub: https://github.com/Lama06/Schneckenhaus
        # Discord: https://discord.com/invite/7cHfHAgGpY
        # PayPal: https://www.paypal.com/paypalme/andreasprues
        # If you enjoy the plugin, please consider making a donation on PayPal :)
        
        """;

    private final SchneckenhausPlugin plugin = SchneckenhausPlugin.INSTANCE;
    private final Logger logger = plugin.getSLF4JLogger();

    private SchneckenhausConfig config;

    public boolean load() {
        if (!Files.exists(getConfigFile())) {
            config = new SchneckenhausConfig();
            save();
            return true;
        }

        try {
            String text = Files.readString(getConfigFile());
            Yaml yaml = new Yaml();
            Map<?, ?> map = yaml.load(text);
            config = new SchneckenhausConfig();
            config.deserialize(map);
        } catch (Exception e) {
            logger.error("failed to load config", e);
            return false;
        }

        save();
        return true;
    }

    public void save() {
        Map<String, Object> map = this.config.serialize();
        DumperOptions options = new DumperOptions();
        options.setDereferenceAliases(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        Yaml yaml = new Yaml(options);
        String config = yaml.dump(map);
        config = HEADER + config;

        try {
            Files.writeString(getConfigFile(), config, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("failed to save config", e);
        }
    }

    private Path getConfigFile() {
        return plugin.getDataPath().resolve(FILE);
    }

    public SchneckenhausConfig getConfig() {
        return config;
    }
}
