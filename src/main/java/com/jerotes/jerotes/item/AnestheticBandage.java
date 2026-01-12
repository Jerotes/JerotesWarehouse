package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.ItemAnesthetized;
import com.jerotes.jerotes.item.tool.ItemToolBaseBandage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AnestheticBandage extends ItemToolBaseBandage implements ItemAnesthetized {
	public AnestheticBandage() {
		super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public float getHealth() {
		return 12.0f;
	}

	@Override
	public void addEffectAbout(LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (!livingEntity.level().isClientSide) {
			float anesthetizedTicks = 0;
			if (livingEntity.hasEffect(JerotesMobEffects.ANESTHETIZED.get())){
				anesthetizedTicks = livingEntity.getEffect(JerotesMobEffects.ANESTHETIZED.get()).getDuration();
			}
			livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.ANESTHETIZED.get(), (int) (this.getAnesthetized() * 20 + anesthetizedTicks), 0, false, false), livingEntity2);
		}
	}

	@Override
	public int getAnesthetized() {
		return 60;
	}
	@Override
	public boolean onlyPlayer() {
		return false;
	}
	@Override
	public boolean onlyThrowing() {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.anesthetized_use", this.getAnesthetized()).withStyle(ChatFormatting.YELLOW));
	}
}
