package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class ArmorButton extends ItemButton {
    protected ArmorButton(ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected ArmorButton(Material material) {
        super(material);
    }

    @Override
    public ArmorButton name(TextComponent component) {
        return (ArmorButton) super.name(component);
    }

    @Override
    public ArmorButton loreLine(TextComponent component) {
        return (ArmorButton) super.loreLine(component);
    }

    @Override
    public ArmorButton amount(int amount) {
        return (ArmorButton) super.amount(amount);
    }

    @Override
    public ArmorButton glint(boolean flag) {
        return (ArmorButton) super.glint(flag);
    }

    @Override
    public ArmorButton itemModel(NamespacedKey id) {
        return (ArmorButton) super.itemModel(id);
    }

    @Override
    public ArmorButton hideTooltip() {
        return (ArmorButton) super.hideTooltip();
    }

    @Override
    public ArmorButton clickSound(ItemButtonClickSound sound) {
        return (ArmorButton) super.clickSound(sound);
    }

    @Override
    public ArmorButton clickSound(Sound sound, float volume, float pitch) {
        return (ArmorButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public ArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (ArmorButton) super.onClick(listener);
    }

    public ArmorButton trim(TrimMaterial material, TrimPattern pattern) {
        itemStackBuilder.trim(material, pattern);
        return this;
    }

    @Override
    public ArmorButton copy() {
        return copy(this, ArmorButton::new);
    }

    @Override
    protected ItemStack build() {
        itemStackBuilder.hideFlag(ItemFlag.HIDE_ARMOR_TRIM);
        itemStackBuilder.attributeModifier(Attribute.ARMOR, NamespacedKey.minecraft("test"), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY, AttributeModifierDisplay.hidden());
        itemStackBuilder.hideFlag(ItemFlag.HIDE_ATTRIBUTES);
        return super.build();
    }
}
