package com.jerotes.jerotes.inventory;


import com.jerotes.jerotes.entity.Interface.ArmorEntity;
import com.jerotes.jerotes.init.JerotesMenus;
import com.jerotes.jerotes.util.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SuchInventoryMenu extends AbstractContainerMenu {
    private Container mobContainer;
    public Mob mob;
    public final Level world;
    public final Player player;

    public SuchInventoryMenu(int n, Inventory inv, FriendlyByteBuf buffer) {
        super(JerotesMenus.SUCH_INVENTORY.get(), n);
        this.player = inv.player;
        this.world = inv.player.level();
        if (world.getEntity(buffer.readVarInt()) instanceof Mob mobs) {
            this.mob = mobs;
        }
        Container container;
        if (mob instanceof ArmorEntity chestEntity) {
            container = chestEntity.inventory();
            this.mobContainer = container;
            container.startOpen(inv.player);
            this.addSlot(new Slot(container, 0, 8, 18) {
                public boolean mayPlace(ItemStack itemStack) {
                    boolean trueItem = mob instanceof ArmorEntity armor ? armor.isSaddle(itemStack) : itemStack.is(Items.SADDLE);
                    return trueItem && !this.hasItem() && mob instanceof Saddleable armor && armor.isSaddleable();
                }

                public boolean isActive() {
                    return mob instanceof Saddleable armor && armor.isSaddleable();
                }
            });
            this.addSlot(new Slot(container, 1, 8, 36) {
                public boolean mayPlace(ItemStack itemStack) {
                    return mob instanceof ArmorEntity armor && armor.isArmor(itemStack);
                }
                public boolean isActive() {
                    return mob instanceof ArmorEntity armor && armor.canWearArmor();
                }

                public int getMaxStackSize() {
                    return 1;
                }
            });
            int inventorySize = chestEntity.getInventorySize();
            if (this.hasChest(mob)) {
                //如果不足15格 左侧
                if (inventorySize <= 15) {
                    int lineCount = (inventorySize + 5 - 1) / 5;
                    int remainder = inventorySize % 5;
                    int lastRowSlots = (remainder == 0) ? 5 : remainder;
                    for (int height = 0; height < lineCount; ++height) {
                        int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : 5;
                        for (int width = 0; width < currentRowSlots; ++width) {
                            this.addSlot(new Slot(container, 2 + width + height * 5, 80 + width * 18, 18 + height * 18));
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
                                this.addSlot(new Slot(container, 2 + width + height * 5, 80 + width * 18, 18 + height * 18));
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
                                this.addSlot(new Slot(container, 17 + width + height * 5, 188 + width * 18, 18 + height * 18));
                            }
                        }
                    }
                }
            }

            int height;
            int width;
            for(height = 0; height < 3; ++height) {
                for(width = 0; width < 9; ++width) {
                    this.addSlot(new Slot(inv, width + height * 9 + 9, 8 + width * 18, 102 + height * 18 + -18));
                }
            }

            for(height = 0; height < 9; ++height) {
                this.addSlot(new Slot(inv, height, 8 + height * 18, 142));
            }
        }
    }

    public boolean stillValid(Player player) {
        float distance = 8;
        if (Main.mobSizeGiant(this.mob)) {
            distance = 32;
        }
        if (Main.mobSizeLarge(this.mob)) {
            distance = 16;
        }
        if (this.mobContainer == null) {
            return false;
        }
        if (this.mob instanceof ArmorEntity armor) {
            return !armor.hasInventoryChanged(this.mobContainer) && this.mobContainer.stillValid(player) && this.mob.isAlive() && this.mob.distanceTo(player) < distance;
        }
        return this.mobContainer.stillValid(player) && this.mob.isAlive() && this.mob.distanceTo(player) < distance;
    }

    private boolean hasChest(Entity mob) {
        return mob instanceof ArmorEntity chestEntity && chestEntity.hasChest();
    }

    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (this.mobContainer == null) {
            return ItemStack.EMPTY;
        }
        if (slot != null && slot.hasItem()) {
            ItemStack $$4 = slot.getItem();
            $$2 = $$4.copy();
            int size = this.mobContainer.getContainerSize();
            if (n < size) {
                if (!this.moveItemStackTo($$4, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace($$4) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo($$4, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace($$4)) {
                if (!this.moveItemStackTo($$4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (size <= 2 || !this.moveItemStackTo($$4, 2, size, false)) {
                int $$6 = size;
                int $$7 = $$6 + 27;
                int $$8 = $$7;
                int $$9 = $$8 + 9;
                if (n >= $$8 && n < $$9) {
                    if (!this.moveItemStackTo($$4, $$6, $$7, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (n >= $$6 && n < $$7) {
                    if (!this.moveItemStackTo($$4, $$8, $$9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo($$4, $$8, $$7, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if ($$4.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return $$2;
    }

    public void removed(Player p_39663_) {
        super.removed(p_39663_);
        if (this.mobContainer == null) {
            return;
        }
        this.mobContainer.stopOpen(p_39663_);
    }
}