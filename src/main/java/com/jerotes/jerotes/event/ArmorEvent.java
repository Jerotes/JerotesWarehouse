package com.jerotes.jerotes.event;

import com.jerotes.jerotes.item.tool.ItemToolBaseUmbrella;
import com.jerotes.jerotes.util.Main;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ArmorEvent {

	//伞
	@SubscribeEvent
	public static void Umbrella(LivingEvent.LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity == null)
			return;
		if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella || livingEntity.getUseItem().getItem() instanceof ItemToolBaseUmbrella) {
			livingEntity.resetFallDistance();
			float verticalSpeedUse = 1.0f;
			float horizontalSpeedUse = 1.0f;
			float verticalSpeedLiquidUse = 1.0f;
			float horizontalSpeedLiquidUse = 1.0f;
			if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella itemToolBaseUmbrella && !(livingEntity.getUseItem().getItem() instanceof ItemToolBaseUmbrella)) {
				verticalSpeedUse = itemToolBaseUmbrella.getVerticalSpeed();
				horizontalSpeedUse = itemToolBaseUmbrella.getHorizontalSpeed();
				verticalSpeedLiquidUse = itemToolBaseUmbrella.getVerticalSpeedLiquid();
				horizontalSpeedLiquidUse = itemToolBaseUmbrella.getHorizontalSpeedLiquid();
			}
			else if (!(livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella) && (livingEntity.getUseItem().getItem() instanceof ItemToolBaseUmbrella itemToolBaseUmbrella)) {
				verticalSpeedUse = itemToolBaseUmbrella.getVerticalSpeed();
				horizontalSpeedUse = itemToolBaseUmbrella.getHorizontalSpeed();
				verticalSpeedLiquidUse = itemToolBaseUmbrella.getVerticalSpeedLiquid();
				horizontalSpeedLiquidUse = itemToolBaseUmbrella.getHorizontalSpeedLiquid();
			}
			else if ((livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella itemToolBaseUmbrella) && (livingEntity.getUseItem().getItem() instanceof ItemToolBaseUmbrella itemToolBaseUmbrella2)) {
				verticalSpeedUse = (itemToolBaseUmbrella.getVerticalSpeed() * itemToolBaseUmbrella2.getVerticalSpeed());
				horizontalSpeedUse = (itemToolBaseUmbrella.getHorizontalSpeed() * itemToolBaseUmbrella2.getHorizontalSpeed());
				verticalSpeedLiquidUse = (itemToolBaseUmbrella.getVerticalSpeedLiquid() * itemToolBaseUmbrella2.getVerticalSpeedLiquid());
				horizontalSpeedLiquidUse = (itemToolBaseUmbrella.getHorizontalSpeedLiquid() * itemToolBaseUmbrella2.getHorizontalSpeedLiquid());
			}
			if (livingEntity.getDeltaMovement().y() > 0) {
				verticalSpeedUse = (verticalSpeedUse + 1) / 2;
				verticalSpeedLiquidUse = (verticalSpeedLiquidUse + 1) / 2;
			}
			if (livingEntity.horizontalCollision) {
				if (livingEntity.getDeltaMovement().y() > 0) {
					verticalSpeedUse = (verticalSpeedUse + 5) / 6;
					verticalSpeedLiquidUse = (verticalSpeedLiquidUse + 5) / 6;
				}
				else if (livingEntity.getDeltaMovement().y() < 0) {
					verticalSpeedUse = (verticalSpeedUse + 1) / 2;
					verticalSpeedLiquidUse = (verticalSpeedLiquidUse + 1) / 2;
				}
			}
			if (!Main.isInFluid(livingEntity)) {
				livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(1, verticalSpeedUse, 1));
				RushAttack(livingEntity, horizontalSpeedUse, horizontalSpeedLiquidUse);
			}
			else {
				livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(1, verticalSpeedLiquidUse, 1));
				RushAttack(livingEntity, horizontalSpeedUse, horizontalSpeedLiquidUse);
			}
		}
	}
	public static boolean RushAttack(LivingEntity livingEntity, float horizontalSpeedUse, float horizontalSpeedLiquidUse) {
		if (livingEntity.getDeltaMovement().x() < 0.0001f && livingEntity.getDeltaMovement().z() < 0.0001f && livingEntity.getDeltaMovement().x() > -0.0001f && livingEntity.getDeltaMovement().z() > -0.0001f) {
			return false;
		}
		if (livingEntity.onGround()) {
			return false;
		}
		float f = livingEntity.getYRot();
		float f2 = livingEntity.getXRot();
		float f3 = -Mth.sin(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
		float f4 = -Mth.sin(f2 * 0.017453292f);
		float f5 = Mth.cos(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
		float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
		float f7 = 0.01f;
		if (!Main.isInFluid(livingEntity)) {
			f7 *= horizontalSpeedUse;
		}
		else {
			f7 *= horizontalSpeedLiquidUse;
		}
		float f8 = f7 * 0.5f;
		if (f8 > 0 || f7 < 0.01f) {
			f8 = 0;
		}
		livingEntity.push(f3 *= f7 / f6 * 2, f4 *= f8 / f6 * 2, f5 *= f7 / f6 * 2);
		return true;
	}
}

