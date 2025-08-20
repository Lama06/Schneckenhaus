package io.github.lama06.schneckenhaus.language;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class Translator extends ConstantsHolder {
    private static final String LANGUAGE_FILE = "language.yml";
    private static final String LANGUAGE_FILE_HEADER = """
        # If you like the plugin, consider making a donation: https://www.paypal.com/paypalme/andreasprues
        
        language: %lang%
        # Available languages: default, %langs%
        
        # Override individual messages:
        # Remove the hash symbol # if you change a message.
        """;

    private Language language; // null if default
    private final Map<Message, String> overrides = new HashMap<>();

    public void loadConfig() {
        if (!Files.exists(getLanguageFile())) {
            saveConfig();
            return;
        }

        Map<?, ?> config;
        try {
            String text = Files.readString(getLanguageFile());
            Yaml yaml = new Yaml();
            config = yaml.load(text);
        } catch (Exception e) {
            logger.error("failed to load {}", LANGUAGE_FILE, e);
            return;
        }

        if (!(config.get("language") instanceof String languageName)) {
            logger.error("no language name specified in {}", LANGUAGE_FILE);
            return;
        }
        language = Arrays.stream(Language.values()).filter(l -> l.getId().equalsIgnoreCase(languageName)).findAny().orElse(null);
        if (language == null) {
            logger.error("invalid language name: {}", languageName);
            return;
        }

        for (Object key : config.keySet()) {
            if (!(key instanceof String messageName)) {
                continue;
            }
            Message message;
            try {
                message = Message.valueOf(messageName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (!(config.get(key) instanceof String value)) {
                continue;
            }
            overrides.put(message, value);
        }

        saveConfig();
    }

    private void saveConfig() {
        String header = LANGUAGE_FILE_HEADER;
        header = header.replace("%lang%", language == null ? "default" : language.getId());
        header = header.replace("%langs%", Arrays.stream(Language.values()).map(Language::getId).collect(Collectors.joining(", ")));

        StringBuilder builder = new StringBuilder().append(header);

        for (Message message : Message.values()) {
            builder.append("\n");
            if (!overrides.containsKey(message)) {
                builder.append("#");
            }
            builder.append(message.getKey()).append(": \"");
            builder.append(translate(message));
            builder.append("\"");
        }

        try {
            Files.writeString(getLanguageFile(), builder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("failed to save {}", LANGUAGE_FILE, e);
        }
    }

    private Path getLanguageFile() {
        return plugin.getDataPath().resolve(LANGUAGE_FILE);
    }

    public String translate(Message message) {
        if (overrides.containsKey(message)) {
            return overrides.get(message);
        }
        return switch (language) {
            case null -> message.getEnglish();
            case ENGLISH -> message.getEnglish();
            case GERMAN -> message.getGerman();
        };
    }

    public String translate(Message message, Object... args) {
        String translation = translate(message);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + (i + 1) + "}", args[i].toString());
        }
        return translation;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        saveConfig();
    }
}
