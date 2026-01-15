package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public interface UseShieldEntity {
    boolean shieldCanUse();

    boolean isDamageSourceBlocks(DamageSource damageSource);

    void disableShield();
    void disableShieldTry();
    void disableShieldBreak(int n);

    //使用盾牌和双手武器
    default void useBlockingItem(Mob mob) {
        if (!mob.level().isClientSide()) {
            if (mob.isAggressive()) {
                //副手盾牌
                if (shieldCanUse() && notBowCrossbow(mob, InteractionHand.MAIN_HAND) && mob.getOffhandItem().getItem() instanceof ShieldItem && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ShieldItem) && mob.attackAnim <= 0.0F) {
                    if (!mob.isUsingItem()) {
                        mob.startUsingItem(InteractionHand.OFF_HAND);
                    }
                }
                //主手盾牌
                else if (shieldCanUse() && notBowCrossbow(mob, InteractionHand.OFF_HAND) && mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldItem && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ShieldItem) && mob.attackAnim <= 0.0F) {
                    if (!mob.isUsingItem()) {
                        mob.startUsingItem(InteractionHand.MAIN_HAND);
                    }
                }
                //主手双手武器
                else if (mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemTwoHanded itemTwoHanded && !(mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && mob.getOffhandItem().isEmpty() && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ItemTwoHanded) && mob.attackAnim <= 0.0F) {
                    if (!mob.isUsingItem()) {
                        mob.startUsingItem(InteractionHand.MAIN_HAND);
                    }
                }
                //副手双手武器
                else if (mob.getOffhandItem().getItem() instanceof ItemTwoHanded itemTwoHanded && !(mob.getOffhandItem().getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ItemTwoHanded) && mob.attackAnim <= 0.0F) {
                    if (!mob.isUsingItem()) {
                        mob.startUsingItem(InteractionHand.OFF_HAND);
                    }
                }
                //其他状态下停止使用
                else {
                    stopUse(mob);
                }
            }
            //非愤怒状态下停止使用
            else {
                stopUse(mob);
            }
        }
    }

    //停止使用
    default boolean notBowCrossbow(Mob mob, InteractionHand interactionHand) {
        ItemStack other = mob.getItemInHand(interactionHand);

        if (mob instanceof InventoryEntity inventoryEntity) {
            return
                    //弓
                    !(InventoryEntity.isBow(inventoryEntity, other))
                            //弩
                            && !(InventoryEntity.isCrossbow(inventoryEntity, other))
                            //远程状态的三叉戟
                            && !(InventoryEntity.isRangeJavelin(inventoryEntity, other) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) > inventoryEntity.meleeOrRangeDistance())
                            //投掷物
                            && !(InventoryEntity.isThrow(inventoryEntity, other))
                            //其他远程
                            && !(InventoryEntity.isOtherRange(inventoryEntity, other));
        }
        return
                //弓
                !(InventoryEntity.isBow(other))
                        //弩
                        && !(InventoryEntity.isCrossbow(other))
                        //远程状态的三叉戟
                        && !(InventoryEntity.isRangeJavelin(other) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) > 8.0f)
                        //投掷物
                        && !(InventoryEntity.isThrow(other))
                        //其他远程
                        && !(InventoryEntity.isOtherRange(other));
    }

    //停止使用
    default void stopUse(Mob mob) {
        if (mob.getUseItem().getItem() instanceof ShieldItem ||
                mob.getUseItem().getItem() instanceof ItemTwoHanded && !(mob.getUseItem().getItem() instanceof ItemToolBasePike)) {
            mob.stopUsingItem();
        }
    }
}

