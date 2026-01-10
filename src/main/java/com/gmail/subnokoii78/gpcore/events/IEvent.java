package com.gmail.subnokoii78.gpcore.events;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IEvent {
    EventType<? extends IEvent> getType();
}
