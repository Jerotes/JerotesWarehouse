package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CloudOfDaggersEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public CloudOfDaggersEntity(EntityType<? extends CloudOfDaggersEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public CloudOfDaggersEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.CLOUD_OF_DAGGERS.get(), p_36926_);
        this.setOwner(p_36932_);
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 2;
    }
    public int getMaxStart() {
        return 40;
    }
    public int getMaxLife() {
        return (spellLevelDamage + 1) * 6 * 20;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.life % 5 == 0 && this.life > 20) {
            boolean bl = false;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
            for (LivingEntity livingEntity : list) {
                if (this.dealDamageTo(livingEntity)) {
                    bl = true;
                }
            }
            if (bl) {
                this.playSound(SoundEvents.PLAYER_ATTACK_CRIT);
            }
        }
    }

    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (livingEntity2 == null) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.CLOUD_OF_DAGGERS, this);
            boolean bl = livingEntity.hurt(damageSource, Main.randomReach(RandomSource.create(), 1, 4) * Math.max(1, (1 + (spellLevelDamage - 2) / 12f)));
            if (bl) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, livingEntity.getX(), livingEntity.getY(0.5), livingEntity.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
        else {
            if (AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
                return false;
            }
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.CLOUD_OF_DAGGERS, this, livingEntity2);
            boolean bl = livingEntity.hurt(damageSource, Main.randomReach(RandomSource.create(), 1, 4) * Math.max(1, (1 + (spellLevelDamage - 2) / 12f)));
            if (bl) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, livingEntity.getX(), livingEntity.getY(0.5), livingEntity.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
        return true;
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/cloud_of_daggers.png");
    }
    public ResourceLocation getDaggersTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/cloud_of_daggers_daggers.png");
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

