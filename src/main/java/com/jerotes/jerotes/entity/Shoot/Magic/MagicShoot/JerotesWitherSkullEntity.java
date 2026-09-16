package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class JerotesWitherSkullEntity extends MagicAboutEntity {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(JerotesWitherSkullEntity.class, EntityDataSerializers.BOOLEAN);

    public JerotesWitherSkullEntity(EntityType<? extends JerotesWitherSkullEntity> entityType, Level level) {
        super(entityType, level);
    }

    public JerotesWitherSkullEntity(int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelExplode, int spellSubcategory, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.JEROTES_WITHER_SKULL.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
        this.spellLevelExplode = spellLevelExplode;
        this.spellSubcategory = spellSubcategory;
    }

    public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
        if (blockState.isAir() || WitherBoss.canDestroy(blockState))
            return this.isDangerous() && blockState.canEntityDestroy(blockGetter, blockPos, this) ? Math.min(0.8F, f) : f;
        return f;
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }
    public void setDangerous(boolean p_37630_) {
        this.entityData.set(DATA_DANGEROUS, p_37630_);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("IsDangerous", this.isDangerous());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setDangerous(compoundTag.getBoolean("IsDangerous"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        boolean bl;
        DamageSource damageSource = AttackFind.findDamageType(this, DamageTypes.WITHER_SKULL, this, this.getOwner());
        if (owner instanceof LivingEntity livingOwner) {
            int baseDamage = this.isDangerous() ? 3 : 1;
            bl = entity.hurt(damageSource, 8.0F + (4.0f * (spellLevelDamage - baseDamage)));
            if (bl) {
                if (entity.isAlive()) {
                    this.doEnchantDamageEffects(livingOwner, entity);
                } else {
                    livingOwner.heal(5.0F);
                }
            }
        } else {
            bl = entity.hurt(this.damageSources().magic(), 5.0F);
        }
        if (bl && entity instanceof LivingEntity target) {
//            int i = 0;
//            Difficulty difficulty = this.level().getDifficulty();
//            if (difficulty == Difficulty.NORMAL) {
//                i = 10;
//            } else if (difficulty == Difficulty.HARD) {
//                i = 40;
//            }
//            if (i > 0) {
//                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 1), this.getEffectSource());
//            }
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), this.getEffectSource());
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.spellLevelExplode, false, spellSubcategory == 1? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
    @Override
    public void lastBreak() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.spellLevelExplode, false, spellSubcategory == 1? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.MOB);
        }
    }

    @Override
    public int getMaxLife() {
        return 120;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : itemstack;
    }
    public boolean isOnFire() {
        return false;
    }
    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73F : 0.95f;
    }
    //@Override
    protected float getLiquidInertia() {
        return 0.8F;
    }
}
