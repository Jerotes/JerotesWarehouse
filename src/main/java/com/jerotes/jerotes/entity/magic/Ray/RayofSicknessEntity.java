package com.jerotes.jerotes.entity.magic.Ray;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RayofSicknessEntity extends BaseRayEntity {
    public RayofSicknessEntity(EntityType<? extends RayofSicknessEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RayofSicknessEntity(EntityType<? extends RayofSicknessEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public RayofSicknessEntity(int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.RAY_OF_SICKNESS.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
        this.summonTod = d;
        this.summonTod2 = d2;
        this.summonTod3 = d3;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.isUseful())
            return;
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();

        if (entity instanceof LivingEntity livingEntity) {
            Entity entity2 = this.getOwner();
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.POISON, this, entity2);
            boolean bl = livingEntity.hurt(damageSource, (spellLevelDamage + 1) * Main.randomReach(RandomSource.create(), 1, 8));
            if (bl) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
            }
            this.playSound(JerotesSounds.SPELL, 3.0f, 1.0f);
            this.setUseful(false);
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
            this.discard();
        }
    }

    public BaseRayEntity getRay() {
        return new RayofSicknessEntity(this.spellLevelDamage, this.spellLevelMainEffectTime, this.spellLevelMainEffectLevel, this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.RAY_OF_SICKNESS.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        if (this.isUseful()) {
            return itemStack.isEmpty() ? new ItemStack(JerotesItems.RAY_OF_SICKNESS.get()) : itemStack;
        }
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
}
