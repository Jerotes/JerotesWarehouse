package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ScorchingRayEntity extends BaseRayEntity {
    public ScorchingRayEntity(EntityType<? extends ScorchingRayEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ScorchingRayEntity(EntityType<? extends ScorchingRayEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public ScorchingRayEntity(int spellLevelDamage, int spellLevelFireTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.SCORCHING_RAY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFireTime = spellLevelFireTime;
        this.summonTod = d;
        this.summonTod2 = d2;
        this.summonTod3 = d3;
    }

    protected void hitEntity(Entity entity) {
        super.hitEntity(entity);
        if (!this.isUseful())
            return;
        this.setUseful(false);
        if (this.level().isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            Entity entity2 = this.getOwner();
            livingEntity.invulnerableTime = 0;
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.FIRE_MAGIC, this, entity2);
            boolean bl = livingEntity.hurt(damageSource,
                  Main.rollDice(2, 6, livingEntity.getRandom())
            );
            if (bl) {
                entity.setSecondsOnFire(spellLevelFireTime);
            }
            this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
        }
    }
    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.isUseful())
            return;
        this.setUseful(false);
    }
    protected void afterHasLineOfSight() {
        super.afterHasLineOfSight();
        if (!this.isUseful())
            return;
        this.setUseful(false);
        if (this.level().isClientSide) {
            return;
        }
        BlockPos center = BlockPos.containing(this.position());
        Level level = this.level();
        Entity owner = this.getOwner();
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx*dx + dy*dy + dz*dz > radius*radius) continue;
                    BlockPos pos = center.offset(dx, dy, dz);
                    Main.placeFire(level, pos, owner);
                    Main.igniteBlock(level, pos, owner);
                }
            }
        }

    }
    public BaseRayEntity getRay() {
        return new ScorchingRayEntity(this.spellLevelDamage, this.spellLevelFireTime, this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    public int getMaxReach() {
        return 36;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.SCORCHING_RAY.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }

    public int beamLightI() {
        return 0xea6121;
    }
    public int beamLightII() {
        return 0xef9324;
    }
}
