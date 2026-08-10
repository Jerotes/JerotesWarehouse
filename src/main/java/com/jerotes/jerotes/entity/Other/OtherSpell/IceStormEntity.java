package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.BreakShieldEntity;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.UseShieldEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class IceStormEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, BreakShieldEntity {
    public static final int RADIUS = 6;
    public static final int HEIGHT_UP = 13;
    public static final int WARNING_TICK = 20;
    private static final int LINGER_TICK = 120;

    public IceStormEntity(EntityType<? extends IceStormEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public IceStormEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.ICE_STORM.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 4;
    }
    public int getMaxStart() {
        return 40;
    }
    public int getMaxLife() {
        return 120;
    }
    public boolean shouldPush() {
        return this.getLifeTick() > 30;
    }
    protected void doPush(Entity entity) {
        double radius = 6.0;
        double radiusSq = radius * radius;
        if (shouldPush() && entity.distanceToSqr(this) <= radiusSq && entity.onGround()) {
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.5D, 1.0D, 0.5D));
        }
    }

    @Override
    public void tick() {
        super.tick();
        //粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 12; ++i) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
            }
            if (this.getLifeTick() > 20) {
                float f = Math.min(0.5f, (this.getLifeTick() - 20) / 30f);
                for (int i = 0; i < 36; ++i) {
                    double x = this.getRandomX(f);
                    double y = this.getY() + 0.2f;
                    double z = this.getRandomZ(f);
                    double dx = x - this.getX();
                    double dz = z - this.getZ();
                    if (dx * dx + dz * dz <= 25.0) {
                        serverLevel.sendParticles(
                                ParticleTypes.SNOWFLAKE,
                                x, y, z,
                                0, 0, 0, 0, 0
                        );
                        serverLevel.sendParticles(
                                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.FROSTED_ICE.defaultBlockState()),
                                x, y, z,
                                0, 0, 0, 0, 0
                        );
                    }
                }
            }
        }
        //攻击
        if (this.getLifeTick() == 20) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1,0,1));
            List<LivingEntity> list2 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().deflate(1,0,1));
            double radius = 6.0;
            double radiusSq = radius * radius;
            for (LivingEntity livingEntity : list) {
                if (livingEntity.distanceToSqr(this) <= radiusSq || list2.contains(livingEntity)) {
                    this.dealDamageTo(livingEntity);
                }
            }
        }
        if (this.getLifeTick() >= 20 && this.getLifeTick() < 30) {
            this.playSound(SoundEvents.GLASS_BREAK, 3.0f, 1.0f);
            this.playSound(SoundEvents.GLASS_HIT, 3.0f, 1.0f);
        }
        //冰化
        if (this.level() instanceof ServerLevel serverLevel && getLifeTick() == 20) {
            BlockPos center = this.blockPosition();
            int radius = 6;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        BlockState state = serverLevel.getBlockState(pos);
                        if (state.getFluidState().getType() == Fluids.WATER && state.getFluidState().isSource()) {
                            serverLevel.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
                        }
                        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                            serverLevel.removeBlock(pos, false);
                        }
                        if (state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT)) {
                            serverLevel.setBlock(
                                    pos,
                                    state.setValue(CampfireBlock.LIT, false),
                                    Block.UPDATE_ALL
                            );
                        }
                        if (state.hasProperty(AbstractCandleBlock.LIT) && state.getValue(AbstractCandleBlock.LIT)) {
                            serverLevel.setBlock(
                                    pos,
                                    state.setValue(AbstractCandleBlock.LIT, false),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int dy = -12; dy < 0; dy++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        BlockState state = serverLevel.getBlockState(pos);
                        if (state.getFluidState().getType() == Fluids.WATER && state.getFluidState().isSource()) {
                            serverLevel.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
                        }
                        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                            serverLevel.removeBlock(pos, false);
                        }
                        if (state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT)) {
                            serverLevel.setBlock(
                                    pos,
                                    state.setValue(CampfireBlock.LIT, false),
                                    Block.UPDATE_ALL
                            );
                        }
                        if (state.hasProperty(AbstractCandleBlock.LIT) && state.getValue(AbstractCandleBlock.LIT)) {
                            serverLevel.setBlock(
                                    pos,
                                    state.setValue(AbstractCandleBlock.LIT, false),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }
        }
    }

    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (livingEntity.getUseItem().getItem() instanceof ShieldItem) {
            if (livingEntity instanceof Player player) {
                player.disableShield(true);
            } else if (livingEntity instanceof UseShieldEntity useShieldEntity) {
                useShieldEntity.disableShield();
            }
        }
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_FREEZE, this);
        DamageSource damageSource2 = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT, this);
        if (livingEntity2 != null) {
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_FREEZE, this, livingEntity2);
            damageSource2 = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT, this);
        }
        boolean saving = EntityAndItemFind.savingSpellThrow(livingEntity);
        boolean bl = livingEntity.hurt(damageSource,
                Main.rollDice(4, 6, getRandom()) * (saving ? 0.5f : 1f)
        );
        boolean bl2 = livingEntity.hurt(damageSource2,
                Main.rollDice((2 + (getSpellLevel() - 4)), 10, getRandom()) * (saving ? 0.5f : 1f)
        );
        if (bl || bl2) {
            livingEntity.setTicksFrozen(Math.max(livingEntity.getTicksFrozen(), 140 + 20 * (6 + getSpellLevel() * 3)));
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 24; ++i) {
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
        return true;
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }

    @Override
    public int getShieldBreakStrength() {
        return 100;
    }
}

