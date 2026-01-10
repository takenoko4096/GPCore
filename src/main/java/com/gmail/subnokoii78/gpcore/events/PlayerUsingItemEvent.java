package com.gmail.subnokoii78.gpcore.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerUsingItemEvent implements IEvent {
    private final Player player;

    private final ItemStack itemStack;

    private final int usedTime;

    protected PlayerUsingItemEvent(Player player, ItemStack itemStack, int usedTime) {
        this.player = player;
        this.itemStack = itemStack;
        this.usedTime = usedTime;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public int getUsedTime() {
        return usedTime;
    }

    public void completeUse() {
        player.completeUsingActiveItem();
    }

    public void interruptUse() {
        player.clearActiveItem();
    }

    @Override
    public EventType<PlayerUsingItemEvent> getType() {
        return EventTypes.PLAYER_USING_ITEM;
    }
}
