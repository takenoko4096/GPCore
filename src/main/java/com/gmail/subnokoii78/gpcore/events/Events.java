package com.gmail.subnokoii78.gpcore.events;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * イベントひとつにつきクラスひとつが必要なイベントAPI
 */
@NullMarked
public class Events {
    private final Map<EventType<?>, EventDispatcher<?>> dispatchers = new HashMap<>();

    public <T extends IEvent> EventDispatcher<T> getDispatcher(@NotNull EventType<T> type) {
        if (dispatchers.containsKey(type)) {
            return (EventDispatcher<T>) dispatchers.get(type);
        }
        else {
            final EventDispatcher<T> dispatcher = new EventDispatcher<>(type);
            dispatchers.put(type, dispatcher);
            return dispatcher;
        }
    }

    public <T extends IEvent> int register(EventType<T> type, Consumer<T> handler) {
        return getDispatcher(type).add(handler);
    }

    public <T extends IEvent> boolean unregister(EventType<T> type, int id) {
        return getDispatcher(type).remove(id);
    }
}
