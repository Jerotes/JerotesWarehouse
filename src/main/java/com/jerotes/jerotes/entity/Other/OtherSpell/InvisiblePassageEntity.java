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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class InvisiblePassageEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {
    public InvisiblePassageEntity(EntityType<? extends InvisiblePassageEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public InvisiblePassageEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.INVISIBLE_PASSAGE.get(), p_36926_);
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
        return 2;
    }
    public int getMaxStart() {
        return 20;
    }
    public int getMaxLife() {
        return 60 * 20 * 60;
    }
    public boolean moveToTarget() {
        return true;
    }
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (getTarget() instanceof LivingEntity livingEntity && getTarget().isAlive()) {
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.INVISIBLE_PASSAGE.get(), 5, 0, false, false));
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
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_INVISIBLE_PASSAGE.magicSpellGet(1,null,null).getSpellName());
        }
        this.discard();
        return true;
    }
}

