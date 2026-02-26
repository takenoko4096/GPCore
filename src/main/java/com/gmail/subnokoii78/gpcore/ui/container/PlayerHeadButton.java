package com.gmail.subnokoii78.gpcore.ui.container;

import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class PlayerHeadButton extends ItemButton {
    protected PlayerHeadButton(ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected PlayerHeadButton() {
        super(Material.PLAYER_HEAD);
    }

    @Override
    public PlayerHeadButton name(TextComponent component) {
        return (PlayerHeadButton) super.name(component);
    }

    @Override
    public PlayerHeadButton loreLine(TextComponent component) {
        return (PlayerHeadButton) super.loreLine(component);
    }

    @Override
    public PlayerHeadButton amount(int amount) {
        return (PlayerHeadButton) super.amount(amount);
    }

    @Override
    public PlayerHeadButton glint(boolean flag) {
        return (PlayerHeadButton) super.glint(flag);
    }

    @Override
    public PlayerHeadButton itemModel(NamespacedKey id) {
        return (PlayerHeadButton) super.itemModel(id);
    }

    @Override
    public PlayerHeadButton hideTooltip() {
        return (PlayerHeadButton) super.hideTooltip();
    }

    @Override
    public PlayerHeadButton clickSound(ItemButtonClickSound sound) {
        return (PlayerHeadButton) super.clickSound(sound);
    }

    @Override
    public PlayerHeadButton clickSound(Sound sound, float volume, float pitch) {
        return (PlayerHeadButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public PlayerHeadButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PlayerHeadButton) super.onClick(listener);
    }

    @Override
    public PlayerHeadButton copy() {
        return copy(this, PlayerHeadButton::new);
    }

    public PlayerHeadButton player(String gamerTag) {
        itemStackBuilder.playerProfile(Bukkit.getOfflinePlayer(gamerTag).getPlayerProfile());
        return this;
    }
}
