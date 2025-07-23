package io.github.lama06.schneckenhaus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record BuildProperties(boolean debug) {
    private static final String FILE = "build.properties";

    public static final BuildProperties FALLBACK = new BuildProperties(false);

    public static BuildProperties load() throws IOException {
        final Map<String, String> properties = getProperties();
        if (!properties.containsKey("debug")) {
            throw new IOException("Missing properties");
        }
        final boolean debug = Boolean.parseBoolean(properties.get("debug"));
        return new BuildProperties(debug);
    }

    private static Map<String, String> getProperties() throws IOException {
        final Map<String, String> properties = new HashMap<>();
        final String file = loadFile();
        final String[] lines = file.split("\n");
        for (final String line : lines) {
            final String[] components = line.split("=");
            if (components.length != 2) {
                throw new IOException("Invalid property: " + line);
            }
            properties.put(components[0], components[1]);
        }
        return properties;
    }

    private static String loadFile() throws IOException {
        try (final InputStream file = BuildProperties.class.getClassLoader().getResourceAsStream(FILE)) {
            final InputStreamReader reader = new InputStreamReader(file, StandardCharsets.UTF_8);
            final StringBuilder builder = new StringBuilder();
            int read;
            while ((read = reader.read()) != -1) {
                builder.append((char) read);
            }
            return builder.toString();
        }
    }
}
