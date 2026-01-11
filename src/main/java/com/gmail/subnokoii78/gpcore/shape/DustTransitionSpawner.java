package com.gmail.subnokoii78.gpcore.shape;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DustTransitionSpawner extends ParticleSpawner<Particle.DustTransition> {
    public DustTransitionSpawner(Particle.DustTransition data) {
        super(Particle.DUST_COLOR_TRANSITION, data);
    }
}
