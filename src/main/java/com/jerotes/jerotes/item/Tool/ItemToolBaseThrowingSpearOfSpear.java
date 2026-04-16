package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.item.Interface.ItemToolBaseThrowingSpear;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemToolBaseThrowingSpearOfSpear extends ItemToolBaseSpear implements ItemToolBaseThrowingSpear {
    //全配置
    public ItemToolBaseThrowingSpearOfSpear(Tier tier, Properties properties, float damage, float speed, float swingTimes, float hitboxMargin, int contactCooldownTicks, int delayTicks,
                             Optional<Condition> dismountConditions, Optional<Condition> knockbackConditions, Optional<Condition> damageConditions,
                             float forwardMovement, float damageMultiplier, SoundEvent sound, SoundEvent hitSound, SoundEvent sound2, SoundEvent hitSound2,
                             float minRange, float maxRange, float minCreativeRange, float maxCreativeRange, float hitboxMargin2, float mobFactor, boolean dealsKnockback, boolean dismounts) {
        super(tier, properties.defaultDurability(tier.getUses()), damage, speed, swingTimes, hitboxMargin, contactCooldownTicks, delayTicks,
                dismountConditions, knockbackConditions, damageConditions,
                forwardMovement, damageMultiplier, sound, hitSound, sound2, hitSound2,
                minRange, maxRange, minCreativeRange, maxCreativeRange, hitboxMargin2, mobFactor, dealsKnockback, dismounts);
    }

    //半配置
    public ItemToolBaseThrowingSpearOfSpear(Tier tier, Properties properties,
                             float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean wooden) {
        //动画时间
        //伤害倍率
        //攻击所需抬起时间
        super(tier, properties, f, f2, f3, f4, f5, f6, f7, f8, f9, wooden);
    }

    public Item getOtherMode() {
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.isShiftKeyDown() && getOtherMode() != null) {
            ItemStack itemStack1 = new ItemStack(getOtherMode());
            itemStack1.deserializeNBT(itemStack.getOrCreateTag());
            itemStack1.setTag(itemStack.getOrCreateTag());
            itemStack1.setDamageValue(itemStack.getDamageValue());
            player.setItemInHand(interactionHand, itemStack1);
            player.getCooldowns().addCooldown(itemStack.getItem(), 20);
            player.getCooldowns().addCooldown(itemStack1.getItem(), 20);
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.throwing_spear").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

