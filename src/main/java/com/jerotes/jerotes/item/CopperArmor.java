package com.jerotes.jerotes.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class CopperArmor extends ArmorItem {
	public CopperArmor(ArmorMaterial material, Type type, Properties properties) {
		super(material, type, properties);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		if (slot == EquipmentSlot.LEGS)
			return "jerotes:textures/models/armor/copper_layer_2.png";
		return "jerotes:textures/models/armor/copper_layer_1.png";
	}
}
