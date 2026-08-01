package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InfestationEntity extends BaseTargetEntity {
   public InfestationEntity(EntityType<? extends InfestationEntity> entityType, Level level) {
        super(entityType, level);
    }

    public InfestationEntity(EntityType<? extends InfestationEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public InfestationEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.INFESTATION.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
       super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOwner() != null && !this.getOwner().isSilent()) {
            if (!EntityAndItemFind.savingSpellThrow(livingEntity)) {
                DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.POISON, this, this.getOwner());
                boolean bl = livingEntity.hurt(damageSource,
                        //单数多出0.5
                        (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ?
                                Main.rollDice(1 + (spellLevelDamage - 2) / 2, 6, livingEntity.getRandom()) + Main.rollDice(1, 4, livingEntity.getRandom()) * 0.5f :
                                Main.rollDice(1 + (spellLevelDamage - 1) / 2, 6, livingEntity.getRandom())
                );
                if (bl) {
                    if (!EntityAndItemFind.isNoSpecialKnockback(livingEntity.getType())) {
                        float random = this.random.nextFloat();
                        if (random > 0.75) {
                            livingEntity.push(0,0,0.5f);
                        } else if (random > 0.5) {
                            livingEntity.push(0,0,-0.5f);
                        } else if (random > 0.25) {
                            livingEntity.push(0.5f,0,0);
                        } else {
                            livingEntity.push(-0.5f,0,0);
                        }
                    }
                }
            }
        }
        this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() instanceof LivingEntity livingEntity) {
            for (int j = 0; j < 4; j++) {
                livingEntity.level().addParticle(JerotesParticleTypes.INSECT_BUG_BLACK.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f),
                        (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f);
                livingEntity.level().addParticle(JerotesParticleTypes.INSECT_BUG_BROWN.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f),
                        (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f);
                livingEntity.level().addParticle(JerotesParticleTypes.INSECT_BUG_YELLOW.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f),
                        (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f);
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int j = 0; j < 4; j++) {
                    serverLevel.sendParticles(JerotesParticleTypes.INSECT_BUG_BLACK.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f), 0,
                            (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f,
                            0.2f);
                    serverLevel.sendParticles(JerotesParticleTypes.INSECT_BUG_BROWN.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f), 0,
                            (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f,
                            0.2f);
                    serverLevel.sendParticles(JerotesParticleTypes.INSECT_BUG_YELLOW.get(), livingEntity.getRandomX(0.5f), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5f), 0,
                            (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f, (this.random.nextFloat() - 0.5f) * 0.1f,
                            0.2f);
                }
            }
        }
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
        return 0x674a3e;
    }
    public int roundLightII() {
        return 0xaf9081;
    }
}

