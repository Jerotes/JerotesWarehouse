package com.jerotes.jerotes.client.gui;

import com.jerotes.jerotes.network.SatchelPageC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.jerotes.jerotes.inventory.BookSatchelMenu;
import com.mojang.blaze3d.systems.RenderSystem;

public class BookSatchelScreen extends AbstractContainerScreen<BookSatchelMenu> {

    private final ResourceLocation texture;

    public BookSatchelScreen(BookSatchelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
        this.texture = menu.getGuiTexture();
    }

    @Override
    protected void init() {
        super.init();

        if (menu.getTotalPages() > 1) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            this.addRenderableWidget(Button.builder(Component.literal("P"), b -> turnPage(false))
                    .bounds(x + imageWidth - 42, y + 3, 16, 14)
                    .build());

            this.addRenderableWidget(Button.builder(Component.literal("N"), b -> turnPage(true))
                    .bounds(x + imageWidth - 22, y + 3, 16, 14)
                    .build());
        }
    }

    private void turnPage(boolean forward) {
        int totalPages = menu.getTotalPages();

        boolean canTurn = forward ? menu.getCurrentPage() < totalPages - 1 : menu.getCurrentPage() > 0;
        if (!canTurn) {
            return;
        }

        SatchelPageC2SPacket.send(forward);
        if (forward) {
            menu.nextPage();
        } else {
            menu.previousPage();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, texture);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 8, 6, 4210752, false);

        if (menu.getTotalPages() > 1) {
            String pageText = (menu.getCurrentPage() + 1) + "/" + menu.getTotalPages();
            int textWidth = font.width(pageText);
            int spacing = 2;
            int pageX = imageWidth - 42 - spacing - textWidth;
            int pageY = 4 + (14 - font.lineHeight) / 2;
            guiGraphics.drawString(font, pageText, pageX, pageY, 0x421e01, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}