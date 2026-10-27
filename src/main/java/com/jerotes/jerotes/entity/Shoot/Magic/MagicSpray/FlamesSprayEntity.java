package com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FlamesSprayEntity extends BaseMagicSprayEntity {
    public FlamesSprayEntity(EntityType<? extends FlamesSprayEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FlamesSprayEntity(EntityType<? extends FlamesSprayEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public FlamesSprayEntity(int spellLevelDamage, int spellLevelFireTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.FLAMES_SPRAY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFireTime = spellLevelFireTime;
    }

    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity2 = this.getOwner();
        if (entity2 instanceof LivingEntity livingEntity2) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_FIRE, this, this.getOwner());
            if (livingEntity.hurt(damageSource,
                    Main.rollDice(3 + (spellLevelDamage - 1), 6, livingEntity2.getRandom()) / 30
            )) {
                livingEntity.setSecondsOnFire(spellLevelFireTime);
            }
        }
    }
    @Override
    protected void handleBlock(BlockPos blockPos, BlockState state) {
        if (this.level().isClientSide()) {
            return;
        }
        if (this.random.nextFloat() > 0.9f) {
            Main.placeFireOnFlammable(this.level(), blockPos, this.getOwner());
        }
        Main.igniteBlock(this.level(), blockPos, this.getOwner());
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.FLAME.get();
    }

    public int beamLightI() {
        return 0xce6d28;
    }
    public int beamLightII() {
        return 0xefd024;
    }
    public int beamLightIII() {
        return 0xb02604;
    }
}
