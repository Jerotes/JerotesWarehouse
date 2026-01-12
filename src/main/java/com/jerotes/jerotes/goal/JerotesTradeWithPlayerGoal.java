package com.jerotes.jerotes.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;

import java.util.EnumSet;

public class JerotesTradeWithPlayerGoal
extends Goal {
    private final Mob mob;

    public JerotesTradeWithPlayerGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        }
        if (this.mob.isInWater()) {
            return false;
        }
        if (!this.mob.onGround()) {
            return false;
        }
        if (this.mob.hurtMarked) {
            return false;
        }
        Player player = ((Merchant)this.mob).getTradingPlayer();
        if (player == null) {
            return false;
        }
        if (this.mob.distanceToSqr(player) > 16.0) {
            return false;
        }
        return player.containerMenu != null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        ((Merchant)this.mob).setTradingPlayer(null);
    }
}

