package com.jerotes.jerotes.client.tooltip;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class SpellClientTooltipComponent implements ClientTooltipComponent {

    private final ResourceLocation texture;

    public SpellClientTooltipComponent(SpellTooltipComponent component) {
        this.texture = component.texture();
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return 20;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.fill(x, y, x + 20, y + 20, 0xFF000000);
        ResourceLocation resourceLocation2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_background.png");
        graphics.blit(resourceLocation2, x + 1, y + 1, 0, 0, 18, 18, 18, 18);
        graphics.blit(texture, x + 1, y + 1, 0, 0, 18, 18, 18, 18);
    }
}