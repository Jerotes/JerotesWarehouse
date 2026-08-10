package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuardianLaserEntity extends BaseTargetEntity {
    public GuardianLaserEntity(EntityType<? extends GuardianLaserEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GuardianLaserEntity(EntityType<? extends GuardianLaserEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        this(entityType, level);
        this.moveTo(d, d2, d3, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (d7 != 0.0) {
            this.xPower = d4 / d7 * 0.1;
            this.yPower = d5 / d7 * 0.1;
            this.zPower = d6 / d7 * 0.1;
        }
    }

    public GuardianLaserEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.GUARDIAN_LASER.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.life <= this.getMaxLife()) {
            if (this.level() instanceof ServerLevel _level && this.getTrailParticle() != null) {
                if (this.tickCount % 20 == 0) {
                    _level.sendParticles(this.getTrailParticle(), this.getX() + ((this.random.nextDouble() - 0.5d)), this.getY() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), this.getZ() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), 0, 0, 0, 0, 0);
                }
            }
            if (this.getTarget() != null) {
                this.moveTo(this.getTarget().getX(), this.getTarget().getY(0.5), this.getTarget().getZ());
            }
        }

        if (this.getOwner() != null && this.getTarget() != null) {
            Entity livingentity = this.getTarget();
            double d5 = this.getAttackAnimationScale(0.0F);
            double d0 = livingentity.getX() - this.getOwner().getX();
            double d1 = livingentity.getY(0.5D) - this.getOwner().getEyeY();
            double d2 = livingentity.getZ() - this.getOwner().getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            double d4 = this.random.nextDouble();
            while(d4 < d3) {
                d4 += 1.8D - d5 + this.random.nextDouble() * (1.7D - d5);
                this.level().addParticle(ParticleTypes.BUBBLE, this.getOwner().getX() + d0 * d4, this.getOwner().getEyeY() + d1 * d4, this.getOwner().getZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
            }

            if (this.level().isClientSide) return;
            if (this.getTarget() == null || !this.getTarget().isAlive()) {
                this.discard();
                return;
            }
            if (this.getOwner() instanceof LivingEntity livingEntity && !livingEntity.hasLineOfSight(this.getTarget())) {
                this.discard();
                return;
            }
        }
    }
    public float getAttackAnimationScale(float p_32813_) {
        return ((float)this.life + p_32813_) / (float)this.getMaxLife();
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOwner() != null) {
            livingEntity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1f + spellLevelDamage * 2f);
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, DamageTypes.MOB_ATTACK, this, this.getOwner());
            livingEntity.hurt(damageSource, 6f + (spellLevelDamage - 1f) * 2f);
        }
    }

    @Override
    public int getUseLife() {
        return 80;
    }

    @Override
    public int getMaxLife() {
        return 85;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
    public int roundLightI() {
        return 0x4c3b36;
    }
    public int roundLightII() {
        return 0x5f4e2f;
    }
}
