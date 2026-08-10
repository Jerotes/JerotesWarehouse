package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class PhantasmalKillerEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {
    public PhantasmalKillerEntity(EntityType<? extends PhantasmalKillerEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public PhantasmalKillerEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.PHANTASMAL_KILLER.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }
    public int getBaseSpellLevelDamage() {
        return 4;
    }
    public int getMaxStart() {
        return 20;
    }
    public int getMaxLife() {
        return 60 * 20;
    }
    public boolean moveToTarget() {
        return this.getLifeTick() >= 20 || !(this.getTarget() instanceof Player);
    }
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        int time = (getMaxLife() - 20) - (this.getLifeTick() - 20);
        if (this.getLifeTick() < 20) {
            return;
        }
        if (this.getLifeTick() == 20) {
            boolean bl = false;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
            list.removeIf(livingEntity -> livingEntity != this.getTarget());
            for (LivingEntity livingEntity : list) {
                if (livingEntity == null) continue;
                Entity entity = this.getOwner();
                if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != entity) {
                    bl = true;
                }
            }
            if (!bl) {
                this.endConcentration();
            }
        }

        if (!this.level().isClientSide()) {
            if (getTarget() instanceof LivingEntity livingEntity && getTarget().isAlive()) {
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.PHANTASMAL_KILLER.get(), time, 0, false, false));
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.ABACK.get(), time, (int)Math.ceil(getSpellLevel() / 3.0), false, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, time, 0, false, false));
                if ((time % 120 == 0) && time != 0) {
                    if (time == 120) {
                        if (EntityAndItemFind.savingSpellThrow(livingEntity)) {
                            if (!livingEntity.level().isClientSide) {
                                livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
                            }
                            stop();
                        } else {
                            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, livingEntity);
                            livingEntity.hurt(damageSource,
                                    Main.rollDice(getSpellLevel(), 10, livingEntity.getRandom())
                            );
                        }
                    }
                    else {
                        if (EntityAndItemFind.savingSpellThrow(livingEntity, 0, true)) {
                            if (!livingEntity.level().isClientSide) {
                                livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
                            }
                            stop();
                        } else {
                            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, livingEntity);
                            livingEntity.hurt(damageSource,
                                    Main.rollDice(getSpellLevel(), 10, livingEntity.getRandom())
                            );
                        }
                    }
                }
            }
            else {
                stop();
            }
        }
        //额外清除
        if (this.getLifeTick() >= getMaxLife()) {
            if (getTarget() instanceof LivingEntity livingEntity) {
                livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
            }
            this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.setTarget(null);
            this.discard();
        }
    }
    public void stop() {
        if (getTarget() instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
        }
        endConcentration();
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
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_PHANTASMAL_KILLER.magicSpellGet(1,null,null).getSpellName());
        }
        if (getTarget() instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
        }
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 10));
        }
        return true;
    }

    public ResourceLocation getTextureLocation() {
            return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/phantasmal_killer.png");
    }
    public ResourceLocation getOtherTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/phantasmal_killer_other.png");
    }
    public ResourceLocation getRollTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/phantasmal_killer_roll.png");
    }
}

