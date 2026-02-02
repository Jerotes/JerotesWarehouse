package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Shoot.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.item.Interface.ItemAnesthetized;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import com.jerotes.jerotes.entity.Shoot.Arrow.ThrownAnestheticJavelinEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

public class AnestheticJavelin extends ItemToolBaseJavelin implements ItemAnesthetized {
	public AnestheticJavelin() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(5), 4.0f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownAnestheticJavelinEntity(livingEntity.level(), livingEntity, itemStack);
	}

	@Override
	public void attackUse(Entity self, Entity attackTo) {
		if (!attackTo.level().isClientSide && attackTo instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 440, 4));
			livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 440, 2));
		}
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public int getAnesthetized() {
		return 360;
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
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.anesthetized_melee_or_throw", this.getAnesthetized()).withStyle(ChatFormatting.YELLOW));
		list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
	}
	public MutableComponent getDisplayName() {
		return Component.translatable(this.getDescriptionId() + ".desc");
	}
}
