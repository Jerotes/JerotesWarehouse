package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class JerotesAnimalPanicGoal extends Goal {
    public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
    protected final Animal mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean isRunning;

    public JerotesAnimalPanicGoal(Animal pathfinderMob, double d) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos blockPos;
        if (!this.shouldPanic()) {
            return false;
        }
        if (this.mob.isOnFire() && (blockPos = this.lookForWater(this.mob.level(), this.mob, 5)) != null) {
            this.posX = blockPos.getX();
            this.posY = blockPos.getY();
            this.posZ = blockPos.getZ();
            return true;
        }
        if (!this.mob.isBaby()) {
            return false;
        }
        return this.findRandomPosition();
    }

    protected boolean shouldPanic() {
        return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
    }

    protected boolean findRandomPosition() {
        Vec3 vec3 = DefaultRandomPos.getPos(this.mob, 5, 4);
        if (vec3 == null) {
            return false;
        }
        this.posX = vec3.x;
        this.posY = vec3.y;
        this.posZ = vec3.z;
        return true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Nullable
    protected BlockPos lookForWater(BlockGetter blockGetter, Entity entity, int n) {
        BlockPos blockPos2 = entity.blockPosition();
        if (!blockGetter.getBlockState(blockPos2).getCollisionShape(blockGetter, blockPos2).isEmpty()) {
            return null;
        }
        return BlockPos.findClosestMatch(entity.blockPosition(), n, 1, blockPos -> blockGetter.getFluidState((BlockPos)blockPos).is(FluidTags.WATER)).orElse(null);
    }
}

