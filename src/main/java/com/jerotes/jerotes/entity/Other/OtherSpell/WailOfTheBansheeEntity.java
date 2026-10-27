package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.List;

public class WailOfTheBansheeEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity {
    public static final double RANGE = 12.0;
    public static final int MAX_TARGETS = 10;
    public static final float INSTANT_DEATH_HP = 100.0F;
    public static final int DEAFEN_TICKS = 20 * 60 * 60;

    private boolean used;

    public WailOfTheBansheeEntity(EntityType<? extends WailOfTheBansheeEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public WailOfTheBansheeEntity(Level level, LivingEntity owner) {
        this(JerotesEntityType.WAIL_OF_THE_BANSHEE.get(), level);
        this.setOwnerUUID(owner.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 9;
    }

    public int getMaxStart() {
        return 10;
    }

    public int getMaxLife() {
        return 62;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLifeTick() == 1 && this.getOwner() != null) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(RANGE));
            list.sort(Comparator.comparingDouble(this::distanceToSqr));
            for (LivingEntity livingEntity : list) {
                if (!livingEntity.level().isClientSide) {
                    livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.QUAKE.get(), 40, 0, false, false, false));
                }
            }
        }
        if (!this.used && !this.level().isClientSide() && this.getLifeTick() >= this.getMaxStart() && this.getOwner() != null) {
            this.used = true;
            this.wail();
            if (this.level() instanceof ServerLevel serverLevel) {
                ParticlesUse.sendBallParticles(this.getOwner(), ParticleTypes.SOUL, true, 6.0f, 0.28f);
            }
        }
    }

    private void wail() {
        if (this.level().isClientSide()) {
            return;
        }
        LivingEntity owner = this.getOwner();
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(RANGE));
        list.sort(Comparator.comparingDouble(this::distanceToSqr));
        int hit = 0;
        for (LivingEntity livingEntity : list) {
            if (hit >= MAX_TARGETS) {
                break;
            }
            if (livingEntity == owner || !livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity.isSpectator()) {
                continue;
            }
            if (livingEntity instanceof Player player && player.isCreative()) {
                continue;
            }
            if (livingEntity.distanceToSqr(this) > RANGE * RANGE) {
                continue;
            }
            if (owner != null && AttackFind.FindCanNotAttack(owner, livingEntity)) {
                continue;
            }
            if (livingEntity.hasEffect(JerotesMobEffects.DEAFENED.get()) || EntityAndItemFind.targetDeafnessTrue(livingEntity)) {
                continue;
            }
            ++hit;
            this.dealDamageTo(livingEntity, owner);
        }
    }

    public boolean dealDamageTo(LivingEntity target, LivingEntity owner) {
        if (this.level().isClientSide() || target.level().isClientSide()) {
            return false;
        }
        DamageSource damageSource;
        if (owner != null) {
            damageSource = AttackFind.findDamageType(target, JerotesDamageTypes.PSYCHIC, this, owner);
        } else {
            damageSource = AttackFind.findDamageType(target, JerotesDamageTypes.PSYCHIC, this);
        }
        if (Main.effectiveHealth(target) <= INSTANT_DEATH_HP) {
            Main.instantKill(target, damageSource);
            return true;
        }
        boolean saving = EntityAndItemFind.savingSpellThrow(target);
        int dc = 10 + this.getSpellLevel();
        float roll = Main.rollDice(1, 20, target.getRandom());
        boolean saved = roll + this.constitutionModifier(target) >= dc;
        float damage = Main.rollDice(12, 10, this.getRandom());
        if (saving || saved) {
            damage *= 0.5f;
        }
        target.invulnerableTime = 0;
        boolean hurt = target.hurt(damageSource, damage);
        if (hurt && !saved) {
            if (!target.level().isClientSide) {
                target.addEffect(new MobEffectInstance(JerotesMobEffects.DEAFENED.get(), DEAFEN_TICKS, 0));
            }
        }
        return hurt;
    }

    private int constitutionModifier(LivingEntity entity) {
        if (entity instanceof SpellUseEntity useEntity) {
            return useEntity.getSpellLevel();
        }
        if (MainConfig.HaveSavingThrowOnlyPlayerAndLegendary && !EntityAndItemFind.isLegendary(entity)) {
            return 0;
        }
        return MainConfig.HaveSavingThrow ? 1 : 0;
    }
}
