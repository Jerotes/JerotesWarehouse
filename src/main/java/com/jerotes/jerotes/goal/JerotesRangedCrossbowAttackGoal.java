package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseCrossbow;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class JerotesRangedCrossbowAttackGoal<T extends Mob & CrossbowAttackMob> extends Goal {
    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T mob;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private final double speedModifier;
    private float attackRadius;
    private float attackRadiusSqr;
    private int attackSpeedCooldownTick;
    private int seeTime;
    private int attackDelay;
    private int attackCooldown;
    private int updatePathDelay;

    public JerotesRangedCrossbowAttackGoal(T t, double d, float f) {
        this.mob = t;
        this.speedModifier = d;
        this.attackRadius = f;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        attackSpeedCooldownTick = 20;
    }

    public JerotesRangedCrossbowAttackGoal(T t, double d, float f, int n) {
        this(t,d,f);
        attackSpeedCooldownTick = n;
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && isHoldingCrossbow(this.mob);
    }

    public static boolean isHoldingCrossbow(Mob mob) {
        Item main = mob.getMainHandItem().getItem();
        Item off = mob.getOffhandItem().getItem();
        float reach = 8;
        if (mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        if (InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= reach) {
            return false;
        }
        return main instanceof CrossbowItem || off instanceof CrossbowItem && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()));
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !((Mob)this.mob).getNavigation().isDone()) && isHoldingCrossbow(this.mob);
    }

    private boolean isValidTarget() {
        return ((Mob)this.mob).getTarget() != null && ((Mob)this.mob).getTarget().isAlive();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
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
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            this.mob.setChargingCrossbow(false);
            CrossbowItem.setCharged(this.mob.getUseItem(), false);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        ItemStack handItem = this.mob.getMainHandItem();
        if ((mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem())) && !this.mob.getOffhandItem().isEmpty()) {
            if (this.mob.getOffhandItem().getItem() instanceof CrossbowItem crossbowItem) {
                handItem = this.mob.getOffhandItem();
            }
        }

        --this.attackCooldown;
        boolean bl;
        boolean bl2;
        LivingEntity livingEntity = ((Mob)this.mob).getTarget();
        if (livingEntity == null) {
            return;
        }
        int addReach = 0;
        if (handItem.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow) {
            addReach = itemToolBaseCrossbow.mobUseAddReach();
        }
        float allReach = Math.max(0, attackRadius + addReach);
        attackRadiusSqr = allReach * allReach;
        boolean bl3 = ((Mob)this.mob).getSensing().hasLineOfSight(livingEntity);
        boolean bl4 = bl = this.seeTime > 0;
        if (bl3 != bl) {
            this.seeTime = 0;
        }
        this.seeTime = bl3 ? ++this.seeTime : --this.seeTime;
        double d = ((Entity)this.mob).distanceToSqr(livingEntity);
        boolean bl5 = bl2 = (d > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
        if (bl2) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
                ((Mob)this.mob).getNavigation().moveTo(livingEntity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(((LivingEntity)this.mob).getRandom());
            }
        } else {
            this.updatePathDelay = 0;
            ((Mob)this.mob).getNavigation().stop();
        }

        ((Mob)this.mob).getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
        if (livingEntity instanceof Mob mobs && mobs.isWithinMeleeAttackRange(this.mob) || this.mob.distanceTo(livingEntity) < 6) {
            float f1 = this.mob.getYRot();
            float f2 = this.mob.getXRot();
            float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f4 = -Mth.sin(f2 * 0.017453292f);
            float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
            float f7 = -0.0225f;
            if (this.mob instanceof JerotesEntity jerotes) {
                jerotes.setSprintingCooldown(10);
            }
            if (this.mob.getDeltaMovement().x <= 0.25 && this.mob.getDeltaMovement().z <= 0.25)
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(f3 *= f7 / f6, 0, f5 *= f7 / f6));
        }

        if (this.crossbowState == CrossbowState.UNCHARGED) {
            if (!bl2) {
                ((LivingEntity)this.mob).startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, handItem.getItem()));
                this.crossbowState = CrossbowState.CHARGING;
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(true);
            }
        } else if (this.crossbowState == CrossbowState.CHARGING) {
            ItemStack itemStack;
            int n;
            if (!((LivingEntity)this.mob).isUsingItem()) {
                this.crossbowState = CrossbowState.UNCHARGED;
            }
            if ((n = ((LivingEntity)this.mob).getTicksUsingItem()) >= CrossbowItem.getChargeDuration(itemStack = ((LivingEntity)this.mob).getUseItem())) {
                ((LivingEntity)this.mob).releaseUsingItem();
                this.crossbowState = CrossbowState.CHARGED;
                this.attackDelay = attackSpeedCooldownTick + ((LivingEntity)this.mob).getRandom().nextInt(attackSpeedCooldownTick);
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
            }
        } else if (this.crossbowState == CrossbowState.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
                this.crossbowState = CrossbowState.READY_TO_ATTACK;
            }
        } else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && bl3 && this.attackCooldown <= 0) {
            ItemStack itemStack = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, handItem.getItem()));
            this.mob.performRangedAttack(livingEntity, 1.0f);
            if (ItemToolBaseCrossbow.getBullet(itemStack) > 1) {
                ItemToolBaseCrossbow.setBullet(itemStack, ItemToolBaseCrossbow.getBullet(itemStack)-1);
                int n = 5;
                if (handItem.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow) {
                    n = itemToolBaseCrossbow.mobUseCooldownTick(handItem);
                }
                this.attackCooldown = n;
            }
            else {
                CrossbowItem.setCharged(itemStack, false);
                this.crossbowState = CrossbowState.UNCHARGED;
            }
        }
        //
    }

    private boolean canRun() {
        return this.crossbowState == CrossbowState.UNCHARGED;
    }

    static final class CrossbowState {
        public static final /* enum */ CrossbowState UNCHARGED = new CrossbowState();
        public static final /* enum */ CrossbowState CHARGING = new CrossbowState();
        public static final /* enum */ CrossbowState CHARGED = new CrossbowState();
        public static final /* enum */ CrossbowState READY_TO_ATTACK = new CrossbowState();
        private static final /* synthetic */ CrossbowState[] $VALUES;

        public static CrossbowState[] values() {
            return (CrossbowState[])$VALUES.clone();
        }

        private static /* synthetic */ CrossbowState[] $values() {
            return new CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
        }

        static {
            $VALUES = CrossbowState.$values();
        }
    }

}

