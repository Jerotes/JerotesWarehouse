package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireBoltEntity extends BaseMagicBoltEntity {
    public FireBoltEntity(EntityType<? extends FireBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FireBoltEntity(int spellLevelDamage, int spellLevelFireTime, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.FIRE_BOLT.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFireTime = spellLevelFireTime;
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.FIRE_MAGIC, this, this.getOwner());
            boolean bl = livingEntity.hurt(damageSource,
                    //单数多出0.5
                    (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ?
                            Main.rollDice(1 + (spellLevelDamage - 2) / 2, 10, livingEntity.getRandom()) + Main.rollDice(1, 10, livingEntity.getRandom()) * 0.5f :
                            Main.rollDice(1 + (spellLevelDamage - 1) / 2, 10, livingEntity.getRandom())
            );
            if (bl) {
                entity.setSecondsOnFire(spellLevelFireTime);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = this.getOwner();
        if (!(entity instanceof Mob) || this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (this.level().isEmptyBlock(blockPos)) {
                this.level().setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level(), blockPos));
            }
            this.discard();
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
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
            }
        }
        this.discard();
    }

    @Override
    public int getMaxLife() {
        return 80;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    @Override
    protected float getInertia() {
        return 0.95f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 0.6f;
    }

    public int beamLightI() {
        return 0xea9d21;
    }
    public int beamLightII() {
        return 0xefd024;
    }
    public int beamLightIII() {
        return 0xb02604;
    }
    public ResourceLocation TextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/fire_bolt.png");
    }
}
