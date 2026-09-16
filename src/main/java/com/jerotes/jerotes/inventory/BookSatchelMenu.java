package com.jerotes.jerotes.inventory;

import com.jerotes.jerotes.init.JerotesMenus;
import com.jerotes.jerotes.item.BookSatchelItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.Level;

import com.jerotes.jerotes.item.BookSatchelItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BookSatchelMenu extends AbstractContainerMenu {

    public static final int PAGE_SIZE = 27;

    private final NonNullList<ItemStack> items;
    private final int pageCount;

    private final Player player;
    private final boolean clientSide;

    private ItemStack satchelStack;
    private InteractionHand hand;

    private int currentPage = 0;

    private BookSatchelMenu(
            int containerId,
            Inventory playerInventory,
            ItemStack satchelStack,
            InteractionHand hand,
            boolean clientSide
    ) {
        super(JerotesMenus.BOOK_SATCHEL.get(), containerId);
        this.player = playerInventory.player;
        this.satchelStack = satchelStack;
        this.hand = hand;
        this.clientSide = clientSide;

        BookSatchelItem item = (BookSatchelItem) satchelStack.getItem();
        this.pageCount = Math.max(1, item.getPageCount());
        this.items = NonNullList.withSize(this.pageCount * PAGE_SIZE, ItemStack.EMPTY);

        if (!clientSide) {
            loadFromStack(satchelStack);
        }

        addSatchelSlots();
        addPlayerInventory(playerInventory);
    }

    public static BookSatchelMenu server(
            int containerId,
            Inventory playerInventory,
            InteractionHand hand
    ) {
        ItemStack stack = playerInventory.player.getItemInHand(hand);

        return new BookSatchelMenu(
                containerId,
                playerInventory,
                stack,
                hand,
                false
        );
    }

    public static BookSatchelMenu client(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        ItemStack stack = buffer.readItem();

        return new BookSatchelMenu(
                containerId,
                playerInventory,
                stack,
                hand,
                true
        );
    }

    public ResourceLocation getGuiTexture() {
        return ((BookSatchelItem) satchelStack.getItem()).getGuiTexture();
    }

    public ItemStack getSatchelStack() {
        return satchelStack;
    }

    public int getTotalPages() {
        return pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setPage(int page) {
        this.currentPage = Mth.clamp(page, 0, pageCount - 1);
    }

    public void nextPage() {
        if (currentPage < pageCount - 1) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    private void addSatchelSlots() {
        SatchelContainer container = new SatchelContainer();

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int localIndex = column + row * 9;

                this.addSlot(new Slot(container, localIndex,
                        8 + column * 18,
                        18 + row * 18) {

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(ItemTags.BOOKSHELF_BOOKS);
                    }
                });
            }
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    142
            ));
        }
    }

    private void loadFromStack(ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }

        CompoundTag tag = stack.getTag();

        if (tag == null) {
            return;
        }

        if (!tag.contains(BookSatchelItem.INVENTORY_TAG, Tag.TAG_LIST)) {
            return;
        }

        ListTag list = tag.getList(
                BookSatchelItem.INVENTORY_TAG,
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < list.size(); i++) {
            CompoundTag slotTag = list.getCompound(i);

            int slot = slotTag.getInt("Slot");

            if (slot < 0 || slot >= items.size()) {
                continue;
            }

            ItemStack itemStack = ItemStack.of(slotTag);

            if (itemStack.is(ItemTags.BOOKSHELF_BOOKS)) {
                items.set(slot, itemStack);
            }
        }
    }

    private void saveToStack() {
        if (clientSide || satchelStack.isEmpty()) {
            return;
        }

        ListTag list = new ListTag();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);

            if (stack.isEmpty()) {
                continue;
            }

            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt("Slot", i);
            stack.save(slotTag);

            list.add(slotTag);
        }

        CompoundTag tag = satchelStack.getOrCreateTag();
        tag.put(BookSatchelItem.INVENTORY_TAG, list);
    }

    private void setChanged() {
        if (!clientSide) {
            saveToStack();
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!clientSide) {
            saveToStack();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < PAGE_SIZE) {
            if (!moveItemStackTo(
                    sourceStack,
                    PAGE_SIZE,
                    this.slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!sourceStack.is(ItemTags.BOOKSHELF_BOOKS)) {
                return ItemStack.EMPTY;
            }

            if (!moveItemStackTo(
                    sourceStack,
                    0,
                    PAGE_SIZE,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (sourceStack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, sourceStack);

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.isRemoved()) {
            return false;
        }

        ItemStack current = player.getItemInHand(hand);

        return current.getItem() instanceof BookSatchelItem;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!clientSide) {
            saveToStack();
        }
    }

    private class SatchelContainer implements Container {

        @Override
        public int getContainerSize() {
            return PAGE_SIZE;
        }

        @Override
        public boolean isEmpty() {
            int base = currentPage * PAGE_SIZE;
            for (int i = 0; i < PAGE_SIZE; i++) {
                if (!items.get(base + i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return items.get(currentPage * PAGE_SIZE + slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack result = ContainerHelper.removeItem(items, currentPage * PAGE_SIZE + slot, amount);

            if (!result.isEmpty()) {
                setChanged();
            }

            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(items, currentPage * PAGE_SIZE + slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (stack.isEmpty() || stack.is(ItemTags.BOOKSHELF_BOOKS)) {
                items.set(currentPage * PAGE_SIZE + slot, stack);
                setChanged();
            }
        }

        @Override
        public void setChanged() {
            BookSatchelMenu.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return BookSatchelMenu.this.stillValid(player);
        }

        @Override
        public void clearContent() {
            int base = currentPage * PAGE_SIZE;
            for (int i = 0; i < PAGE_SIZE; i++) {
                items.set(base + i, ItemStack.EMPTY);
            }

            setChanged();
        }
    }
}