package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Iterator;

public class JerotesTameTryFindWaterGoal extends Goal {
    private final PathfinderMob mob;

    public JerotesTameTryFindWaterGoal(PathfinderMob p_25964_) {
        this.mob = p_25964_;
    }

    public boolean canUse() {
        if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            return false;
        }
        return this.mob.onGround() && !this.mob.level().getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
    }

    public void start() {
        BlockPos $$0 = null;
        Iterable<BlockPos> $$1 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0), Mth.floor(this.mob.getY() - 2.0), Mth.floor(this.mob.getZ() - 2.0), Mth.floor(this.mob.getX() + 2.0), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + 2.0));
        Iterator var3 = $$1.iterator();

        while(var3.hasNext()) {
            BlockPos $$2 = (BlockPos)var3.next();
            if (this.mob.level().getFluidState($$2).is(FluidTags.WATER)) {
                $$0 = $$2;
                break;
            }
        }

        if ($$0 != null) {
            this.mob.getMoveControl().setWantedPosition((double)$$0.getX(), (double)$$0.getY(), (double)$$0.getZ(), 1.0);
        }

    }
}
