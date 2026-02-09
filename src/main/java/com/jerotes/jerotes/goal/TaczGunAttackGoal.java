package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.util.Main;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.function.Supplier;

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
        if (main.isEmpty()) {
            return false;
        }
        try {
            Class<?> iGunClass = Class.forName("com.tacz.guns.api.item.IGun");
            Method getIGunOrNullMethod = iGunClass.getMethod("getIGunOrNull", ItemStack.class);
            Object iGun = getIGunOrNullMethod.invoke(null, main);
            return iGun != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        try {
            Class<?> iGunClass = Class.forName("com.tacz.guns.api.item.IGun");
            Method getIGunOrNullMethod = iGunClass.getMethod("getIGunOrNull", ItemStack.class);
            Object iGun = getIGunOrNullMethod.invoke(null, handItem);

            if (iGun != null) {
                Class<?> abstractGunItemClass = Class.forName("com.tacz.guns.api.item.gun.AbstractGunItem");
                if (abstractGunItemClass.isInstance(iGun)) {
                    Method useDummyAmmoMethod = iGunClass.getMethod("useDummyAmmo", ItemStack.class);
                    boolean useDummyAmmo = (Boolean) useDummyAmmoMethod.invoke(iGun, handItem);
                    Method getDummyAmmoAmountMethod = iGunClass.getMethod("getDummyAmmoAmount", ItemStack.class);
                    int dummyAmmoAmount = (Integer) getDummyAmmoAmountMethod.invoke(iGun, handItem);
                    if (useDummyAmmo && dummyAmmoAmount == 0) {
                        handItem.getOrCreateTag().remove("DummyAmmo");
                    }
                    Method canReloadMethod = abstractGunItemClass.getMethod("canReload", LivingEntity.class, ItemStack.class);
                    return (Boolean) canReloadMethod.invoke(iGun, this.mob, handItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private int getAmmoCount(ItemStack handItem) {
        try {
            Class<?> iGunClass = Class.forName("com.tacz.guns.api.item.IGun");
            Method getIGunOrNullMethod = iGunClass.getMethod("getIGunOrNull", ItemStack.class);

            Object iGun = getIGunOrNullMethod.invoke(null, handItem);

            if (iGun != null) {
                Class<?> timelessAPIClass = Class.forName("com.tacz.guns.api.TimelessAPI");
                Class<?> resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation");
                Method getCommonGunIndexMethod = timelessAPIClass.getMethod("getCommonGunIndex", resourceLocationClass);
                Method getGunIdMethod = iGunClass.getMethod("getGunId", ItemStack.class);
                Object gunId = getGunIdMethod.invoke(iGun, handItem);
                Object optional = getCommonGunIndexMethod.invoke(null, gunId);
                Class<?> optionalClass = Class.forName("java.util.Optional");
                Method isPresentMethod = optionalClass.getMethod("isPresent");
                Method getMethod = optionalClass.getMethod("get");
                if ((Boolean) isPresentMethod.invoke(optional)) {
                    Object commonGunIndex = getMethod.invoke(optional);
                    Class<?> commonGunIndexClass = Class.forName("com.tacz.guns.resource.index.CommonGunIndex");
                    Method getGunDataMethod = commonGunIndexClass.getMethod("getGunData");
                    Object gunData = getGunDataMethod.invoke(commonGunIndex);
                    if (gunData != null) {
                        Class<?> gunDataClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.GunData");
                        Method getBoltMethod = gunDataClass.getMethod("getBolt");
                        Object bolt = getBoltMethod.invoke(gunData);
                        Class<?> boltClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.Bolt");
                        Object openBoltValue = boltClass.getField("OPEN_BOLT").get(null);
                        boolean isOpenBolt = bolt.equals(openBoltValue);
                        Method useInventoryAmmoMethod = iGunClass.getMethod("useInventoryAmmo", ItemStack.class);
                        Method getCurrentAmmoCountMethod = iGunClass.getMethod("getCurrentAmmoCount", ItemStack.class);
                        Method hasBulletInBarrelMethod = iGunClass.getMethod("hasBulletInBarrel", ItemStack.class);
                        boolean useInventoryAmmo = (Boolean) useInventoryAmmoMethod.invoke(iGun, handItem);
                        int currentAmmoCount = (Integer) getCurrentAmmoCountMethod.invoke(iGun, handItem);
                        boolean hasBulletInBarrel = (Boolean) hasBulletInBarrelMethod.invoke(iGun, handItem);
                        return useInventoryAmmo ? -1 :
                                currentAmmoCount + (hasBulletInBarrel && !isOpenBolt ? 1 : 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        try {
            Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
            Method fromLivingEntityMethod = iGunOperatorClass.getMethod("fromLivingEntity", LivingEntity.class);
            Object gunOperator = fromLivingEntityMethod.invoke(null, this.mob);
            Class<?> supplierClass = Class.forName("java.util.function.Supplier");
            Method drawMethod = iGunOperatorClass.getMethod("draw", supplierClass);
            drawMethod.invoke(gunOperator, (Supplier<ItemStack>) this.mob::getMainHandItem);
            Method getDataHolderMethod = iGunOperatorClass.getMethod("getDataHolder");
            Object dataHolder = getDataHolderMethod.invoke(gunOperator);
            Class<?> dataHolderClass = dataHolder.getClass();
            java.lang.reflect.Field drawTimestampField = dataHolderClass.getDeclaredField("drawTimestamp");
            drawTimestampField.setAccessible(true);
            drawTimestampField.set(dataHolder, System.currentTimeMillis() - 10000);

        } catch (Exception e) {
            e.printStackTrace();
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
        this.seeTime = 0;
        if (isHoldingGun(this.mob)) {
            try {
                Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                Method fromLivingEntityMethod = iGunOperatorClass.getMethod("fromLivingEntity", LivingEntity.class);
                Object gunOperator = fromLivingEntityMethod.invoke(null, this.mob);
                Method cancelReloadMethod = iGunOperatorClass.getMethod("cancelReload");
                cancelReloadMethod.invoke(gunOperator);
                Method aimMethod = iGunOperatorClass.getMethod("aim", boolean.class);
                aimMethod.invoke(gunOperator, false);
                try {
                    ItemStack handItem = this.mob.getMainHandItem();
                    Class<?> iGunClass = Class.forName("com.tacz.guns.api.item.IGun");
                    Method getIGunOrNullMethod = iGunClass.getMethod("getIGunOrNull", ItemStack.class);
                    Object iGun = getIGunOrNullMethod.invoke(null, handItem);
                    if (iGun != null) {
                        Class<?> abstractGunItemClass = Class.forName("com.tacz.guns.api.item.gun.AbstractGunItem");
                        if (abstractGunItemClass.isInstance(iGun)) {
                            Method getGunIdMethod = iGunClass.getMethod("getGunId", ItemStack.class);
                            Object gunId = getGunIdMethod.invoke(iGun, handItem);
                            Class<?> timelessAPIClass = Class.forName("com.tacz.guns.api.TimelessAPI");
                            Class<?> resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation");
                            Method getCommonGunIndexMethod = timelessAPIClass.getMethod("getCommonGunIndex", resourceLocationClass);
                            Object optional = getCommonGunIndexMethod.invoke(null, gunId);
                            Class<?> optionalClass = Class.forName("java.util.Optional");
                            Method isPresentMethod = optionalClass.getMethod("isPresent");
                            Method getMethod = optionalClass.getMethod("get");
                            if ((Boolean) isPresentMethod.invoke(optional)) {
                                Object commonGunIndex = getMethod.invoke(optional);
                                Class<?> commonGunIndexClass = Class.forName("com.tacz.guns.resource.index.CommonGunIndex");
                                Method getGunDataMethod = commonGunIndexClass.getMethod("getGunData");
                                Object gunData = getGunDataMethod.invoke(commonGunIndex);
                                if (gunData != null) {
                                    Class<?> gunDataClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.GunData");
                                    Method getBoltMethod = gunDataClass.getMethod("getBolt");
                                    Object bolt = getBoltMethod.invoke(gunData);
                                    Class<?> boltClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.Bolt");
                                    Object manualActionValue = boltClass.getField("MANUAL_ACTION").get(null);
                                    if (bolt.equals(manualActionValue)) {
                                        Method boltMethod = iGunOperatorClass.getMethod("bolt");
                                        boltMethod.invoke(gunOperator);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        if (isHoldingGun(this.mob)) {
            Object abstractGunItem = null;
            Class<?> abstractGunItemClass = null;

            try {
                abstractGunItemClass = Class.forName("com.tacz.guns.api.item.gun.AbstractGunItem");
                if (abstractGunItemClass.isInstance(handItem.getItem())) {
                    abstractGunItem = handItem.getItem();
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Object gunOperator = null;
            try {
                Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                Method fromLivingEntityMethod = iGunOperatorClass.getMethod("fromLivingEntity", LivingEntity.class);
                gunOperator = fromLivingEntityMethod.invoke(null, this.mob);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                Method isOverheatLockedMethod = abstractGunItemClass.getMethod("isOverheatLocked", ItemStack.class);
                boolean overheatLocked = (Boolean) isOverheatLockedMethod.invoke(abstractGunItem, handItem);
                if (overheatLocked) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (this.crossbowState == CrossbowState.UNCHARGED && hasAmmo()) {
                this.crossbowState = CrossbowState.CHARGED;
                ammoCount = getAmmoCount(handItem);
            }
            else if (this.crossbowState == CrossbowState.CHARGED && !hasAmmo()) {
                this.crossbowState = CrossbowState.UNCHARGED;
            }

            if (this.crossbowState == CrossbowState.UNCHARGED) {
                if (!bl2 && canReload()) {
                    try {
                        Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                        Method reloadMethod = iGunOperatorClass.getMethod("reload");
                        reloadMethod.invoke(gunOperator);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.crossbowState = CrossbowState.CHARGING;
                }
            } else if (this.crossbowState == CrossbowState.CHARGING) {
                if (!isHoldingGun(this.mob)) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                }

                try {
                    Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                    Method getDataHolderMethod = iGunOperatorClass.getMethod("getDataHolder");
                    Object dataHolder = getDataHolderMethod.invoke(gunOperator);
                    Class<?> dataHolderClass = dataHolder.getClass();
                    java.lang.reflect.Field reloadStateTypeField = dataHolderClass.getDeclaredField("reloadStateType");
                    reloadStateTypeField.setAccessible(true);
                    Object reloadStateType = reloadStateTypeField.get(dataHolder);
                    Method isReloadingMethod = reloadStateType.getClass().getMethod("isReloading");
                    boolean isReloading = (Boolean) isReloadingMethod.invoke(reloadStateType);

                    if (!isReloading) {
                        if (hasAmmo()) {
                            this.crossbowState = CrossbowState.CHARGED;
                            this.attackDelay = 10 + this.mob.getRandom().nextInt(20);
                            ammoCount = getAmmoCount(handItem);
                        } else {
                            this.crossbowState = CrossbowState.UNCHARGED;
                        }

                        if (this.mob instanceof CrossbowAttackMob crossbowAttackMob) {
                            crossbowAttackMob.setChargingCrossbow(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (this.crossbowState == CrossbowState.CHARGED) {
                if (--this.attackDelay <= 0) {
                    try {
                        Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                        Method aimMethod = iGunOperatorClass.getMethod("aim", boolean.class);
                        aimMethod.invoke(gunOperator, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.crossbowState = CrossbowState.READY_TO_ATTACK;
                    this.attackDelay = 0;
                }
            }
            else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && Main.canSeeAngle(this.mob, livingEntity.getEyePosition(), 40) && this.mob.distanceTo(livingEntity) < this.attackRadius * 2) {
                try {
                    Class<?> timelessAPIClass = Class.forName("com.tacz.guns.api.TimelessAPI");
                    Class<?> resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation");
                    Method getCommonGunIndexMethod = timelessAPIClass.getMethod("getCommonGunIndex", resourceLocationClass);
                    Method getGunIdMethod = abstractGunItemClass.getMethod("getGunId", ItemStack.class);
                    Object gunId = getGunIdMethod.invoke(abstractGunItem, handItem);

                    Object optional = getCommonGunIndexMethod.invoke(null, gunId);

                    Class<?> optionalClass = Class.forName("java.util.Optional");
                    Method isPresentMethod = optionalClass.getMethod("isPresent");
                    Method getMethod = optionalClass.getMethod("get");

                    if ((Boolean) isPresentMethod.invoke(optional)) {
                        Object commonGunIndex = getMethod.invoke(optional);
                        Class<?> commonGunIndexClass = Class.forName("com.tacz.guns.resource.index.CommonGunIndex");
                        Method getGunDataMethod = commonGunIndexClass.getMethod("getGunData");
                        Object gunData = getGunDataMethod.invoke(commonGunIndex);

                        if (gunData != null) {
                            Class<?> gunDataClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.GunData");
                            Class<?> fireModeClass = Class.forName("com.tacz.guns.api.item.gun.FireMode");
                            Object autoEnum = fireModeClass.getField("AUTO").get(null);
                            Method getFireModeMethod = abstractGunItemClass.getMethod("getFireMode", ItemStack.class);
                            Object fireMode = getFireModeMethod.invoke(abstractGunItem, handItem);
                            boolean isAuto = fireMode.equals(autoEnum);
                            Method getRPMMethod = abstractGunItemClass.getMethod("getRPM", ItemStack.class);
                            Object rpmObj = getRPMMethod.invoke(abstractGunItem, handItem);
                            double rpm;
                            if (rpmObj instanceof Integer) {
                                rpm = ((Integer) rpmObj).doubleValue();
                            } else if (rpmObj instanceof Double) {
                                rpm = (Double) rpmObj;
                            } else {
                                rpm = ((Number) rpmObj).doubleValue();
                            }

                            attackCount += isAuto ?
                                    rpm / 1200D :
                                    rpm / (Math.max(1200D * (livingEntity.distanceTo(this.mob) / 8), 2400D));

                            for (; attackCount >= 1; attackCount--) {
                                if (isHoldingGun(this.mob)) {
                                    Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
                                    Method shootMethod = iGunOperatorClass.getMethod("shoot", Supplier.class, Supplier.class);
                                    shootMethod.invoke(gunOperator, (Supplier<Float>) this.mob::getXRot, (Supplier<Float>) this.mob::getYHeadRot);
                                    Method getBoltMethod = gunDataClass.getMethod("getBolt");
                                    Object bolt = getBoltMethod.invoke(gunData);
                                    Class<?> boltClass = Class.forName("com.tacz.guns.resource.pojo.data.gun.Bolt");
                                    Object manualActionValue = boltClass.getField("MANUAL_ACTION").get(null);
                                    if (bolt.equals(manualActionValue)) {
                                        Method boltMethod = iGunOperatorClass.getMethod("bolt");
                                        boltMethod.invoke(gunOperator);
                                    }
                                    if (ammoCount > 0) {
                                        ammoCount -= 1;
                                    }
                                    if (ammoCount == 0) {
                                        Method aimMethod = iGunOperatorClass.getMethod("aim", boolean.class);
                                        aimMethod.invoke(gunOperator, false);
                                        attackCount = 0;
                                        this.crossbowState = CrossbowState.UNCHARGED;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private boolean canRun() {
        return this.crossbowState != CrossbowState.CHARGING;
    }
    public boolean canMeleeAttack(Object gunOperator, LivingEntity target) {
        try {
            Class<?> iGunOperatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
            Method getDataHolderMethod = iGunOperatorClass.getMethod("getDataHolder");
            Object dataHolder = getDataHolderMethod.invoke(gunOperator);
            Class<?> dataHolderClass = dataHolder.getClass();
            java.lang.reflect.Field meleeTimestampField = dataHolderClass.getDeclaredField("meleeTimestamp");
            meleeTimestampField.setAccessible(true);
            long meleeTimestamp = meleeTimestampField.getLong(dataHolder);
            return System.currentTimeMillis() - meleeTimestamp > 3000 &&
                    isValidTarget() &&
                    getAttackReachSqr(target) >= this.mob.distanceToSqr(target);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public double getAttackReachSqr(LivingEntity target) {
        return this.mob.getBbWidth() * 2.0 * this.mob.getBbWidth() * 2.0 + target.getBbWidth();
    }

    static final class CrossbowState {
        public static final CrossbowState UNCHARGED = new CrossbowState();
        public static final CrossbowState CHARGING = new CrossbowState();
        public static final CrossbowState CHARGED = new CrossbowState();
        public static final CrossbowState READY_TO_ATTACK = new CrossbowState();
        private static final CrossbowState[] $VALUES;

        public static CrossbowState[] values() {
            return (CrossbowState[])$VALUES.clone();
        }

        private static CrossbowState[] $values() {
            return new CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
        }

        static {
            $VALUES = CrossbowState.$values();
        }
    }
}