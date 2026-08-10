package com.jerotes.jerotes.network;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.item.AASpellJavelin;
import com.jerotes.jerotes.item.Interface.MagicItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellJavelinPacket {
    public final int slot;
    public final ItemStack carried;

    public SpellJavelinPacket(int slot, ItemStack carried) {
        this.slot = slot;
        this.carried = carried.copy();
    }

    public static void encode(SpellJavelinPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
        buf.writeItem(packet.carried);
    }

    public static SpellJavelinPacket decode(FriendlyByteBuf buf) {
        return new SpellJavelinPacket(buf.readInt(), buf.readItem());
    }

    public static void consume(SpellJavelinPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null)
                return;

            AbstractContainerMenu menu = player.containerMenu;

            if (message.slot < 0 || message.slot >= menu.slots.size())
                return;

            Slot slot = menu.getSlot(message.slot);
            ItemStack javelinStack = slot.getItem();
            ItemStack magicStack = message.carried;
            if (!(javelinStack.getItem() instanceof AASpellJavelin javelin))
                return;
            if (!(magicStack.getItem() instanceof MagicItem))
                return;
            javelin.setSpellJavelin(javelinStack, magicStack);
            slot.setChanged();
            menu.broadcastChanges();System.out.println(javelin.getSpellIdMagicItem(javelinStack));
        });
        context.get().setPacketHandled(true);
    }
}