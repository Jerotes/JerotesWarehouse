package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.WizardEntity;
import com.jerotes.jerotes.item.Interface.MagicItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class JerotesCombatIVMagicAttackGoal<T extends PathfinderMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private final float canSeeAsMeleeDistance;
    private final float attackRadiusSqr;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private float fleeDistanceSqr;
    private int fleeTimer = 0;

    public JerotesCombatIVMagicAttackGoal(T t, double d, float f, float f2) {
        this.mob = t;
        this.speedModifier = d;
        this.canSeeAsMeleeDistance = f2;
        this.attackRadiusSqr = f * f;
        this.fleeDistanceSqr = f2 * f2;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!(this.mob instanceof WizardEntity wizardEntity && wizardEntity.getCombatStyle() == 4)) {
            return false;
        }
        return this.mob.getTarget() != null && isHoldingMagic(mob, canSeeAsMeleeDistance);
    }

    public static boolean isHoldingMagic(Mob mob, float f) {
        float reach = 8;
        if (mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        if (InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= reach) {
            return false;
        }
        boolean main = mob.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getMainHandItem()) && !magicItem.isMelee(mob.getMainHandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean off = mob.getOffhandItem().getItem() instanceof MagicItem magicItem && !magicItem.isHelp(mob.getOffhandItem()) && !magicItem.isMelee(mob.getOffhandItem()) && magicItem.getSpellDistance(mob.getMainHandItem()) > f;
        boolean mainSpell = (mob instanceof WizardEntity wizardEntity && !wizardEntity.MainSpellList().isEmpty());
        boolean offSpell = (mob instanceof WizardEntity wizardEntity && !wizardEntity.AddSpellList().isEmpty());
        return (main || off || mainSpell || offSpell);
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
        LivingEntity livingEntity = this.mob.getTarget();
        if (!(livingEntity != null && this.mob instanceof InventoryEntity inventoryEntity && inventoryEntity.isCanChangeMeleeOrRange())) {
            this.mob.setAggressive(false);
            this.mob.setTarget(null);
        }
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
            if (fleeTimer <= 0 && d0 < fleeDistanceSqr) {
                startFleeing(livingentity);
            }
            if (fleeTimer > 0) {
                handleFleeing(livingentity);
                return;
            }

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
            }
            else {
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

    private void startFleeing(LivingEntity target) {
        Vec3 fleePos = DefaultRandomPos.getPosAway(
                this.mob,
                16,
                7,
                target.position()
        );

        if (fleePos != null && target.distanceToSqr(fleePos) > target.distanceToSqr(this.mob)) {
            this.mob.getNavigation().moveTo(
                    this.mob.getNavigation().createPath(fleePos.x, fleePos.y, fleePos.z, 0),
                    this.speedModifier * 1.8
            );
            fleeTimer = 40;
        }
    }
    private void handleFleeing(LivingEntity target) {
        fleeTimer--;
        this.mob.getLookControl().setLookAt(target, 360.0F, 360.0F);
        if (this.mob.getNavigation().isDone()) {
            fleeTimer = 0;
        }
    }
}