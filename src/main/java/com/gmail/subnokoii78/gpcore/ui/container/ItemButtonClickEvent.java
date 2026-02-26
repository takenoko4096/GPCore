package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.EventType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ItemButtonClickEvent implements ContainerInteractionEvent {
    private final Player player;

    private final ContainerInteraction interaction;

    private final int slot;

    private final ItemButton button;

    protected ItemButtonClickEvent(Player player, ContainerInteraction interaction, int slot, ItemButton button) {
        this.player = player;
        this.interaction = interaction;
        this.slot = slot;
        this.button = button;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public ContainerInteraction getInteraction() {
        return interaction;
    }

    public int getSlot() {
        return slot;
    }

    public ItemButton getClickedButton() {
        return button;
    }

    public void close() {
        player.closeInventory();
    }

    @Override
    public EventType<ItemButtonClickEvent> getType() {
        return ITEM_BUTTON_CLICK;
    }
}
