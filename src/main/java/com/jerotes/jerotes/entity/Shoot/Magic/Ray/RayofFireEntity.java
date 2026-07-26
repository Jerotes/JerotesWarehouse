package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class RayofFireEntity extends BaseRayEntity {
    public RayofFireEntity(EntityType<? extends RayofFireEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RayofFireEntity(EntityType<? extends RayofFireEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public RayofFireEntity(int spellLevelDamage, int spellLevelFireTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.RAY_OF_FIRE.get(), livingEntity, d, d2, d3, level);
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
        if (this.level().isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            Entity entity2 = this.getOwner();
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.FIRE_MAGIC, this, entity2);
            boolean bl = livingEntity.hurt(damageSource,
                    //单数多出0.5
                    (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ?
                            Main.rollDice(1 + (spellLevelDamage - 2) / 2, 8, livingEntity.getRandom()) + Main.rollDice(1, 8, livingEntity.getRandom()) / 0.5f :
                            Main.rollDice(1 + (spellLevelDamage - 1) / 2, 8, livingEntity.getRandom())
            );
            if (bl) {
                entity.setSecondsOnFire(spellLevelFireTime);
            }
            this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
            this.setUseful(false);
        }
    }
    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }
    protected void afterHasLineOfSight() {
        super.afterHasLineOfSight();
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }

    public BaseRayEntity getRay() {
        return new RayofFireEntity(this.spellLevelDamage, this.spellLevelFireTime, this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
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
        return 0xb02604;
    }
    public int beamLightII() {
        return 0xce6d28;
    }
}
