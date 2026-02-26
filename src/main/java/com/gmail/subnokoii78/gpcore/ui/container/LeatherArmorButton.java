package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public class LeatherArmorButton extends ArmorButton {
    protected LeatherArmorButton(ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected LeatherArmorButton(Material material) {
        super(material);
    }

    @Override
    public LeatherArmorButton name(TextComponent component) {
        return (LeatherArmorButton) super.name(component);
    }

    @Override
    public LeatherArmorButton loreLine(TextComponent component) {
        return (LeatherArmorButton) super.loreLine(component);
    }

    @Override
    public LeatherArmorButton amount(int amount) {
        return (LeatherArmorButton) super.amount(amount);
    }

    @Override
    public LeatherArmorButton glint(boolean flag) {
        return (LeatherArmorButton) super.glint(flag);
    }

    @Override
    public LeatherArmorButton itemModel(NamespacedKey id) {
        return (LeatherArmorButton) super.itemModel(id);
    }

    @Override
    public LeatherArmorButton hideTooltip() {
        return (LeatherArmorButton) super.hideTooltip();
    }

    @Override
    public LeatherArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (LeatherArmorButton) super.onClick(listener);
    }

    public LeatherArmorButton color(@Nullable Color color) {
        itemStackBuilder.leatherArmorColor(color);
        return this;
    }

    @Override
    public LeatherArmorButton clickSound(ItemButtonClickSound sound) {
        return (LeatherArmorButton) super.clickSound(sound);
    }

    @Override
    public LeatherArmorButton clickSound(Sound sound, float volume, float pitch) {
        return (LeatherArmorButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public LeatherArmorButton copy() {
        return copy(this, LeatherArmorButton::new);
    }

    @Override
    protected ItemStack build() {
        itemStackBuilder.hideFlag(ItemFlag.HIDE_DYE);
        return super.build();
    }
}
