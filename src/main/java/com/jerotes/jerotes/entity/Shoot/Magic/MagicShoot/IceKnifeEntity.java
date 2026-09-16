package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.IceKnifeShardEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;

public class IceKnifeEntity extends MagicAboutEntity {
    public IceKnifeEntity(EntityType<? extends IceKnifeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public IceKnifeEntity(int spellLevelDamage, int spellLevelFreezeTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.ICE_KNIFE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFreezeTime = spellLevelFreezeTime;
    }
    @Override
    public void tick() {
        super.tick();
        this.lookAt(EntityAnchorArgument.Anchor.FEET, this.position().add(getDeltaMovement()));
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT, this, this.getOwner());
            boolean bl = livingEntity.hurt(damageSource,
                    Main.rollDice(1, 10, livingEntity.getRandom())
            );
            if (bl) {
                if (livingEntity.getTicksFrozen() < 140 + 20 * spellLevelFreezeTime) {
                    livingEntity.setTicksFrozen(140 + 20 * spellLevelFreezeTime);
                }
            }
            this.iceKnife(livingEntity);
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide) {
            return;
        }
        BlockPos center = blockHitResult.getBlockPos();
        Level level = this.level();
        Entity owner = this.getOwner();
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx*dx + dy*dy + dz*dz > radius*radius) continue;
                    BlockPos pos = center.offset(dx, dy, dz);
                    Main.freezeBlock(level, pos, owner);
                    Main.extinguishFire(level, pos, owner);
                }
            }
        }
    }
    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            int count = 40;
            for (int i = 0; i < count; i++) {
                double dx = (random.nextDouble() - 0.5) * 2.0;
                double dy = (random.nextDouble() - 0.5) * 2.0;
                double dz = (random.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
                serverLevel.sendParticles(JerotesParticleTypes.FROST.get(),
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
            }
        }
        this.iceKnife(this);
        this.discard();
    }

    @Override
    public void lastBreak() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            int count = 40;
            for (int i = 0; i < count; i++) {
                double dx = (random.nextDouble() - 0.5) * 2.0;
                double dy = (random.nextDouble() - 0.5) * 2.0;
                double dz = (random.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
                serverLevel.sendParticles(JerotesParticleTypes.FROST.get(),
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
            }
        }
        this.iceKnife(this);
    }
    public void iceKnife(Entity entity) {
        this.playSound(SoundEvents.GLASS_BREAK, 3.0f, 1.0f);
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 12; ++i) {
                IceKnifeShardEntity shard = new IceKnifeShardEntity(
                        JerotesEntityType.ICE_KNIFE_SHARD.get(),
                        this.level()
                );
                shard.setPos(entity.getX(), entity.getY(), entity.getZ());
                shard.setDeltaMovement(
                        (this.random.nextFloat() * 2.0F - 1.0F) / 3.0F,
                        0.45F,
                        (this.random.nextFloat() * 2.0F - 1.0F) / 3.0F
                );
                if (this.getOwner() != null) {
                    shard.setOwner(this.getOwner());
                }
                serverLevel.addFreshEntity(shard);
            }
        }
        Vec3 center = entity.position();
        double radius = 1.5D;
        AABB box = new AABB(
                center.x - radius,
                center.y - radius,
                center.z - radius,
                center.x + radius,
                center.y + radius,
                center.z + radius
        );
        List<LivingEntity> list = this.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                livingEntity -> livingEntity.isAlive()
                        && livingEntity != this.getOwner()
                        && livingEntity != entity
        );

        for (LivingEntity livingEntity : list) {
            this.dealDamageTo(livingEntity);
        }
    }
    public void dealDamageTo(LivingEntity livingEntity) {
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_FREEZE, this, this.getOwner());
        boolean bl = livingEntity.hurt(damageSource,
                Main.rollDice(2 + (spellLevelDamage - 1), 6, livingEntity.getRandom())
        );
        if (bl) {
            if (livingEntity.getTicksFrozen() < 140 + 20 * spellLevelFreezeTime) {
                livingEntity.setTicksFrozen(140 + 20 * spellLevelFreezeTime);
            }
        }
    }
    @Override
    public int getMaxLife() {
        return (int) (18 * MainConfig.SomeBulletSpellReachMultiplier);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }
    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }
}
