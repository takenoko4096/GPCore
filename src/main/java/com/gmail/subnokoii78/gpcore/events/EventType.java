package com.gmail.subnokoii78.gpcore.events;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventType<T extends IEvent> {
    private final Class<T> clazz;

    public EventType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<T> getEventClass() {
        return clazz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        else if (object == this) return true;
        else if (object.getClass() != getClass()) return false;
        else return ((EventType<?>) object).clazz.equals(this.clazz);
    }
}
