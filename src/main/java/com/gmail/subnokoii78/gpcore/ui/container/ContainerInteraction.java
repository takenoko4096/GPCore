package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.Events;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class ContainerInteraction {
    private static final class InteractionInventoryHolder implements InventoryHolder {
        private final ContainerInteraction interaction;

        private final Inventory inventory;

        private InteractionInventoryHolder(ContainerInteraction interaction) {
            this.interaction = interaction;
            inventory = Bukkit.createInventory(this, interaction.maxColumn * 9, interaction.name);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private final TextComponent name;

    private final int maxColumn;

    private final Map<Integer, ItemButton> buttons = new HashMap<>();

    private final Events events = new Events();

    public ContainerInteraction(TextComponent name, int maxColumn) {
        this.name = name;
        this.maxColumn = maxColumn;
    }

    public TextComponent getName() {
        return name;
    }

    public int getSize() {
        return maxColumn * 9;
    }

    public int getFirstEmptySlot() throws IllegalStateException {
        for (int i = 0; i < getSize(); i++) {
            if (buttons.containsKey(i)) continue;
            return i;
        }
        throw new IllegalStateException("空のスロットが存在しません");
    }

    public boolean hasEmptySlot() {
        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) return true;
        }
        return false;
    }

    public int getEmptySlotCount() {
        int count = 0;
        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) count++;
        }
        return count;
    }

    public ContainerInteraction set(int slot, @Nullable ItemButton button) throws IllegalArgumentException {
        if (getSize() <= slot) {
            throw new IllegalArgumentException("範囲外のスロットが渡されました");
        }

        if (button == null) buttons.remove(slot);
        else buttons.put(slot, button);
        return this;
    }

    public ContainerInteraction add(ItemButton button) throws IllegalStateException {
        buttons.put(getFirstEmptySlot(), button);
        return this;
    }

    public boolean has(int slot) {
        return buttons.containsKey(slot);
    }

    public ItemButton get(int slot) throws IllegalStateException {
        if (buttons.containsKey(slot)) {
            return buttons.get(slot);
        }
        else {
            throw new IllegalStateException("スロット " + slot + " にボタンがありません");
        }
    }

    public ContainerInteraction remove(int slot) {
        buttons.remove(slot);
        return this;
    }

    public ContainerInteraction fillRow(int index, ItemButton button) {
        for (int i = index * 9; i < index * 9 + 9; i++) {
            set(i, button);
        }
        return this;
    }

    public ContainerInteraction fillColumn(int index, ItemButton button) {
        for (int i = 0; i < maxColumn; i++) {
            set(i * 9 + index, button);
        }
        return this;
    }

    public ContainerInteraction clear() {
        buttons.clear();
        return this;
    }

    public ContainerInteraction onClick(Consumer<ItemButtonClickEvent> listener) {
        events.register(ContainerInteractionEvent.ITEM_BUTTON_CLICK, listener);
        return this;
    }

    public ContainerInteraction onClose(Consumer<InteractionCloseEvent> listener) {
        events.register(ContainerInteractionEvent.INTERACTION_CLOSE, listener);
        return this;
    }

    public ContainerInteraction copy() {
        final ContainerInteraction copy = new ContainerInteraction(name, maxColumn);
        copy.onClose(events.getDispatcher(ContainerInteractionEvent.INTERACTION_CLOSE)::dispatch);
        buttons.forEach(copy::set);
        return copy;
    }

    public void open(Player player) {
        final InteractionInventoryHolder inventoryHolder = new InteractionInventoryHolder(this);

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) continue;
            inventoryHolder.inventory.setItem(i, buttons.get(i).build());
        }

        player.closeInventory();
        player.openInventory(inventoryHolder.inventory);
    }

    public static final class ContainerEventObserver implements Listener {
        private ContainerEventObserver() {}

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;

            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;
            if (!ItemButton.isButton(itemStack)) return;

            final int slot = event.getSlot();

            if (!(inventory.getHolder(false) instanceof InteractionInventoryHolder holder)) return;

            final ItemButton button = holder.interaction.buttons.get(slot);
            if (button == null) return;

            final ItemButtonClickEvent e = new ItemButtonClickEvent(player, holder.interaction, slot, button);
            holder.interaction.events.getDispatcher(ContainerInteractionEvent.ITEM_BUTTON_CLICK).dispatch(e);
            button.click(e);
            event.setCancelled(true);
        }

        @EventHandler
        public void onMove(InventoryMoveItemEvent event) {
            if (event.getDestination().getHolder(false) instanceof InteractionInventoryHolder) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;

            if (event.getInventory().getHolder(false) instanceof InteractionInventoryHolder holder) {
                holder.interaction.events.getDispatcher(ContainerInteractionEvent.INTERACTION_CLOSE).dispatch(new InteractionCloseEvent(holder.interaction, player));
            }
        }

        public static final ContainerEventObserver INSTANCE = new ContainerEventObserver();
    }
}
