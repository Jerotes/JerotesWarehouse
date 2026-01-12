package com.jerotes.jerotes.entity.magic.Breath;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class PoisonBreathEntity extends BaseBreathEntity {
    public PoisonBreathEntity(EntityType<? extends PoisonBreathEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PoisonBreathEntity(EntityType<? extends PoisonBreathEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public PoisonBreathEntity(int spellLevelDamage, int spellLevelMaxDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.POISON_BREATH.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelMaxDamage = spellLevelMaxDamage;
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();

        if (entity instanceof LivingEntity livingEntity) {
            Entity entity2 = this.getOwner();
            boolean bl = false;
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.POISON, this, entity2);
            if (this.random.nextFloat() > (livingEntity.getMaxHealth() / 12 + 5) / 100){
                bl = livingEntity.hurt(damageSource, spellLevelDamage *  Main.randomReach(RandomSource.create(), 1, spellLevelMaxDamage));
            }
            else{
                bl =  livingEntity.hurt(damageSource, (float) (spellLevelDamage * Main.randomReach(RandomSource.create(), 1, spellLevelMaxDamage)) /2);
            }
            if (bl) {
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.DEADLY_POISON.get(), 20 * spellLevelMainEffectTime * 6, spellLevelMainEffectLevel - 1), this.getEffectSource());
                this.playSound(JerotesSounds.SPELL, 3.0f, 1.0f);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().getBlockState(BlockPos.containing(blockHitResult.getLocation().x, blockHitResult.getLocation().y, blockHitResult.getLocation().z)).getBlock() == Blocks.WATER) {
            return;
        }
        if (!this.level().isClientSide) {
            this.Cloud(blockHitResult.getLocation().x, blockHitResult.getLocation().y, blockHitResult.getLocation().z);
            this.discard();
        }
    }

    @Override
    public boolean hasCloud() {
        return true;
    }
    @Override
    public float getCloudRadius() {
        return 1f;
    }
    @Override
    public float getCloudEffectTimeMultiple() {
        return 6f;
    }
    @Override
    public ParticleOptions getCloudParticle() {
        return JerotesParticleTypes.POISON_BREATH_FOG.get();
    }
    @Override
    public MobEffect getCloudEffect() {
        return JerotesMobEffects.DEADLY_POISON.get();
    }

    @Override
    public int getMaxLife() {
        return 30;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.POISON_BREATH.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.POISON_BREATH.get()) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 0.95f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 0.6f;
    }
}