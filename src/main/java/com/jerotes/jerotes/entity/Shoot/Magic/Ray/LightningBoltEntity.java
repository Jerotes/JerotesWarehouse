package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class LightningBoltEntity extends BaseRayEntity {
    public LightningBoltEntity(EntityType<? extends LightningBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningBoltEntity(EntityType<? extends LightningBoltEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public LightningBoltEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.LIGHTNING_BOLT.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
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
            DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), this, this.getOwner());
            double noUse = 0;
            if (livingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                noUse = livingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            }
            if (this.random.nextFloat() > (noUse / 3 + 35) / 100){
                livingEntity.hurt(damageSource, (spellLevelDamage + 5) * Main.randomReach(RandomSource.create(), 1, 6));
            }
            else{
                livingEntity.hurt(damageSource, (float) ((spellLevelDamage + 5) * Main.randomReach(RandomSource.create(), 1, 6)) /2);
            }
            this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 10.0f, 1.0f);
        }
    }

    public boolean canNotHurtLastHurt() {
        return false;
    }
    public boolean useUUIDFindHurtAndDisable() {
        return true;
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }

    public BaseRayEntity getRay() {
        return new LightningBoltEntity(this.spellLevelDamage, this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.LIGHTNING_BOLT.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        if (this.isUseful()) {
            return itemStack.isEmpty() ? new ItemStack(JerotesItems.LIGHTNING_BLOT.get()) : itemStack;
        }
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

    public float beamScale() {
        return 8.0f;
    }
    public int beamLightI() {
        return 0x6a68ff;
    }
    public int beamLightII() {
        return 0x9998eb;
    }
}
