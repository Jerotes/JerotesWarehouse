package com.jerotes.jerotes.inventory;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.init.JerotesMenus;
import com.jerotes.jerotes.util.Main;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MobInventoryGUIMenu extends AbstractContainerMenu {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player player;
    public int x, y, z;
    public float scale = 0.25f;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private IItemHandler internal;
    private boolean bound = false;
    private Supplier<Boolean> boundItemMatcher = null;
    public Entity boundEntity = null;
    private BlockEntity boundBlockEntity = null;
    public boolean canPutAll = false;
    public boolean canUse = false;
    public boolean canUseMainHand = false;
    public boolean canUseOffHand = false;
    public boolean canUseHelmet = false;
    public boolean canUseChestplate = false;
    public boolean canUseLeggings = false;
    public boolean canUseBoots = false;
    public boolean canUseInventory = false;
    public final ContainerData data;
    public String string = "";
    public String string2 = "";
    public String string3 = "";
    public List<String> stringList = new ArrayList<>();
    public List<String> stringList2 = new ArrayList<>();
    private SimpleContainer invs;
    private Container invAdds;
    public MobInventoryGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData, boolean bl, boolean bl2,
                               boolean canUseMainHand, boolean canUseOffHand, boolean canUseHelmet, boolean canUseChestplate, boolean canUseLeggings, boolean canUseBoots, boolean canUseInventory) {
        this(id, inv, extraData);
        this.canPutAll = bl;
        this.canUse = bl2;
        this.canUseMainHand = canUseMainHand;
        this.canUseOffHand = canUseOffHand;
        this.canUseHelmet = canUseHelmet;
        this.canUseChestplate = canUseChestplate;
        this.canUseLeggings = canUseLeggings;
        this.canUseBoots = canUseBoots;
        this.canUseInventory = canUseInventory;
    }

    public MobInventoryGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData, boolean bl, boolean bl2,
                               boolean canUseMainHand, boolean canUseOffHand, boolean canUseHelmet, boolean canUseChestplate, boolean canUseLeggings, boolean canUseBoots) {
        this(id, inv, extraData);
        this.canPutAll = bl;
        this.canUse = bl2;
        this.canUseMainHand = canUseMainHand;
        this.canUseOffHand = canUseOffHand;
        this.canUseHelmet = canUseHelmet;
        this.canUseChestplate = canUseChestplate;
        this.canUseLeggings = canUseLeggings;
        this.canUseBoots = canUseBoots;
        this.canUseInventory = bl2;
    }

    
    public MobInventoryGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(JerotesMenus.MOB_INVENTORY_GUI.get(), id);
        this.player = inv.player;
        this.world = inv.player.level();
        this.internal = new ItemStackHandler(5);

        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            access = ContainerLevelAccess.create(world, pos);
        }

        if (pos != null) {
            if (extraData.readableBytes() == 1) {
                byte hand = extraData.readByte();
                ItemStack itemstack = hand == 0 ? this.player.getMainHandItem() : this.player.getOffhandItem();
                this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.player.getMainHandItem() : this.player.getOffhandItem());
                itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                    this.internal = capability;
                    this.bound = true;
                });
            } else if (extraData.readableBytes() > 1) {
                extraData.readByte();
                boundEntity = world.getEntity(extraData.readVarInt());
                if (boundEntity != null)
                    boundEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            } else {
                boundBlockEntity = this.world.getBlockEntity(pos);
                if (boundBlockEntity != null)
                    boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            }
        }
        if (boundEntity != null && boundEntity instanceof LivingEntity livingEntity) {
            this.invs = new InventoryArmor(livingEntity);
            this.invs.startOpen(player);
        } else {
            this.invs = new SimpleContainer(6);
        }

        //栏位
        //装备
        this.addSlot(new Slot(this.invs, 0, 8, 62) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseMainHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.MAINHAND))))) {
                    return false;
                }
                return super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseMainHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.MAINHAND))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseMainHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.MAINHAND))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS,  new ResourceLocation(JerotesWarehouse.MODID, "item/empty_slot_sword"));
            }
        });
        this.addSlot(new Slot(this.invs, 1, 26, 62) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseOffHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.OFFHAND))))) {
                    return false;
                }
                return super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseOffHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.OFFHAND))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseOffHand ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.OFFHAND))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
        this.addSlot(new Slot(this.invs, 2, 62, 8) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseHelmet ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.HEAD))))) {
                    return false;
                }
                return (stack.canEquip(EquipmentSlot.HEAD, boundEntity) || stack.getItem() instanceof BannerItem || canPutAll) && super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseHelmet ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.HEAD))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }

            @Override
            public int getMaxStackSize() {
                if (canPutAll) {
                    return super.getMaxStackSize();
                }
                return 1;
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseHelmet ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.HEAD))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
            }
        });
        this.addSlot(new Slot(this.invs, 3, 62, 26) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseChestplate ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.CHEST))))) {
                    return false;
                }
                return (stack.canEquip(EquipmentSlot.CHEST, boundEntity) || canPutAll) && super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseChestplate ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.CHEST))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }

            @Override
            public int getMaxStackSize() {
                if (canPutAll) {
                    return super.getMaxStackSize();
                }
                return 1;
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseChestplate ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.CHEST))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
            }
        });
        this.addSlot(new Slot(this.invs, 4, 62, 44) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseLeggings ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.LEGS))))) {
                    return false;
                }
                return (stack.canEquip(EquipmentSlot.LEGS, boundEntity) || canPutAll) && super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseLeggings ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.LEGS))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }

            @Override
            public int getMaxStackSize() {
                if (canPutAll) {
                    return super.getMaxStackSize();
                }
                return 1;
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseLeggings ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.LEGS))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
            }
        });
        this.addSlot(new Slot(this.invs, 5, 62, 62) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!canPutAll && (!canUse || !canUseBoots ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.FEET))))) {
                    return false;
                }
                return (stack.canEquip(EquipmentSlot.FEET, boundEntity) || canPutAll) && super.mayPlace(stack);
            }
            @Override
            public boolean mayPickup(@NotNull Player playerIn) {
                if (!canPutAll && (!canUse || !canUseBoots ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.FEET))))) {
                    return false;
                }
                return super.mayPickup(playerIn);
            }

            @Override
            public int getMaxStackSize() {
                if (canPutAll) {
                    return super.getMaxStackSize();
                }
                return 1;
            }
            @Override
            public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                if (!canPutAll && (!canUse || !canUseBoots ||
                        (boundEntity instanceof LivingEntity living && Main.isCanNotMove(living.getItemBySlot(EquipmentSlot.FEET))))) {
                    return Optional.empty();
                }
                return super.tryRemove(n, n2, player);
            }
            @Override
            public @NotNull Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
            }
        });

        //物品
        for (int si = 0; si < 3; ++si) {
            for (int sj = 0; sj < 9; ++sj) {
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
            }
        }
        for (int si = 0; si < 9; ++si) {
            this.addSlot(new Slot(inv, si, 8 + si * 18, 142));
        }
        //额外背包
        int size = 4;
        Container container;
        if (this.boundEntity instanceof InventoryEntity inventory) {
            container = inventory.mobInventory();
            this.invAdds = container;
            this.invAdds.startOpen(player);
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    this.addSlot(new Slot(invAdds, width + height * size, 176 + 8 + width * 18, 50 + height * 18) {
                         @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                             if (!canPutAll && (!canUse || !canUseInventory)) {
                                 return false;
                             }
                            return super.mayPlace(stack);
                        }
                        @Override
                        public boolean mayPickup(@NotNull Player playerIn) {
                            if (!canPutAll && (!canUse || !canUseInventory)) {
                                return false;
                            }
                            return super.mayPickup(playerIn);
                        }
                        @Override
                        public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                            if (!canPutAll && (!canUse || !canUseInventory)) {
                                return Optional.empty();
                            }
                            return super.tryRemove(n, n2, player);
                        }
                    });
                }
            }
        }
        else if (this.boundEntity instanceof AbstractVillager inventory) {
            container = inventory.getInventory();
            this.invAdds = container;
            this.invAdds.startOpen(player);
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    this.addSlot(new Slot(invAdds, width + height * size, 176 + 8 + width * 18, 50 + height * 18) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            if (!canUse) {
                                return false;
                            }
                            return super.mayPlace(stack);
                        }
                        @Override
                        public boolean mayPickup(@NotNull Player playerIn) {
                            return canUse && super.mayPickup(playerIn);
                        }
                        @Override
                        public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                            if (canUse) {
                                return super.tryRemove(n, n2, player);
                            }
                            return Optional.empty();
                        }
                    });
                }
            }
        }
        else if (this.boundEntity instanceof Piglin inventory) {
            container = inventory.getInventory();
            this.invAdds = container;
            this.invAdds.startOpen(player);
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    this.addSlot(new Slot(invAdds, width + height * size, 176 + 8 + width * 18, 50 + height * 18) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            if (!canUse) {
                                return false;
                            }
                            return super.mayPlace(stack);
                        }
                        @Override
                        public boolean mayPickup(@NotNull Player playerIn) {
                            return canUse && super.mayPickup(playerIn);
                        }
                        @Override
                        public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                            if (canUse) {
                                return super.tryRemove(n, n2, player);
                            }
                            return Optional.empty();
                        }
                    });
                }
            }
        }
        else if (this.boundEntity instanceof Allay inventory) {
            container = inventory.getInventory();
            this.invAdds = container;
            this.invAdds.startOpen(player);
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    this.addSlot(new Slot(invAdds, width + height * size, 176 + 8 + width * 18, 50 + height * 18) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            if (!canUse) {
                                return false;
                            }
                            return super.mayPlace(stack);
                        }
                        @Override
                        public boolean mayPickup(@NotNull Player playerIn) {
                            return canUse && super.mayPickup(playerIn);
                        }
                        @Override
                        public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                            if (canUse) {
                                return super.tryRemove(n, n2, player);
                            }
                            return Optional.empty();
                        }
                    });
                }
            }
        }
        else if (this.boundEntity instanceof Player inventory) {
            container = inventory.getInventory();
            this.invAdds = container;
            this.invAdds.startOpen(player);
            int inventoryCount = container.getContainerSize();
            if (inventoryCount > 20) {
                size = 8;
            }
            int lineCount = (inventoryCount + size - 1) / size;
            int remainder = inventoryCount % size;
            int lastRowSlots = (remainder == 0) ? size : remainder;
            for (int height = 0; height < lineCount; ++height) {
                int currentRowSlots = (height == lineCount - 1) ? lastRowSlots : size;
                for (int width = 0; width < currentRowSlots; ++width) {
                    this.addSlot(new Slot(invAdds, width + height * size, 176 + 8 + width * 18, 50 + height * 18) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            if (!canUse) {
                                return false;
                            }
                            return super.mayPlace(stack);
                        }
                        @Override
                        public boolean mayPickup(@NotNull Player playerIn) {
                            return canUse && super.mayPickup(playerIn);
                        }
                        @Override
                        public @NotNull Optional<ItemStack> tryRemove(int n, int n2, @NotNull Player player) {
                            if (canUse) {
                                return super.tryRemove(n, n2, player);
                            }
                            return Optional.empty();
                        }
                    });
                }
            }
        }
        //攻击
        this.data = new SimpleContainerData(17);
        if (!world.isClientSide) {
            double d = 0;
            double d2 = 0;
            double d3 = 0;
            double d4 = 0;
            double d5 = 0;
            double d6 = 0;
            double d7 = 0;

            double d8 = 0;
            double d9 = 0;

            double d10 = 0;
            double d11 = 0;
            double d12 = 0;
            double d13 = 0;
            double d14 = 0;
            double d15 = 0;

            double d16 = 0;
            double d17 = 0;
            if (boundEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    d = livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 100;
                    d2 = livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 100;
                }
                if (livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
                    d3 = livingEntity.getAttributeValue(Attributes.ATTACK_KNOCKBACK) * 1000;
                    d4 = livingEntity.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) * 1000;
                }
                if (livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                    d5 = livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) * 1000;
                    d6 = livingEntity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) * 1000;
                }
                if (livingEntity instanceof SpellUseEntity spellUseEntity) {
                    d7 = spellUseEntity.getSpellLevel();
                }
                d16 = livingEntity.getRemainingFireTicks();
                d17 = livingEntity.getTicksFrozen();
            }
            if (this.canPutAll) {
                d8 = 1;
            }
            if (this.canUse) {
                d9 = 1;
            }
            if (!canUseMainHand) {
                d10 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseMainHand))) {
                d10 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseOffHand))) {
                d11 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseHelmet))) {
                d12 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseChestplate))) {
                d13 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseLeggings))) {
                d14 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseBoots))) {
                d15 = 1;
            }
            this.data.set(0, (int) d);
            this.data.set(1, (int) d2);
            this.data.set(2, (int) d3);
            this.data.set(3, (int) d4);
            this.data.set(4, (int) d5);
            this.data.set(5, (int) d6);
            this.data.set(6, (int) d7);
            this.data.set(7, (int) d8);
            this.data.set(8, (int) d9);
            this.data.set(9, (int) d10);
            this.data.set(10, (int) d11);
            this.data.set(11, (int) d12);
            this.data.set(12, (int) d13);
            this.data.set(13, (int) d14);
            this.data.set(14, (int) d15);
            this.data.set(15, (int) d16);
            this.data.set(16, (int) d17);
        }
        this.addDataSlots(this.data);

        if (extraData != null) {
            string = extraData.readUtf();
            string2 = extraData.readUtf();
            string3 = extraData.readUtf();
            stringList = extraData.readList(FriendlyByteBuf::readUtf);
            stringList2 = extraData.readList(FriendlyByteBuf::readUtf);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.world.isClientSide) {

            double d = 0;
            double d2 = 0;
            double d3 = 0;
            double d4 = 0;
            double d5 = 0;
            double d6 = 0;
            double d7 = 0;

            double d8 = 0;
            double d9 = 0;

            double d10 = 0;
            double d11 = 0;
            double d12 = 0;
            double d13 = 0;
            double d14 = 0;
            double d15 = 0;

            double d16 = 0;
            double d17 = 0;
            if (boundEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    d = livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 100;
                    d2 = livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 100;
                }
                if (livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
                    d3 = livingEntity.getAttributeValue(Attributes.ATTACK_KNOCKBACK) * 1000;
                    d4 = livingEntity.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) * 1000;
                }
                if (livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                    d5 = livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) * 1000;
                    d6 = livingEntity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) * 1000;
                }
                if (livingEntity instanceof SpellUseEntity spellUseEntity) {
                    d7 = spellUseEntity.getSpellLevel();
                }
                d16 = livingEntity.getRemainingFireTicks();
                d17 = livingEntity.getTicksFrozen();
            }
            if (this.canPutAll) {
                d8 = 1;
            }
            if (this.canUse) {
                d9 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseMainHand))) {
                d10 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseOffHand))) {
                d11 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseHelmet))) {
                d12 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseChestplate))) {
                d13 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseLeggings))) {
                d14 = 1;
            }
            if (!(!canPutAll && (!canUse || !canUseBoots))) {
                d15 = 1;
            }
            this.data.set(0, (int) d);
            this.data.set(1, (int) d2);
            this.data.set(2, (int) d3);
            this.data.set(3, (int) d4);
            this.data.set(4, (int) d5);
            this.data.set(5, (int) d6);
            this.data.set(6, (int) d7);
            this.data.set(7, (int) d8);
            this.data.set(8, (int) d9);
            this.data.set(9, (int) d10);
            this.data.set(10, (int) d11);
            this.data.set(11, (int) d12);
            this.data.set(12, (int) d13);
            this.data.set(13, (int) d14);
            this.data.set(14, (int) d15);
            this.data.set(15, (int) d16);
            this.data.set(16, (int) d17);
        }
        if (!this.world.isClientSide) {
            if (boundEntity instanceof LivingEntity livingEntity) {
                for (int x = 0; x < 6; x++) {
                    ItemStack stack = livingEntity.getItemBySlot(JerotesWarehouse.slot[x]);
                    if (invs instanceof InventoryArmor inventoryArmor)
                        inventoryArmor.updateSlotContents(x, stack);
                }
            }
        }
    }

    public String getMobFaction() {
        return string;
    }
    public String getMobFactionModId() {
        return string2;
    }
    public String getMobFactionModIdSelf() {
        return string3;
    }
    public List<String> getFactionTypeList() {
        return stringList;
    }
    public List<String> getFactionTypeEvenThoughTame() {
        return stringList2;
    }
    public double getAttackDamage() {
        return this.data.get(0);
    }
    public double getBaseAttackDamage() {
        return this.data.get(1);
    }
    public double getAttackKnockback() {
        return this.data.get(2);
    }
    public double getBaseAttackKnockback() {
        return this.data.get(3);
    }
    public double getKnockbackResistance() {
        return this.data.get(4);
    }
    public double getBaseKnockbackResistance() {
        return this.data.get(5);
    }
    public double getSpellLevel() {
        return this.data.get(6);
    }
    public boolean isCanPutAll() {
        return this.data.get(7) == 1;
    }
    public boolean isCanUse() {
        return this.data.get(8) == 1;
    }
    public boolean isCanUseMainHand() {
        return this.data.get(9) == 1;
    }
    public boolean isCanUseOffHand() {
        return this.data.get(10) == 1;
    }
    public boolean isCanUseHelmet() {
        return this.data.get(11) == 1;
    }
    public boolean isCanUseChestplate() {
        return this.data.get(12) == 1;
    }
    public boolean isCanUseLeggings() {
        return this.data.get(13) == 1;
    }
    public boolean isCanUseBoots() {
        return this.data.get(14) == 1;
    }
    public double getFireTick() {
        return this.data.get(15);
    }
    public double getFreezeTick() {
        return this.data.get(16);
    }
    public float getScale() {
        return scale;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 6) {
                if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(itemstack1, itemstack);
            }
            else if (!this.moveItemStackTo(itemstack1, 0, 6, false)) {
                if (index < 6 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 6 + 27, this.slots.size(), true))
                        return ItemStack.EMPTY;
                }
                else {
                    if (!this.moveItemStackTo(itemstack1, 6, 6 + 27, false))
                        return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
                }
            }
        }
    }

    public static class InventoryArmor extends SimpleContainer {

        private final LivingEntity mob;

        public InventoryArmor(LivingEntity living) {
            super(6);
            this.mob = living;
            for (int x = 0; x < 6; x++) {
                ItemStack stack = living.getItemBySlot(JerotesWarehouse.slot[x]);
                this.updateSlotContents(x, stack);
            }
        }

        @Override
        public void setItem(int index, @NotNull ItemStack stack) {
            this.mob.setItemSlot(JerotesWarehouse.slot[index], stack);
            super.setItem(index, stack);
        }

        public void updateSlotContents(int index, ItemStack stack) {
            super.setItem(index, stack);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}