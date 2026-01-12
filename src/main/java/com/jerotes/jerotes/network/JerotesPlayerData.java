package com.jerotes.jerotes.network;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JerotesPlayerData {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
	}
	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(PlayerVariables.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide())
				(event.getEntity().getCapability(CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level().isClientSide())
				(event.getEntity().getCapability(CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide())
				(event.getEntity().getCapability(CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			PlayerVariables original = (event.getOriginal().getCapability(CAPABILITY, null).orElse(new PlayerVariables()));
			PlayerVariables clone = (event.getEntity().getCapability(CAPABILITY, null).orElse(new PlayerVariables()));
			clone.MainSpellTarget = original.MainSpellTarget;
			clone.MainSpellTargetLevel = original.MainSpellTargetLevel;
			clone.MainSpellUseCoolDownTick = original.MainSpellUseCoolDownTick;
			clone.MainSpellUseCoolDownTickMax = original.MainSpellUseCoolDownTickMax;
			clone.AddSpellTarget = original.AddSpellTarget;
			clone.AddSpellTargetLevel = original.AddSpellTargetLevel;
			clone.AddSpellUseCoolDownTick = original.AddSpellUseCoolDownTick;
			clone.AddSpellUseCoolDownTickMax = original.AddSpellUseCoolDownTickMax;
			clone.syncPlayerVariables(event.getEntity());

		}
	}

	public static final Capability<PlayerVariables> CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() {
	});

	@Mod.EventBusSubscriber
	private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player)
				event.addCapability(new ResourceLocation(JerotesWarehouse.MODID, "player_variables"), new PlayerVariablesProvider());
		}

		private final PlayerVariables playerVariables = new PlayerVariables();
		private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return playerVariables.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			playerVariables.readNBT(nbt);
		}
	}

	public static class PlayerVariables {
		public String MainSpellTarget = "";
		public int MainSpellTargetLevel = 1;
		public int MainSpellUseCoolDownTick = 0;
		public int MainSpellUseCoolDownTickMax = 0;
		public String AddSpellTarget = "";
		public int AddSpellTargetLevel = 1;
		public int AddSpellUseCoolDownTick = 0;
		public int AddSpellUseCoolDownTickMax = 0;

		public void setMainSpellTarget(String string){
			this.MainSpellTarget = string;
		}
		public void setMainSpellTargetLevel(int n){
			this.MainSpellTargetLevel = n;
		}
		public void setMainSpellUseCoolDownTick(int n){
			this.MainSpellUseCoolDownTick = n;
		}
		public void setMainSpellUseCoolDownTickMax(int n){
			this.MainSpellUseCoolDownTickMax = n;
		}

		public void setAddSpellTarget(String string){
			this.AddSpellTarget = string;
		}
		public void setAddSpellTargetLevel(int n){
			this.AddSpellTargetLevel = n;
		}
		public void setAddSpellUseCoolDownTick(int n){
			this.AddSpellUseCoolDownTick = n;
		}
		public void setAddSpellUseCoolDownTickMax(int n){
			this.AddSpellUseCoolDownTickMax = n;
		}

		public void syncPlayerVariables(Entity entity) {
			if (!entity.level().isClientSide() && entity instanceof ServerPlayer serverPlayer) {
				if (this.writeNBT() != null) {
					//1.20.1//
					PacketHandler.NETWORK_WRAPPER.sendTo(
							new PlayerVariablesSyncMessage(this.writeNBT()), serverPlayer.connection.connection,
							NetworkDirection.PLAY_TO_CLIENT);
				}
			}
		}


		public CompoundTag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("JerotesMainSpellTarget", MainSpellTarget);
			nbt.putInt("JerotesMainSpellTargetLevel", MainSpellTargetLevel);
			nbt.putInt("JerotesMainSpellUseCoolDownTick", MainSpellUseCoolDownTick);
			nbt.putInt("JerotesMainSpellUseCoolDownTickMax", MainSpellUseCoolDownTickMax);
			nbt.putString("JerotesAddSpellTarget", AddSpellTarget);
			nbt.putInt("JerotesAddSpellTargetLevel", AddSpellTargetLevel);
			nbt.putInt("JerotesAddSpellUseCoolDownTick", AddSpellUseCoolDownTick);
			nbt.putInt("JerotesAddSpellUseCoolDownTickMax", AddSpellUseCoolDownTickMax);
			return nbt;
		}

		public void readNBT(Tag tag) {
			CompoundTag nbt = (CompoundTag) tag;
			MainSpellTarget = nbt.getString("JerotesMainSpellTarget");
			MainSpellTargetLevel = nbt.getInt("JerotesMainSpellTargetLevel");
			MainSpellUseCoolDownTick = nbt.getInt("JerotesMainSpellUseCoolDownTick");
			MainSpellUseCoolDownTickMax = nbt.getInt("JerotesMainSpellUseCoolDownTickMax");
			AddSpellTarget = nbt.getString("JerotesAddSpellTarget");
			AddSpellTargetLevel = nbt.getInt("JerotesAddSpellTargetLevel");
			AddSpellUseCoolDownTick = nbt.getInt("JerotesAddSpellUseCoolDownTick");
			AddSpellUseCoolDownTickMax = nbt.getInt("JerotesAddSpellUseCoolDownTickMax");
		}
	}


	public static class PlayerVariablesSyncMessage {
		private final CompoundTag data;
		public PlayerVariablesSyncMessage(CompoundTag nbt) {
			this.data = nbt;
		}
		public static void encode(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeNbt(message.data);
		}
		public static PlayerVariablesSyncMessage decode(FriendlyByteBuf buffer) {
			return new PlayerVariablesSyncMessage(buffer.readNbt());
		}
		public static void consume(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				Minecraft minecraft = Minecraft.getInstance();
				if (minecraft.player != null) {
					minecraft.player.getCapability(CAPABILITY).ifPresent(cap -> {
						cap.readNBT(message.data);
					});
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}
