package io.github.lama06.schneckenhaus.language;

public enum Language {
    ENGLISH("english", "English", "en.yml"),
    GERMAN("german", "Deutsch", "de.yml");

    public final String id;
    public final String name;
    public final String file;

    Language(String id, String name, String file) {
        this.id = id;
        this.name = name;
        this.file = file;
    }
}
