package com.jerotes.jerotes.client.gui;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ArmorEntity;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.world.inventory.SuchInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public class SuchInventoryScreen extends AbstractContainerScreen<SuchInventoryMenu> {
    public static final ResourceLocation CHEST_SLOTS_SPRITE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/chest_slots.png");
    public static final ResourceLocation SADDLE_SLOT_SPRITE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/saddle_slot.png");
    public static final ResourceLocation LLAMA_ARMOR_SLOT_SPRITE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/llama_armor_slot.png");
    public static final ResourceLocation GIANT_BEAST_ARMOR_SLOT_SPRITE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/giant_beast_armor_slot.png");
    public static final ResourceLocation ARMOR_SLOT_SPRITE = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/armor_slot.png");
    public static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/container/horse.png");
    public static final ResourceLocation HORSE_INVENTORY_LARGE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/container/horse_large.png");
    public static final ResourceLocation SLOT = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/slot.png");
    private final Mob mob;
    private float xMouse;
    private float yMouse;

    public SuchInventoryScreen(SuchInventoryMenu suchInventoryMenu, Inventory inventory, Component component) {
        super(suchInventoryMenu, inventory, component);
        this.mob = this.getMenu().mob;
    }

   @Override
   protected void renderBg(GuiGraphics guiGraphics, float f, int n, int n2) {
       int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        if (mob instanceof ArmorEntity chestEntity && chestEntity.hasChest()) {
            if (chestEntity.getInventorySize() > 15) {
                guiGraphics.blit(HORSE_INVENTORY_LARGE_LOCATION, n3, n4, 0, 0, 268, 185, 320, 320);
            }
            else {
                guiGraphics.blit(HORSE_INVENTORY_LOCATION, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
            }
        }
        else {
            guiGraphics.blit(HORSE_INVENTORY_LOCATION, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        }
        Entity mob = this.mob;
        if (mob instanceof ArmorEntity chestEntity && chestEntity.hasChest()) {
            int inventorySize = chestEntity.getInventorySize();
            //如果不足15格 左侧
            if (inventorySize <= 15) {
                int lineCount = (inventorySize + 5 - 1) / 5;
                int remainder = inventorySize % 5;
                int lastRowSlots = (remainder == 0) ? 5 : remainder;
                for (int height = 0; height < lineCount; ++height) {
                    int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : 5;
                    for (int width = 0; width < currentRowSlots; ++width) {
                        guiGraphics.blit(SLOT, this.leftPos + 79 + width * 18, this.topPos + 17 + height * 18, 0, 0, 18, 18, 18, 18);
                    }
                }
            }
            //如果超过15格
            else {
                //如果超过15格 左侧
                {
                    int lineCount = (15 + 5 - 1) / 5;
                    int remainder = 15 % 5;
                    int lastRowSlots = (remainder == 0) ? 5 : remainder;
                    for (int height = 0; height < lineCount; ++height) {
                        int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : 5;
                        for (int width = 0; width < currentRowSlots; ++width) {
                            guiGraphics.blit(SLOT, this.leftPos + 79 + width * 18, this.topPos + 17 + height * 18, 0, 0, 18, 18, 18, 18);
                        }
                    }
                }
                inventorySize = inventorySize - 15;
                //如果超过15格 右侧
                {
                    int lineCount = (inventorySize + 4 - 1) / 4;
                    int remainder = inventorySize % 4;
                    int lastRowSlots = (remainder == 0) ? 4 : remainder;
                    for (int height = 0; height < lineCount; ++height) {
                        int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : 4;
                        for (int width = 0; width < currentRowSlots; ++width) {
                            guiGraphics.blit(SLOT, this.leftPos + 187 + width * 18, this.topPos + 17 + height * 18, 0, 0, 18, 18, 18, 18);
                        }
                    }
                }
            }

        }

        if (mob instanceof Saddleable saddleable && saddleable.isSaddleable()) {
            if (mob instanceof ArmorEntity armor && armor.notBaseSaddle()) {
                guiGraphics.blit(armor.notBaseSaddleResourceLocation(), n3 + 7, n4 + 35 - 18, 0, 0, 18, 18, 18, 18);
            } else {
                guiGraphics.blit(SADDLE_SLOT_SPRITE, n3 + 7, n4 + 35 - 18, 0, 0, 18, 18, 18, 18);
            }
        }
        if (mob instanceof ArmorEntity armor && armor.canWearArmor()) {
            if (armor.notBaseBeastArmor()) {
                guiGraphics.blit(armor.notBaseBeastArmorResourceLocation(), n3 + 7, n4 + 35, 0, 0, 18, 18, 18, 18);
            }
            else if (armor.isArmor(Items.WHITE_CARPET.getDefaultInstance())) {
                guiGraphics.blit(LLAMA_ARMOR_SLOT_SPRITE, n3 + 7, n4 + 35, 0, 0, 18, 18, 18, 18);
            }
            else if (armor.isArmor(JerotesItems.GIANT_BEAST_ARMOR_BASE.get().getDefaultInstance())) {
                guiGraphics.blit(GIANT_BEAST_ARMOR_SLOT_SPRITE, n3 + 7, n4 + 35, 0, 0, 18, 18, 18, 18);
            }
            else {
                guiGraphics.blit(ARMOR_SLOT_SPRITE, n3 + 7, n4 + 35, 0, 0, 18, 18, 18, 18);
            }
        }
       InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, n3 + 51, n4 + 60, 17, (float)(n3 + 51) - this.xMouse, (float)(n4 + 75 - 50) - this.yMouse, this.mob);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int n, int n2, float f) {
        this.xMouse = n;
        this.yMouse = n2;
        super.render(guiGraphics, n, n2, f);
        this.renderTooltip(guiGraphics, n, n2);
    }
}

