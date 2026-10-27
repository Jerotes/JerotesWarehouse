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
import com.jerotes.jerotes.block.IceSurfaceBlock;

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

    @Override
    public void tick() {
        super.tick();
        //粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 12; ++i) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
            }
        }
        //攻击
        if (this.getLifeTick() == 20) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1,0,1));
            double radius = 6.0;
            double radiusSq = radius * radius;
            for (LivingEntity livingEntity : list) {
                if (livingEntity.distanceToSqr(this) <= (radiusSq + ((Main.mobWidth(livingEntity) / 2) * (Main.mobWidth(livingEntity) / 2)))) {
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
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }
                    for (int dy = -radius; dy <= radius; dy++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        Main.freezeBlock(serverLevel, pos, getOwner());
                        BlockState here = serverLevel.getBlockState(pos);
                        if (here.isAir() || here.getBlock() instanceof IceSurfaceBlock) {
                            if (here.getBlock() instanceof IceSurfaceBlock || here.isAir() && serverLevel.getBlockState(pos.below()).isSolid()) {
                                Main.placeIceSurface(serverLevel, pos, 120);
                            }
                        }
                        Main.extinguishFire(serverLevel, pos, getOwner());
                    }
                }
            }
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }
                    for (int dy = -12; dy < 0; dy++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        Main.freezeBlock(serverLevel, pos, getOwner());
                        Main.extinguishFire(serverLevel, pos, getOwner());
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
        if (AttackFind.FindCanNotAttack(this.getOwner(), livingEntity))
            return false;
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
    public int getShieldBreakStrength() {
        return 100;
    }
}

