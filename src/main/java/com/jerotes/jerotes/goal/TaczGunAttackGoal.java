package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.InventoryEntity;
import com.jerotes.jerotes.util.Main;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TaczGunAttackGoal<T extends Mob> extends Goal {
    //有参考掠夺者的枪Pillager's Gun 其作者:绯桐Scarasol
    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T mob;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private final double speedModifier;
    private float attackRadius;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;
    private int updatePathDelay2;
    private double attackCount;
    private int ammoCount;

    public TaczGunAttackGoal(T t, double d, float f) {
        this.mob = t;
        this.speedModifier = d;
        this.attackRadius = f;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return isHoldingGun(this.mob) && (this.isValidTarget() || (!hasAmmo() && canReload()));
    }

    public static boolean isHoldingGun(Mob mob) {
        ItemStack main = mob.getMainHandItem();
        return !main.isEmpty() && IGun.getIGunOrNull(main) != null;
    }

    @Override
    public boolean canContinueToUse() {
        return isHoldingGun(this.mob) && (this.isValidTarget() || (!hasAmmo() && canReload()));
    }

    private boolean isValidTarget() {
        return ((Mob)this.mob).getTarget() != null && ((Mob)this.mob).getTarget().isAlive();
    }

    private boolean canReload() {
        if (!MainConfig.MobUseOtherShrinkItem) {
            return true;
        }
        ItemStack handItem = this.mob.getMainHandItem();
        if (handItem.getItem() instanceof AbstractGunItem abstractGunItem) {
            if (abstractGunItem.useDummyAmmo(handItem) &&
                    abstractGunItem.getDummyAmmoAmount(handItem) == 0) {
                handItem.getOrCreateTag().remove("DummyAmmo");
            }
            return abstractGunItem.canReload(this.mob, handItem);
        }
        return false;
    }

    private int getAmmoCount(ItemStack handItem) {
        IGun iGun = IGun.getIGunOrNull(handItem);
        if (iGun != null) {
            GunData gunData = TimelessAPI.getCommonGunIndex(iGun.getGunId(handItem)).map(CommonGunIndex::getGunData).orElse(null);
            if (gunData != null) {
                return iGun.useInventoryAmmo(handItem) ? -1 : iGun.getCurrentAmmoCount(handItem) + (iGun.hasBulletInBarrel(handItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
            }
        }
        return 0;
    }
    private boolean hasAmmo() {
        return getAmmoCount(this.mob.getItemInHand(InteractionHand.MAIN_HAND)) != 0;
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        this.crossbowState = CrossbowState.UNCHARGED;
        IGunOperator.fromLivingEntity(this.mob).draw(this.mob::getMainHandItem);
        IGunOperator.fromLivingEntity(this.mob).getDataHolder().drawTimestamp = System.currentTimeMillis() - 10000;
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
        if (isHoldingGun(this.mob)) {
            IGunOperator gunOperator = IGunOperator.fromLivingEntity(this.mob);
            gunOperator.cancelReload();
            gunOperator.aim(false);
            if (IGun.getIGunOrNull(this.mob.getMainHandItem()) instanceof AbstractGunItem abstractGunItem) {
                GunData gunData = TimelessAPI.getCommonGunIndex(abstractGunItem.getGunId(this.mob.getMainHandItem())).map(CommonGunIndex::getGunData).orElse(null);
                if (gunData != null && gunData.getBolt() == Bolt.MANUAL_ACTION) {
                    gunOperator.bolt();
                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        ItemStack handItem = this.mob.getMainHandItem();
        boolean bl;
        boolean bl2;
        LivingEntity livingEntity = ((Mob)this.mob).getTarget();
        if (livingEntity == null) {
            return;
        }
        int addReach = 0;
        float allReach = Math.max(0, attackRadius + addReach);
        boolean bl3 = ((Mob)this.mob).getSensing().hasLineOfSight(livingEntity);
        boolean bl4 = bl = this.seeTime > 0;
        if (bl3 != bl) {
            this.seeTime = 0;
        }
        this.seeTime = bl3 ? ++this.seeTime : --this.seeTime;
        double d = this.mob.distanceTo(livingEntity);
        boolean bl5 = bl2 = (d > allReach || this.seeTime < 5) && this.attackDelay == 0;
        float minDistance = this.crossbowState == CrossbowState.CHARGING ? 8f : 3f;
        float currentDistance = this.mob.distanceTo(livingEntity);
        if (this.updatePathDelay > 0) {
            --this.updatePathDelay;
        }
        if (this.updatePathDelay2 > 0) {
            --this.updatePathDelay2;
        }

        if (bl2) {
            //远距离且未后退，靠近
            if (this.updatePathDelay <= 0 && this.updatePathDelay2 <= 0) {
                ((Mob) this.mob).getNavigation().moveTo(livingEntity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(((LivingEntity) this.mob).getRandom());
            }
        }
        else {
            //远距离且未后退，取消行动
            if (currentDistance >= minDistance && this.updatePathDelay2 <= 0) {
                this.updatePathDelay = 0;
                ((Mob) this.mob).getNavigation().stop();
            }
            //近距离且未后退，后退
            if (currentDistance < minDistance && this.updatePathDelay2 <= 0) {
                Vec3 awayFromTarget = this.mob.position()
                        .subtract(livingEntity.position())
                        .normalize()
                        .scale(8.0);
                Vec3 retreatPos = this.mob.position().add(awayFromTarget);
                this.mob.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, this.speedModifier * 1.3);
                this.updatePathDelay = 40;
                this.updatePathDelay2 = 40;
            }
        }
        if (this.crossbowState != CrossbowState.CHARGING && (this.updatePathDelay2 <= 0 || this.mob.hurtTime > 0)) {
            ((Mob) this.mob).getLookControl().setLookAt(livingEntity, 360.0f, 360.0f);
            ((Mob) this.mob).lookAt(livingEntity, 360.0f, 360.0f);
        }
        //下述参考掠夺者的枪
        if (isHoldingGun(this.mob) && mob.getMainHandItem().getItem() instanceof AbstractGunItem abstractGunItem) {
            IGunOperator gunOperator = IGunOperator.fromLivingEntity(this.mob);
            if (isValidTarget() && canMeleeAttack(gunOperator, livingEntity)) {
                gunOperator.melee();
                return;
            }
            if (abstractGunItem.isOverheatLocked(handItem)) {
                this.crossbowState = CrossbowState.UNCHARGED;
                return;
            }
            if (this.crossbowState == CrossbowState.UNCHARGED && hasAmmo()) {
                this.crossbowState = CrossbowState.CHARGED;
                ammoCount = getAmmoCount(handItem);
            }
            else if (this.crossbowState == CrossbowState.CHARGED && !hasAmmo()){
                this.crossbowState = CrossbowState.UNCHARGED;
            }
            if (this.crossbowState == CrossbowState.UNCHARGED) {
                if (!bl2 && canReload()) {
                    gunOperator.reload();
                    this.crossbowState = CrossbowState.CHARGING;
                }
            } else if (this.crossbowState == CrossbowState.CHARGING) {
                if (!isHoldingGun(this.mob)) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                }
                if (!gunOperator.getDataHolder().reloadStateType.isReloading()) {
                    if (hasAmmo()) {
                        this.crossbowState = CrossbowState.CHARGED;
                        this.attackDelay = 10 + this.mob.getRandom().nextInt(20);
                        ammoCount = getAmmoCount(handItem);
                    }else {
                        this.crossbowState = CrossbowState.UNCHARGED;
                    }
                    if (this.mob instanceof CrossbowAttackMob crossbowAttackMob) {
                        crossbowAttackMob.setChargingCrossbow(false);
                    }
                }

            } else if (this.crossbowState == CrossbowState.CHARGED) {
                if (--this.attackDelay <= 0) {
                    gunOperator.aim(true);
                    this.crossbowState = CrossbowState.READY_TO_ATTACK;
                    this.attackDelay = 0;
                }
            }
            else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && Main.canSeeAngle(this.mob, livingEntity.getEyePosition(), 40) && this.mob.distanceTo(livingEntity) < this.attackRadius * 2) {
                GunData gunData = TimelessAPI.getCommonGunIndex(abstractGunItem.getGunId(handItem)).map(CommonGunIndex::getGunData).orElse(null);
                if (gunData != null) {
                    attackCount += abstractGunItem.getFireMode(handItem) == FireMode.AUTO ? abstractGunItem.getRPM(handItem) / 1200D : abstractGunItem.getRPM(handItem) / (Math.max(1200D * (livingEntity.distanceTo(this.mob) / 8), 2400D));
                    for (; attackCount >= 1; attackCount--) {
                        if (isHoldingGun(this.mob)) {
                            gunOperator.shoot(this.mob::getXRot, this.mob::getYHeadRot);

                            if (gunData.getBolt() == Bolt.MANUAL_ACTION) {
                                gunOperator.bolt();
                            }
                            if (ammoCount > 0) {
                                ammoCount -= 1;
                            }

                            if (ammoCount == 0) {
                                gunOperator.aim(false);
                                attackCount = 0;
                                this.crossbowState = CrossbowState.UNCHARGED;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    private boolean canRun() {
        return this.crossbowState != CrossbowState.CHARGING;
    }
    public boolean canMeleeAttack(IGunOperator gunOperator, LivingEntity target) {
        return System.currentTimeMillis() - gunOperator.getDataHolder().meleeTimestamp > 3000 && isValidTarget() && getAttackReachSqr(target) >= this.mob.distanceToSqr(target);
    }
    public double getAttackReachSqr(LivingEntity target) {
        return this.mob.getBbWidth() * 2.0 * this.mob.getBbWidth() * 2.0 + target.getBbWidth();
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