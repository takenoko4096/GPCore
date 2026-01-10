package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.EventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InteractionCloseEvent implements ContainerInteractionEvent {
    private final ContainerInteraction interaction;

    private final Player player;

    protected InteractionCloseEvent(@NotNull ContainerInteraction interaction, @NotNull Player player) {
        this.interaction = interaction;
        this.player = player;
    }

    @Override
    public @NotNull ContainerInteraction getInteraction() {
        return interaction;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull EventType<InteractionCloseEvent> getType() {
        return INTERACTION_CLOSE;
    }
}
