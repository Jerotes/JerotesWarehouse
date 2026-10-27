package com.jerotes.jerotes.entity.Other.ShootTargetSpell;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FireballEntity extends AbstractFireballEntity {

    public FireballEntity(EntityType<? extends FireballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FireballEntity(Level level, LivingEntity owner) {
        this(JerotesEntityType.FIREBALL.get(), level);
        this.setOwner(owner);
    }

    @Override
    protected Vec3 computeNowPosition(Vec3 startPos, Vec3 selfPos, float progress) {
        double distance = startPos.distanceTo(selfPos);
        float startLife = (this.life - 5) * 10f;
        float startCount = (float) (distance * 10);
        float t = Mth.clamp(startLife / startCount, 0.0f, 1.0f);
        return selfPos.multiply(t, t, t).add(startPos.multiply(1 - t, 1 - t, 1 - t));
    }

    @Override
    protected void performExplosion() {
        this.level().broadcastEntityEvent(this, (byte)3);
        DamageSource damageSources = AttackFind.findDamageType(this, JerotesDamageTypes.FIRE_MAGIC, this, this.getOwner());
        float distance = 6;

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, this.getSoundSource(), 15.0f, (float) (0.8f + Math.random() * 0.4f));
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 15.0f, (float) (0.8f + Math.random() * 0.4f));

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 10; i++) {
                ParticlesUse.sendBallParticles(this, ParticleTypes.FLAME, true, 6.0f, 0.8f);
                for (int j = 0; j < 36; j++) {
                    float angle = (float) (j / 36.0 * 2 * Math.PI);
                    float radius = i / 2f + random.nextFloat() * 2.0f;
                    double x = this.getRandomX((this.random.nextDouble() - 0.5f) * 3) + radius * Math.cos(angle);
                    double z = this.getRandomZ((this.random.nextDouble() - 0.5f) * 3) + radius * Math.sin(angle);
                    serverLevel.sendParticles(ParticleTypes.FLASH, x, this.getY() + 0.5, z, 0, 0, 0, 0, 0);
                }
            }
        }

        java.util.List<Entity> lists = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(distance + 2, distance + 2, distance + 2));
        for (Entity hurt : lists) {
            if (hurt.distanceTo(this) > distance) continue;
            float strength = (24 - this.distanceTo(hurt)) / 8f;
            if (this.getOwner() != null && AttackFind.FindCanNotAttack(this.getOwner(), hurt)) continue;

            float visibility = getSeenPercent(hurt, this);
            float damageMultiplier = Math.max(0.0f, visibility);
            if (damageMultiplier <= 0.01f) continue;

            if (!(hurt instanceof ItemEntity)){
                hurt.setDeltaMovement(0, strength / 5, 0);
            }

            if (hurt instanceof LivingEntity livingHurt) {
                boolean bl = hurt.hurt(damageSources,
                        Main.rollDice(8 + (getSpellLevel() - 3), 6, this.random)
                );
                if (bl) {
                    hurt.setSecondsOnFire(6 + getSpellLevel() * 3);
                }

                double knock;
                if (livingHurt.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                    knock = livingHurt.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                } else {
                    knock = 0.0;
                }
                if (!EntityAndItemFind.isNoSpecialKnockback(hurt.getType())) {
                    double d2 = Math.max(0.2, 1.2 - knock);
                    livingHurt.setDeltaMovement(livingHurt.getDeltaMovement().add(-(this.getX() - livingHurt.getX()) * d2 / 3, -(this.getY() - livingHurt.getY()) * d2 / 4, -(this.getZ() - livingHurt.getZ()) * d2 / 3));

                    if (Main.mobSizeSmall(hurt) || Main.mobSizeMedium(hurt) || Main.mobSizeLarge(hurt)) {
                        livingHurt.setDeltaMovement(livingHurt.getDeltaMovement().add(0, (this.getY() < livingHurt.getY() ? -1 : 1) * strength / 24, 0));
                    }
                }
            }
            if (hurt instanceof ItemEntity itemEntity && itemEntity.getItem().getRarity() == Rarity.COMMON) {
                itemEntity.setDeltaMovement(0, strength / 6, 0);
            }
            if (hurt instanceof LivingEntity livingHurt && !livingHurt.level().isClientSide) {
                livingHurt.addEffect(new MobEffectInstance(JerotesMobEffects.QUAKE.get(), 120, 0, false, false), this);
            }
        }
        this.setDeltaMovement(0,0,0);
        this.level().explode(this, damageSources, null, this.getX(), this.getY(), this.getZ(), Math.min(spellLevelDamage/1.5f, 3), true,
                getSubcategory() == 1 ? (getOwner() instanceof Player player ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.MOB) : Level.ExplosionInteraction.NONE);
        AABB aABB = new AABB(this.position().x + 6, this.position().y + 6, this.position().z + 6, this.position().x - 6, this.position().y - 6, this.position().z - 6).move(0, -2, 0);
        for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
            if (blockPos.getCenter().distanceTo(this.position()) <= 6) {
                Main.placeFire(this.level(), blockPos, this.getOwner());
                Main.igniteBlock(this.level(), blockPos, this.getOwner());
            }
        }
    }

    @Override
    protected void onLifeBomb(int tick) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if (tick == 8) {
                for (int j = 0; j < 128; j++) {
                    float angle = (float) (j / 128.0 * 2 * Math.PI);
                    float radius = 4;
                    double x = this.getX() + radius * Math.cos(angle);
                    double z = this.getZ() + radius * Math.sin(angle);
                    serverLevel.sendParticles(ParticleTypes.LAVA, x, this.getY() + 2.5, z, 0, 0, 0, 0, 0f);
                }
                Main.spawnUnevenBlockByPos(serverLevel, BlockPos.containing(this.position()), 3);
            } else if (tick == 10) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 36; j++) {
                        float angle = (float) (j / 36.0 * 2 * Math.PI);
                        float radius = Math.max(4, i/2f + random.nextFloat() * 2.0f);
                        double x = this.getRandomX((this.random.nextDouble() - 0.5f) * 3) + radius * Math.cos(angle);
                        double z = this.getRandomZ((this.random.nextDouble() - 0.5f) * 3) + radius * Math.sin(angle);
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, this.getY() + 0.5, z, 0, 0, i * 0.2f * random.nextFloat(), 0, 0.3f);
                    }
                }
            }
        }
    }

    @Override
    protected int trailMaxLife() {
        return 12;
    }

    @Override
    protected int trailMaxPoints() {
        return 12;
    }

    @Override
    public int beamLightI() {
        return 0xe6903b;
    }

    @Override
    public int beamLightII() {
        return 0x580900;
    }
}