package com.gmail.subnokoii78.gpcore.ui.container;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemButtonClickSound(Sound sound, float volume, float pitch) {
    public void play(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static final ItemButtonClickSound BASIC = new ItemButtonClickSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10.0f, 2.0f);
}
