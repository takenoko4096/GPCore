package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.events.Events;
import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.gpcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class ItemButton {
    protected final ItemStackBuilder itemStackBuilder;

    @Nullable
    protected ItemButtonClickSound sound = null;

    private final Events events = new Events();

    protected ItemButton(ItemStackBuilder itemStackBuilder) {
        this.itemStackBuilder = itemStackBuilder;
    }

    protected ItemButton(Material material) {
        this(new ItemStackBuilder(material));
    }

    public ItemButton name(TextComponent component) {
        itemStackBuilder.customName(
            component.decoration(TextDecoration.ITALIC).equals(TextDecoration.State.NOT_SET)
                ? component.decoration(TextDecoration.ITALIC, false)
                : component
        );
        return this;
    }

    public ItemButton loreLine(TextComponent component) {
        itemStackBuilder.lore(component);
        return this;
    }

    public ItemButton amount(int amount) {
        if (amount < 1 || amount > 99) {
            throw new IllegalArgumentException("個数としては範囲外の値です");
        }
        else if (itemStackBuilder.build() instanceof Damageable damageable && damageable.hasMaxDamage()) {
            throw new IllegalArgumentException("最大ダメージが設定されていないときのみ個数を変更できます");
        }

        itemStackBuilder.maxStackSize(amount);
        itemStackBuilder.count(amount);

        return this;
    }

    public ItemButton glint(boolean flag) {
        itemStackBuilder.glint(flag);
        return this;
    }

    public ItemButton itemModel(NamespacedKey id) {
        itemStackBuilder.itemModel(id);
        return this;
    }

    public ItemButton damage(float rate) throws IllegalStateException {
        if (itemStackBuilder.build().getMaxStackSize() != 1) {
            throw new IllegalStateException("耐久力の表示はアイテムの個数が1のときのみ利用できます");
        }

        itemStackBuilder.maxDamage(100);
        itemStackBuilder.damage((int) (rate * 100));
        return this;
    }

    public ItemButton hideTooltip() {
        itemStackBuilder.hideTooltip(true);
        return this;
    }

    public ItemButton clickSound(Sound sound, float volume, float pitch) {
        this.sound = new ItemButtonClickSound(sound, volume, pitch);
        return this;
    }

    public ItemButton clickSound(ItemButtonClickSound sound) {
        this.sound = sound;
        return this;
    }

    public ItemButton onClick(Consumer<ItemButtonClickEvent> listener) {
        events.register(ContainerInteractionEvent.ITEM_BUTTON_CLICK, listener);
        return this;
    }

    public ItemButton copy() {
        return copy(this, ItemButton::new);
    }

    protected ItemStack build() {
        return itemStackBuilder
            .customData(PATH, (byte) 1)
            .build();
    }

    public ItemStack visualItemStack() {
        return itemStackBuilder.build();
    }

    protected void click(ItemButtonClickEvent event) {
        events.getDispatcher(ContainerInteractionEvent.ITEM_BUTTON_CLICK).dispatch(event);
        if (sound != null) {
            sound.play(event.getPlayer());
        }
    }

    private static final MojangsonPath PATH = MojangsonPath.of("generic_plugin_core.container_interaction.is_button");

    protected static <T extends ItemButton> T copy(T itemButton, Function<ItemStackBuilder, T> constructor) {
        final T copy = constructor.apply(itemButton.itemStackBuilder);
        copy.sound = itemButton.sound;
        copy.onClick(((ItemButton) itemButton).events.getDispatcher(ContainerInteractionEvent.ITEM_BUTTON_CLICK)::dispatch);
        return copy;
    }

    protected static boolean isButton(ItemStack itemStack) {
        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();
        if (!compound.has(PATH)) return false;
        else if (!compound.getTypeOf(PATH).equals(MojangsonValueTypes.BYTE)) return false;
        return compound.get(PATH, MojangsonValueTypes.BYTE).getAsBooleanValueOrNull() == Boolean.TRUE;
    }

    public static PotionButton potion() {
        return new PotionButton(Material.POTION);
    }

    public static PotionButton splashPotion() {
        return new PotionButton(Material.SPLASH_POTION);
    }

    public static PotionButton lingeringPotion() {
        return new PotionButton(Material.LINGERING_POTION);
    }

    public static PotionButton tippedArrow() {
        return new PotionButton(Material.TIPPED_ARROW);
    }

    public static PlayerHeadButton playerHead() {
        return new PlayerHeadButton();
    }

    public static LeatherArmorButton leatherArmor(Material material) {
        return new LeatherArmorButton(material);
    }

    public static ArmorButton armor(Material material) {
        return new ArmorButton(material);
    }

    public static ItemButton item(Material material) {
        return new ItemButton(material);
    }
}
