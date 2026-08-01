package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class InsectPlagueEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {
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
//    public boolean isPickable() {
//        return true;
//    }
//    public boolean canBeHitByProjectile() {
//        return false;
//    }
    public int getMaxStart() {
        return 40;
    }
    public boolean shouldPush() {
        return this.getLifeTick() > 20;
    }
    public int getMaxLife() {
        return 10 * 60 * 20;
    }
    protected void doPush(Entity entity) {
        if (shouldPush()) {
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.5D, 0.5D, 0.5D));
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (this.getLifeTick() == 8) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int j = 0; j < 6; j++) {
                    ParticlesUse.sendBallParticles(this, JerotesParticleTypes.INSECT_LOCUST.get(), true, 6.0f, 0.28f);
                }
            }
        }
            if (this.getLifeTick() <= 40 && this.getLifeTick() > 30) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int j = 0; j < 6; j++) {
                        serverLevel.sendParticles(JerotesParticleTypes.INSECT_LOCUST.get(), this.getRandomX(0.5f), this.getRandomY(), this.getRandomZ(0.5f), 0,
                                this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f,
                                0.2f);
                    }
                }
            } else if (this.getLifeTick() > 40) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int j = 0; j < 20; j++) {
                        serverLevel.sendParticles(JerotesParticleTypes.INSECT_LOCUST.get(), this.getRandomX(0.5f), this.getRandomY(), this.getRandomZ(0.5f), 0,
                                this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f,
                                0.2f);
                    }
                }
            }
        if (this.getLifeTick() % 5 == 0) {
            if (this.getLifeTick() > 20) {
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
        //额外清除
        if (this.getLifeTick() >= getMaxLife()) {
            if (getTarget() instanceof LivingEntity livingEntity) {
                livingEntity.removeEffect(JerotesMobEffects.HOLD_MOB.get());
            }
            this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.setTarget(null);
            this.discard();
        }
    }

//    @Override
//    public InteractionResult interact(Player player, InteractionHand interactionHand) {
//        if (!this.isAlive()) {
//            return super.interact(player, interactionHand);
//        }
//        if (player == this.getOwner()) {
//            if (player.isShiftKeyDown()) {
//                this.life = Math.max(this.life, getMaxLife() - 30);
//                return InteractionResult.SUCCESS;
//            }
//            return super.interact(player, interactionHand);
//        }
//        else {
//            if (!player.isShiftKeyDown()) {
//                dealDamageTo(player);
//                return InteractionResult.SUCCESS;
//            }
//            return super.interact(player, interactionHand);
//        }
//    }
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

    @Override
    public boolean endConcentration() {
        if (getOwner() != null) {
            SpellType.setConcentrationSpellTypeJerotes(getOwner(), "");
            if (getOwner() instanceof Player player) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setConcentrationSpellTarget("");
                    capability.syncPlayerVariables(player);
                });
            }
            SpellType.setConcentrationSpellTargetJerotes(getOwner(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_INSECT_PLAGUE.magicSpellGet(1,null,null).getSpellName());
        }
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 10));
        }
        return true;
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/insect_plague.png");
    }
}

