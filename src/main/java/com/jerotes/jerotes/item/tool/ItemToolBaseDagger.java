package com.jerotes.jerotes.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.jerotes.jerotes.item.ItemSpecialEffect;
import com.jerotes.jerotes.item.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ToolActions.SWORD_DIG;

public class ItemToolBaseDagger extends TieredItem implements ItemSpecialEffect, MeleeItem {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public ItemToolBaseDagger(Tier tier, int damage, float speed, Properties properties) {
        super(tier, properties);
        this.attackDamage = (float)damage + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)speed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public float getDamage() {
        return this.attackDamage;
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }
    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        itemStack.hurtAndBreak(1, livingEntity2, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(2, livingEntity, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }

    public boolean canShiftKeyDownUse(LivingEntity livingEntity) {
        return true;
    }
    public boolean canShiftKeyDownDamage(LivingEntity livingEntity) {
        return true;
    }

    //匕首
    public static final Set<ToolAction> ONE_HANDED_BREAK_SHIELD_ACTIONS = of(SWORD_DIG);
    private static Set<ToolAction> of(ToolAction... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ONE_HANDED_BREAK_SHIELD_ACTIONS.contains(toolAction);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() instanceof ItemToolBaseDagger) {
            if (enchantment == Enchantments.SWEEPING_EDGE) {
                return false;
            }
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.dagger").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

