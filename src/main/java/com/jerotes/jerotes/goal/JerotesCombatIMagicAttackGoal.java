package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.InventoryEntity;
import com.jerotes.jerotes.entity.WizardEntity;
import com.jerotes.jerotes.item.MagicItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class JerotesCombatIMagicAttackGoal<T extends PathfinderMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private final float canSeeAsMeleeDistance;
    private final float attackRadiusSqr;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public JerotesCombatIMagicAttackGoal(T t, double d, float f, float f2) {
        this.mob = t;
        this.speedModifier = d;
        this.canSeeAsMeleeDistance = f2;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.mob instanceof WizardEntity wizardEntity && wizardEntity.getCombatStyle() != 1) {
            return false;
        }
        return this.mob.getTarget() != null && isHoldingMagic(mob, canSeeAsMeleeDistance);
    }

    public static boolean isHoldingMagic(Mob mob, float f) {
        boolean noWeapon = !JerotesRangedBowAttackGoal.isHoldingBow(mob)
                && !JerotesRangedCrossbowAttackGoal.isHoldingCrossbow(mob)
                && !JerotesRangedThrowAttackGoal.isHoldingThrow(mob)
                && !JerotesRangedJavelinAttackGoal.isHoldingRangedJavelin(mob, f);
        boolean main = !(InventoryEntity.isMeleeWeapon(mob.getMainHandItem()))
                && mob.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getMainHandItem()) && !magicItem.isMelee(mob.getMainHandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean off = !(InventoryEntity.isMeleeWeapon(mob.getMainHandItem()))
                && mob.getOffhandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getOffhandItem()) && !magicItem.isMelee(mob.getOffhandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean mainSpell = !(InventoryEntity.isMeleeWeapon(mob.getMainHandItem()))
                && (mob instanceof WizardEntity wizardEntity && !wizardEntity.MainSpellList().isEmpty());
        boolean offSpell = !(InventoryEntity.isMeleeWeapon(mob.getMainHandItem()))
                && (mob instanceof WizardEntity wizardEntity && !wizardEntity.AddSpellList().isEmpty());
        return noWeapon && (main || off || mainSpell || offSpell);
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && isHoldingMagic(mob, canSeeAsMeleeDistance);
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.mob.setTarget(null);
        this.mob.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                Entity entity = this.mob.getControlledVehicle();
                if (entity instanceof Mob mob) {
                    mob.lookAt(livingentity, 360.0f, 360.0f);
                }
                this.mob.lookAt(livingentity, 360.0f, 360.0f);
            } else {
                this.mob.getLookControl().setLookAt(livingentity, 360.0f, 360.0f);
            }
        }
    }
}

