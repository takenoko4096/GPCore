package com.gmail.subnokoii78.gpcore.events;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher<T extends IEvent> {
    private final EventType<T> type;

    private final Map<Integer, Consumer<T>> handlers = new HashMap<>();

    private int maxId = Integer.MIN_VALUE;

    public EventDispatcher(@NotNull EventType<T> eventType) {
        this.type = eventType;
    }

    public @NotNull EventType<T> getType() {
        return type;
    }

    public int add(@NotNull Consumer<T> handler) {
        final int id = maxId++;
        handlers.put(id, handler);
        return id;
    }

    public boolean remove(int id) {
        if (handlers.containsKey(id)) {
            handlers.remove(id);
            return true;
        }
        else return false;
    }

    public void dispatch(T event) {
        handlers.forEach((id, handler) -> handler.accept(event));
    }
}
