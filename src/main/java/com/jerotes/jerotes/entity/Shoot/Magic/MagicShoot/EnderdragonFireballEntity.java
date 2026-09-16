package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EnderdragonFireballEntity extends MagicAboutEntity {
    public EnderdragonFireballEntity(EntityType<? extends EnderdragonFireballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public EnderdragonFireballEntity(int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.ENDERDRAGON_FIREBALL.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
    }

    protected void onHit(HitResult p_36913_) {
        super.onHit(p_36913_);
        if (p_36913_.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)p_36913_).getEntity())) {
            if (!this.level().isClientSide()) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                SpellCloudEntity areaeffectcloud = new SpellCloudEntity(this.level(), this.getX(), this.getY(), this.getZ());
                Entity entity = this.getOwner();
                if (entity instanceof LivingEntity) {
                    areaeffectcloud.setOwner((LivingEntity)entity);
                }
                areaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
                areaeffectcloud.setRadius(3.0F);
                areaeffectcloud.setEnderdragonBreath(true);
                areaeffectcloud.setDuration(600);
                areaeffectcloud.setRadiusPerTick((7.0F - areaeffectcloud.getRadius()) / (float)areaeffectcloud.getDuration());
                areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HARM, spellLevelMainEffectTime, spellLevelMainEffectLevel-1));
                if (!list.isEmpty()) {
                    for(LivingEntity livingentity : list) {
                        double d0 = this.distanceToSqr(livingentity);
                        if (d0 < 16.0D) {
                            areaeffectcloud.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                            break;
                        }
                    }
                }
                this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level().addFreshEntity(areaeffectcloud);
                this.discard();
            }
        }
    }
    @Override
    public void lastBreak() {
        if (!this.level().isClientSide()) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
            SpellCloudEntity areaeffectcloud = new SpellCloudEntity(this.level(), this.getX(), this.getY(), this.getZ());
            Entity entity = this.getOwner();
            if (entity instanceof LivingEntity) {
                areaeffectcloud.setOwner((LivingEntity)entity);
            }
            areaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
            areaeffectcloud.setRadius(3.0F);
            areaeffectcloud.setEnderdragonBreath(true);
            areaeffectcloud.setDuration(600);
            areaeffectcloud.setRadiusPerTick((7.0F - areaeffectcloud.getRadius()) / (float)areaeffectcloud.getDuration());
            areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HARM, spellLevelMainEffectTime, spellLevelMainEffectLevel-1));
            if (!list.isEmpty()) {
                for(LivingEntity livingentity : list) {
                    double d0 = this.distanceToSqr(livingentity);
                    if (d0 < 16.0D) {
                        areaeffectcloud.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                        break;
                    }
                }
            }

            this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level().addFreshEntity(areaeffectcloud);
            this.discard();
        }
    }

    @Override
    public int getMaxLife() {
        return 60;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.95f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 0.8F;
    }

}
