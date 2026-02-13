package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.UseThrowEntity;
import com.jerotes.jerotes.entity.Interface.UseThrownJavelinEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.phys.Vec3;

public class JerotesRangedThrowAttackGoal<T extends Mob & UseThrowEntity> extends JerotesCanMoveRangedAttackGoal {
    private final Mob mob;

    public JerotesRangedThrowAttackGoal(RangedAttackMob rangedAttackMob, double d, int n, float f) {
        super(rangedAttackMob, d, n, f);
        this.mob = (Mob)rangedAttackMob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && isHoldingThrow(this.mob);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && isHoldingThrow(this.mob);
    }

    public static boolean isHoldingThrow(Mob mob) {
        float reach = 8;
        if (mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        if (InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= reach) {
            return false;
        }
        if (mob instanceof InventoryEntity inventoryEntity) {
            return InventoryEntity.isThrow(inventoryEntity, mob.getMainHandItem())
                    || InventoryEntity.isThrow(mob.getOffhandItem())
                    && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && !InventoryEntity.isThrow(mob.getMainHandItem()));
        }
        return InventoryEntity.isThrow(mob.getMainHandItem())
                || InventoryEntity.isThrow(mob.getOffhandItem())
                && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && !InventoryEntity.isThrow(mob.getMainHandItem()));
      }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        if (InventoryEntity.isThrow(this.mob.getMainHandItem())) {
            if (!(this.mob.getMainHandItem().getItem() instanceof ThrowablePotionItem))
             this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        }
        else if (InventoryEntity.isThrow(mob.getOffhandItem())
                && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && !InventoryEntity.isThrow(mob.getMainHandItem()))) {

            if (!(this.mob.getOffhandItem().getItem() instanceof ThrowablePotionItem))
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
        Vec3 vec = LandRandomPos.getPosAway((PathfinderMob) this.mob, 30, 15, this.target.position());
        if (vec != null && this.mob.distanceTo(this.target) < 4) {
            mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.05d);
        }
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!bl) {
                return;
            }
            float f = (float)Math.sqrt(d) / this.attackRadius;
            float f2 = Mth.clamp(f, 0.1f, 1.0f);
            this.rangedAttackMob.performRangedAttack(this.target, f2);
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
