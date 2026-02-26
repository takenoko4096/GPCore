package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InteractionCloseEvent implements ContainerInteractionEvent {
    private final ContainerInteraction interaction;

    private final Player player;

    protected InteractionCloseEvent(ContainerInteraction interaction, Player player) {
        this.interaction = interaction;
        this.player = player;
    }

    @Override
    public ContainerInteraction getInteraction() {
        return interaction;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public EventType<InteractionCloseEvent> getType() {
        return INTERACTION_CLOSE;
    }
}
