package com.jerotes.jerotes.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jerotes.jerotes.item.ItemSpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseShield extends ShieldItem implements ItemSpecialEffect {
	private final int enchantmentValue;
    private final Ingredient repairIngredient;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
	public ItemToolBaseShield(Properties properties, float n, float f, int enchantmentValue, Ingredient repairItem) {
		super(properties);
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairItem;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", n - 1f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", f - 4f, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			return this.defaultModifiers;
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable("item.jerotes.shield").withStyle(ChatFormatting.YELLOW));
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
	@Override
	public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
		return this.repairIngredient.test(itemStack2) || super.isValidRepairItem(itemStack, itemStack2);
	}
}


