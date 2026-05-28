package com.jerotes.jerotes.goal;

import net.minecraft.world.entity.Mob;

public class JerotesOpenTrapdoorGoal extends JerotesTrapDoorInteractGoal {
    private final boolean closeDoor;
    private int forgetTime;

    public JerotesOpenTrapdoorGoal(Mob mob, boolean bl) {
        super(mob);
        this.mob = mob;
        this.closeDoor = bl;
    }

    @Override
    public boolean canContinueToUse() {
        return this.closeDoor && this.forgetTime > 0 && super.canContinueToUse();
    }

    @Override
    public void start() {
        this.forgetTime = 20;
        this.setOpen(true);
    }

    @Override
    public void stop() {
        this.setOpen(false);
    }

    @Override
    public void tick() {
        --this.forgetTime;
        super.tick();
    }
}

