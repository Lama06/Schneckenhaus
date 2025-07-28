package io.github.lama06.schneckenhaus.language;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Translator {
    /**
     * Translate
     */
    public static String t(String key, Object... args) {
        return SchneckenPlugin.INSTANCE.getTranslator().translate(key, args);
    }

    private final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;

    private Map<Language, Map<String, String>> languages = new HashMap<>();

    private Language language; // or null if default
    private Map<String, String> overrides = new HashMap<>();

    public void load() {
        loadEmbeddedLanguages();
        if (!Files.exists(plugin.getDataPath().resolve("language.yml"))) {
            saveLanguageConfig();
        }
        boolean success = loadLanguageConfig();
        if (success) {
            saveLanguageConfig();
        }
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        saveLanguageConfig();
    }

    private void loadEmbeddedLanguages() {
        for (Language language : Language.values()) {
            YamlConfiguration languageFile =
                YamlConfiguration.loadConfiguration(plugin.getTextResourcePublic("lang/" + language.file));
            Map<String, String> translations = new HashMap<>();
            for (String key : languageFile.getKeys(false)) {
                translations.put(key, languageFile.getString(key));
            }
            languages.put(language, translations);
        }
    }

    private boolean loadLanguageConfig() {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(new File(plugin.getDataFolder(), "language.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getSLF4JLogger().error("FAILED TO LOAD language.yml!", e);
            return false;
        }

        String languageName = configuration.getString("language");
        this.language = Arrays.stream(Language.values()).filter(l -> l.id.equals(languageName)).findAny().orElse(null);

        for (String key : configuration.getKeys(false)) {
            if (key.equals("language")) {
                continue;
            }
            overrides.put(key, configuration.getString(key));
        }

        return true;
    }

    private void saveLanguageConfig() {
        String header = """
# If you enjoy the plugin, consider making a donation on PayPal: andreasprues36@gmail.com
# If you agree, your donation will be mentioned on the plugin's websites
# If you have translated the plugin into your native language, please send me the translation so that everyone profits.





# STEP 1: Select a base language:
language: %lang%
# Available options: default, %langs%





# STEP 2: If necessary, override certain messages.
# Remove the hash symbol # if you change a message. All lines starting with # will be ignored.""";
        header = header.replace("%lang%", language == null ? "default" : language.id);
        header = header.replace("%langs%", Arrays.stream(Language.values()).map(l -> l.id).collect(Collectors.joining(", ")));

        StringBuilder builder = new StringBuilder().append(header);

        for (String key : languages.get(Language.ENGLISH).keySet().stream().sorted().toList()) {
            builder.append("\n");
            if (!overrides.containsKey(key)) {
                builder.append("#");
            }
            builder.append(key).append(": \"");
            builder.append(getFormat(key));
            builder.append("\"");
        }

        try {
            Files.writeString(plugin.getDataPath().resolve("language.yml"), builder.toString());
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("Failed to save language.yml", e);
        }
    }

    private String getFormat(String key) {
        if (overrides.containsKey(key)) {
            return overrides.get(key);
        } else if (language != null && languages.get(language).containsKey(key)) {
            return languages.get(language).get(key);
        } else return languages.get(Language.ENGLISH).getOrDefault(key, key);
    }

    public String translate(String key, Object... args) {
        String result = getFormat(key);
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + (i + 1) + "}", args[i].toString());
        }
        return result;
    }
}
