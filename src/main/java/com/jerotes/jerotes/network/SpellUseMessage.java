
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellUseMessage {
	int type, pressedms;
	boolean isAdd;

	public SpellUseMessage(int type, int pressedms, boolean isAdd) {
		this.type = type;
		this.pressedms = pressedms;
		this.isAdd = isAdd;
	}

	public SpellUseMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
		this.isAdd = buffer.readBoolean();
	}

	public static void encode(com.jerotes.jerotes.network.SpellUseMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
		buffer.writeBoolean(message.isAdd);
	}
	public static com.jerotes.jerotes.network.SpellUseMessage decode(FriendlyByteBuf buffer) {
		return new com.jerotes.jerotes.network.SpellUseMessage(buffer.readInt(),buffer.readInt(),buffer.readBoolean());
	}

	public static void consume(com.jerotes.jerotes.network.SpellUseMessage message, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayer playerEntity = context.get().getSender();
			if (playerEntity != null) {
				pressAction(context.get().getSender(), message.type, message.pressedms, message.isAdd);
			}
		});
		context.get().setPacketHandled(true);
	}

	public static void pressAction(Player player, int type, int pressedms, boolean isAdd) {
		Level level = player.level();
		if (!level.hasChunkAt(player.blockPosition()))
			return;
		if (type == 0) {
			//主
			if (!isAdd) {
				if (SpellFindUseEvent.GetMainSpellUseCoolDownTick(player) > 0) {
					return;
				}
				MainSpellUse(player);
			}
			//次
			else {
				if (SpellFindUseEvent.GetAddSpellUseCoolDownTick(player) > 0) {
					return;
				}
				AddSpellUse(player);
			}
		}
		else if (type == 1) {
			//取消施法专注
			SpellType.endConcentration(player);
		}
		else if (type == 2) {
			//修改法术子类别
			//如果正在手持施法物品
			if (player.getMainHandItem().getItem() instanceof MagicItem magicItem && magicItem.hasSpellMagicItem()) {
				ItemStack itemStack = player.getMainHandItem();
				var ref = new Object() {
					int n = 0;
				};
				String magic = magicItem.getSpellIdMagicItem(itemStack);
				int levelSpell = magicItem.getSpellLevelMagicItem(itemStack);
				if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
					Entity target = player;
					int trueLevel = SpellListByString.getSpell(levelSpell, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();
					MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
					int max = magicSpell.getSubcategoryReach();
					ref.n = magicItem.getSubcategoryMagicItem(itemStack);
					ref.n += 1;
					if (ref.n > max) {
						ref.n = 0;
					}
					if (max > 0) {
						Component components = magicSpell.getSpellSubcategoryReachDesc(ref.n).copy();
						Component component = Component.empty()
								.append(Component.translatable("message.jerotes.change_spell_subcategory", ref.n).withStyle(ChatFormatting.GOLD))
								.append(components.copy().withStyle(ChatFormatting.AQUA));
						if (level instanceof ServerLevel) {
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
				player.setItemSlot(EquipmentSlot.MAINHAND, magicItem.setSubcategoryMagicItem(itemStack, ref.n));
			}
			else if (player.getOffhandItem().getItem() instanceof MagicItem magicItem && magicItem.hasSpellMagicItem()) {
				ItemStack itemStack = player.getOffhandItem();
				var ref = new Object() {
					int n = 0;
				};
				String magic = magicItem.getSpellIdMagicItem(itemStack);
				int levelSpell = magicItem.getSpellLevelMagicItem(itemStack);
				if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
					Entity target = player;
					int trueLevel = SpellListByString.getSpell(levelSpell, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();
					MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
					int max = magicSpell.getSubcategoryReach();
					ref.n = magicItem.getSubcategoryMagicItem(itemStack);
					ref.n += 1;
					if (ref.n > max) {
						ref.n = 0;
					}
					if (max > 0) {
						Component components = magicSpell.getSpellSubcategoryReachDesc(ref.n).copy();
						Component component = Component.empty()
								.append(Component.translatable("message.jerotes.change_spell_subcategory", ref.n).withStyle(ChatFormatting.GOLD))
								.append(components.copy().withStyle(ChatFormatting.AQUA));
						if (level instanceof ServerLevel) {
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
				player.setItemSlot(EquipmentSlot.OFFHAND, magicItem.setSubcategoryMagicItem(itemStack, ref.n));
			}
			else {
				var ref = new Object() {
					int n = 0;
				};
				String magic = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget;
				int levelSpell = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel;
				if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
					Entity target = player;
					int trueLevel = SpellListByString.getSpell(levelSpell, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();
					MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
					int max = magicSpell.getSubcategoryReach();
					ref.n = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).SpellSubcategoryFind;
					ref.n += 1;
					if (ref.n > max) {
						ref.n = 0;
					}
					if (max > 0) {
						Component components = magicSpell.getSpellSubcategoryReachDesc(ref.n).copy();
						Component component = Component.empty()
								.append(Component.translatable("message.jerotes.change_spell_subcategory", ref.n).withStyle(ChatFormatting.GOLD))
								.append(components.copy().withStyle(ChatFormatting.AQUA));
						if (level instanceof ServerLevel) {
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
				player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
					capability.setSpellSubcategoryFind(ref.n);
				});
			}
		}
	}

	public static void MainSpellUse(Player player) {
		if (SpellType.getCastCooldownJerotes(player) > 0) {
			return;
		}
		String magic = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget;
		int level = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel;
		if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
			Entity target = player;
			int trueLevel = SpellListByString.getSpell(level, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));

			if (Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) != null &&
					(Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) instanceof LivingEntity || magicSpellStart.canTargetNonLivingEntity())) {
				if (magicSpellStart.getMagicType() != MagicType.SELF)
					target = Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable());
			}
			if (target == null) {
				target = player;
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					boolean bl = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic)).spellUse();
					if (bl) {
						player.swing(InteractionHand.MAIN_HAND);
						//增加cd
						player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
							capability.setMainSpellUseCoolDownTick(magicSpell.playerUseCooldownTick(player));
						});
					}
				}
			}

		}
	}
	public static void AddSpellUse(Player player) {
		if (SpellType.getCastCooldownJerotes(player) > 0) {
			return;
		}
		String magic = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget;
		int level = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTargetLevel;
		if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
			Entity target = player;
			int trueLevel = SpellListByString.getSpell(level, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));

			if (Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) != null &&
					(Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) instanceof LivingEntity || magicSpellStart.canTargetNonLivingEntity())) {
				if (magicSpellStart.getMagicType() != MagicType.SELF)
					target = Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable());
			}
			if (target == null) {
				target = player;
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					boolean bl = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic)).spellUse();
					if (bl) {
						player.swing(InteractionHand.OFF_HAND);
						//增加cd
						player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
							capability.setAddSpellUseCoolDownTick(magicSpell.playerUseCooldownTick(player));
						});
					}
				}
			}

		}
	}
}
