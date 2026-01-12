package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class JerotesBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
    protected final Animal animal;
    private final Class<? extends Animal> partnerClass;
    protected final Level level;
    @Nullable
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;

    public JerotesBreedGoal(Animal p_25122_, double p_25123_) {
        this(p_25122_, p_25123_, p_25122_.getClass());
    }

    public JerotesBreedGoal(Animal p_25125_, double p_25126_, Class<? extends Animal> p_25127_) {
        this.animal = p_25125_;
        this.level = p_25125_.level();
        this.partnerClass = p_25127_;
        this.speedModifier = p_25126_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 ;
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        if (this.partner != null) {
            this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
            this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
            ++this.loveTime;
            if (this.loveTime >= this.adjustedTickDelay(60)
                    && (this.animal.distanceToSqr(this.partner) < 9.0
                    || this.animal.getBoundingBox().intersects(this.partner.getBoundingBox())
                    || this.animal.distanceTo(this.partner) < Main.mobWidth(this.animal) / 2 + Main.mobWidth(this.partner) / 2)) {
                this.breed();
            }
        }
    }

    @Nullable
    private Animal getFreePartner() {
        List<? extends Animal> $$0 = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
        double $$1 = Double.MAX_VALUE;
        Animal $$2 = null;

        for (Animal $$3 : $$0) {
            if (this.animal.canMate($$3) && this.animal.distanceToSqr($$3) < $$1) {
                $$2 = $$3;
                $$1 = this.animal.distanceToSqr($$3);
            }
        }

        return $$2;
    }

    protected void breed() {
        if (this.partner != null) {
            this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
        }
    }
}
