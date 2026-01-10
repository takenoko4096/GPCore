package com.gmail.subnokoii78.gpcore.events;

import com.gmail.subnokoii78.gpcore.files.PluginConfigLoader;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PluginConfigUpdateEvent implements IEvent {
    private final PluginConfigLoader loader;

    public PluginConfigUpdateEvent(PluginConfigLoader loader) {
        this.loader = loader;
    }

    public PluginConfigLoader getLoader() {
        return loader;
    }

    @Override
    public EventType<? extends IEvent> getType() {
        return EventTypes.PLUGIN_CONFIG_UPDATE;
    }
}
