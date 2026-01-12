package com.jerotes.jerotes.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class WaterAndGroundPathNavigation extends PathNavigation {
    public WaterAndGroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int n) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, n);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
    }

    @Override
    protected double getGroundY(Vec3 vec3) {
        return vec3.y;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec32) {
        if (this.mob.isInWater()) {
            return AmphibiousPathNavigation.isClearForMovementBetween(this.mob, vec3, vec32, false);
        }
        return false;
    }

    @Override
    public boolean isStableDestination(BlockPos blockPos) {
        return !this.level.getBlockState(blockPos.below()).isAir();
    }

    @Override
    public void setCanFloat(boolean bl) {
    }

    public void setCanOpenDoors(boolean bl) {
        this.nodeEvaluator.setCanOpenDoors(bl);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }
}

