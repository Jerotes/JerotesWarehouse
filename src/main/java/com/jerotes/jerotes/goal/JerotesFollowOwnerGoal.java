/*
 * Decompiled with CFR 0.146.
 */
package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class JerotesFollowOwnerGoal extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
    private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public JerotesFollowOwnerGoal(TamableAnimal p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
        this.tamable = p_25294_;
        this.level = p_25294_.level();
        this.speedModifier = p_25295_;
        this.navigation = p_25294_.getNavigation();
        this.startDistance = p_25296_;
        this.stopDistance = p_25297_;
        this.canFly = p_25298_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.tamable.getTarget() != null) {
            return false;
        }
        LivingEntity $$0 = this.tamable.getOwner();
        if ($$0 == null) {
            return false;
        } else if ($$0.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.tamable.distanceToSqr($$0) < (double)(this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = $$0;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.tamable.getTarget() != null) {
            return false;
        }
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
        }
    }

    private boolean unableToMove() {
        return this.tamable.isOrderedToSit() || this.tamable.isPassenger() || this.tamable.isLeashed() || tamable.getOwner() != null && tamable.level() != tamable.getOwner().level();
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.tamable.distanceToSqr(this.owner) >= 144.0) {
                this.teleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }

        }
    }

    private void teleportToOwner() {
        BlockPos $$0 = this.owner.blockPosition();

        for(int $$1 = 0; $$1 < 10; ++$$1) {
            int $$2 = this.randomIntInclusive(-3, 3);
            int $$3 = this.randomIntInclusive(-1, 1);
            int $$4 = this.randomIntInclusive(-3, 3);
            boolean $$5 = this.maybeTeleportTo($$0.getX() + $$2, $$0.getY() + $$3, $$0.getZ() + $$4);
            if ($$5) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
        if (Math.abs((double)p_25304_ - this.owner.getX()) < 2.0 && Math.abs((double)p_25306_ - this.owner.getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
            return false;
        } else {
            this.tamable.moveTo((double)p_25304_ + 0.5, (double)p_25305_, (double)p_25306_ + 0.5, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos p_25308_) {
        BlockPathTypes $$1 = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, p_25308_.mutable());
        if ($$1 != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState $$2 = this.level.getBlockState(p_25308_.below());
            if (!this.canFly && $$2.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos $$3 = p_25308_.subtract(this.tamable.blockPosition());
                return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move($$3));
            }
        }
    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.tamable.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }
}
