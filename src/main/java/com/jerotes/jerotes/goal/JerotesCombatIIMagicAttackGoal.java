package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.WizardEntity;
import com.jerotes.jerotes.item.MagicItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

public class JerotesCombatIIMagicAttackGoal<T extends PathfinderMob> extends JerotesMeleeAttackGoal {
    private final float canSeeAsMeleeDistance;
    public JerotesCombatIIMagicAttackGoal(T t, double d, boolean bl, float f) {
        super(t, d, bl);
        canSeeAsMeleeDistance = f;
    }

    public boolean canUse() {
        if (!(this.mob instanceof WizardEntity wizardEntity && wizardEntity.getCombatStyle() == 2)) {
            return false;
        }
        return this.mob.getTarget() != null && isHoldingMagic(mob, canSeeAsMeleeDistance) && super.canUse();
    }

    public static boolean isHoldingMagic(Mob mob, float f) {
        boolean noWeapon = !JerotesRangedBowAttackGoal.isHoldingBow(mob)
                && !JerotesRangedCrossbowAttackGoal.isHoldingCrossbow(mob)
                && !JerotesRangedThrowAttackGoal.isHoldingThrow(mob)
                && !JerotesRangedJavelinAttackGoal.isHoldingRangedJavelin(mob, f);
        boolean main = mob.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getMainHandItem()) && !magicItem.isMelee(mob.getMainHandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean off = mob.getOffhandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getOffhandItem()) && !magicItem.isMelee(mob.getOffhandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean mainSpell = (mob instanceof WizardEntity wizardEntity && !wizardEntity.MainSpellList().isEmpty());
        boolean offSpell = (mob instanceof WizardEntity wizardEntity && !wizardEntity.AddSpellList().isEmpty());
        return noWeapon && (main || off || mainSpell || offSpell);
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && isHoldingMagic(mob, canSeeAsMeleeDistance) && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setTarget(null);
        this.mob.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(livingEntity, 360.0f, 360.0f);
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
            this.pathedTargetX = livingEntity.getX();
            this.pathedTargetY = livingEntity.getY();
            this.pathedTargetZ = livingEntity.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            double d = this.mob.distanceToSqr(livingEntity);
            if (d > 1024.0) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (d > 256.0) {
                this.ticksUntilNextPathRecalculation += 5;
            }
            if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
            this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
        }
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
    }
}

