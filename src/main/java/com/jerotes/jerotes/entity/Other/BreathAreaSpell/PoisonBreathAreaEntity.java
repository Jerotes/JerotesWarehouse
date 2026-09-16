package com.jerotes.jerotes.entity.Other.BreathAreaSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;

public class PoisonBreathAreaEntity extends BaseBreathAreaEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public PoisonBreathAreaEntity(EntityType<? extends PoisonBreathAreaEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PoisonBreathAreaEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.POISON_BREATH_AREA.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
    }

    public DamageSource getDamageSource() {
        LivingEntity livingEntity2 = this.getOwner();
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this);
        if (livingEntity2 != null) {
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this, livingEntity2);
        }
        return damageSource;
    }
    public float getMaxReach() {
        int level = getSpellLevel();
        if (level <= 3) return 4.5f;
        if (level <= 6) return 9.0f;
        if (level <= 8) return 18.0f;
        return 27.0f;
    }
    public int getParticleCount() {
        int level = getSpellLevel();
        if (level <= 3) return 40;
        if (level <= 6) return 80;
        if (level <= 8) return 160;
        return 240;
    }
    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (AttackFind.FindCanNotAttack(this.getOwner(), livingEntity))
            return false;
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this);
        if (livingEntity2 != null) {
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this, livingEntity2);
        }
        if (livingEntity2 != null && AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
            return false;
        }
        if (!EntityAndItemFind.savingSpellThrow(livingEntity)) {
            boolean bl = livingEntity.hurt(damageSource, Main.rollDice(getSpellLevel() * 2, 6, this.getRandom()));
            if (bl) {
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.DEADLY_POISON.get(),
                        (6 + (getSpellLevel() - 1) * 3) * 20,
                        (int)Math.ceil((getSpellLevel()) / 2.0)), this.getOwner() != null ? this.getOwner() : this);
                this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
            }
        } else {
            boolean bl = livingEntity.hurt(damageSource, Main.rollDice(getSpellLevel() * 2, 6, this.getRandom()) / 2);
            if (bl) {
                this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
            }
        }
        return true;
    }


    public ParticleOptions getParticle() {
        return JerotesParticleTypes.POISON_BREATH.get();
    }
    public int beamLightI() {
        return 0x0b440e;
    }
    public int beamLightII() {
        return 0x1a751f;
    }
    public int beamLightIII() {
        return 0x063009;
    }
}

