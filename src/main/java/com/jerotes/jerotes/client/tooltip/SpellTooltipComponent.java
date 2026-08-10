package com.jerotes.jerotes.client.tooltip;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SpellTooltipComponent(ResourceLocation texture) implements TooltipComponent {
}