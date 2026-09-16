package com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SparksSprayEntity extends BaseMagicSprayEntity {
    public SparksSprayEntity(EntityType<? extends SparksSprayEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SparksSprayEntity(EntityType<? extends SparksSprayEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public SparksSprayEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.SPARKS_SPRAY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
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
            }
        }
    }
    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.SHOCK.get();
    }

    public int beamLightI() {
        return 0xae9aff;
    }
    public int beamLightII() {
        return 0xc3ddff;
    }
    public int beamLightIII() {
        return 0x9079ff;
    }
    public int getParticleCount() {
        return 1;
    }
}
