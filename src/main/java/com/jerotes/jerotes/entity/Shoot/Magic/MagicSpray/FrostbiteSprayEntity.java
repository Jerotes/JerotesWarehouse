package com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrostbiteSprayEntity extends BaseMagicSprayEntity {
    public FrostbiteSprayEntity(EntityType<? extends FrostbiteSprayEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FrostbiteSprayEntity(EntityType<? extends FrostbiteSprayEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        this(entityType, level);
        this.moveTo(d, d2, d3, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (d7 != 0.0) {
            this.xPower = d4 / d7 * 0.1;
            this.yPower = d5 / d7 * 0.1;
            this.zPower = d6 / d7 * 0.1;
        }
    }

    public FrostbiteSprayEntity(int spellLevelDamage, int spellLevelFreezeTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.FROSTBITE_SPRAY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFreezeTime = spellLevelFreezeTime;
    }

    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity2 = this.getOwner();
        if (entity2 instanceof LivingEntity livingEntity2) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_FREEZE, this, this.getOwner());
            if (livingEntity.hurt(damageSource,
                    Main.rollDice(3 + (spellLevelDamage - 1), 6, livingEntity2.getRandom()) / 30
            ))
            {
                if (livingEntity.getTicksFrozen() < 140 + 20 * spellLevelFreezeTime) {
                    livingEntity.setTicksFrozen(140 + 20 * spellLevelFreezeTime);
                }
            }
        }
    }
    @Override
    protected void handleBlock(BlockPos blockPos, BlockState state) {
        if (this.level().isClientSide()) {
            return;
        }
        Main.extinguishFire(this.level(), blockPos, this.getOwner());
        Main.freezeBlock(this.level(), blockPos, this.getOwner());
    }
    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.FROST.get();
    }

    public int beamLightI() {
        return 0xa1f9ff;
    }
    public int beamLightII() {
        return 0xc0fbff;
    }
    public int beamLightIII() {
        return 0x86d1f6;
    }
}
