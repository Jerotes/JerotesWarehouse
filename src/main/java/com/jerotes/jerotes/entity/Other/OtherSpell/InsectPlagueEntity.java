package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class InsectPlagueEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    public InsectPlagueEntity(EntityType<? extends InsectPlagueEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public InsectPlagueEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.INSECT_PLAGUE.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 2;
    }
    public boolean isPickable() {
        return true;
    }
    public int getMaxStart() {
        return 40;
    }
    public boolean shouldPush() {
        return true;
    }
    public int getMaxLife() {
        return (spellLevelDamage + 1) * 6 * 20 * 2;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.life % 5 == 0) {
            if (this.life <= 20) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int j = 0; j < 32; j++) {
                        serverLevel.sendParticles(JerotesParticleTypes.INSECT_LOCUST.get(), this.getRandomX(0.5f), this.getRandomY(), this.getRandomZ(0.5f), 0,
                                this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f,
                                0.2f);
                    }
                }
            }
            else {
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int j = 0; j < 128; j++) {
                        serverLevel.sendParticles(JerotesParticleTypes.INSECT_LOCUST.get(), this.getRandomX(0.5f), this.getRandomY(), this.getRandomZ(0.5f), 0,
                                this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f,
                                0.2f);
                    }
                }
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
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!this.isAlive()) {
            return super.interact(player, interactionHand);
        }
        if (player == this.getOwner()) {
            this.life = Math.max(this.life, getMaxLife() - 30);
            return InteractionResult.SUCCESS;
        }
        else {
            if (!player.isShiftKeyDown()) {
                dealDamageTo(player);
            }
            return InteractionResult.SUCCESS;
        }
    }

    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (livingEntity2 == null) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.CLOUD_OF_DAGGERS, this);
            boolean bl = livingEntity.hurt(damageSource,
                    Main.rollDice((4 + (getSpellLevel() - 5)), 10, getRandom()) / 12f * (EntityAndItemFind.savingSpellThrow(livingEntity) ? 0.5f : 1f)
            );
        }
        else {
            if (AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
                return false;
            }
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.CLOUD_OF_DAGGERS, this, livingEntity2);
            boolean bl = livingEntity.hurt(damageSource,
                    Main.rollDice((4 + (getSpellLevel() - 5)), 10, getRandom()) / 12f * (EntityAndItemFind.savingSpellThrow(livingEntity) ? 0.5f : 1f)
            );
        }
        return true;
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

