package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.InventoryEntity;
import com.jerotes.jerotes.entity.UseThrownJavelinEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;

public class JerotesRangedJavelinAttackGoal<T extends Mob & UseThrownJavelinEntity> extends JerotesCanMoveRangedAttackGoal {
    private final Mob mob;
    private final float reach;

    public JerotesRangedJavelinAttackGoal(RangedAttackMob rangedAttackMob, double d, int n, float f, float reach) {
        super(rangedAttackMob, d, n, f);
        this.mob = (Mob)rangedAttackMob;
        this.reach = reach;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && isHoldingRangedJavelin(this.mob, reach);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && isHoldingRangedJavelin(this.mob, reach);
    }

    public static boolean isHoldingRangedJavelin(Mob mob, float reach) {
        if (InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= reach) {
            return false;
        }
        return mob.getTarget() != null && mob.distanceTo(mob.getTarget()) > reach &&
                (InventoryEntity.isRangeJavelin(mob.getMainHandItem()) ||
                        InventoryEntity.isRangeJavelin(mob.getOffhandItem()) && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem())));
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        if (InventoryEntity.isThrow(this.mob.getMainHandItem())) {
            this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        }
        else if (InventoryEntity.isRangeJavelin(mob.getOffhandItem()) && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()))) {
            this.mob.startUsingItem(InteractionHand.OFF_HAND);
        }
    }

    @Override
    public void stop() {
        super.stop();
        LivingEntity livingEntity = this.mob.getTarget();
        if (!(livingEntity != null && this.mob instanceof InventoryEntity inventoryEntity && inventoryEntity.isCanChangeMeleeOrRange())) {
            this.mob.setAggressive(false);
            this.mob.setTarget(null);
        }
        else {
            this.mob.getNavigation().stop();
        }
        this.mob.stopUsingItem();
    }


    @Override
    public void tick() {
        ItemStack handItem = this.mob.getMainHandItem();
        if ((mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && !InventoryEntity.isRangeJavelin(mob.getMainHandItem())) && !this.mob.getOffhandItem().isEmpty()) {
            if (InventoryEntity.isRangeJavelin(mob.getOffhandItem())) {
                handItem = this.mob.getOffhandItem();
            }
        }

        double d = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean bl = this.mob.getSensing().hasLineOfSight(this.target);
        this.seeTime = bl ? ++this.seeTime : 0;
        this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        this.mob.getLookControl().setLookAt(this.target, 360.0f, 360.0f);
        if (--this.attackTime == 0) {
            if (!bl) {
                return;
            }
            float f = (float)Math.sqrt(d) / this.attackRadius;
            float f2 = Mth.clamp(f, 0.1f, 1.0f);
            this.rangedAttackMob.performRangedAttack(this.target, f2);
            if (!(this.mob instanceof UseThrownJavelinEntity useThrownJavelinEntity && !useThrownJavelinEntity.UseThrownJavelinEntityStopUse())) {
                this.mob.stopUsingItem();
            }
            this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }

        {
            float f = (float) Math.sqrt(d) / this.attackRadius;
            if (!this.mob.isUsingItem()
                    && !(this.mob instanceof UseThrownJavelinEntity useThrownJavelinEntity && !useThrownJavelinEntity.UseThrownJavelinEntityStopUse())
                    && this.attackTime < Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin) - 10) {

                if (handItem == this.mob.getMainHandItem()) {
                    this.mob.startUsingItem(InteractionHand.MAIN_HAND);
                }
                else {
                    this.mob.startUsingItem(InteractionHand.OFF_HAND);
                }
            }
            if (this.mob.getUseItem() != handItem
                    && !(this.mob instanceof UseThrownJavelinEntity useThrownJavelinEntity && !useThrownJavelinEntity.UseThrownJavelinEntityStopUse())
                    && this.attackTime < Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin) - 10) {
                this.mob.stopUsingItem();

                if (handItem == this.mob.getMainHandItem()) {
                    this.mob.startUsingItem(InteractionHand.MAIN_HAND);
                }
                else {
                    this.mob.startUsingItem(InteractionHand.OFF_HAND);
                }
            }
        }
    }
}
