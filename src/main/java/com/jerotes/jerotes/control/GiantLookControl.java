package com.jerotes.jerotes.control;

import com.jerotes.jerotes.entity.Interface.StopLook;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class GiantLookControl extends LookControl {
    private final int maxYRotFromCenter;
    private final float speed;
    public GiantLookControl(Mob mob, int n) {
        super(mob);
        this.maxYRotFromCenter = n;
        this.speed = 1.5f;
    }
    public GiantLookControl(Mob mob, int n, float speed) {
        super(mob);
        this.maxYRotFromCenter = n;
        this.speed = speed;
    }

    @Override
    public void tick() {
        if (this.mob instanceof StopLook stopLook && stopLook.stopLookTime()) {
            return;
        }
        if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            this.getYRotD().ifPresent(f -> {
                this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, f.floatValue(), this.yMaxRotSpeed);
            });
            this.getXRotD().ifPresent(f -> this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), f.floatValue(), this.xMaxRotAngle)));
        } else {
            if (this.mob.getNavigation().isDone()) {
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0f, 5.0f));
            }
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }
        float f2 = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (f2 < (float)(-this.maxYRotFromCenter)) {
            this.mob.yBodyRot -= speed;
        } else if (f2 > (float)this.maxYRotFromCenter) {
            this.mob.yBodyRot += speed;
        }
    }
}

