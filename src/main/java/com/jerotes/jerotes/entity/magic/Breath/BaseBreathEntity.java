package com.jerotes.jerotes.entity.magic.Breath;

import com.jerotes.jerotes.entity.magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;

public class BaseBreathEntity extends MagicAboutEntity {
    public BaseBreathEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseBreathEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseBreathEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
    }

    @Override
    public void tick() {
        if (this.life >= this.getMaxLife() && this.hasCloud()) {
            this.Cloud(this.getX(), this.getY(), this.getZ());
        }
        super.tick();
        if (this.getTrailParticle() != null) {
            for (int i = 0; i < 5 + life; ++i) {
                this.level().addParticle(this.getTrailParticle(), this.getX() + ((this.random.nextDouble() - 0.5d) * getBbWidth() * (life * 0.1)), this.getY() + ((this.random.nextDouble() - 0.5d) * getBbWidth() * (life * 0.1)), this.getZ() + ((this.random.nextDouble() - 0.5d) * getBbWidth() * (life * 0.1)), 0.0d, 0.0d, 0.0d);
            }
        }
    }

    public boolean Cloud(double x, double y, double z) {
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
        list.removeIf(livingEntity -> livingEntity == this.getOwner());
        AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level(), x, y, z);
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            areaEffectCloud.setOwner((LivingEntity)entity);
        }
        areaEffectCloud.setParticle(this.getCloudParticle());
        areaEffectCloud.setRadius(this.getCloudRadius());
        areaEffectCloud.setDuration((int) (20 * spellLevelMainEffectTime * this.getCloudEffectTimeMultiple()));
        areaEffectCloud.setRadiusPerTick((2.0f - areaEffectCloud.getRadius()) / (float)areaEffectCloud.getDuration());
        areaEffectCloud.addEffect(new MobEffectInstance(getCloudEffect(), 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel-1));
        if (!list.isEmpty()) {
            for (LivingEntity livingEntity : list) {
                double d = this.distanceToSqr(livingEntity);
                if (!(d < 16.0)) continue;
                areaEffectCloud.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                break;
            }
        }
        this.playSound(JerotesSounds.BREATH, 1.0f, 1.0f);
        this.level().addFreshEntity(areaEffectCloud);
        return true;
    }

    public boolean hasCloud() {
        return true;
    }
    public float getCloudRadius() {
        return 1f;
    }
    public float getCloudEffectTimeMultiple() {
        return 1f;
    }
    public ParticleOptions getCloudParticle() {
        return JerotesParticleTypes.NULL.get();
    }
    public MobEffect getCloudEffect() {
        return MobEffects.POISON;
    }

    @Override
    public int getMaxLife() {
        return 30;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
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