package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemToolBaseSpear extends ItemToolBaseSpearBase implements ItemSpecialEffect, MeleeItem {
    //全配置
    public ItemToolBaseSpear(Tier tier, Properties properties, float damage, float speed, float swingTimes, float hitboxMargin, int contactCooldownTicks, int delayTicks,
                             Optional<Condition> dismountConditions, Optional<Condition> knockbackConditions, Optional<Condition> damageConditions,
                             float forwardMovement, float damageMultiplier, SoundEvent sound, SoundEvent hitSound, SoundEvent sound2, SoundEvent hitSound2,
                             float minRange, float maxRange, float minCreativeRange, float maxCreativeRange, float hitboxMargin2, float mobFactor, boolean dealsKnockback, boolean dismounts) {
        super(tier, properties, damage, speed, swingTimes, hitboxMargin, contactCooldownTicks, delayTicks,
                dismountConditions, knockbackConditions, damageConditions,
                forwardMovement, damageMultiplier, sound, hitSound, sound2, hitSound2,
                minRange, maxRange, minCreativeRange, maxCreativeRange, hitboxMargin2, mobFactor, dealsKnockback, dismounts);
    }

    //半配置
    public ItemToolBaseSpear(Tier tier, Properties properties, float damage, float speed,
                             float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean wooden) {
        //动画时间
        //伤害倍率
        //攻击所需抬起时间
        super(tier, properties, damage, speed, f,
                0.25f, 10, (int) (f3 * 20.0f),
                Condition.ofAttackerSpeed((int) (f4 * 20.0f), f5),
                Condition.ofAttackerSpeed((int) (f6 * 20.0f), f7),
                Condition.ofRelativeSpeed((int) (f8 * 20.0f), f9),
                0.38f, f2,
                wooden ? JerotesSoundEvents.SPEAR_WOOD_USE : JerotesSoundEvents.SPEAR_USE,
                wooden ? JerotesSoundEvents.SPEAR_WOOD_HIT : JerotesSoundEvents.SPEAR_HIT,
                wooden ? JerotesSoundEvents.SPEAR_WOOD_ATTACK : JerotesSoundEvents.SPEAR_ATTACK,
                wooden ? JerotesSoundEvents.SPEAR_WOOD_HIT : JerotesSoundEvents.SPEAR_HIT,
                2.0f, 4.5f, 2.0f, 6.5f, 0.125f, 0.5f, true, false
        );
    }
    public ItemToolBaseSpear(Tier tier, Properties properties, float damage, float speed, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        this(tier, properties, damage, speed, f, f2, f3, f4, f5, f6, f7, f8, f9, false);
    }
    public ItemToolBaseSpear(Tier tier, Properties properties, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean wooden) {
        this(tier, properties, 0, (1.0f / f) - 4f, f, f2, f3, f4, f5, f6, f7, f8, f9, wooden);
    }
    public ItemToolBaseSpear(Tier tier, Properties properties, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        this(tier, properties, 0, (1.0f / f) - 4f, f, f2, f3, f4, f5, f6, f7, f8, f9, false);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.spear").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

