package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.EventType;
import com.gmail.subnokoii78.gpcore.events.IEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ContainerInteractionEvent extends IEvent {
    @NotNull ContainerInteraction getInteraction();

    @NotNull Player getPlayer();

    @Override
    @NotNull EventType<? extends IEvent> getType();

    EventType<InteractionCloseEvent> INTERACTION_CLOSE = new EventType<>(InteractionCloseEvent.class);

    EventType<ItemButtonClickEvent> ITEM_BUTTON_CLICK = new EventType<>(ItemButtonClickEvent.class);
}
