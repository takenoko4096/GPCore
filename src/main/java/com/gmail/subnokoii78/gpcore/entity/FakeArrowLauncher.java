package com.gmail.subnokoii78.gpcore.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii78.gpcore.events.CancellableEvent;
import com.gmail.subnokoii78.gpcore.events.EventType;
import com.gmail.subnokoii78.gpcore.events.Events;
import com.gmail.subnokoii78.gpcore.events.IEvent;
import com.gmail.subnokoii78.gpcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.gpcore.vector.DualAxisRotationBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public class FakeArrowLauncher {
    private final ProjectileSource source;

    private double power = 1.0;

    private double damage = 1.0;

    private boolean isCritical = false;

    private boolean isFlame = false;

    private boolean isGravitated = true;

    private double duration = Double.POSITIVE_INFINITY;

    @Nullable
    private ParticleSpawner<?> particle = null;

    public FakeArrowLauncher(ProjectileSource source) {
        this.source = source;
    }

    private Class<? extends AbstractArrow> getEntityClassOf(ItemStack itemStack) {
        return switch (itemStack.getType()) {
            case Material.ARROW, Material.TIPPED_ARROW -> Arrow.class;
            case Material.SPECTRAL_ARROW -> SpectralArrow.class;
            case Material.TRIDENT -> Trident.class;
            default -> throw new IllegalArgumentException();
        };
    }

    private void initializeArrowProperties(AbstractArrow entity, ItemStack weapon, ItemStack arrow) {
        entity.setShooter(source);
        entity.setHasLeftShooter(false);
        entity.setWeapon(weapon);

        entity.setLifetimeTicks(1200);
        entity.setItemStack(arrow);

        entity.setDamage(damage);
        entity.setCritical(isCritical);
        entity.setFireTicks(isFlame ? 5000 : -1);
        entity.setGravity(isGravitated);

        if (arrow.getType().equals(Material.TIPPED_ARROW)) {
            final Arrow arrowEntity = (Arrow) entity;
            final PotionMeta potionMeta = (PotionMeta) arrow.getItemMeta();
            for (PotionEffect effect : potionMeta.getAllEffects()) {
                arrowEntity.addCustomEffect(effect, true);
            }
        }
    }

    public FakeArrow launch(ItemStack weapon, ItemStack arrow, DualAxisRotationBuilder rotation) {
        final AbstractArrow projectile = source.launchProjectile(
            getEntityClassOf(arrow),
            rotation.getDirection3d().scale(power).toBukkitVector(),
            entity -> initializeArrowProperties(entity, weapon, arrow)
        );

        return new FakeArrow(this, projectile);
    }

    public ProjectileSource getSource() {
        return source;
    }

    public double getPower() {
        return power;
    }

    public FakeArrowLauncher setPower(double value) {
        power = value;
        return this;
    }

    public double getDamage() {
        return damage;
    }

    public FakeArrowLauncher setDamage(double value) {
        damage = value;
        return this;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public FakeArrowLauncher setCritical(boolean value) {
        isCritical = value;
        return this;
    }

    public boolean isFlame() {
        return isFlame;
    }

    public FakeArrowLauncher setFlame(boolean value) {
        isFlame = value;
        return this;
    }

    public boolean isGravitated() {
        return isGravitated;
    }

    public FakeArrowLauncher setGravitated(boolean value) {
        isGravitated = value;
        return this;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Nullable
    public ParticleSpawner<?> getParticle() {
        return particle == null ? null : particle.copy();
    }

    public FakeArrowLauncher setParticle(@Nullable ParticleSpawner<?> value) {
        particle = value;
        return this;
    }

    public static PlayerFakeArrowLauncher ofPlayer(Player player) {
        return new PlayerFakeArrowLauncher(player);
    }

    public static final class FakeArrowEventListener extends BukkitRunnable implements Listener {
        private FakeArrowEventListener() {}

        @Override
        public void run() {
            for (final FakeArrow fakeArrow : FakeArrow.arrows) {
                final ParticleSpawner<?> spawner = fakeArrow.particle;

                if (spawner != null) {
                    spawner.place(fakeArrow.getEntity().getLocation());
                    spawner.spawn();
                }

                if (fakeArrow.lifetime >= fakeArrow.duration) {
                    fakeArrow.arrow.remove();
                }

                fakeArrow.lifetime++;
            }
        }

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            final FakeArrow fakeArrow = FakeArrow.getFakeArrow(event.getEntity());
            if (fakeArrow == null) return;

            final ProjectileSource source = fakeArrow.arrow.getShooter();

            if (source == null) {
                throw new IllegalStateException("Projectile source must be non-null");
            }

            final Block hitBlock = event.getHitBlock();
            if (hitBlock != null) {
                final BlockFace hitBlockFace = Objects.requireNonNull(event.getHitBlockFace());
                fakeArrow.events.getDispatcher(FakeArrow.BLOCK_HIT).dispatch(new FakeArrow.BlockHitEvent(
                    fakeArrow,
                    source,
                    hitBlock,
                    hitBlockFace
                ));
            }

            final Entity hitEntity = event.getHitEntity();
            if (hitEntity != null) {
                fakeArrow.events.getDispatcher(FakeArrow.ENTITY_HIT).dispatch(new FakeArrow.EntityHitEvent(
                    event,
                    fakeArrow,
                    source,
                    hitEntity
                ));
            }
        }

        @EventHandler
        public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
            final Iterator<FakeArrow> iterator = FakeArrow.arrows.iterator();

            FakeArrow arrow;
            while (iterator.hasNext()) {
                arrow = iterator.next();

                if (arrow.getEntity().equals(event.getEntity())) {
                    FakeArrow.arrows.remove(arrow);
                    break;
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            for (final FakeArrow fakeArrow : FakeArrow.arrows) {
                fakeArrow.arrow.remove();
            }
        }

        public static final FakeArrowEventListener INSTANCE = new FakeArrowEventListener();
    }

    public static final class FakeArrow {
        private static final Set<FakeArrow> arrows = new HashSet<>();

        private static @Nullable FakeArrow getFakeArrow(Entity entity) {
            final Iterator<FakeArrow> iterator = FakeArrow.arrows.iterator();

            FakeArrow arrow;
            while (iterator.hasNext()) {
                arrow = iterator.next();

                if (arrow.getEntity().equals(entity)) {
                    return arrow;
                }
            }

            return null;
        }

        private final FakeArrowLauncher launcher;

        private final AbstractArrow arrow;

        private final Events events = new Events();

        private final double duration;

        @Nullable
        private final ParticleSpawner<?> particle;

        private int lifetime = 0;

        private final Location launchLocation;

        private final ItemStack weapon;

        private FakeArrow(FakeArrowLauncher launcher, AbstractArrow arrow) {
            this.launcher = launcher;
            this.arrow = arrow;
            this.duration = launcher.getDuration();
            this.particle = launcher.getParticle();
            this.launchLocation = arrow.getLocation();
            this.weapon = Objects.requireNonNullElse(arrow.getWeapon(), ItemStack.empty());
            arrows.add(this);
        }

        public FakeArrowLauncher getLauncher() {
            return launcher;
        }

        public ItemStack getWeapon() {
            return weapon;
        }

        public ItemStack getItemStack() {
            return arrow.getItemStack();
        }

        public AbstractArrow getEntity() {
            return arrow;
        }

        public int getLifetime() {
            return lifetime;
        }

        public double getDuration() {
            return duration;
        }

        public Location getLaunchLocation() {
            return launchLocation;
        }

        public void onEntityHit(Consumer<EntityHitEvent> listener) {
            events.register(ENTITY_HIT, listener);
        }

        public void onBlockHit(Consumer<BlockHitEvent> listener) {
            events.register(BLOCK_HIT, listener);
        }

        public static final class BlockHitEvent implements IEvent {
            private final FakeArrow fakeArrow;

            private final ProjectileSource source;

            private final Block block;

            private final BlockFace blockFace;

            private BlockHitEvent(FakeArrow fakeArrow, ProjectileSource source, Block block, BlockFace blockFace) {
                this.fakeArrow = fakeArrow;
                this.source = source;
                this.block = block;
                this.blockFace = blockFace;
            }

            public FakeArrow getFakeArrow() {
                return fakeArrow;
            }

            public ProjectileSource getSource() {
                return source;
            }

            public Block getBlock() {
                return block;
            }

            public BlockFace getBlockFace() {
                return blockFace;
            }

            @Override
            public EventType<BlockHitEvent> getType() {
                return BLOCK_HIT;
            }
        }

        public static final class EntityHitEvent extends CancellableEvent {
            private final FakeArrow fakeArrow;

            private final ProjectileSource source;

            private final Entity entity;

            private EntityHitEvent(ProjectileHitEvent event, FakeArrow fakeArrow, ProjectileSource source, Entity entity) {
                super(event);
                this.fakeArrow = fakeArrow;
                this.source = source;
                this.entity = entity;
            }

            public FakeArrow getFakeArrow() {
                return fakeArrow;
            }

            public ProjectileSource getSource() {
                return source;
            }

            public Entity getEntity() {
                return entity;
            }

            @Override
            public EventType<EntityHitEvent> getType() {
                return ENTITY_HIT;
            }
        }

        private static final EventType<BlockHitEvent> BLOCK_HIT = new EventType<>(BlockHitEvent.class);

        private static final EventType<EntityHitEvent> ENTITY_HIT = new EventType<>(EntityHitEvent.class);
    }

    public static final class PlayerFakeArrowLauncher extends FakeArrowLauncher {
        private PlayerFakeArrowLauncher(Player source) {
            super(source);
        }

        @Override
        public Player getSource() {
            return (Player) super.getSource();
        }

        public FakeArrow launch(ItemStack weapon, ItemStack arrow) {
            return super.launch(weapon, arrow, DualAxisRotationBuilder.from(getSource()));
        }
    }
}
