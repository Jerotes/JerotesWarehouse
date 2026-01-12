package com.jerotes.jerotes.goal;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class JerotesFlyingRandomStrollGoal extends Goal {
    public static final int DEFAULT_INTERVAL = 120;
    protected final PathfinderMob mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;

    public JerotesFlyingRandomStrollGoal(PathfinderMob pathfinderMob, double d) {
        this(pathfinderMob, d, 120);
    }

    public JerotesFlyingRandomStrollGoal(PathfinderMob pathfinderMob, double d, int n) {
        this(pathfinderMob, d, n, true);
    }

    public JerotesFlyingRandomStrollGoal(PathfinderMob pathfinderMob, double d, int n, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.interval = n;
        this.checkNoActionTime = bl;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Vec3 vec3;
        if (this.mob.hasControllingPassenger()) {
            return false;
        }
        if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(JerotesFlyingRandomStrollGoal.reducedTickDelay(this.interval)) != 0) {
                return false;
            }
        }
        if ((vec3 = this.getPosition()) == null) {
            return false;
        }
        this.wantedX = vec3.x;
        this.wantedY = vec3.y;
        this.wantedZ = vec3.z;
        this.forceTrigger = false;
        return true;
    }

    @Nullable
    protected Vec3 getPosition() {
        RandomSource random = this.mob.getRandom();
        double dir_x = this.mob.getX() + ((random.nextFloat() * 2 - 1) * 16);
        double dir_y = this.mob.getY() + ((random.nextFloat() * 2 - 1) * 16);
        double dir_z = this.mob.getZ() + ((random.nextFloat() * 2 - 1) * 16);
        return new Vec3(dir_x, dir_y, dir_z);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.hasControllingPassenger();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    public void setInterval(int n) {
        this.interval = n;
    }
}

