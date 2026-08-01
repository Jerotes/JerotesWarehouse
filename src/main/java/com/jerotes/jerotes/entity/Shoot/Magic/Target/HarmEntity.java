package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HarmEntity extends BaseTargetEntity {
    public HarmEntity(EntityType<? extends HarmEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HarmEntity(EntityType<? extends HarmEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public HarmEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.HARM.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        float baseHealth = livingEntity.getHealth();
        DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.NECROTIC), this, this.getOwner());
        boolean bl = EntityAndItemFind.savingSpellThrow(livingEntity);
        livingEntity.hurt(damageSource,
                Main.rollDice(14 + (spellLevelDamage - 6) * 2, 6, livingEntity.getRandom()) * (bl ? 0.5f : 1f)
        );
        if (!bl) {
            if (livingEntity.getMaxHealth() > 1) {
                float healthAbout = baseHealth - livingEntity.getHealth();
                if (livingEntity.getMaxHealth() - healthAbout < 1) {
                    healthAbout = livingEntity.getMaxHealth() - 1;
                }
                AttackFind.MaxHealthReduceAdd(livingEntity, healthAbout);
            }
        }
        this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
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
        return JerotesParticleTypes.HARM.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.HARM.get()) : itemStack;
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
        return 0x316a37;
    }
    public int roundLightII() {
        return 0x65c769;
    }
}
