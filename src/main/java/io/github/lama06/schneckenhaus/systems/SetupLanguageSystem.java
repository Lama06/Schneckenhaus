package io.github.lama06.schneckenhaus.systems;

import com.destroystokyo.paper.ClientOption;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.language.Language;
import io.github.lama06.schneckenhaus.language.Translator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Locale;

public class SetupLanguageSystem implements Listener {
    @EventHandler
    private void on(PlayerJoinEvent event) {
        Translator translator = SchneckenPlugin.INSTANCE.getTranslator();
        if (translator.getLanguage() != null) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            return;
        }
        Locale systemLocale = Locale.getDefault();
        String clientLocale = event.getPlayer().getClientOption(ClientOption.LOCALE);
        if (clientLocale.toLowerCase().startsWith("de_") || systemLocale.toString().toLowerCase().startsWith("de_")) {
            translator.setLanguage(Language.GERMAN);
        }
    }
}
