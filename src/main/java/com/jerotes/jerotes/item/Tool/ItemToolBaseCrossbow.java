package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseCrossbow extends CrossbowItem implements ItemSpecialEffect {
    public ItemToolBaseCrossbow(Properties properties) {
        super(properties);
    }

    //使用
    public boolean useCustomShoot() {
        return false;
    }
    public int mobUseCooldownTick(ItemStack itemStack) {
        return 4;
    }
    public int mobUseAddReach() {
        return 0;
    }
    public void customShoot(LivingEntity livingEntity) {
    }
    public void customShoot(LivingEntity livingEntity, ItemStack itemStack) {
        customShoot(livingEntity);
    }
    //选择特殊箭矢
    public boolean useBaseShootArrow() {
        return false;
    }
    public AbstractArrow customBaseShootArrow(LivingEntity livingEntity, ItemStack itemStack) {
        return null;
    }
    //选择特殊箭矢几率
    public float customBaseShootArrowChance() {
        return 1.0f;
    }
    public float customBaseShootArrowChance(LivingEntity livingEntity) {
        return customBaseShootArrowChance();
    }
    //特殊发射效果
    public void customShootProjectile(Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float f2, float f3, float f4) {
    }
    //使用特殊使用
    public boolean useCustomUse() {
        return false;
    }
    //特殊使用
    public InteractionResultHolder<ItemStack> customUse(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        return InteractionResultHolder.consume(itemstack);
    }

    public float getShootingPower(ItemStack itemStack) {
        return containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }
    public float getArrowInaccuracy() {
        return 1.0F;
    }
    @Override
    public int getUseDuration(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemToolBaseCrossbow firepowerPourerCrossbow)
            return firepowerPourerCrossbow.getChargeDurations(itemStack) + 3;
        return ItemToolBaseCrossbow.getChargeDuration(itemStack) + 3;
    }
    public int getChargeDurations(ItemStack itemStack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemStack);
        return i == 0 ? 25 : 25 - 5 * i;
    }
    public static int getBullet(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            return compoundTag.getInt("JerotesBullet");
        }
        return 0;
    }
    public static void setBullet(ItemStack itemStack, int bl) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putInt("JerotesBullet", bl);
    }
    public int maxBullet() {
        return 0;
    }
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if ((itemStack.getItem() instanceof ItemToolBaseCrossbow crossbowItem && crossbowItem.maxBullet() != 0)) {
            list.add(Component.translatable("item.jerotes.crossbow_repeater",
                    getShootingPower(Items.ARROW.getDefaultInstance()),
                    getShootingPower(Items.FIREWORK_ROCKET.getDefaultInstance()),
                    getDefaultProjectileRange(),
                    getChargeDurations(itemStack)/20f,
                    getArrowInaccuracy(),
                    maxBullet()
            ).withStyle(ChatFormatting.YELLOW));
        }
        else {
            list.add(Component.translatable("item.jerotes.crossbow",
                    getShootingPower(Items.ARROW.getDefaultInstance()),
                    getShootingPower(Items.FIREWORK_ROCKET.getDefaultInstance()),
                    getDefaultProjectileRange(),
                    getChargeDurations(itemStack)/20f,
                    getArrowInaccuracy()
            ).withStyle(ChatFormatting.YELLOW));
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

