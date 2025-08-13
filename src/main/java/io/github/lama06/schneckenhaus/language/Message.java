package io.github.lama06.schneckenhaus.language;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;

import java.util.Locale;

public enum Message implements ComponentLike {
    SNAIL_SHELL("Snail Shell", "Schneckenhaus"),
    ID("ID", "ID"),
    TYPE("Type", "Typ"),
    CREATOR("Creator", "Ersteller"),
    CREATION_TIME("Creation Time", "Erstellungsdatum"),
    OWNERS("Owners", "Eigentümer"),
    UNKNOWN_PLAYER("unknown player", "unbekannter Spieler"),
    WORLD("World", "Welt"),
    GRID_POSITION("Grid Position", "Position im Häuserraster"),
    POSITION_1("Position 1", "Position 1"),
    POSITION_2("Position 2", "Position 2"),
    ENTER_PERMISSION("Enter Permission", "Zutrittsrecht"),
    BUILD_PERMISSION("Build Permission", "Baurecht"),
    ACCESS_COUNT("Access Count", "Zutrittszahl"),
    SIZE("Size", "Größe"),
    COLOR("Color", "Farbe"),
    RAINBOW("Rainbow", "Regenbogen"),
    RAINBOW_MODE("Rainbow Mode", "Regenbogenmodus"),
    RAINBOW_COLORS("Rainbow Colors", "Regenbogenfarben"),
    TEMPLATE("Template", "Vorlage"),

    ON("on", "an"),
    OFF("off", "aus"),
    SELECTED("Selected", "Ausgewählt"),
    CLICK_TO_ENABLE("Click to enable", "Zum Einschalten klicken"),
    CLICK_TO_DISABLE("Click to disable", "Zum Ausschalten klicken"),
    CLICK_TO_SELECT("Click to select", "Zum Auswählen klicken"),
    CLICK_TO_REMOVE("Click to remove", "Zum Entfernen klicken"),
    CLICK_TO_EDIT("Click to edit", "Zum Bearbeiten klicken"),
    BACK("Back", "Zurück"),
    CONFIRM_ACTION("Confirm: {1}", "Bestätigen: {1}"),
    CANCEL("Cancel", "Abbrechen"),
    CONFIRM("Confirm", "Bestätigen"),
    COST("Cost", "Kosten"),
    ERROR_NOT_AFFORDABLE("You can't afford this", "Das kannst du dir nicht leisten"),
    ERROR_PERMISSION("Missing permission: {1}", "Fehlende Berechtigung: {1}"),

    EVERYBODY("Everybody", "Jeder"),
    BLACKLIST("Blacklist", "Negativliste"),
    WHITELIST("Whitelist", "Positivliste"),
    NOBODY("Nobody", "Keiner"),
    EDIT_WHITELIST("Edit whitelist", "Positivliste bearbeiten"),
    EDIT_BLACKLIST("Edit blacklist", "Negativliste bearbeiten"),
    WHITELIST_DISABLED("The whitelist is currently disabled", "Die Positivliste ist aktuell deaktiviert"),
    BLACKLIST_DISABLED("The blacklist is currently disabled", "Die Negativliste ist aktuell deaktiviert"),
    LOCK_SHELL_SUCCESS("You successfully locked this snail shell", "Du hast das Schneckenhaus erfolgreich abgeschlossen"),
    UNLOCK_SHELL_SUCCESS("You successfully unlocked this snail shell", "Du hast das Schneckenhaus erfolgreich aufgeschlossen"),
    ERROR_ENTER_PERMISSION("You are not allowed to enter this snail shell", "Du darfst dieses Schneckenhaus nicht betreten"),
    ERROR_STEAL_PERMISSION(
        "You are not allowed to break other player's snail shells",
        "Du darfst die Schneckenhäuser anderer Spieler nicht abbauen"
    ),

    ADD_PLAYER("Add player", "Spieler hinzufügen"),
    PLAYER_NAME_INPUT("Player Name Input", "Spielernameneingabe"),
    ADD_PLAYER_SUCCESS("{1} was successfully added", "{1} wurde erfolgreich hinzugefügt"),
    PLAYER_NOT_FOUND("{1} has never player on this server", "{1} hat nie auf diesem Server gespielt"),

    CLICK_TO_CREATE_SHELL_COPY("Click to create copy", "Zum Anfertigen einer Kopie klicken"),

    SNAIL_SHELL_NAME("Snail shell name: {1}", "Name des Schneckenhauses: {1}"),
    NAME_NOT_SET("Name: not set", "Name: nicht festgelegt"),
    CLICK_TO_CHANGE_NAME("Click to change name", "Zum Festlegen eines Namens klicken"),
    RENAME_SHELL_TITLE("Rename", "Umbenennen"),
    RENAME_SHELL_SUCCESS("Successfully renamed snail shell to {1}", "Namen des Schneckenhauses erfolgreich auf {1} geändert"),

    SIZE_UPGRADE("Size Upgrade", "Vergrößerung"),
    CURRENT_SIZE("Current size", "Aktuelle Größe"),
    SIZE_AFTER_UPGRADE("Upgraded size", "Neue Größe"),
    SIZE_UPGRADE_SUCCESS("Successfully upgraded size!", "Schneckenhaus erfolgreich vergrößert!"),

    WHITE("white", "weiß"),
    ORANGE("orange", "orange"),
    MAGENTA("magenta", "magenta"),
    LIGHT_BLUE("light blue", "hellblau"),
    YELLOW("yellow", "gelb"),
    LIME("lime", "hellgrün"),
    PINK("pink", "pink"),
    GRAY("gray", "grau"),
    LIGHT_GRAY("light gray", "hellgrau"),
    CYAN("cyan", "türkis"),
    PURPLE("purple", "lila"),
    BLUE("blue", "blau"),
    BROWN("brown", "brown"),
    GREEN("green", "grün"),
    RED("red", "rot"),
    BLACK("black", "black"),;

    public static Message getDyeColor(DyeColor color) {
        return valueOf(color.name());
    }

    public static Message getBool(boolean bool) {
        return bool ? ON : OFF;
    }

    public static Message getClickToEnableDisable(boolean enable) {
        return enable ? CLICK_TO_ENABLE : CLICK_TO_DISABLE;
    }

    public static Message getSelectedOrClickToSelect(boolean selected) {
        return selected ? SELECTED : CLICK_TO_SELECT;
    }

    private final String key;
    private final String english;
    private final String german;

    Message(String english, String german) {
        key = name().toLowerCase(Locale.ROOT);
        this.english = english;
        this.german = german;
    }

    public String toString(String... args) {
        return SchneckenPlugin.INSTANCE.getTranslator().translate(this, args);
    }

    @Override
    public String toString() {
        return SchneckenPlugin.INSTANCE.getTranslator().translate(this);
    }

    @Override
    public Component asComponent() {
        return Component.text(toString());
    }

    public Component asComponent(String... args) {
        return Component.text(toString(args));
    }

    public Component asComponent(TextColor color, String... args) {
        return Component.text(toString(args), color);
    }

    public String getKey() {
        return key;
    }

    public String getEnglish() {
        return english;
    }

    public String getGerman() {
        return german;
    }
}
