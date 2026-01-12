package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class JerotesTwoDoorInteractGoal
extends Goal {
    protected Mob mob;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public JerotesTwoDoorInteractGoal(Mob mob) {
        this.mob = mob;
        if (!GoalUtils.hasGroundPathNavigation(mob)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        }
        BlockState blockState = this.mob.level().getBlockState(this.doorPos);
        if (!(blockState.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
        }
        return blockState.getValue(DoorBlock.OPEN);
    }

    protected void setOpen(boolean bl) {
        BlockState blockState;
        if (this.hasDoor && (blockState = this.mob.level().getBlockState(this.doorPos)).getBlock() instanceof DoorBlock) {
            ((DoorBlock)blockState.getBlock()).setOpen(this.mob, this.mob.level(), blockState, this.doorPos, bl);
        }
        if (this.hasDoor && (this.mob.level().getBlockState(this.doorPos.east())).getBlock() instanceof DoorBlock) {
            ((DoorBlock)this.mob.level().getBlockState(this.doorPos.east()).getBlock()).setOpen(this.mob, this.mob.level(), this.mob.level().getBlockState(this.doorPos.east()), this.doorPos.east(), bl);
        }
        if (this.hasDoor && (this.mob.level().getBlockState(this.doorPos.west())).getBlock() instanceof DoorBlock) {
            ((DoorBlock)this.mob.level().getBlockState(this.doorPos.west()).getBlock()).setOpen(this.mob, this.mob.level(), this.mob.level().getBlockState(this.doorPos.west()), this.doorPos.west(), bl);

        }
        if (this.hasDoor && (this.mob.level().getBlockState(this.doorPos.north())).getBlock() instanceof DoorBlock) {
            ((DoorBlock)this.mob.level().getBlockState(this.doorPos.north()).getBlock()).setOpen(this.mob, this.mob.level(), this.mob.level().getBlockState(this.doorPos.north()), this.doorPos.north(), bl);
        }
        if (this.hasDoor && (this.mob.level().getBlockState(this.doorPos.south())).getBlock() instanceof DoorBlock) {
            ((DoorBlock)this.mob.level().getBlockState(this.doorPos.south()).getBlock()).setOpen(this.mob, this.mob.level(), this.mob.level().getBlockState(this.doorPos.south()), this.doorPos.south(), bl);
        }
    }

    @Override
    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
            return false;
        }
        if (!this.mob.horizontalCollision) {
            return false;
        }
        GroundPathNavigation groundPathNavigation = (GroundPathNavigation)this.mob.getNavigation();
        Path path = groundPathNavigation.getPath();
        if (path == null || path.isDone() || !groundPathNavigation.canOpenDoors()) {
            return false;
        }
        for (int i = 0; i < Math.min(path.getNextNodeIndex() + 2, path.getNodeCount()); ++i) {
            Node node = path.getNode(i);
            this.doorPos = new BlockPos(node.x, node.y + 1, node.z);
            if (this.mob.distanceToSqr(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25) continue;
            this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level(), this.doorPos);
            if (!this.hasDoor) continue;
            return true;
        }
        this.doorPos = this.mob.blockPosition().above();
        this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level(), this.doorPos);
        return this.hasDoor;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        this.doorOpenDirZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        float f;
        float f2 = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        float f3 = this.doorOpenDirX * f2 + this.doorOpenDirZ * (f = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ()));
        if (f3 < 0.0f) {
            this.passed = true;
        }
    }
}

