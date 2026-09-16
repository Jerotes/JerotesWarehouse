package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesFallingBlock;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.List;
import java.util.Random;

public class ThunderwaveEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    public ThunderwaveEntity(EntityType<? extends ThunderwaveEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThunderwaveEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.THUNDERWAVE.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    @Override
    public int getMaxLife() {
        return 60;
    }
    @Override
    public boolean moveToTarget() {
        return true;
    }
    public void moveToTargetTick() {
        Entity target = this.getTarget();
        if (target != null) {
            Vec3 look = getLookAngle(target).normalize();
            Vec3 targetPos = target.position()
                    .add(0,  target.onGround() ? Main.mobHeight(target) : Main.mobHeight(target)/2, 0)
                    .add(0, -Main.mobHeight(this)/2, 0)
                    .add(look.scale(Main.mobWidth(this)/2));
            Vec3 currentPos = this.position();
            double speed = 0.5;
            Vec3 newPos = currentPos.lerp(targetPos, speed);
            this.setPos(newPos.x, newPos.y, newPos.z);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getLifeTick() == 1) {
                this.playSound(JerotesSoundEvents.WIND_SHORT, 5.625f, 1.0f);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 5.625f, 1.0f);
            }
            if (this.getLifeTick() == 10) {
                Entity caster = this.getOwner() != null ? this.getOwner() : this;
                if (caster == null) return;

                Vec3 origin = caster.position();
                Vec3 lookVec = getLookAngle(caster);
                Vec3 forward = new Vec3(lookVec.x, lookVec.y, lookVec.z).normalize();
                Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();

                double halfLength = 4.5;
                double halfWidth = 2.25;
                double halfHeight = 2.25;
                AABB roughBox = AABB.ofSize(origin, 9, 9, 9);
                BlockPos.betweenClosedStream(roughBox).forEach(blockPos -> {
                    Vec3 posVec = Vec3.atCenterOf(blockPos);
                    Vec3 toPos = posVec.subtract(origin);
                    double forwardDist = toPos.dot(forward);
                    double rightDist = toPos.dot(right);
                    double verticalDist = toPos.y;

                    if (forwardDist >= 0 && forwardDist <= halfLength
                            && Math.abs(rightDist) <= halfWidth
                            && Math.abs(verticalDist) <= halfHeight) {
                        spawnFallingBlockByPos(serverLevel, blockPos, 0.2f);
                    }
                });

                List<Entity> list = this.level().getEntitiesOfClass(Entity.class, roughBox, e -> e != this && e != caster);
                for (Entity entity : list) {
                    Vec3 toTarget = entity.position().subtract(origin);
                    double forwardDist = toTarget.dot(forward);
                    double rightDist = toTarget.dot(right);
                    double verticalDist = toTarget.y;

                    if (forwardDist >= 0 && forwardDist <= halfLength
                            && Math.abs(rightDist) <= halfWidth
                            && Math.abs(verticalDist) <= halfHeight) {

                        if (entity instanceof LivingEntity livingEntity) {
                            this.dealDamageTo(livingEntity);
                        }
                        else if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity livingEntity) {
                            this.dealDamageTo(livingEntity);
                        }
                        else if (entity instanceof ItemEntity itemEntity) {
                            Vec3 horizontalDir = forward;
                            double speed = 0.75;
                            double verticalSpeed = 0.2;
                            itemEntity.setDeltaMovement(
                                    horizontalDir.x * speed,
                                    horizontalDir.y * speed,
                                    horizontalDir.z * speed
                            );
                            itemEntity.hurtMarked = true;
                        }
                    }
                }
            }
        }
        if (this.level().isClientSide()) {
            int life = this.getLifeTick();
            Entity caster = this.getOwner() != null ? this.getOwner() : this;
            Vec3 look = caster.getViewVector(1.0f);
            if (look.lengthSqr() < 0.001) return;
            look = look.normalize();

            RandomSource random = this.getRandom();

            Vec3 worldUp = new Vec3(0, 1, 0);
            Vec3 right = look.cross(worldUp);
            if (right.lengthSqr() < 0.001) {
                right = look.cross(new Vec3(1, 0, 0));
            }
            right = right.normalize();
            Vec3 up = right.cross(look).normalize();

            Vec3 origin = this.getOwner() != null ? this.getOwner().position().add(0,2,0) : this.position();

            double halfWidth = 2.25;
            double halfHeight = 2.25;
            double halfLength = 4.5;
            int impactTick = 10;

            if (life >= 1 && life < impactTick) {
                float windupT = Mth.clamp((float) life / impactTick, 0f, 1f);
                int count = 3 + (int) (windupT * 3);

                for (int i = 0; i < count; i++) {
                    float baseAngle = (float) (2 * Math.PI * random.nextDouble());
                    double radius = Mth.lerp(1f - windupT, halfWidth * 1.1, halfWidth * 0.25) * (0.6 + random.nextDouble() * 0.4);
                    double forwardOff = 0.3 + random.nextDouble() * 1.2;

                    Vec3 ringCenter = origin.add(look.scale(forwardOff));
                    Vec3 ringPos = ringCenter
                            .add(right.scale(Math.cos(baseAngle) * radius))
                            .add(up.scale(Math.sin(baseAngle) * radius));

                    Vec3 tangent = right.scale(-Math.sin(baseAngle)).add(up.scale(Math.cos(baseAngle)));
                    double spinSpeed = 0.12 + random.nextDouble() * 0.06;
                    double inwardSpeed = 0.03 + windupT * 0.05;

                    Vec3 inward = ringPos.subtract(ringCenter).normalize().scale(-inwardSpeed);
                    Vec3 vel = tangent.scale(spinSpeed).add(inward);

                    this.level().addParticle(JerotesParticleTypes.THUNDERWAVE.get(),
                            ringPos.x, ringPos.y, ringPos.z,
                            vel.x, vel.y, vel.z);
                }
            } else if (life >= impactTick && life <= impactTick + 6) {
                float burstT = Mth.clamp((float) (life - impactTick) / 6f, 0f, 1f);
                int count = 8;

                for (int i = 0; i < count; i++) {
                    double distAlong = random.nextDouble() * halfLength;
                    double widthAtDist = halfWidth * (0.3 + 0.7 * (distAlong / halfLength));
                    double heightAtDist = halfHeight * (0.3 + 0.7 * (distAlong / halfLength));

                    float spinAngle = (float) (2 * Math.PI * random.nextDouble());
                    double rightOff = Math.cos(spinAngle) * widthAtDist * (0.3 + random.nextDouble() * 0.7);
                    double upOff = Math.sin(spinAngle) * heightAtDist * (0.3 + random.nextDouble() * 0.7);

                    Vec3 pos = origin
                            .add(look.scale(distAlong))
                            .add(right.scale(rightOff))
                            .add(up.scale(upOff));

                    Vec3 tangent = right.scale(-Math.sin(spinAngle)).add(up.scale(Math.cos(spinAngle)));
                    double forwardSpeed = 0.32 + random.nextDouble() * 0.15 * (1f - burstT * 0.3f);
                    double spinSpeed = 0.08 + random.nextDouble() * 0.05;

                    Vec3 vel = look.scale(forwardSpeed).add(tangent.scale(spinSpeed));

                    this.level().addParticle(JerotesParticleTypes.THUNDERWAVE.get(),
                            pos.x, pos.y, pos.z,
                            vel.x, vel.y, vel.z);
                }
            }
        }
    }
    public void spawnFallingBlockByPos(ServerLevel level, BlockPos pos, float fallingFactor) {
        Random random = new Random();
        BlockPos abovePos = new BlockPos(pos).above();
        BlockState block = level.getBlockState(pos);
        BlockState blockAbove = level.getBlockState(abovePos);
        if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
            JerotesFallingBlock fallingBlock = new JerotesFallingBlock(level, block, fallingFactor * (1 + this.getRandom().nextFloat() / 3));
            fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            level.addFreshEntity(fallingBlock);
        }
    }
    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (AttackFind.FindCanNotAttack(this.getOwner(), livingEntity))
            return false;
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.THUNDER, this);
        if (livingEntity2 != null) {
            if (AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
                return false;
            }
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.THUNDER, this, livingEntity2);
        }

        if (!EntityAndItemFind.savingSpellThrow(livingEntity)){
            livingEntity.hurt(damageSource, Main.rollDice(2 + (getSpellLevel() - 1), 6, livingEntity.getRandom()));
            if (!EntityAndItemFind.isNoSpecialKnockback(livingEntity.getType())) {
                Entity caster = this.getOwner() != null ? this.getOwner() : this;
                if (caster != null) {
                    Vec3 lookVec = getLookAngle(caster);
                    Vec3 horizontalDir = new Vec3(lookVec.x, lookVec.y, lookVec.z).normalize();
                    if (!horizontalDir.equals(Vec3.ZERO)) {
                        double speed = 1.5f;
                        double verticalSpeed = 0.2;
                        livingEntity.setDeltaMovement(
                                horizontalDir.x * speed,
                                horizontalDir.y * speed,
                                horizontalDir.z * speed
                        );
                        livingEntity.hurtMarked = true;
                    }
                }
            }
        }
        else {
            livingEntity.hurt(damageSource,
                    Main.rollDice(8 + (getSpellLevel() - 3), 6, livingEntity.getRandom()) / 2
            );
        }
        return true;
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }

    protected static Vec3 getLookAngle(Entity entity) {
        return entity.getViewVector(1.0f);
    }
}

