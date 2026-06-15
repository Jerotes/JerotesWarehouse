package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class HoldPersonEntity extends BaseTargetEntity {
    public HoldPersonEntity(EntityType<? extends HoldPersonEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HoldPersonEntity(EntityType<? extends HoldPersonEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public HoldPersonEntity(int spellLevelMainEffectTime, int spellLevelMainEffectLevel, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.HOLD_PERSON.get(), livingEntity, d, d2, d3, level);
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.MAGIC_EFFECT), this, this.getOwner());
        livingEntity.hurt(damageSource, 0f);
        if (EntityFactionFind.isHumanoid(livingEntity)) {
            livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.HOLD_MOB.get(), 20 * spellLevelMainEffectTime - 1, spellLevelMainEffectLevel - 1), this.getEffectSource());
        }
        this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
        this.discard();
    }

    @Override
    public int getUseLife() {
        return 10;
    }

    @Override
    public int getMaxLife() {
        return 20;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.HOLD_PERSON.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.HOLD_PERSON.get()) : itemStack;
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
        return 0x35555e;
    }
    public int roundLightII() {
        return 0x4c7f8e;
    }
}
