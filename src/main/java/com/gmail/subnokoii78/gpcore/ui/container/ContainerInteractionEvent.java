package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.EventType;
import com.gmail.subnokoii78.gpcore.events.IEvent;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ContainerInteractionEvent extends IEvent {
    ContainerInteraction getInteraction();

    Player getPlayer();

    @Override
    EventType<? extends IEvent> getType();

    EventType<InteractionCloseEvent> INTERACTION_CLOSE = new EventType<>(InteractionCloseEvent.class);

    EventType<ItemButtonClickEvent> ITEM_BUTTON_CLICK = new EventType<>(ItemButtonClickEvent.class);
}
