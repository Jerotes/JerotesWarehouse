package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.BaseTargetEntity;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class BestowCurseEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    public BestowCurseEntity(EntityType<? extends BestowCurseEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public BestowCurseEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.BESTOW_CURSE.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }
    public int getBaseSpellLevelDamage() {
        return 3;
    }
    public boolean scaleAboutBoundingBox() {
        return this.getLifeTick() > 20;
    }
    public int getMaxStart() {
        return 10;
    }
    public int getMaxLife() {
        if (this.getSpellLevel() >= 9)
            return 365 * 24 * 60 * 60 * 20 + 15;
        if (this.getSpellLevel() >= 7)
            return 24 * 60 * 60 * 20 + 15;
        if (this.getSpellLevel() >= 5)
            return 8 * 60 * 60 * 20 + 15;
        if (this.getSpellLevel() >= 5)
            return 10 * 60 * 20 + 15;
        return 60 * 20 + 15;
    }
    public boolean moveToTarget() {
        return this.getLifeTick() >= 10 || !(this.getTarget() instanceof Player);
    }
    public void moveToTargetTick() {
        Entity target = this.getTarget();
        if (target != null) {
            this.moveTo(this.getTarget().getX(), this.getTarget().getY(0.5), this.getTarget().getZ());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLifeTick() < 10) {
            return;
        }
        if (this.getLifeTick() == 10) {
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

        if (this.getLifeTick() <= this.getMaxLife()) {
            if (this.level() instanceof ServerLevel serverLevel && this.getTrailParticle() != null) {
                if (this.tickCount % 20 == 0) {
                    serverLevel.sendParticles(this.getTrailParticle(), this.getX() + ((this.random.nextDouble() - 0.5d)), this.getY() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), this.getZ() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), 0, 0, 0, 0, 0);
                }
                if (getTarget() instanceof LivingEntity livingEntity) {
                    serverLevel.sendParticles(this.getTrailParticle(),
                            livingEntity.getRandomX(0.5f),
                            livingEntity.getRandomY(),
                            livingEntity.getRandomZ(0.5f),0, 0, 0, 0, 0);
                }
            }
        }

        if (!this.level().isClientSide()) {
            if (getTarget() instanceof LivingEntity livingEntity && getTarget().isAlive()) {
                if (getSubcategory() == 0) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, (int)Math.ceil((getSpellLevel()) / 3.0) - 1), this.getOwner());
                } else if (getSubcategory() == 1) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, (int)Math.ceil((getSpellLevel()) / 2.0) - 1), this.getOwner());
                } else if (getSubcategory() == 2) {
                    livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.ABACK.get(), 20, (int)Math.ceil((getSpellLevel()) / 3.0) - 1), this.getOwner());
                } else {
                    livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.CURSED_ADDITIONAL_DAMAGE.get(), 20, (int)Math.ceil((getSpellLevel()) / 2.0) - 1), this.getOwner());
                }
            }
            else {
                stop();
            }
        }
        //额外清除
        if (this.getLifeTick() >= getMaxLife() - 5) {
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
    public boolean endConcentration() {
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 10));
        }
        return true;
    }

    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.BESTOW_CURSE.get();
    }

    public ItemStack getItem() {
        return new ItemStack(JerotesItems.BESTOW_CURSE.get());
    }

    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
    public int roundLightI() {
        return 0x27263d;
    }
    public int roundLightII() {
        return 0x4f4a74;
    }
}
