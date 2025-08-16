package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public final class EventUtil {
    private EventUtil() { }

    /**
     * Like {@link PluginManager#registerEvents(Listener, Plugin)}, but also registers private methods from super classes.
     */
    public static void registerAll(Listener listener) {
        registerAll(listener, listener.getClass());
    }

    private static void registerAll(Listener listener, Class<?> listenerClass) {
        Method[] methods = listenerClass.getDeclaredMethods();
        for (Method method : methods) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) {
                continue;
            }
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1) {
                continue;
            }
            Class<?> parameterClass = parameters[0];
            if (!Event.class.isAssignableFrom(parameterClass)) {
                throw new RuntimeException("Invalid parameter type: " + method.getName());
            }
            method.setAccessible(true);
            Class<? extends Event> eventClass = parameterClass.asSubclass(Event.class);
            EventExecutor executor = new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        method.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        SchneckenhausPlugin.INSTANCE.getLogger().log(Level.SEVERE, "", e);
                    }
                }
            };
            Bukkit.getPluginManager().registerEvent(
                eventClass,
                listener,
                annotation.priority(),
                executor,
                SchneckenhausPlugin.INSTANCE,
                annotation.ignoreCancelled()
            );
        }
        Class<?> superclass = listenerClass.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return;
        }
        registerAll(listener, superclass);
    }
}