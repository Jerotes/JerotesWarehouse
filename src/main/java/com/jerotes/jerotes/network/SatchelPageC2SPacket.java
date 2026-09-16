
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import com.jerotes.jerotes.inventory.BookSatchelMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SatchelPageC2SPacket {

    private final boolean forward;

    public SatchelPageC2SPacket(boolean forward) {
        this.forward = forward;
    }

    public static void encode(SatchelPageC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.forward);
    }

    public static SatchelPageC2SPacket decode(FriendlyByteBuf buf) {
        return new SatchelPageC2SPacket(buf.readBoolean());
    }

    public static void consume(SatchelPageC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof BookSatchelMenu menu) {
                if (msg.forward) {
                    menu.nextPage();
                } else {
                    menu.previousPage();
                }
                // 立刻标记一次变化，避免等下一个 tick 才同步，翻页手感会更跟手
                menu.broadcastChanges();
            }
        });
        ctx.setPacketHandled(true);
    }

    public static void send(boolean forward) {
        PacketHandler.NETWORK_WRAPPER.sendToServer(new SatchelPageC2SPacket(forward));
    }
}
