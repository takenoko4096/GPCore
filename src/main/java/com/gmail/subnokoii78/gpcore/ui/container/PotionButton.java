package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class PotionButton extends ItemButton {
    protected PotionButton(ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected PotionButton(Material material) {
        super(material);
    }

    public PotionButton color(Color color) {
        itemStackBuilder.potionColor(color);
        return this;
    }

    @Override
    public PotionButton clickSound(Sound sound, float volume, float pitch) {
        return (PotionButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public PotionButton clickSound(ItemButtonClickSound sound) {
        return (PotionButton) super.clickSound(sound);
    }

    @Override
    public PotionButton name(TextComponent component) {
        return (PotionButton) super.name(component);
    }

    @Override
    public PotionButton loreLine(TextComponent component) {
        return (PotionButton) super.loreLine(component);
    }

    @Override
    public PotionButton amount(int amount) {
        return (PotionButton) super.amount(amount);
    }

    @Override
    public PotionButton glint(boolean flag) {
        return (PotionButton) super.glint(flag);
    }

    @Override
    public PotionButton itemModel(NamespacedKey id) {
        return (PotionButton) super.itemModel(id);
    }

    @Override
    public PotionButton hideTooltip() {
        return (PotionButton) super.hideTooltip();
    }

    @Override
    public PotionButton copy() {
        return copy(this, PotionButton::new);
    }

    @Override
    public PotionButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PotionButton) super.onClick(listener);
    }

    @Override
    protected ItemStack build() {
        itemStackBuilder.hideComponent(DataComponentTypes.POTION_CONTENTS);
        return super.build();
    }
}
