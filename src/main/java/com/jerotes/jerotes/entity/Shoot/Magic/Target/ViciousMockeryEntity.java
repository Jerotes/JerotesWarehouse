package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ViciousMockeryEntity extends BaseTargetEntity {
   public ViciousMockeryEntity(EntityType<? extends ViciousMockeryEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ViciousMockeryEntity(EntityType<? extends ViciousMockeryEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public ViciousMockeryEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.VICIOUS_MOCKERY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
       super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOwner() != null && !this.getOwner().isSilent()) {
            DamageSource damageSource = livingEntity.level().damageSources().wither();
            livingEntity.hurt(damageSource, spellLevelDamage * Main.randomReach(RandomSource.create(), 1, 4));
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
        return JerotesParticleTypes.VICIOUS_MOCKERY.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.VICIOUS_MOCKERY.get()) : itemStack;
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
        return 0xb30e0b;
    }
    public int roundLightII() {
        return 0xff6461;
    }
}

