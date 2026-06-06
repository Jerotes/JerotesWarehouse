package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.block.Interface.JerotesChangeFenceGate;
import com.jerotes.jerotes.block.Interface.JerotesChangeTrapDoor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class JerotesFenceGateInteractGoal extends Goal {
    protected Mob mob;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public JerotesFenceGateInteractGoal(Mob mob) {
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
        if (!this.mob.horizontalCollision) return false;
        PathNavigation navigation = this.mob.getNavigation();
        Path path = navigation.getPath();
        if (path == null || path.isDone()) return false;
        int checkNodes = Math.min(path.getNextNodeIndex() + 2, path.getNodeCount());
        for (int i = 0; i < checkNodes; i++) {
            Node node = path.getNode(i);
            BlockPos nodePos = new BlockPos(node.x, node.y, node.z);
            if (isFenceGateAt(nodePos) && isNearDoor(nodePos)) {
                this.doorPos = nodePos;
                this.hasDoor = true;
                return true;
            }
            BlockPos abovePos = nodePos.above();
            if (isFenceGateAt(abovePos) && isNearDoor(abovePos)) {
                this.doorPos = abovePos;
                this.hasDoor = true;
                return true;
            }
        }
        BlockPos posAbove = this.mob.blockPosition().above();
        if (isFenceGateAt(posAbove) && isNearDoor(posAbove)) {
            this.doorPos = posAbove;
            this.hasDoor = true;
            return true;
        }

        return false;
    }

    private boolean isFenceGateAt(BlockPos pos) {
        return this.mob.level().getBlockState(pos).getBlock() instanceof JerotesChangeFenceGate fenceGate && fenceGate.isJerotesWoodenDoor();
    }

    private boolean isNearDoor(BlockPos pos) {
        double dx = pos.getX() + 0.5 - this.mob.getX();
        double dz = pos.getZ() + 0.5 - this.mob.getZ();
        return dx * dx + dz * dz <= 2.25;
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