package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Iterator;

public class JerotesTameBreathAirGoal extends Goal {
    private final PathfinderMob mob;

    public JerotesTameBreathAirGoal(PathfinderMob p_25103_) {
        this.mob = p_25103_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            return false;
        }
        return this.mob.getAirSupply() < 140;
    }

    public boolean canContinueToUse() {
        if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            return false;
        }
        return this.canUse();
    }

    public boolean isInterruptable() {
        return false;
    }

    public void start() {
        this.findAirPosition();
    }

    private void findAirPosition() {
        Iterable<BlockPos> $$0 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 1.0), this.mob.getBlockY(), Mth.floor(this.mob.getZ() - 1.0), Mth.floor(this.mob.getX() + 1.0), Mth.floor(this.mob.getY() + 8.0), Mth.floor(this.mob.getZ() + 1.0));
        BlockPos $$1 = null;
        Iterator var3 = $$0.iterator();

        while(var3.hasNext()) {
            BlockPos $$2 = (BlockPos)var3.next();
            if (this.givesAir(this.mob.level(), $$2)) {
                $$1 = $$2;
                break;
            }
        }

        if ($$1 == null) {
            $$1 = BlockPos.containing(this.mob.getX(), this.mob.getY() + 8.0, this.mob.getZ());
        }

        this.mob.getNavigation().moveTo((double)$$1.getX(), (double)($$1.getY() + 1), (double)$$1.getZ(), 1.0);
    }

    public void tick() {
        this.findAirPosition();
        this.mob.moveRelative(0.02F, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }

    private boolean givesAir(LevelReader p_25107_, BlockPos p_25108_) {
        BlockState $$2 = p_25107_.getBlockState(p_25108_);
        return (p_25107_.getFluidState(p_25108_).isEmpty() || $$2.is(Blocks.BUBBLE_COLUMN)) && $$2.isPathfindable(p_25107_, p_25108_, PathComputationType.LAND);
    }
}
