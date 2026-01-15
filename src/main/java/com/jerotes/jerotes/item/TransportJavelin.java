package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.jerotes.jerotes.entity.Arrow.*;

import javax.annotation.Nullable;
import java.util.List;

public class TransportJavelin extends ItemToolBaseJavelin {
	public TransportJavelin() {
		super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(5), 4f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownTransportJavelinEntity(livingEntity.level(), livingEntity, itemStack);
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
	public int getEnchantmentValue() {
		return 15;
	}
}
