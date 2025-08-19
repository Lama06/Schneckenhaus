package io.github.lama06.schneckenhaus.language;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;

import java.util.Locale;

public enum Message implements ComponentLike {
    SNAIL_SHELL("Snail Shell", "Schneckenhaus"),
    ID("ID", "ID"),
    TAGS("Tags", "Schlagwörter"),
    TYPE("Type", "Typ"),
    CHEST("Chest", "Truhe"),
    SHULKER("Shulker Box", "Shulker-Kiste"),
    HEAD("Head", "Kopf"),
    CUSTOM("Custom", "Benutzerdefiniert"),
    CREATOR("Creator", "Ersteller"),
    CREATION_TIME("Creation Time", "Erstellungsdatum"),
    OWNERS("Owners", "Eigentümer"),
    UNKNOWN_PLAYER("unknown player", "unbekannter Spieler"),
    WORLD("World", "Welt"),
    GRID_POSITION("Grid Position", "Position im Häuserraster"),
    POSITION_1("Position 1", "Position 1"),
    POSITION_2("Position 2", "Position 2"),
    AREA("Area", "Bereich"),
    ENTER_PERMISSION("Enter Permission", "Zutrittsrecht"),
    BUILD_PERMISSION("Build Permission", "Baurecht"),
    ACCESS_COUNT("Access Count", "Zutrittszahl"),
    SIZE("Size", "Größe"),
    COLOR("Color", "Farbe"),
    RAINBOW("Rainbow", "Regenbogen"),
    RAINBOW_MODE("Rainbow Mode", "Regenbogenmodus"),
    RAINBOW_COLORS("Rainbow Colors", "Regenbogenfarben"),
    WOOD("Wood", "Holz"),
    TEMPLATE("Template", "Vorlage"),

    ON("on", "an"),
    OFF("off", "aus"),
    EMPTY("empty", "leer"),
    SELECTED("Selected", "Ausgewählt"),
    CLICK_TO_ENABLE("Click to enable", "Zum Einschalten klicken"),
    CLICK_TO_DISABLE("Click to disable", "Zum Ausschalten klicken"),
    CLICK_TO_SELECT("Click to select", "Zum Auswählen klicken"),
    CLICK_TO_COPY("Click to copy", "Zum Kopieren klicken"),
    CLICK_FOR_DETAILS("Click for details", "Zum Anzeigen von Details klicken"),
    CLICK_TO_REMOVE("Click to remove", "Zum Entfernen klicken"),
    CLICK_TO_ADD("Click to add", "Zum Hinzufügen klicken"),
    CLICK_TO_EDIT("Click to edit", "Zum Bearbeiten klicken"),
    CLICK_HERE("Click here", "Hier klicken"),
    TELEPORT("Teleport", "Teleportieren"),
    OPEN_MENU("Open menu", "Menü öffnen"),
    GET_ITEM("Get item", "Item erstellen"),
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

    COMMAND_ERROR_NOT_PLAYER("Only players can use this command", "Nur Spieler können diesen Befehl verwenden"),
    INVALID_SHELL_TYPE("Invalid shell type: {1}", "Unbekannter Schneckenhaustyp: {1}"),
    SELECT_SHELLS_SUCCESS("Selected {1} shells", "{1} Schneckenhäuser ausgewählt"),
    CREATE_SHELL_SUCCESS("Successfully created shell with ID {1}", "Schneckenhaus mit der ID {1} erfolgreich erstellt"),

    SELECTOR_SELECTION_DESCRIPTION("Result of /sh select ...", "Ergbnis von /sh select ..."),
    SELECTOR_HERE_DESCRIPTION("Refers to the shell at your location", "Bezieht sich auf das Schneckenhaus bei deiner Position"),
    INVALID_SELECTOR("Invalid shell selector: {1}", "Ungültige Schneckenhausauswahl: {1}"),
    SELECTOR_ERROR_NOT_AT_SHELL("There is no shell at your location", "Du befindest dich nicht in einem Schneckenhaus"),
    SELECTOR_ERROR_EMPTY_SELECTION("Your selection is empty. Use /sh select ...", "Keine Schneckenhäuser sind ausgewählt. Verwende /sh select ..."),
    SELECTOR_ERROR_INVALID_ID("Shell not found: {1}", "Schneckenhaus nicht gefunden: {1}"),

    SELECTION("Selection", "Auswahl"),
    CLEAR_SELECTION_SUCCESS("Your selection was cleared", "Deine Auswahl wurde gelöscht"),

    TAG_ADD_SUCCESS("Tag {1} was added to {2} shells", "{2} Schneckenhäuser wurden mit {1} verschlagwortet"),
    TAG_REMOVE_SUCCESS("Tag {1} was removed from {2} shells", "Schlagwort {1} von {2} Schneckenhäusern entfernt"),

    ALL_TIME_COUNT("All time snail shell count", "Gesamtzahl aller jemals erstellter Schneckenhäuser"),
    CURRENT_COUNT("Current snail shell count", "Aktuelle Anzahl an Schneckenhäusern"),
    WORLD_COUNT("There are {1} snail shells in {2}", "Es gibt {1} Schneckenhäuser in {2}"),

    DELETE("Delete", "Löschen"),
    DELETE_SUCCESS("Successfully deleted {1} snail shell(s)", "{1} Schneckenhaus/-häuser erfolgreich gelöscht"),

    INVALID_CUSTOM_SHELL_TYPE("Unknown custom shell type: {1}", "Unbekannter Schneckenhaustyp: {1}"),
    CUSTOM_SHELL_TYPE_NAME_TAKEN("A custom shell type with this name already exists", "Es gibt bereits einen Schneckenhaustyp mit diesem Namen"),
    ADD_CUSTOM_SHELL_TYPE_SUCCESS(
        "Successfully added a new snail shell type! You can edit it in the config file or using /sh custom edit",
        "Neuen Schneckenhaustyp erfolgreich hinzugefügt! Du kannst ihn in der Konfigurationsdatei oder mit /sh custom edit bearbeiten"
    ),

    JOIN_DISCORD("Join the plugin's Discord server: {1}", "Tritt dem Discord-Server des Plugins bei: {1}"),

    CUSTOM_SHELL_EXPORT_SUCCESS("Successfully exported custom shell type", "Schneckenhaustyp erfolgreich exportiert"),
    CUSTOM_SHELL_EXPORT_FAIL("Error white exporting, look in the console", "Fehler beim Exportieren, schau in der Konsole nach"),

    CUSTOM_SHELL_IMPORT_SUCCESS("Successfully imported custom shell type", "Schneckenhaustyp erfolgreich importiert"),
    CUSTOM_SHELL_IMPORT_FAIL("Error white importing, look in the console", "Fehler beim Importieren, schau in der Konsole nach"),

    LANGUAGE_CHANGE_SUCCESSFUL("Changed language to {1}", "Sprache geändert auf {1}"),

    OAK("Oak", "Eiche"),
    SPRUCE("Spruce", "Fichte"),
    BIRCH("Birch", "Birke"),
    JUNGLE("Jungle", "Tropen"),
    ACACIA("Acacia", "Akazie"),
    DARK_OAK("Dark Oak", "Schwarzeiche"),
    MANGROVE("Mangrove", "Mangrove"),
    CHERRY("Cherry", "Kirsche"),
    PALE_OAK("Pale Oak", "Blasseiche"),
    CRIMSON("Crimson", "Karmesin"),
    WARPED("Warped", "Wirr"),

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
    BLACK("black", "black");

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

    public String toString(Object... args) {
        return SchneckenhausPlugin.INSTANCE.getTranslator().translate(this, args);
    }

    @Override
    public String toString() {
        return SchneckenhausPlugin.INSTANCE.getTranslator().translate(this);
    }

    @Override
    public Component asComponent() {
        return Component.text(toString());
    }

    public Component asComponent(Object... args) {
        return Component.text(toString(args));
    }

    public Component asComponent(TextColor color, Object... args) {
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
