package com.gmail.subnokoii78.gpcore.shape;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DustSpawner extends ParticleSpawner<Particle.DustOptions> {
    public DustSpawner(Particle.DustOptions data) {
        super(Particle.DUST, data);
    }
}
