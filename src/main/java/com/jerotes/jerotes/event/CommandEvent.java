package com.jerotes.jerotes.event;

import com.jerotes.jerotes.alchemy.Alchemy;
import com.jerotes.jerotes.argument.SpellArgumentType;
import com.jerotes.jerotes.entity.Interface.JerotesChangeAbstractHorse;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellListByString;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Objects;

@Mod.EventBusSubscriber
public class CommandEvent {

	@SubscribeEvent
	public static void registerCommand4(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("jerotes").requires(s -> s.hasPermission(4))
				//炼金
				.then(Commands.literal("alchemy")
						.then(Commands.literal("potion")
								.then(Commands.argument("item1", ItemArgument.item(event.getBuildContext()))
										.then(Commands.argument("item2", ItemArgument.item(event.getBuildContext()))
												.then(Commands.argument("item3", ItemArgument.item(event.getBuildContext()))
														.then(Commands.argument("item4", ItemArgument.item(event.getBuildContext()))
																.then(Commands.argument("item5", ItemArgument.item(event.getBuildContext()))
																.then(Commands.argument("player", EntityArgument.player()).executes(arguments -> {
																			Item item1 = ItemArgument.getItem(arguments, "item1").getItem();
																			Item item2 = ItemArgument.getItem(arguments, "item2").getItem();
																			Item item3 = ItemArgument.getItem(arguments, "item3").getItem();
																			Item item4 = ItemArgument.getItem(arguments, "item4").getItem();
																			Item item5 = ItemArgument.getItem(arguments, "item5").getItem();
																			Player playerTo = EntityArgument.getPlayer(arguments, "player");
																			ItemStack resultPotion = Alchemy.resultPotion(playerTo, true, item1.getDefaultInstance(), item2.getDefaultInstance(), item3.getDefaultInstance(), item4.getDefaultInstance(), item5.getDefaultInstance());
																			playerTo.addItem(resultPotion);
																			return 0;
																				}
																		)
																)
																)
														)
												)
										)
								)
						)
				)
				//法术
				.then(Commands.literal("spell")
						.then(Commands.literal("use")
								.then(Commands.argument("number", DoubleArgumentType.doubleArg(1))
										.then(Commands.argument("caster", EntityArgument.entity())
												.then(Commands.argument("entity", EntityArgument.entity())
														.then(Commands.argument("string", StringArgumentType.word())
																.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS)
																.executes(arguments -> {
																			Entity caster = EntityArgument.getEntity(arguments, "caster");
																			int n = (int) DoubleArgumentType.getDouble(arguments, "number");
																			Entity entity = EntityArgument.getEntity(arguments, "entity");
																			if (caster instanceof LivingEntity living)
																				SpellListByString.getSpell(n, living, entity, SpellRegistry.getSpellTypeById(StringArgumentType.getString(arguments, "string"))).spellUse();
																			return 0;
																		}
																)
														)
												)
										)
								)
						)
						.then(Commands.literal("set_target")
								.then(Commands.literal("main")
										.then(Commands.argument("player", EntityArgument.player())
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS)
														.then(Commands.argument("number", DoubleArgumentType.doubleArg(1)).executes(arguments -> {
																			Player playerTo = EntityArgument.getPlayer(arguments, "player");

																			if (!StringArgumentType.getString(arguments, "string").isEmpty()) {
																				int n = Math.max(SpellListByString.getSpell((int)DoubleArgumentType.getDouble(arguments, "number"), null, null, SpellRegistry.getSpellTypeById(StringArgumentType.getString(arguments, "string"))).baseSpellLevel(), (int)DoubleArgumentType.getDouble(arguments, "number"));
																				playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																					capability.setMainSpellTarget(StringArgumentType.getString(arguments, "string"));
																					capability.setMainSpellTargetLevel(Math.max(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget)).baseSpellLevel(), (int)DoubleArgumentType.getDouble(arguments, "number")));
																				});
																				playerTo.sendSystemMessage(
																						Component.translatable("message.jerotes.target_magic")
																								.append(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget)).getSpellName())
																								.append(Component.translatable("spell.jerotes.spell_base", playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel))
																								.withStyle(ChatFormatting.DARK_PURPLE));
																			}
																			else {
																				playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																					capability.setMainSpellTarget("");
																					capability.setMainSpellTargetLevel(1);
																				});
																			}
																	if (!playerTo.level().isClientSide()) {
																		(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
																	}
																			return 0;
																		}
																)
														)
												)
										)
								)
								.then(Commands.literal("add")
										.then(Commands.argument("player", EntityArgument.player())
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS)
														.then(Commands.argument("number", DoubleArgumentType.doubleArg(1)).executes(arguments -> {
																			Player playerTo = EntityArgument.getPlayer(arguments, "player");

																			if (!StringArgumentType.getString(arguments, "string").isEmpty()) {
																				int n = Math.max(SpellListByString.getSpell((int)DoubleArgumentType.getDouble(arguments, "number"), null, null, SpellRegistry.getSpellTypeById(StringArgumentType.getString(arguments, "string"))).baseSpellLevel(), (int)DoubleArgumentType.getDouble(arguments, "number"));
																				playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																					capability.setAddSpellTarget(StringArgumentType.getString(arguments, "string"));
																					capability.setAddSpellTargetLevel(Math.max(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget)).baseSpellLevel(), (int)DoubleArgumentType.getDouble(arguments, "number")));
																				});
																				playerTo.sendSystemMessage(
																						Component.translatable("message.jerotes.target_magic")
																								.append(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget)).getSpellName())
																								.append(Component.translatable("spell.jerotes.spell_base", playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTargetLevel))
																								.withStyle(ChatFormatting.DARK_PURPLE));
																			}
																			else {
																				playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																					capability.setAddSpellTarget("");
																					capability.setAddSpellTargetLevel(1);
																				});
																			}
																	if (!playerTo.level().isClientSide()) {
																		(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
																	}
																			return 0;
																		}
																)
														)
												)
										)
								)
						)
						.then(Commands.literal("get_target")
								.then(Commands.literal("main")
										.then(Commands.argument("player", EntityArgument.player()).executes(arguments -> {
															Player playerTo = EntityArgument.getPlayer(arguments, "player");

													playerTo.sendSystemMessage(
															Component.translatable("message.jerotes.target_magic")
																	.append(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget)).getSpellName())
																	.append(Component.translatable("spell.jerotes.spell_base", playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel))
																	.withStyle(ChatFormatting.DARK_PURPLE));
													if (!playerTo.level().isClientSide()) {
														(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
													}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("add")
										.then(Commands.argument("player", EntityArgument.player())
												.executes(arguments -> {
															Player playerTo = EntityArgument.getPlayer(arguments, "player");

															playerTo.sendSystemMessage(
																	Component.translatable("message.jerotes.target_magic")
																			.append(SpellListByString.getSpellEasy(SpellRegistry.getSpellTypeById(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget)).getSpellName())
																			.append(Component.translatable("spell.jerotes.spell_base", playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTargetLevel))
																			.withStyle(ChatFormatting.DARK_PURPLE));
															if (!playerTo.level().isClientSide()) {
																(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
															}
															return 0;
														}
												)
										)
								)
						)
				)
				//修改
				.then(Commands.literal("player_change")
						.then(Commands.argument("player", EntityArgument.player())
								.then(Commands.literal("legend")
										.then(Commands.literal("true")
												.executes(arguments -> {
															Player playerTo = EntityArgument.getPlayer(arguments, "player");
															playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																capability.setLegend(true);
															});
															if (!playerTo.level().isClientSide()) {
																(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
															}
															playerTo.sendSystemMessage(
																	Component.translatable("message.jerotes.legend_true").withStyle(ChatFormatting.GOLD));
															return 0;
														}
												)
										)
										.then(Commands.literal("false")
												.executes(arguments -> {
															Player playerTo = EntityArgument.getPlayer(arguments, "player");
															playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																capability.setLegend(false);
															});
															if (!playerTo.level().isClientSide()) {
																(playerTo.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(playerTo);
															}
															playerTo.sendSystemMessage(
																	Component.translatable("message.jerotes.legend_false")
																			.withStyle(ChatFormatting.GOLD));
															return 0;
														}
												)
										)
								)
						)
				)
				.then(Commands.literal("entity_change")
						.then(Commands.argument("entities", EntityArgument.entities())
								.then(Commands.literal("anesthetized")
										.then(Commands.argument("number", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
															Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
															double d = DoubleArgumentType.getDouble(arguments, "number");
															for (Entity entity : entities) {
																Main.getJerotesPersistentData(entity).putDouble("jerotes_anesthetized", d);
															}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("main_spell_cooldown")
										.then(Commands.argument("number", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
															Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
															double d = DoubleArgumentType.getDouble(arguments, "number");
															for (Entity entity : entities) {
																entity.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																		capability.setMainSpellUseCoolDownTick((int) d);
																	});
																	if (!entity.level().isClientSide()) {
																		(entity.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(entity);
																	}
															}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("add_spell_cooldown")
										.then(Commands.argument("number", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
															Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
															double d = DoubleArgumentType.getDouble(arguments, "number");
															for (Entity entity : entities) {
																entity.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
																		capability.setAddSpellUseCoolDownTick((int) d);
																	});
																	if (!entity.level().isClientSide()) {
																		(entity.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(entity);
																	}
															}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("mob_faction")
										.then(Commands.argument("string", StringArgumentType.word()).executes(arguments -> {
															Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
															String string = StringArgumentType.getString(arguments, "string");
															for (Entity entity : entities) {
																Main.getJerotesPersistentData(entity).putString("jerotes_mob_faction", string);
															}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("mob_faction_mod_id")
										.then(Commands.argument("string", StringArgumentType.word()).executes(arguments -> {
															Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
															String string = StringArgumentType.getString(arguments, "string");
															for (Entity entity : entities) {
																Main.getJerotesPersistentData(entity).putString("jerotes_mob_faction_mod_id", string);
															}
															return 0;
														}
												)
										)
								)
								.then(Commands.literal("main_spell_list")
										.then(Commands.literal("add")
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS).executes(arguments -> {
																	Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
																	String string = StringArgumentType.getString(arguments, "string");
																	for (Entity entity : entities) {
																		CompoundTag data = Main.getJerotesPersistentData(entity);
																		ListTag spellList = data.getList("jerotes_main_spell_string_list", Tag.TAG_STRING);
																		spellList.add(StringTag.valueOf(string));
																		data.put("jerotes_main_spell_string_list", spellList);
																	}
																	return 0;
																}
														)
												)
										)
										.then(Commands.literal("remove")
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS).executes(arguments -> {
																	Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
																	String string = StringArgumentType.getString(arguments, "string");
																	for (Entity entity : entities) {
																		CompoundTag data = Main.getJerotesPersistentData(entity);
																		ListTag spellList = data.getList("jerotes_main_spell_string_list", Tag.TAG_STRING);
																		spellList.remove(StringTag.valueOf(string));
																		data.put("jerotes_main_spell_string_list", spellList);
																	}
																	return 0;
																}
														)
												)
										)
								)
								.then(Commands.literal("add_spell_list")
										.then(Commands.literal("add")
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS).executes(arguments -> {
																	Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
																	String string = StringArgumentType.getString(arguments, "string");
																	for (Entity entity : entities) {
																		CompoundTag data = Main.getJerotesPersistentData(entity);
																		ListTag spellList = data.getList("jerotes_add_spell_string_list", Tag.TAG_STRING);
																		spellList.add(StringTag.valueOf(string));
																		data.put("jerotes_add_spell_string_list", spellList);
																	}
																	return 0;
																}
														)
												)
										)
										.then(Commands.literal("remove")
												.then(Commands.argument("string", StringArgumentType.word())
														.suggests(SpellArgumentType.SPELL_ID_SUGGESTIONS).executes(arguments -> {
																	Collection<? extends Entity> entities = EntityArgument.getEntities(arguments, "entities");
																	String string = StringArgumentType.getString(arguments, "string");
																	for (Entity entity : entities) {
																		CompoundTag data = Main.getJerotesPersistentData(entity);
																		ListTag spellList = data.getList("jerotes_add_spell_string_list", Tag.TAG_STRING);
																		spellList.remove(StringTag.valueOf(string));
																		data.put("jerotes_add_spell_string_list", spellList);
																	}
																	return 0;
																}
														)
												)
										)
								)
						)
				)
				//其他
				.then(Commands.literal("other")
						.then(Commands.literal("inventory")
								.then(Commands.argument("entity", EntityArgument.entity())
										.then(Commands.argument("player", EntityArgument.player()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "entity");
															Player playerTo = EntityArgument.getPlayer(arguments, "player");
															if (entity instanceof LivingEntity livingEntity && playerTo instanceof ServerPlayer serverPlayer) {
																Main.openMobInventoryGui(serverPlayer, livingEntity, true, true, true, true, true, true, true, true);
															}
															return 0;
														}
												)
										)
								)
						)
						.then(Commands.literal("horse_inventory")
								.then(Commands.argument("entity", EntityArgument.entity())
										.then(Commands.argument("player", EntityArgument.player()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "entity");
															Player playerTo = EntityArgument.getPlayer(arguments, "player");
															if (entity instanceof JerotesChangeAbstractHorse livingEntity && playerTo instanceof ServerPlayer serverPlayer && (entity.getType() == EntityType.HORSE || entity.getType() == EntityType.DONKEY || entity.getType() == EntityType.MULE || entity.getType() == EntityType.CAMEL || entity.getType() == EntityType.LLAMA || entity.getType() == EntityType.TRADER_LLAMA)) {
																livingEntity.openHorseInventoryJerotes(serverPlayer);
															}
															else if (entity instanceof LivingEntity livingEntity && playerTo instanceof ServerPlayer serverPlayer) {
																Main.openSuchInventoryGui(serverPlayer, livingEntity);
															}
															return 0;
														}
												)
										)
								)
						)
						.then(Commands.literal("inventory_only_see")
								.then(Commands.argument("entity", EntityArgument.entity())
										.then(Commands.argument("player", EntityArgument.player()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "entity");
															Player playerTo = EntityArgument.getPlayer(arguments, "player");
															if (entity instanceof LivingEntity livingEntity && playerTo instanceof ServerPlayer serverPlayer) {
																Main.openMobInventoryGui(serverPlayer, livingEntity, false, false, false, false, false, false, false, false);
															}
															return 0;
														}
												)
										)
								)
						)
						.then(Commands.literal("faction")
								.then(Commands.argument("entity", EntityArgument.entity())
										.then(Commands.argument("entity2", EntityArgument.entity()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "entity");
															Entity entity2 = EntityArgument.getEntity(arguments, "entity2");
															Entity entitys = arguments.getSource().getEntity();
															if (entitys instanceof Player player && entity instanceof LivingEntity living && entity2 instanceof LivingEntity living2) {
																if (EntityFactionFind.isFaction(living,living2)) {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_faction_yes").withStyle(ChatFormatting.WHITE));
																}
																else {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_faction_no").withStyle(ChatFormatting.WHITE));
																}														}
															return 0;
														}
												)
										)
								)
						)
						.then(Commands.literal("findcannotattack")
								.then(Commands.argument("entity", EntityArgument.entity())
										.then(Commands.argument("entity2", EntityArgument.entity()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "entity");
															Entity entity2 = EntityArgument.getEntity(arguments, "entity2");
															Entity entitys = arguments.getSource().getEntity();
															if (entitys instanceof Player player && entity instanceof LivingEntity living && entity2 instanceof LivingEntity living2) {
																if (AttackFind.FindCanNotAttack(living,living2)) {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_find_can_not_attack_yes").withStyle(ChatFormatting.WHITE));
																}
																else {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_find_can_not_attack_no").withStyle(ChatFormatting.WHITE));
																}														}
															return 0;
														}
												)
										)
								)
						)
						.then(Commands.literal("avoiddamage")
								.then(Commands.argument("attacker", EntityArgument.entity())
										.then(Commands.argument("hurt", EntityArgument.entity()).executes(arguments -> {
															Entity entity = EntityArgument.getEntity(arguments, "attacker");
															Entity entity2 = EntityArgument.getEntity(arguments, "hurt");
															Entity entitys = arguments.getSource().getEntity();
															if (entitys instanceof Player player && entity instanceof LivingEntity living && entity2 instanceof LivingEntity living2) {
																if (AttackFind.SameFactionAvoidDamage(living,living2)) {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_avoid_damage_yes").withStyle(ChatFormatting.WHITE));
																}
																else {
																	player.sendSystemMessage(Component.translatable("message.jerotes.is_avoid_damage_no").withStyle(ChatFormatting.WHITE));
																}														}
															return 0;
														}
												)
										)
								)
						)
				)
				//角色
				.then(Commands.literal("character")
						.then(Commands.literal("NetheriteWarrior").executes(arguments ->
										{
											JerotesPlayerEntity player = JerotesEntityType.JEROTES_PLAYER.get().spawn(arguments.getSource().getLevel(), BlockPos.containing(arguments.getSource().getPosition().x,arguments.getSource().getPosition().y, arguments.getSource().getPosition().z), MobSpawnType.MOB_SUMMONED);
											if (player != null) {
												player.setLookLevel(10);
												player.setBowLevel(20);
												player.setShieldLevel(3);
												player.setChangeInventoryCooldownTick(5);
												Objects.requireNonNull(player.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(128);
												player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.AIR));

												//剑
												ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
												sword.enchant(Enchantments.SMITE, 5);
												sword.enchant(Enchantments.KNOCKBACK, 2);
												sword.enchant(Enchantments.MOB_LOOTING, 3);
												sword.enchant(Enchantments.SWEEPING_EDGE, 3);
												sword.enchant(Enchantments.UNBREAKING, 3);
												//弓
												ItemStack bow = new ItemStack(Items.BOW);
												bow.enchant(Enchantments.POWER_ARROWS, 5);
												bow.enchant(Enchantments.INFINITY_ARROWS, 1);
												bow.enchant(Enchantments.PUNCH_ARROWS, 2);
												bow.enchant(Enchantments.UNBREAKING, 3);
												//盔甲
												ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
												ItemStack chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
												ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
												ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
												helmet.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												chestplate.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												leggings.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												boots.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												helmet.enchant(Enchantments.UNBREAKING, 3);
												helmet.enchant(Enchantments.THORNS, 3);
												chestplate.enchant(Enchantments.UNBREAKING, 3);
												chestplate.enchant(Enchantments.THORNS, 3);
												leggings.enchant(Enchantments.UNBREAKING, 3);
												leggings.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.UNBREAKING, 3);
												boots.enchant(Enchantments.FALL_PROTECTION, 4);
												boots.enchant(Enchantments.THORNS, 3);
												//
												player.mobInventory().addItem(sword);
												player.mobInventory().addItem(helmet);
												player.mobInventory().addItem(chestplate);
												player.mobInventory().addItem(leggings);
												player.mobInventory().addItem(boots);
												player.mobInventory().addItem(bow);
												ItemStack arrow = new ItemStack(Items.SPECTRAL_ARROW, 64);
												player.mobInventory().addItem(arrow);
												ItemStack shield = new ItemStack(Items.SHIELD);
												shield.enchant(Enchantments.UNBREAKING, 3);
												player.mobInventory().addItem(shield);
												player.mobInventory().addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 64));
											}
											return 0;
										}
								)
						)
						.then(Commands.literal("NetheriteKnight").executes(arguments ->
										{
											JerotesPlayerEntity player = JerotesEntityType.JEROTES_PLAYER.get().spawn(arguments.getSource().getLevel(), BlockPos.containing(arguments.getSource().getPosition().x,arguments.getSource().getPosition().y, arguments.getSource().getPosition().z), MobSpawnType.MOB_SUMMONED);
											if (player != null) {
												player.setLookLevel(10);
												player.setBowLevel(20);
												player.setShieldLevel(3);
												player.setChangeInventoryCooldownTick(5);
												Objects.requireNonNull(player.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(128);
												player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.AIR));

												//矛
												ItemStack spear = new ItemStack(JerotesItems.NETHERITE_SPEAR.get());
												spear.enchant(Enchantments.SMITE, 5);
												spear.enchant(Enchantments.KNOCKBACK, 2);
												spear.enchant(Enchantments.MOB_LOOTING, 3);
												spear.enchant(JerotesEnchantments.LUNGE.get(), 3);
												spear.enchant(Enchantments.UNBREAKING, 3);
												//弓
												ItemStack bow = new ItemStack(Items.BOW);
												bow.enchant(Enchantments.POWER_ARROWS, 5);
												bow.enchant(Enchantments.INFINITY_ARROWS, 1);
												bow.enchant(Enchantments.PUNCH_ARROWS, 2);
												bow.enchant(Enchantments.UNBREAKING, 3);
												//盔甲
												ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
												ItemStack chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
												ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
												ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
												helmet.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												chestplate.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												leggings.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												boots.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												helmet.enchant(Enchantments.UNBREAKING, 3);
												helmet.enchant(Enchantments.THORNS, 3);
												chestplate.enchant(Enchantments.UNBREAKING, 3);
												chestplate.enchant(Enchantments.THORNS, 3);
												leggings.enchant(Enchantments.UNBREAKING, 3);
												leggings.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.UNBREAKING, 3);
												boots.enchant(Enchantments.FALL_PROTECTION, 4);
												boots.enchant(Enchantments.THORNS, 3);
												//
												player.mobInventory().addItem(spear);
												player.mobInventory().addItem(helmet);
												player.mobInventory().addItem(chestplate);
												player.mobInventory().addItem(leggings);
												player.mobInventory().addItem(boots);
												player.mobInventory().addItem(bow);
												ItemStack arrow = new ItemStack(Items.SPECTRAL_ARROW, 64);
												player.mobInventory().addItem(arrow);
												ItemStack shield = new ItemStack(Items.SHIELD);
												shield.enchant(Enchantments.UNBREAKING, 3);
												player.mobInventory().addItem(shield);
												player.mobInventory().addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 64));

												JerotesHorseEntity jerotesHorseEntity = 
														JerotesEntityType.JEROTES_HORSE.get().
																spawn(arguments.getSource().getLevel(), BlockPos.containing(player.getX(), player.getY(), player.getZ()), MobSpawnType.MOB_SUMMONED);
												PlayerTeam teams = (PlayerTeam) player.getTeam();
												if (jerotesHorseEntity != null) {
													jerotesHorseEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
													jerotesHorseEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3375);
													jerotesHorseEntity.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(1.0);
													jerotesHorseEntity.setBaby(false);
													jerotesHorseEntity.tameLivingEntity(player);
													if (teams != null) {
														arguments.getSource().getLevel().getScoreboard().addPlayerToTeam(jerotesHorseEntity.getStringUUID(), teams);
													}
													jerotesHorseEntity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0f);
													//鞍
													jerotesHorseEntity.equipSaddle(SoundSource.NEUTRAL);
													//盔甲
													jerotesHorseEntity.equipArmor(null, new ItemStack(JerotesItems.NETHERITE_WAR_BEAST_ARMOR.get()));
													if (!player.level().isClientSide()) {
														jerotesHorseEntity.setBaby(false);
														player.startRiding(jerotesHorseEntity);
													}
												}
											}
											return 0;
										}
								)
						)
						.then(Commands.literal("Archmage").executes(arguments ->
										{
											JerotesPlayerEntity player = JerotesEntityType.JEROTES_PLAYER.get().spawn(arguments.getSource().getLevel(), BlockPos.containing(arguments.getSource().getPosition().x,arguments.getSource().getPosition().y, arguments.getSource().getPosition().z), MobSpawnType.MOB_SUMMONED);
											if (player != null) {
												player.setLookLevel(10);
												player.setBowLevel(20);
												player.setShieldLevel(3);
												player.setCanPickUpLoot(false);
												player.setChangeInventoryCooldownTick(5);
												player.setCombatStyle(4);
												player.setArchmage(true);
												Objects.requireNonNull(player.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(128);
												player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.AIR));
												player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.AIR));

												//盔甲
												ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
												ItemStack chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
												ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
												ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
												helmet.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												chestplate.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												leggings.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												boots.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
												helmet.enchant(Enchantments.UNBREAKING, 3);
												helmet.enchant(Enchantments.THORNS, 3);
												chestplate.enchant(Enchantments.UNBREAKING, 3);
												chestplate.enchant(Enchantments.THORNS, 3);
												leggings.enchant(Enchantments.UNBREAKING, 3);
												leggings.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.THORNS, 3);
												boots.enchant(Enchantments.UNBREAKING, 3);
												boots.enchant(Enchantments.FALL_PROTECTION, 4);
												boots.enchant(Enchantments.THORNS, 3);
												//
												player.mobInventory().addItem(helmet);
												player.mobInventory().addItem(chestplate);
												player.mobInventory().addItem(leggings);
												player.mobInventory().addItem(boots);
												player.mobInventory().addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 64));
											}
											return 0;
										}
								)
						)
				)
		);
	}

	@SubscribeEvent
	public static void registerCommand0(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("jerotes").requires(s -> s.hasPermission(0)));
	}
}