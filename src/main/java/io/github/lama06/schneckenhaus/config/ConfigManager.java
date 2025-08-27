package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public final class ConfigManager extends ConstantsHolder {
    private static final String FILE = "config.yml";
    private static final String HEADER = """
        # Schneckenhaus Plugin by Lama06
        
        # Documentation (config, commands, permissions etc.): https://github.com/Lama06/Schneckenhaus#documentation
        
        # Spigot: https://www.spigotmc.org/resources/schneckenhaus.116315/
        # Discord (support, feedback): https://discord.com/invite/7cHfHAgGpY
        
        # PayPal: https://www.paypal.com/paypalme/andreasprues
        # If you enjoy the plugin, please consider making a donation :)
        
        """;

    private SchneckenhausConfig config;

    public boolean load() {
        if (!Files.exists(getConfigFile())) {
            logger.info(FILE + " not found, creating default config");
            config = new SchneckenhausConfig();
            save();
            return true;
        }

        try {
            logger.info("loading config...");
            String text = Files.readString(getConfigFile());
            Yaml yaml = new Yaml();
            Map<?, ?> map = yaml.load(text);
            config = new SchneckenhausConfig();
            config.deserialize(map);
            save();
            return true;
        } catch (Exception e) {
            logger.error("failed to load config", e);
            return false;
        }
    }

    public void save() {
        Map<String, Object> map = config.serialize();
        DumperOptions options = new DumperOptions();
        options.setDereferenceAliases(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        Yaml yaml = new Yaml(new Representer(options) {
            @Override
            protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
                // make custom.block_restrictions more readable
                if (mapping.size() >= 100) {
                    flowStyle = DumperOptions.FlowStyle.FLOW;
                }
                return super.representMapping(tag, mapping, flowStyle);
            }
        }, options);
        String text = yaml.dump(map);
        text = HEADER + text;

        try {
            Files.writeString(getConfigFile(), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
