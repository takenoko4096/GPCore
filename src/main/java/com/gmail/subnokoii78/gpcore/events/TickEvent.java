package com.gmail.subnokoii78.gpcore.events;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class TickEvent implements IEvent {
    private final boolean isFrozen;

    protected TickEvent(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    @Override
    public EventType<? extends IEvent> getType() {
        return EventTypes.TICK;
    }
}
