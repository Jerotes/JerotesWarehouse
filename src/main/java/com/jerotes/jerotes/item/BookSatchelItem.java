package com.jerotes.jerotes.item;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.inventory.BookSatchelMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookSatchelItem extends Item {
	public static final String INVENTORY_TAG = "BookSatchelInventory";
	public static final String UUID_TAG = "BookSatchelUUID";
	public static final int INVENTORY_SIZE = 27;

	private final int pageCount;
	private final ResourceLocation guiTexture;

	public BookSatchelItem(Properties properties) {
		this(properties, 1, new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/container/book_satchel.png"));
	}
	public BookSatchelItem(Properties properties, int pageCount) {
		this(properties, pageCount, new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/container/book_satchel.png"));
	}
	public BookSatchelItem(Properties properties, int pageCount, ResourceLocation guiTexture) {
		super(properties.stacksTo(1));
		this.pageCount = Math.max(1, pageCount);
		this.guiTexture = guiTexture;
	}

	public int getPageCount() {
		return pageCount;
	}

	public ResourceLocation getGuiTexture() {
		return guiTexture;
	}

	public int getInventorySize() {
		return pageCount * INVENTORY_SIZE;
	}

	public UUID getSatchelUUID(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();

		if (!tag.hasUUID(UUID_TAG)) {
			UUID uuid = UUID.randomUUID();
			tag.putUUID(UUID_TAG, uuid);
			return uuid;
		}

		return tag.getUUID(UUID_TAG);
	}

	@Nullable
	public static UUID readSatchelUUID(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.hasUUID(UUID_TAG)) {
			return tag.getUUID(UUID_TAG);
		}
		return null;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		playInsertSound(player);
		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			getSatchelUUID(stack);

			NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return stack.getItem().getName(stack);
				}

				@Override
				public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
					return BookSatchelMenu.server(containerId, inventory, hand);
				}
			}, buffer -> {
				buffer.writeEnum(hand);
				buffer.writeItem(stack);
			});
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.book_satchel", this.pageCount).withStyle(ChatFormatting.YELLOW));
		List<ItemStack> contents = getStoredItems(itemStack);
		if (!contents.isEmpty()) {
			int maxDisplay = 5;
			int count = 0;
			for (ItemStack stack : contents) {
				if (count >= maxDisplay) {
					break;
				}
				list.add(Component.literal("- ").append(stack.getHoverName()).withStyle(ChatFormatting.GRAY));
				count++;
			}
			if (contents.size() > maxDisplay) {
				list.add(Component.translatable("container.shulkerBox.more", contents.size() - maxDisplay)
						.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}
	private List<ItemStack> getStoredItems(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) return List.of();
		ListTag itemsTag = tag.getList(INVENTORY_TAG, 10);
		List<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < itemsTag.size(); i++) {
			CompoundTag itemTag = itemsTag.getCompound(i);
			ItemStack item = ItemStack.of(itemTag);
			if (!item.isEmpty()) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
		if (clickAction != ClickAction.SECONDARY) {
			return false;
		}
		ItemStack slotStack = slot.getItem();
		if (slotStack.isEmpty()) {
			Optional<ItemStack> removed = removeFirst(itemStack);
			if (removed.isPresent()) {
				ItemStack result = slot.safeInsert(removed.get());
				if (!result.isEmpty()) {
					addItem(itemStack, result);
				} else {
					playRemoveOneSound(player);
				}
			}
		} else if (slotStack.is(ItemTags.BOOKSHELF_BOOKS)) {
			int amount = addItem(itemStack, slot.safeTake(slotStack.getCount(), slotStack.getMaxStackSize(), player));
			if (amount > 0) {
				playInsertSound(player);
			}
		}

		return true;
	}
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}
		if (otherStack.isEmpty()) {
			Optional<ItemStack> removed = removeFirst(itemStack);
			if (removed.isPresent()) {
				playRemoveOneSound(player);
				slotAccess.set(removed.get());
			}
		} else {
			int amount = addItem(itemStack, otherStack);
			if (amount > 0) {
				playInsertSound(player);
				otherStack.shrink(amount);
			}
		}
		return true;
	}
	private static int addItem(ItemStack satchelStack, ItemStack stack) {
		if (stack.isEmpty() || !stack.is(ItemTags.BOOKSHELF_BOOKS)) {
			return 0;
		}
		CompoundTag tag = satchelStack.getOrCreateTag();
		if (!tag.contains(INVENTORY_TAG, Tag.TAG_LIST)) {
			tag.put(INVENTORY_TAG, new ListTag());
		}
		ListTag list = tag.getList(INVENTORY_TAG, Tag.TAG_COMPOUND);
		int maxAmount = 0;
		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompound(i);
			ItemStack stored = ItemStack.of(itemTag);
			if (ItemStack.isSameItemSameTags(stored, stack)) {
				maxAmount += stored.getMaxStackSize() - stored.getCount();
			}
		}
		maxAmount += getEmptySlotCount(satchelStack) * stack.getMaxStackSize();
		if (maxAmount <= 0) {
			return 0;
		}
		int amount = Math.min(stack.getCount(), maxAmount);
		int remaining = amount;
		for (int i = 0; i < list.size() && remaining > 0; i++) {
			CompoundTag itemTag = list.getCompound(i);
			ItemStack stored = ItemStack.of(itemTag);
			if (!ItemStack.isSameItemSameTags(stored, stack)) {
				continue;
			}
			int space = stored.getMaxStackSize() - stored.getCount();
			int move = Math.min(space, remaining);
			if (move > 0) {
				stored.grow(move);
				stored.save(itemTag);
				remaining -= move;
			}
		}
		while (remaining > 0 && getStoredSlotCount(list) < getInventorySize(satchelStack)) {
			int move = Math.min(stack.getMaxStackSize(), remaining);
			ItemStack newStack = stack.copyWithCount(move);
			CompoundTag itemTag = new CompoundTag();
			itemTag.putInt("Slot", getFirstFreeSlot(list));
			newStack.save(itemTag);
			list.add(itemTag);
			remaining -= move;
		}

		return amount - remaining;
	}
	private static int getInventorySize(ItemStack stack) {
		if (stack.getItem() instanceof BookSatchelItem satchel) {
			return satchel.getInventorySize();
		}

		return INVENTORY_SIZE;
	}

	private static int getStoredSlotCount(ListTag list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompound(i);
			if (tag.contains("Slot", Tag.TAG_INT)) {
				count++;
			}
		}
		return count;
	}
	private static int getFirstFreeSlot(ListTag list) {
		for (int slot = 0; slot < 1024; slot++) {
			boolean occupied = false;
			for (int i = 0; i < list.size(); i++) {
				if (list.getCompound(i).getInt("Slot") == slot) {
					occupied = true;
					break;
				}
			}
			if (!occupied) {
				return slot;
			}
		}
		return -1;
	}
	private static int getEmptySlotCount(ItemStack stack) {
		int size = getInventorySize(stack);
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(INVENTORY_TAG, Tag.TAG_LIST)) {
			return size;
		}
		ListTag list = tag.getList(INVENTORY_TAG, Tag.TAG_COMPOUND);
		return Math.max(0, size - list.size());
	}
	private static Optional<ItemStack> removeFirst(ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag == null || !tag.contains(INVENTORY_TAG, Tag.TAG_LIST)) {
			return Optional.empty();
		}
		ListTag list = tag.getList(INVENTORY_TAG, Tag.TAG_COMPOUND);
		if (list.isEmpty()) {
			return Optional.empty();
		}
		int firstIndex = -1;
		int firstSlot = Integer.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompound(i);
			int slot = itemTag.getInt("Slot");

			if (slot < firstSlot) {
				firstSlot = slot;
				firstIndex = i;
			}
		}
		if (firstIndex < 0) {
			return Optional.empty();
		}
		CompoundTag itemTag = list.getCompound(firstIndex);
		ItemStack result = ItemStack.of(itemTag);
		list.remove(firstIndex);
		if (list.isEmpty()) {
			stack.removeTagKey(INVENTORY_TAG);
		}
		return result.isEmpty() ? Optional.empty() : Optional.of(result);
	}
	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BOOK_PAGE_TURN, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BOOK_PAGE_TURN, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}
}