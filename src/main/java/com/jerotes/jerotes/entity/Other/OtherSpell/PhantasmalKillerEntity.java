package com.jerotes.jerotes.entity.Other.OtherSpell;

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
    public boolean attackEndConcentration() {
        return true;
    }
    public boolean spellEndConcentration() {
        return true;
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
        return this.life >= 20 || !(this.getTarget() instanceof Player);
    }
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        int time = (getMaxLife() - 20) - (life - 20);
        if (this.life < 20) {
            return;
        }
        if (this.life == 20) {
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
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.ABACK.get(), time, 0, false, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, time, 0, false, false));
                if ((time % 120 == 0) && time != 0) {
                    if (EntityAndItemFind.savingSpellThrow(livingEntity)){
                        if (!livingEntity.level().isClientSide) {
                            livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
                        }
                        endConcentration();
                    }
                    else {
                        DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, livingEntity);
                        livingEntity.hurt(damageSource,
                                Main.rollDice(getSpellLevel(), 10, livingEntity.getRandom())
                        );
                    }
                }
            }
            else {
                endConcentration();
            }
        }
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
        this.discard();
        return true;
    }
}

