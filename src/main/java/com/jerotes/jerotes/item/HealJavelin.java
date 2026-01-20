package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import com.jerotes.jerotes.entity.Arrow.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class HealJavelin extends ItemToolBaseJavelin {
	public HealJavelin() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(5),0.1f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownHealJavelinEntity(livingEntity.level(), livingEntity, itemStack);
	}

	@Override
	public void attackUse(Entity self, Entity attackTo) {
		if (!attackTo.level().isClientSide && attackTo instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 240, 2));
			MobEffectInstance mobEffect = new MobEffectInstance(MobEffects.HEAL, 1, 3);
			if (mobEffect.getEffect().isInstantenous()) {
				mobEffect.getEffect().applyInstantenousEffect(self, self, livingEntity, mobEffect.getAmplifier(), 1);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(this.getDisplayName_0().withStyle(ChatFormatting.GRAY));
		list.add(this.getDisplayName_1().withStyle(ChatFormatting.GRAY));
	}

	public MutableComponent getDisplayName_0() {
		return Component.translatable(this.getDescriptionId() + ".desc_0");
	}
	public MutableComponent getDisplayName_1() {
		return Component.translatable(this.getDescriptionId() + ".desc_1");
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}
}
