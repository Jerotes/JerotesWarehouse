package com.jerotes.jerotes.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ImprovedGroundPathNavigation extends GroundPathNavigation {

    private double waypointReachFactor = 0.7;

    private double verticalReachTolerance = 1.2;

    private int stuckCheckInterval = 80;

    private double stuckMinMovementRatio = 0.15;

    private boolean widthAwarePathfinding = true;

    private Vec3 lastCheckPos = Vec3.ZERO;
    private int lastCheckTick = -100;
    private double expectedMoveDistance = 0.0;
    private int noProgressTicks = 0;
    private BlockPos lastPathTarget = null;
    public final PathFinder pathFinder;
    public boolean isStuck;

    public ImprovedGroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
        if (widthAwarePathfinding) {
            this.nodeEvaluator = new WidthAwareWalkNodeEvaluator(mob.getBbWidth());
            this.pathFinder = new PathFinder(this.nodeEvaluator, 200);
        }
        else {
            int i = Mth.floor(mob.getAttributeValue(Attributes.FOLLOW_RANGE) * 16.0D);
            this.pathFinder = this.createPathFinder(i);
        }
    }

    public void setWaypointReachFactor(double factor) { this.waypointReachFactor = factor; }
    public void setVerticalReachTolerance(double tolerance) { this.verticalReachTolerance = tolerance; }
    public void setStuckCheckInterval(int interval) { this.stuckCheckInterval = interval; }
    public void setWidthAwarePathfinding(boolean enable) { this.widthAwarePathfinding = enable; }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void followThePath() {
        if (this.path == null || this.path.isDone()) {
            return;
        }

        Vec3 currentPos = getTempMobPos();
        float dynamicReach = (float) (mob.getBbWidth() * waypointReachFactor);
        dynamicReach = Math.max(dynamicReach, 0.3f);

        Node nextNode = this.path.getNextNode();
        Vec3 nextNodePos = Vec3.atBottomCenterOf(nextNode.asBlockPos());

        double dx = Math.abs(currentPos.x - nextNodePos.x);
        double dz = Math.abs(currentPos.z - nextNodePos.z);
        double dy = Math.abs(currentPos.y - nextNodePos.y);

        boolean horizontalReached = dx <= dynamicReach && dz <= dynamicReach;
        boolean verticalReached = dy <= verticalReachTolerance;

        if (horizontalReached && verticalReached) {
            this.path.advance();
            noProgressTicks = 0;
        } else {
            if (horizontalReached && !verticalReached) {
                noProgressTicks = 0;
            } else if (dx <= 0.2 && dz <= 0.2 && dy <= 1.0) {
                noProgressTicks++;
            } else {
                noProgressTicks = 0;
            }
        }

        if (this.path.getNextNodeIndex() + 1 < this.path.getNodeCount()) {
            Node nextNextNode = this.path.getNode(this.path.getNextNodeIndex() + 1);
            Vec3 nextNextPos = Vec3.atBottomCenterOf(nextNextNode.asBlockPos());
            if (canMoveDirectly(currentPos, nextNextPos)) {
                this.path.advance();
            }
        }

        if (!this.path.isDone()) {
            Vec3 targetPos = this.path.getNextEntityPos(mob);
            double targetY = getGroundY(targetPos);
            this.mob.getMoveControl().setWantedPosition(targetPos.x, targetY, targetPos.z, this.speedModifier);
        }
    }

    @Override
    protected void doStuckDetection(Vec3 currentPos) {
        if (this.path == null || this.path.isDone()) {
            resetStuckStatus();
            return;
        }

        int tickDiff = this.tick - lastStuckCheck;
        if (tickDiff >= stuckCheckInterval) {
            double moveSpeed = this.mob.getSpeed();
            double expectedDist = moveSpeed * tickDiff;
            double actualDistSq = currentPos.distanceToSqr(lastCheckPos);
            double actualDist = Math.sqrt(actualDistSq);

            boolean isSeverelyStuck = actualDist < expectedDist * stuckMinMovementRatio;
            boolean nodeNotAdvancing = (noProgressTicks > 60);

            if (isSeverelyStuck || nodeNotAdvancing) {
                if (isActuallyStuck(currentPos)) {
                    this.isStuck = true;
                    this.stop();
                    return;
                } else {
                    noProgressTicks = 0;
                }
            } else {
                this.isStuck = false;
            }

            lastCheckPos = currentPos;
            lastStuckCheck = this.tick;
        }

        super.doStuckDetection(currentPos);
    }

    private boolean isActuallyStuck(Vec3 currentPos) {
        if (this.path == null || this.path.isDone()) return false;
        Node nextNode = this.path.getNextNode();
        Vec3 target = Vec3.atBottomCenterOf(nextNode.asBlockPos());
        boolean lineBlocked = !canMoveDirectly(currentPos, target);
        return lineBlocked && noProgressTicks > 40;
    }

    private void resetStuckStatus() {
        this.isStuck = false;
        noProgressTicks = 0;
        lastStuckCheck = this.tick - stuckCheckInterval;
    }

    @Nullable
    @Override
    protected Path createPath(java.util.Set<BlockPos> targets, int range, boolean canJumpOutOfWater, int maxDistanceToTarget, float followRange) {
        float adjustedRange = followRange + (float) (mob.getBbWidth() - 1.0f) * 2.0f;
        return super.createPath(targets, range, canJumpOutOfWater, maxDistanceToTarget, adjustedRange);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (mob.getBbWidth() > 1.5f) {
            int radius = (int) Math.ceil(mob.getBbWidth() / 2.0);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = pos.offset(dx, -1, dz);
                    if (!this.level.getBlockState(checkPos).isSolid()) {
                        return false;
                    }
                }
            }
        }
        return super.isStableDestination(pos);
    }

    private static class WidthAwareWalkNodeEvaluator extends WalkNodeEvaluator {
        private final float entityWidth;

        public WidthAwareWalkNodeEvaluator(float width) {
            this.entityWidth = width;
        }

        @Override
        public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
            if (entityWidth > 1.0f) {
                int half = (int) Math.ceil(entityWidth / 2.0);
                boolean allWalkable = true;
                for (int dx = -half; dx <= half; dx++) {
                    for (int dz = -half; dz <= half; dz++) {
                        BlockPathTypes subType = super.getBlockPathType(level, x + dx, y, z + dz);
                        if (subType != BlockPathTypes.WALKABLE && subType != BlockPathTypes.OPEN) {
                            allWalkable = false;
                            break;
                        }
                    }
                }
                if (allWalkable) return BlockPathTypes.WALKABLE;
                return BlockPathTypes.BLOCKED;
            }
            return super.getBlockPathType(level, x, y, z);
        }
    }
}