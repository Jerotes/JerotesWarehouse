package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Shoot.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.Shoot.Arrow.ThrownExplosiveJavelinEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosiveJavelin extends ItemToolBaseJavelin {
	public ExplosiveJavelin() {
		super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(5),0.1f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownExplosiveJavelinEntity(livingEntity.level(), livingEntity, itemStack);
	}
	public float getThrowingDamage() {
		return 3.0f;
	}

	@Override
	public void attackUse(Entity self, Entity attackTo) {
		if (!attackTo.level().isClientSide && attackTo instanceof LivingEntity livingEntity) {
			self.level().explode(self, attackTo.getX(), attackTo.getY(), attackTo.getZ(), 1, Level.ExplosionInteraction.MOB);
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
	}
	public MutableComponent getDisplayName() {
		return Component.translatable(this.getDescriptionId() + ".desc");
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
