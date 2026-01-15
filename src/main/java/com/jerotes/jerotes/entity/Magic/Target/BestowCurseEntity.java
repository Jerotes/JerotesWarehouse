package com.jerotes.jerotes.entity.Magic.Target;

import com.jerotes.jerotes.init.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BestowCurseEntity extends BaseTargetEntity {
    public BestowCurseEntity(EntityType<? extends BestowCurseEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BestowCurseEntity(EntityType<? extends BestowCurseEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public BestowCurseEntity(int spellLevelMainEffectTime, int spellLevelMainEffectLevel, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.BESTOW_CURSE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.MAGIC_EFFECT), this, this.getOwner());
        livingEntity.hurt(damageSource, 0f);
        float random = this.random.nextFloat();
        if (random > 0.75) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
        } else if (random > 0.5) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
        } else if (random > 0.25) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
        } else {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
        }
        this.playSound(JerotesSounds.SPELL, 3.0f, 1.0f);
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
        return JerotesParticleTypes.BESTOW_CURSE.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.BESTOW_CURSE.get()) : itemStack;
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
