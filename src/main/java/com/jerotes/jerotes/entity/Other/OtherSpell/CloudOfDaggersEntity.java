package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.network.SpellUseMessage;
import com.jerotes.jerotes.spell.SpellList;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class CloudOfDaggersEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {

    public CloudOfDaggersEntity(EntityType<? extends CloudOfDaggersEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public CloudOfDaggersEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.CLOUD_OF_DAGGERS.get(), p_36926_);
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
    public int getMaxLife() {
        return 60 * 20;
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
                    Main.rollDice((4 + (getSpellLevel() - 2) * 2), 4, getRandom()) / 12f
            );
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
            boolean bl = livingEntity.hurt(damageSource,
                    Main.rollDice((4 + (getSpellLevel() - 2) * 2), 4, getRandom()) / 12f
            );
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
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_CLOUD_OF_DAGGERS.magicSpellGet(1,null,null).getSpellName());
        }
        this.discard();
        return true;
    }
}

