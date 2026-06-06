package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.block.Interface.JerotesChangeTrapDoor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class JerotesTrapDoorInteractGoal extends Goal {
    protected Mob mob;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public JerotesTrapDoorInteractGoal(Mob mob) {
        this.mob = mob;
    }

    protected boolean isOpen() {
        if (!this.hasDoor) return false;
        BlockState blockState = this.mob.level().getBlockState(this.doorPos);
        if (!(blockState.getBlock() instanceof FenceGateBlock)) {
            this.hasDoor = false;
            return false;
        }
        return blockState.getValue(FenceGateBlock.OPEN);
    }

    protected void setOpen(boolean open) {
        if (this.hasDoor) {
            BlockState blockstate = this.mob.level().getBlockState(this.doorPos);
            if (blockstate.getBlock() instanceof FenceGateBlock) {
                this.mob.level().setBlock(this.doorPos, blockstate.setValue(FenceGateBlock.OPEN, open), 10);
            }
        }
    }

    @Override
    public boolean canUse() {
        PathNavigation navigation = this.mob.getNavigation();
        Path path = navigation.getPath();
        if (path == null || path.isDone()) return false;
        BlockPos posBelow = this.mob.blockPosition().below();
        if (isTrapDoorAt(posBelow)) {
            this.doorPos = posBelow;
            this.hasDoor = true;
            return true;
        }
        BlockPos pos = this.mob.blockPosition();
        if (isTrapDoorAt(pos)) {
            this.doorPos = pos;
            this.hasDoor = true;
            return true;
        }

        return false;
    }

    private boolean isTrapDoorAt(BlockPos pos) {
        return this.mob.level().getBlockState(pos).getBlock() instanceof JerotesChangeTrapDoor trapDoorBlock && trapDoorBlock.isJerotesWoodenDoor();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float) (this.doorPos.getX() + 0.5 - this.mob.getX());
        this.doorOpenDirZ = (float) (this.doorPos.getZ() + 0.5 - this.mob.getZ());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        float dx = (float) (this.doorPos.getX() + 0.5 - this.mob.getX());
        float dz = (float) (this.doorPos.getZ() + 0.5 - this.mob.getZ());
        float dot = this.doorOpenDirX * dx + this.doorOpenDirZ * dz;
        if (dot < 0.0F) {
            this.passed = true;
        }
    }
}