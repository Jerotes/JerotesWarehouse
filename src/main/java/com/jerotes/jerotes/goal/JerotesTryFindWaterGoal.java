package com.jerotes.jerotes.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class JerotesTryFindWaterGoal extends Goal {
    private final Mob mob;

    public JerotesTryFindWaterGoal(Mob pathfinderMob) {
        this.mob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        return this.mob.onGround() && !this.mob.level().getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    public void start() {
        Vec3i vec3i = null;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0), Mth.floor(this.mob.getY() - 2.0), Mth.floor(this.mob.getZ() - 2.0), Mth.floor(this.mob.getX() + 2.0), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + 2.0));
        for (BlockPos blockPos : iterable) {
            if (!this.mob.level().getFluidState(blockPos).is(FluidTags.WATER)) continue;
            vec3i = blockPos;
            break;
        }
        if (vec3i != null) {
            this.mob.getMoveControl().setWantedPosition(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 1.0);
        }
    }
}

