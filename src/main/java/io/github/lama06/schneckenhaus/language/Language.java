package io.github.lama06.schneckenhaus.language;

public enum Language {
    ENGLISH("english", "English"),
    GERMAN("german", "Deutsch");

    private final String id;
    private final String name;

    Language(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
