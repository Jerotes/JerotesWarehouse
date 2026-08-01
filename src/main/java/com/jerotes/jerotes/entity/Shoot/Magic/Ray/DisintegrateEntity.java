package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.entity.Interface.DisintegrateLikeEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DisintegrateEntity extends BaseRayEntity implements DisintegrateLikeEntity {
    public DisintegrateEntity(EntityType<? extends DisintegrateEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DisintegrateEntity(EntityType<? extends DisintegrateEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public DisintegrateEntity(int spellLevelDamage, int spellSubcategory, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.DISINTEGRATE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellSubcategory = spellSubcategory;
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
            //敏捷判定
            if (!EntityAndItemFind.savingSpellThrow(livingEntity)){
                DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.FORCE, this, this.getOwner());
                Main.getJerotesPersistentData(livingEntity).putUUID("jerotes_disintegrate_damage_from", this.getUUID());
                Main.getJerotesPersistentData(livingEntity).putFloat("jerotes_disintegrate_tick", 10);
                boolean bl = livingEntity.hurt(damageSource,
                        Main.rollDice(10 + 3 * (spellLevelDamage - 6), 6, livingEntity.getRandom())
                                + 40
                );
                if (bl && !livingEntity.isAlive()) {
                    if (!EntityAndItemFind.isLoreUndying(livingEntity)) {
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 60; ++i) {
                                serverLevel.sendParticles(JerotesParticleTypes.DISINTEGRATE.get(), livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0.0, -0.2f, 0.0, 0.2f);
                                serverLevel.sendParticles(ParticleTypes.ASH, livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0.0, 0.2f, -0.0, 0.2f);
                                serverLevel.sendParticles(ParticleTypes.SMOKE, livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0.0, 0.2f, -0.0, 0.2f);
                            }
                        }
                        livingEntity.remove(RemovalReason.KILLED);
                    }
                }
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
        if (!this.isUseful()) return;
        if (this.level().isClientSide) return;

        BlockPos centerPos = BlockPos.containing(this.position());
        LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null;

        if (this.spellSubcategory == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos pos = centerPos.offset(dx, dy, dz);
                        BlockState state = this.level().getBlockState(pos);
                        if (state.isAir()) continue;

                        float hardness = state.getDestroySpeed(this.level(), pos);
                        if (hardness < 0) continue;

                        int threshold = 40 + Math.max(0, (spellLevelDamage - 6)) * 10;
                        if (hardness >= threshold) continue;

                        boolean canDestroy = true;
                        if (owner != null && !(owner instanceof Player)) {
                            if (!net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(owner, pos, state)) {
                                canDestroy = false;
                            }
                            if (!net.minecraftforge.common.ForgeHooks.canEntityDestroy(owner.level(), pos, owner)) {
                                canDestroy = false;
                            }
                        }

                        if (canDestroy) {
                            this.level().destroyBlock(pos, false, owner);
                        }
                    }
                }
            }
        }

        this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }

    public BaseRayEntity getRay() {
        return new DisintegrateEntity(this.spellLevelDamage, spellSubcategory, this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.DISINTEGRATE.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        if (this.isUseful()) {
            return itemStack.isEmpty() ? new ItemStack(JerotesItems.DISINTEGRATE.get()) : itemStack;
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
        return 0.75f;
    }
    public int beamLightI() {
        return 0x18a127;
    }
    public int beamLightII() {
        return 0x5df478;
    }

    @Override
    public boolean shouldDisintegrate(LivingEntity livingEntity, ItemStack itemStack) {
        return itemStack.getItem().getRarity(itemStack) != Rarity.EPIC;
    }
}
