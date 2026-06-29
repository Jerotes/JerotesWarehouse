package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.item.Interface.ItemBaseWarBeastArmor;
import com.jerotes.jerotes.item.Interface.ItemBeastArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWarBeastArmorDyeable extends DyeableHorseArmorItem implements ItemBeastArmor, ItemBaseWarBeastArmor {
	private final int protection;
	private final double toughness;
	private final double knockback_resistance;
	private final int color;
	private final ResourceLocation texture;
	private final String modIdString;
	private final String textureString;

	public ItemWarBeastArmorDyeable(int n, int color, String string, String string2, Properties properties) {
		this(n, color, new ResourceLocation(string, "textures/entity/war_beast_armor/" + string2 + ".png"), string, string2, properties);
	}

	public ItemWarBeastArmorDyeable(int n, double n2, double n3, int color, String string, String string2, Properties properties) {
		this(n, n2, n3, color, new ResourceLocation(string, "textures/entity/war_beast_armor/" + string2 + ".png"), string, string2, properties);
	}

	public ItemWarBeastArmorDyeable(int n, int color, ResourceLocation resourceLocation2, String string, String string2, Properties properties) {
		super(n, resourceLocation2, properties);
		this.protection = n;
		this.toughness = 0;
		this.knockback_resistance = 0;
		this.color = color;
		this.texture = resourceLocation2;
		this.modIdString = string;
		this.textureString = string2;
	}

	public ItemWarBeastArmorDyeable(int n, double n2, double n3, int color, ResourceLocation resourceLocation2, String string, String string2, Properties properties) {
		super(n, resourceLocation2, properties);
		this.protection = n;
		this.toughness = n2;
		this.knockback_resistance = n3;
		this.color = color;
		this.texture = resourceLocation2;
		this.modIdString = string;
		this.textureString = string2;
	}

	@Override
	public String getTextureString() {
		return textureString;
	}
	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
	public ResourceLocation getTextureOverlay() {
		return new ResourceLocation(modIdString, "textures/entity/war_beast_armor/" + textureString + "_overlay" + ".png");
	}

	@Override
	public int getProtection() {
		return this.protection;
	}

	@Override
	public double getToughness() {
		return this.toughness;
	}

	@Override
	public double getKnockbackResistance() {
		return this.knockback_resistance;
	}

	@Override
	public boolean hasOverlay() {
		return this == JerotesItems.LEATHER_WAR_BEAST_ARMOR.get();
	}

	@Override
	public int getColor() {
		return this.color;
	}
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.war_beast_armor_base.desc").withStyle(ChatFormatting.GRAY));
		if (this.protection != 0)
			list.add(Component.literal("+" + this.protection + " ").withStyle(ChatFormatting.BLUE).append(Component.translatable("attribute.name.generic.armor").withStyle(ChatFormatting.BLUE)));
		if (this.toughness != 0)
			list.add(Component.literal("+" + this.toughness + " ").withStyle(ChatFormatting.BLUE).append(Component.translatable("attribute.name.generic.armor_toughness").withStyle(ChatFormatting.BLUE)));
		if (this.knockback_resistance != 0)
			list.add(Component.literal("+" + this.knockback_resistance + " ").withStyle(ChatFormatting.BLUE).append(Component.translatable("attribute.name.generic.knockback_resistance").withStyle(ChatFormatting.BLUE)));
	}
}
