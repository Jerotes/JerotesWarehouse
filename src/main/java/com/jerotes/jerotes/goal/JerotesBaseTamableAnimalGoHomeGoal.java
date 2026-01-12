package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.HasHomePosEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class JerotesBaseTamableAnimalGoHomeGoal extends Goal {
    private final PathfinderMob magicSummoned;
    private final double speedModifier;

    public JerotesBaseTamableAnimalGoHomeGoal(PathfinderMob magicSummoned, double d) {
        this.magicSummoned = magicSummoned;
        this.speedModifier = d;
    }

    @Override
    public boolean canUse() {
        if (this.magicSummoned.isAggressive()) {
            return false;
        }
        if (((ChangePoseAbout)this.magicSummoned).getChangeType() != 3) {
            return false;
        }
        return !((HasHomePosEntity)this.magicSummoned).getHomePos().closerToCenterThan(this.magicSummoned.position(), 0);
    }
    @Override
    public boolean canContinueToUse() {
        if (this.magicSummoned.isAggressive()) {
            return false;
        }
        if (this.unableToMove()) {
            return false;
        }
        if (((ChangePoseAbout)this.magicSummoned).getChangeType() != 3) {
            return false;
        }
        return !((HasHomePosEntity)this.magicSummoned).getHomePos().closerToCenterThan(this.magicSummoned.position(), 0);
    }

    @Override
    public void start() {
        ((HasHomePosEntity)this.magicSummoned).setGoingHome(true);
    }

    @Override
    public void stop() {
        ((HasHomePosEntity)this.magicSummoned).setGoingHome(false);
    }


    private boolean unableToMove() {
        return ((ChangePoseAbout)this.magicSummoned).getChangeType() != 3 || this.magicSummoned.isPassenger() || this.magicSummoned.isLeashed();
    }


    private void teleportTo() {
        BlockPos blockPos = ((HasHomePosEntity)this.magicSummoned).getHomePos();
        for(int i = 0; i < 10; ++i) {
            int n = this.randomIntInclusive(-3, 3);
            int n2 = this.randomIntInclusive(-1, 1);
            int n3 = this.randomIntInclusive(-3, 3);
            boolean bl = this.maybeTeleportTo(blockPos.getX() + n, blockPos.getY() + n2, blockPos.getZ() + n3);
            if (bl) {
                return;
            }
        }
    }
    private int randomIntInclusive(int n, int n2) {
        return ((Mob)this.magicSummoned).getRandom().nextInt(n2 - n + 1) + n;
    }
    private boolean maybeTeleportTo(int n, int n2, int n3) {
        if (Math.abs((double)n - ((HasHomePosEntity)this.magicSummoned).getHomePos().getX()) < 2.0 && Math.abs((double)n3 - ((HasHomePosEntity)this.magicSummoned).getHomePos().getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(n, n2, n3))) {
            return false;
        } else {
            ((Mob)this.magicSummoned).moveTo((double)n + 0.5, (double)n2, (double)n3 + 0.5, ((Mob)this.magicSummoned).getYRot(), ((Mob)this.magicSummoned).getXRot());
            return true;
        }
    }
    private boolean canTeleportTo(BlockPos blockPos) {
        BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(this.magicSummoned.level(), blockPos.mutable());
        if (pathType != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.magicSummoned.level().getBlockState(blockPos.below());
            BlockPos blockPos2 = blockPos.subtract(((Mob)this.magicSummoned).blockPosition());
            return this.magicSummoned.level().noCollision((Mob)this.magicSummoned, ((Mob)this.magicSummoned).getBoundingBox().move(blockPos2));
        }
    }

    @Override
    public void tick() {
        BlockPos blockPos = ((HasHomePosEntity)this.magicSummoned).getHomePos();
        if (blockPos.distToCenterSqr(this.magicSummoned.getOnPos().getCenter()) <= 1024) {
            if (this.magicSummoned.distanceToSqr(blockPos.getCenter()) >= 144.0) {
                this.teleportTo();
            } else {
                this.magicSummoned.getNavigation().moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, this.speedModifier);
            }
        }
    }
}