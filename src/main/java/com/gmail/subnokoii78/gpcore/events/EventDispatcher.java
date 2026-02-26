package com.gmail.subnokoii78.gpcore.events;

import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class EventDispatcher<T extends IEvent> {
    private final EventType<T> type;

    private final Map<Integer, Consumer<T>> handlers = new HashMap<>();

    private int maxId = Integer.MIN_VALUE;

    protected EventDispatcher(EventType<T> eventType) {
        this.type = eventType;
    }

    public EventType<T> getType() {
        return type;
    }

    public int add(Consumer<T> handler) {
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

    public boolean clear() {
        if (handlers.isEmpty()) return false;
        else {
            handlers.clear();
            return true;
        }
    }

    public void dispatch(T event) {
        handlers.forEach((id, handler) -> handler.accept(event));
    }
}
