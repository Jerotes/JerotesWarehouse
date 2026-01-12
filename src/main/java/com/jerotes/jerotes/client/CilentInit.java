package com.jerotes.jerotes.client;

import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CilentInit
{
    public static void clientInit() {
        JerotesItems.REGISTRY.getEntries().forEach(item -> {
            ItemProperties.register(item.get(), new ResourceLocation("throwing"), (itemStack, clientLevel, livingEntity, n) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f);
            ItemProperties.register(item.get(), new ResourceLocation("jerotes_swing"), (itemStack, clientLevel, livingEntity, n) -> livingEntity != null && livingEntity.getMainHandItem() == itemStack && livingEntity.swinging ? 1.0f : 0.0f);
            ItemProperties.register(item.get(), new ResourceLocation("jerotes_charge_stage"), (itemStack, clientLevel, livingEntity, n) -> {
                if (livingEntity == null || !livingEntity.isUsingItem() || livingEntity.getUseItem() != itemStack) {
                    return 0.0f;
                }
                Item baseItem = itemStack.getItem();
                if (!(baseItem instanceof ItemToolBaseSpearBase spear)) {
                    return 0.0f;
                }

                int useTime = livingEntity.getTicksUsingItem();

                int delayTicks = spear.delayTicks;
                int dismountEnd = delayTicks + spear.dismountConditions.map(c -> c.maxDurationTicks()).orElse(0);
                int knockbackEnd = delayTicks + spear.knockbackConditions.map(c -> c.maxDurationTicks()).orElse(0);
                int damageEnd = delayTicks + spear.damageConditions.map(c -> c.maxDurationTicks()).orElse(0);
                if (useTime < delayTicks) {
                    return 0.0f;
                } else if (useTime < dismountEnd) {
                    return 1.0f;
                } else if (useTime < knockbackEnd) {
                    return 2.0f;
                } else if (useTime < damageEnd) {
                    return 3.0f;
                } else {
                    return 0.0f;
                }
            });
        });
    }
}
