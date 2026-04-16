package com.jerotes.jerotes.item.Tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ToolActions.*;

public class ItemToolBaseTwoHandedAxe extends AxeItem implements ItemTwoHanded, ItemSpecialEffect, MeleeItem {
    protected static final UUID BASE_ENTITY_REACH_UUID = UUID.fromString("c0b279fb-83be-442f-b062-c577d26065d3");
    protected static final UUID BASE_BLOCK_REACH_UUID = UUID.fromString("2ab44e2a-0eb7-4825-8a10-88709f6e2219");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final float attackDamageBaseline;

    public ItemToolBaseTwoHandedAxe(Tier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.attackDamageBaseline = damage + tier.getAttackDamageBonus();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", speed, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BASE_ENTITY_REACH_UUID, "Tool modifier", 0.5, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BASE_BLOCK_REACH_UUID, "Tool modifier", 0.5, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }
    @Override
    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!player.getOffhandItem().isEmpty()) {
            return InteractionResultHolder.fail(itemStack);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    //双手武器-斧
    public static final Set<ToolAction> TWO_HANDED_AXE_ACTIONS = of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF, SWORD_SWEEP);
    private static Set<ToolAction> of(ToolAction... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return TWO_HANDED_AXE_ACTIONS.contains(toolAction);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
            return this.isMeleeWeapon();
        }
        if (enchantment instanceof SweepingEdgeEnchantment) {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getBlockReduction() {
        return 50;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.two_handed_axe").withStyle(ChatFormatting.YELLOW));
        list.add(Component.translatable("item.jerotes.two_handed", this.getBlockReduction()).withStyle(ChatFormatting.YELLOW));
    }
}

