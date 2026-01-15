package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.TameMobEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class JerotesTameMobFollowOwnerGoal extends Goal {
    private final TameMobEntity tameMob;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public JerotesTameMobFollowOwnerGoal(TameMobEntity tameMob, double d, float f, float f2, boolean bl) {
        this.tameMob = tameMob;
        this.level = ((Mob)tameMob).level();
        this.speedModifier = d;
        this.navigation = ((Mob)tameMob).getNavigation();
        this.startDistance = f;
        this.stopDistance = f2;
        this.canFly = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.tameMob instanceof Mob mob && mob.getTarget() != null) {
            return false;
        }
        LivingEntity livingEntity = ((OwnableEntity)tameMob).getOwner();
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity.isSpectator()) {
            return false;
        }
        if (this.unableToMove()) {
            return false;
        }
        if (((Mob)tameMob).distanceToSqr(livingEntity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        }
        this.owner = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.tameMob instanceof Mob mob && mob.getTarget() != null) {
            return false;
        }
        if (this.navigation.isDone()) {
            return false;
        }
        if (this.unableToMove()) {
            return false;
        }
        return !(((Mob)tameMob).distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
    }

    private boolean unableToMove() {
        return this.tameMob.isOrderedToSit() || ((Mob)tameMob).isPassenger() || ((Mob)tameMob).isLeashed() || ((OwnableEntity)tameMob).getOwner() != null && ((Mob)tameMob).level() != ((OwnableEntity)tameMob).getOwner().level();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = ((Mob)tameMob).getPathfindingMalus(BlockPathTypes.WATER);
        ((Mob)tameMob).setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        ((Mob)tameMob).setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        ((Mob)tameMob).getLookControl().setLookAt(this.owner, 10.0f, ((Mob)tameMob).getMaxHeadXRot());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = this.adjustedTickDelay(10);
        if (((Mob)tameMob).distanceToSqr(this.owner) >= 144.0) {
            this.teleportToOwner();
        } else {
            this.navigation.moveTo(this.owner, this.speedModifier);
        }
    }

    private void teleportToOwner() {
        BlockPos blockPos = this.owner.blockPosition();
        for (int i = 0; i < 10; ++i) {
            int n = this.randomIntInclusive(-3, 3);
            int n2 = this.randomIntInclusive(-1, 1);
            int n3 = this.randomIntInclusive(-3, 3);
            boolean bl = this.maybeTeleportTo(blockPos.getX() + n, blockPos.getY() + n2, blockPos.getZ() + n3);
            if (!bl) continue;
            return;
        }
    }

    private boolean maybeTeleportTo(int n, int n2, int n3) {
        if (Math.abs((double)n - this.owner.getX()) < 2.0 && Math.abs((double)n3 - this.owner.getZ()) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos(n, n2, n3))) {
            return false;
        }
        ((Mob)tameMob).moveTo((double)n + 0.5, n2, (double)n3 + 0.5, ((Mob)tameMob).getYRot(), ((Mob)tameMob).getXRot());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos blockPos) {
        BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, blockPos.mutable());
        if (pathType != BlockPathTypes.WALKABLE) {
            return false;
        }
        BlockState blockState = this.level.getBlockState(blockPos.below());
        if (!this.canFly && blockState.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos blockPos2 = blockPos.subtract(((Mob)tameMob).blockPosition());
        return this.level.noCollision(((Mob)tameMob), ((Mob)tameMob).getBoundingBox().move(blockPos2));
    }

    private int randomIntInclusive(int n, int n2) {
        return ((Mob)tameMob).getRandom().nextInt(n2 - n + 1) + n;
    }
}

