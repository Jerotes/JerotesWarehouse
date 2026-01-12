package com.jerotes.jerotes.alchemy.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.alchemy.effect.*;
import com.jerotes.jerotes.alchemy.forge.JerotesAlchemyMaterialEffectEvent;
import com.jerotes.jerotes.alchemy.forge.JerotesAlchemyTooltipEvent;
import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class MaterialEvent {
	@SubscribeEvent
	public static void Material(ItemTooltipEvent event) {
		ItemStack itemStack = event.getItemStack();
		JerotesAlchemyTooltipEvent eventss = new JerotesAlchemyTooltipEvent(event.getEntity());
		MinecraftForge.EVENT_BUS.post(eventss);
		if (!eventss.isShow()) {
			return;
		}
		JerotesAlchemyMaterialEffectEvent events = new JerotesAlchemyMaterialEffectEvent(itemStack);
		MinecraftForge.EVENT_BUS.post(events);

		if (events.getEffect1() != null && !(events.getEffect1() instanceof AAANullAlchemyEffect) ||
				events.getEffect2() != null && !(events.getEffect2() instanceof AAANullAlchemyEffect) ||
				events.getEffect3() != null && !(events.getEffect3() instanceof AAANullAlchemyEffect) ||
				events.getEffect4() != null && !(events.getEffect4() instanceof AAANullAlchemyEffect) ||
				events.getEffect5() != null && !(events.getEffect5() instanceof AAANullAlchemyEffect)) {
			event.getToolTip().add(Component.translatable("alchemy.jerotes.effect").withStyle(ChatFormatting.GREEN));
		}
		if (events.getEffect1() != null && !(events.getEffect1() instanceof AAANullAlchemyEffect)) {
			AAAAlchemyEffect alchemyEffect = events.getEffect1();
			event.getToolTip().add(alchemyEffect.getTooltip().copy().withStyle(ChatFormatting.GREEN));
		}
		if (events.getEffect2() != null && !(events.getEffect2() instanceof AAANullAlchemyEffect)) {
			AAAAlchemyEffect alchemyEffect = events.getEffect2();
			event.getToolTip().add(alchemyEffect.getTooltip().copy().withStyle(ChatFormatting.GREEN));
		}
		if (events.getEffect3() != null && !(events.getEffect3() instanceof AAANullAlchemyEffect)) {
			AAAAlchemyEffect alchemyEffect = events.getEffect3();
			event.getToolTip().add(alchemyEffect.getTooltip().copy().withStyle(ChatFormatting.GREEN));
		}
		if (events.getEffect4() != null && !(events.getEffect4() instanceof AAANullAlchemyEffect)) {
			AAAAlchemyEffect alchemyEffect = events.getEffect4();
			event.getToolTip().add(alchemyEffect.getTooltip().copy().withStyle(ChatFormatting.GREEN));
		}
		if (events.getEffect5() != null && !(events.getEffect5() instanceof AAANullAlchemyEffect)) {
			AAAAlchemyEffect alchemyEffect = events.getEffect5();
			event.getToolTip().add(alchemyEffect.getTooltip().copy().withStyle(ChatFormatting.GREEN));
		}
	}
	@SubscribeEvent
	public static void addWeaponEffect(JerotesAlchemyTooltipEvent event) {
		Player player = event.getPlayer();
		if (player == null)
			return;
		if (player.hasEffect(JerotesMobEffects.ANALYTICAL_EYE.get())) {
			event.setShow(true);
		}
	}

	@SubscribeEvent
	public static void Material(JerotesAlchemyMaterialEffectEvent event) {
		ItemStack itemStack = event.getMaterial();
		if (itemStack == null)
			return;
		if (itemStack.is(Items.AIR)) {
			event.setEffectCount(99999);
			event.setMaxCount(99999);
		}
		//毒马铃薯
		if (itemStack.is(Items.POISONOUS_POTATO)) {
			event.setEffectCount(3);
			event.setEffect1(new PoisonAlchemyEffect(2, 1));
			event.setEffect2(new SlownessAlchemyEffect(1, 1));
			event.setEffect3(new ResistanceAlchemyEffect(1, 1));
		}
		//曲奇
		if (itemStack.is(Items.COOKIE)) {
			event.setEffectCount(3);
			event.setEffect1(new PoisonAlchemyEffect(1, 2));
			event.setEffect2(new AnesthetizedAlchemyEffect(1, 2));
			event.setEffect3(new LuckAlchemyEffect(1, 2));
		}
		//骨粉
		if (itemStack.is(Items.BONE_MEAL)) {
			event.setEffectCount(3);
			event.setEffect1(new MiningFatigueAlchemyEffect(1, 1));
			event.setEffect2(new RegenerationAlchemyEffect(2, 1));
			event.setEffect3(new StrengthAlchemyEffect(1, 1));
		}
		//苹果
		if (itemStack.is(Items.APPLE)) {
			event.setEffectCount(3);
			event.setEffect1(new LuckAlchemyEffect(1, 1));
			event.setEffect2(new RegenerationAlchemyEffect(1, 2));
			event.setEffect3(new SpeedAlchemyEffect(1, 1));
		}
		//金苹果
		if (itemStack.is(Items.GOLDEN_APPLE)) {
			event.setEffectCount(4);
			event.setEffect1(new LuckAlchemyEffect(1, 1));
			event.setEffect2(new RegenerationAlchemyEffect(1, 2));
			event.setEffect3(new SpeedAlchemyEffect(2, 1));
			event.setEffect4(new ResistanceAlchemyEffect(2, 1));
		}
		//附魔金苹果
		if (itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
			event.setEffectCount(4);
			event.setEffect1(new LuckAlchemyEffect(1, 1));
			event.setEffect2(new RegenerationAlchemyEffect(1, 2));
			event.setEffect3(new SpeedAlchemyEffect(2, 1));
			event.setEffect4(new ResistanceAlchemyEffect(3, 2));
		}
		//火药
		if (itemStack.is(Items.GUNPOWDER)) {
			event.setEffectCount(3);
			event.setEffect1(new ExplosionAlchemyEffect(1, 1));
			event.setEffect2(new FireAbsorptionAlchemyEffect(1, 1));
			event.setEffect3(new JumpBoostAlchemyEffect(1, 1));
		}
		//烈焰粉
		if (itemStack.is(Items.BLAZE_POWDER)) {
			event.setEffectCount(3);
			event.setEffect1(new ExplosionAlchemyEffect(1, 1));
			event.setEffect2(new FireAbsorptionAlchemyEffect(2, 1));
			event.setEffect3(new FireResistanceAlchemyEffect(1, 1));
		}
		//烈焰棒
		if (itemStack.is(Items.BLAZE_ROD)) {
			event.setEffectCount(3);
			event.setEffect1(new ExplosionAlchemyEffect(2, 1));
			event.setEffect2(new FireAbsorptionAlchemyEffect(1, 2));
			event.setEffect3(new FireResistanceAlchemyEffect(1, 2));
		}
		//山羊角
		if (itemStack.is(Items.GOAT_HORN)) {
			event.setEffectCount(3);
			event.setEffect1(new NauseaAlchemyEffect(1, 2));
			event.setEffect2(new SpeedAlchemyEffect(1, 2));
			event.setEffect3(new JumpBoostAlchemyEffect(1, 2));
		}
		//兔子脚
		if (itemStack.is(Items.RABBIT_FOOT)) {
			event.setEffectCount(3);
			event.setEffect1(new LuckAlchemyEffect(2, 1));
			event.setEffect2(new UnluckAlchemyEffect(1, 2));
			event.setEffect3(new JumpBoostAlchemyEffect(2, 1));
		}
		//紫颂果
		if (itemStack.is(Items.CHORUS_FRUIT)) {
			event.setEffectCount(3);
			event.setEffect1(new AbsorptionAlchemyEffect(2, 1));
			event.setEffect2(new LevitationAlchemyEffect(1, 2));
			event.setEffect3(new AnesthetizedAlchemyEffect(1, 2));
		}
		//闪烁的西瓜片
		if (itemStack.is(Items.GLISTERING_MELON_SLICE)) {
			event.setEffectCount(3);
			event.setEffect1(new AbsorptionAlchemyEffect(1, 2));
			event.setEffect2(new LevitationAlchemyEffect(1, 2));
			event.setEffect3(new InstantHealthAlchemyEffect(2, 1));
		}
		//小麦
		if (itemStack.is(Items.WHEAT)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(1, 2));
			event.setEffect2(new FogAlchemyEffect(1, 1));
			event.setEffect3(new HungerAlchemyEffect(2, 1));
		}
		//皮革 兔子皮
		if (itemStack.is(Items.LEATHER) || itemStack.is(Items.RABBIT_HIDE)) {
			event.setEffectCount(3);
			event.setEffect1(new FreezeAbsorptionAlchemyEffect(1, 2));
			event.setEffect2(new WeaknessAlchemyEffect(2, 2));
			event.setEffect3(new NauseaAlchemyEffect(1, 1));
		}
		//黏土
		if (itemStack.is(Items.CLAY_BALL)) {
			event.setEffectCount(3);
			event.setEffect1(new AnesthetizedAlchemyEffect(1, 1));
			event.setEffect2(new MiningFatigueAlchemyEffect(2, 1));
			event.setEffect3(new PruritusAlchemyEffect(1, 2));
		}
		//黏液球
		if (itemStack.is(Items.SLIME_BALL)) {
			event.setEffectCount(3);
			event.setEffect1(new SlownessAlchemyEffect(1, 2));
			event.setEffect2(new CorrosiveAlchemyEffect(1, 1));
			event.setEffect3(new PruritusAlchemyEffect(2, 1));
		}
		//附魔之瓶
		if (itemStack.is(Items.EXPERIENCE_BOTTLE)) {
			event.setEffectCount(3);
			event.setEffect1(new UnluckAlchemyEffect(1, 2));
			event.setEffect2(new CorrosiveAlchemyEffect(2, 1));
			event.setEffect3(new ExplosionAlchemyEffect(1, 1));
		}
		//鸡蛋
		if (itemStack.is(Items.EGG)) {
			event.setEffectCount(3);
			event.setEffect1(new MagicAbsorptionAlchemyEffect(1, 1));
			event.setEffect2(new UnluckAlchemyEffect(1, 2));
			event.setEffect3(new JumpBoostAlchemyEffect(2, 1));
		}
		//雪球
		if (itemStack.is(Items.EGG)) {
			event.setEffectCount(3);
			event.setEffect1(new SlownessAlchemyEffect(2, 1));
			event.setEffect2(new FreezeAbsorptionAlchemyEffect(1, 1));
			event.setEffect3(new MiningFatigueAlchemyEffect(1, 2));
		}
		//甜浆果
		if (itemStack.is(Items.SWEET_BERRIES)) {
			event.setEffectCount(3);
			event.setEffect1(new PruritusAlchemyEffect(1, 1));
			event.setEffect2(new AnesthetizedAlchemyEffect(1, 1));
			event.setEffect3(new BleedingAlchemyEffect(1, 1));
		}
		//仙人掌
		if (itemStack.is(Items.CACTUS)) {
			event.setEffectCount(3);
			event.setEffect1(new AnesthetizedAlchemyEffect(1, 1));
			event.setEffect2(new SaturationAlchemyEffect(1, 1));
			event.setEffect3(new BleedingAlchemyEffect(2, 1));
		}
		//甜菜根
		if (itemStack.is(Items.BEETROOT)) {
			event.setEffectCount(3);
			event.setEffect1(new HasteAlchemyEffect(1, 1));
			event.setEffect2(new HealthBoostAlchemyEffect(1, 2));
			event.setEffect3(new MiningFatigueAlchemyEffect(1, 1));
		}
		//河豚
		if (itemStack.is(Items.PUFFERFISH)) {
			event.setEffectCount(3);
			event.setEffect1(new PoisonAlchemyEffect(2, 2));
			event.setEffect2(new DeadlyPoisonAlchemyEffect(2, 1));
			event.setEffect3(new InstantDamageAlchemyEffect(1, 1));
		}
		//鹦鹉螺壳
		if (itemStack.is(Items.NAUTILUS_SHELL)) {
			event.setEffectCount(3);
			event.setEffect1(new DolphinsGraceAlchemyEffect(1, 2));
			event.setEffect2(new WaterBreathingAlchemyEffect(1, 2));
			event.setEffect3(new InstantDamageAlchemyEffect(2, 1));
		}
		//海晶砂粒
		if (itemStack.is(Items.NAUTILUS_SHELL)) {
			event.setEffectCount(3);
			event.setEffect1(new DolphinsGraceAlchemyEffect(2, 1));
			event.setEffect2(new NightVisionAlchemyEffect(1, 2));
			event.setEffect3(new InvisiblePassageAlchemyEffect(1, 2));
		}
		//萤石粉
		if (itemStack.is(Items.GLOWSTONE_DUST)) {
			event.setEffectCount(3);
			event.setEffect1(new GlowingAlchemyEffect(1, 2));
			event.setEffect2(new NightVisionAlchemyEffect(1, 1));
			event.setEffect3(new SlowFallingAlchemyEffect(1, 1));
		}
		//红石
		if (itemStack.is(Items.REDSTONE)) {
			event.setEffectCount(3);
			event.setEffect1(new LightningAbsorptionAlchemyEffect(1, 2));
			event.setEffect2(new TruesightAlchemyEffect(1, 2));
			event.setEffect3(new PruritusAlchemyEffect(1, 1));
		}
		//鳞片
		if (itemStack.is(Items.SCUTE)) {
			event.setEffectCount(3);
			event.setEffect1(new UnluckAlchemyEffect(2, 1));
			event.setEffect2(new WaterBreathingAlchemyEffect(1, 3));
			event.setEffect3(new ResistanceAlchemyEffect(2, 1));
		}
		//鱼类
		if (itemStack.is(Items.COD) || itemStack.is(Items.SALMON) || itemStack.is(Items.TROPICAL_FISH)) {
			event.setEffectCount(3);
			event.setEffect1(new DolphinsGraceAlchemyEffect(1, 1));
			event.setEffect2(new WaterBreathingAlchemyEffect(1, 1));
			event.setEffect3(new HasteAlchemyEffect(1, 1));
		}
		//熟鱼类
		if (itemStack.is(Items.COOKED_COD) || itemStack.is(Items.COOKED_SALMON)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(1, 1));
			event.setEffect2(new WaterBreathingAlchemyEffect(1, 2));
			event.setEffect3(new HasteAlchemyEffect(1, 2));
		}
		//肉类
		if (itemStack.is(Items.BEEF) || itemStack.is(Items.PORKCHOP) || itemStack.is(Items.MUTTON) || itemStack.is(Items.CHICKEN) || itemStack.is(Items.RABBIT)) {
			event.setEffectCount(3);
			event.setEffect1(new SpeedAlchemyEffect(1, 1));
			event.setEffect2(new WeaknessAlchemyEffect(1, 1));
			event.setEffect3(new HungerAlchemyEffect(1, 1));
		}
		//熟肉类
		if (itemStack.is(Items.COOKED_BEEF) || itemStack.is(Items.COOKED_PORKCHOP) || itemStack.is(Items.COOKED_MUTTON) || itemStack.is(Items.COOKED_CHICKEN) || itemStack.is(Items.COOKED_RABBIT)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(1, 1));
			event.setEffect2(new SpeedAlchemyEffect(1, 2));
			event.setEffect3(new WeaknessAlchemyEffect(1, 2));
		}
		//奶桶
		if (itemStack.is(Items.MILK_BUCKET)) {
			event.setEffectCount(3);
			event.setEffect1(new AbsorptionAlchemyEffect(1, 1));
			event.setEffect2(new LevitationAlchemyEffect(2, 1));
			event.setEffect3(new InstantHealthAlchemyEffect(2, 1));
		}
		//下界之星
		if (itemStack.is(Items.NETHER_STAR)) {
			event.setEffectCount(3);
			event.setEffect1(new WitherAlchemyEffect(2, 2));
			event.setEffect2(new GlowingAlchemyEffect(1, 1));
			event.setEffect3(new NightVisionAlchemyEffect(2, 1));
		}
		//回响碎片
		if (itemStack.is(Items.ECHO_SHARD)) {
			event.setEffectCount(3);
			event.setEffect1(new BlindnessAlchemyEffect(1, 2));
			event.setEffect2(new GlowingAlchemyEffect(1, 2));
			event.setEffect3(new InvisbilityAlchemyEffect(1, 1));
		}
		//凋灵玫瑰 凋灵骷髅头颅
		if (itemStack.is(Items.WITHER_ROSE) || itemStack.is(Items.WITHER_SKELETON_SKULL)) {
			event.setEffectCount(3);
			event.setEffect1(new WitherAlchemyEffect(1, 1));
			event.setEffect2(new DarknessAlchemyEffect(1, 1));
			event.setEffect3(new HungerAlchemyEffect(1, 2));
		}
		//潜影壳
		if (itemStack.is(Items.SHULKER_SHELL)) {
			event.setEffectCount(3);
			event.setEffect1(new LevitationAlchemyEffect(1, 1));
			event.setEffect2(new SlowFallingAlchemyEffect(1, 1));
			event.setEffect3(new InvisbilityAlchemyEffect(1, 1));
		}
		//火焰弹 岩浆膏
		if (itemStack.is(Items.FIRE_CHARGE) || itemStack.is(Items.MAGMA_CREAM)) {
			event.setEffectCount(3);
			event.setEffect1(new ExplosionAlchemyEffect(1, 1));
			event.setEffect2(new FireResistanceAlchemyEffect(1, 1));
			event.setEffect3(new FogAlchemyEffect(1, 2));
		}
		//原色玻璃类
		if (itemStack.is(Items.GLASS) || itemStack.is(Items.GLASS_PANE)) {
			event.setEffectCount(3);
			event.setEffect1(new InvisiblePassageAlchemyEffect(1, 1));
			event.setEffect2(new InvisbilityAlchemyEffect(1, 2));
			event.setEffect3(new GlowingAlchemyEffect(1, 1));
		}
		//恶魂之泪
		if (itemStack.is(Items.HONEY_BOTTLE)) {
			event.setEffectCount(3);
			event.setEffect1(new NauseaAlchemyEffect(1, 2));
			event.setEffect2(new InvisiblePassageAlchemyEffect(1, 2));
			event.setEffect3(new SlowFallingAlchemyEffect(1, 2));
		}
		//蜘蛛眼
		if (itemStack.is(Items.SPIDER_EYE)) {
			event.setEffectCount(3);
			event.setEffect1(new TruesightAlchemyEffect(1, 1));
			event.setEffect2(new DeadlyPoisonAlchemyEffect(1, 1));
			event.setEffect3(new BlindnessAlchemyEffect(1, 1));
		}
		//发酵蛛眼
		if (itemStack.is(Items.SPIDER_EYE)) {
			event.setEffectCount(3);
			event.setEffect1(new TruesightAlchemyEffect(1, 2));
			event.setEffect2(new DeadlyPoisonAlchemyEffect(2, 1));
			event.setEffect3(new BlindnessAlchemyEffect(1, 2));
		}
		//蜂蜜瓶
		if (itemStack.is(Items.HONEY_BOTTLE)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(1, 1));
			event.setEffect2(new RegenerationAlchemyEffect(2, 1));
			event.setEffect3(new HasteAlchemyEffect(2, 1));
		}
		//金胡萝卜
		if (itemStack.is(Items.GOLDEN_CARROT)) {
			event.setEffectCount(3);
			event.setEffect1(new LightningAbsorptionAlchemyEffect(1, 1));
			event.setEffect2(new SaturationAlchemyEffect(2, 1));
			event.setEffect3(new SpeedAlchemyEffect(1, 2));
		}
		//金类
		if (itemStack.is(Items.RAW_GOLD) || itemStack.is(Items.GOLD_INGOT)) {
			event.setEffectCount(3);
			event.setEffect1(new InstantHealthAlchemyEffect(1, 2));
			event.setEffect2(new MagicAbsorptionAlchemyEffect(1, 2));
			event.setEffect3(new StrengthAlchemyEffect(1, 2));
		}
		if (itemStack.is(Items.GOLD_NUGGET)) {
			event.setEffectCount(3);
			event.setEffect1(new InstantHealthAlchemyEffect(1, 1));
			event.setEffect2(new MagicAbsorptionAlchemyEffect(1, 1));
			event.setEffect3(new StrengthAlchemyEffect(1, 1));
		}
		if (itemStack.is(Items.GOLD_BLOCK)) {
			event.setEffectCount(3);
			event.setEffect1(new InstantHealthAlchemyEffect(2, 2));
			event.setEffect2(new MagicAbsorptionAlchemyEffect(2, 2));
			event.setEffect3(new StrengthAlchemyEffect(2, 2));
		}
		//龙息
		if (itemStack.is(Items.DRAGON_BREATH)) {
			event.setEffectCount(3);
			event.setEffect1(new CorrosiveAlchemyEffect(1, 2));
			event.setEffect2(new InstantDamageAlchemyEffect(3, 1));
			event.setEffect3(new HealthBoostAlchemyEffect(3, 1));
		}
		//蒲公英
		if (itemStack.is(Items.DANDELION)) {
			event.setEffectCount(3);
			event.setEffect1(new NightVisionAlchemyEffect(2, 1));
			event.setEffect2(new GlowingAlchemyEffect(1, 1));
			event.setEffect3(new InvisbilityAlchemyEffect(1, 1));
		}
		//虞美人
		if (itemStack.is(Items.POPPY)) {
			event.setEffectCount(3);
			event.setEffect1(new NightVisionAlchemyEffect(2, 1));
			event.setEffect2(new UnluckAlchemyEffect(1, 1));
			event.setEffect3(new NauseaAlchemyEffect(1, 1));
		}
		//兰花
		if (itemStack.is(Items.BLUE_ORCHID)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(2, 1));
			event.setEffect2(new DeadlyPoisonAlchemyEffect(1, 1));
			event.setEffect3(new SlownessAlchemyEffect(1, 1));
		}
		//绒球葱
		if (itemStack.is(Items.ALLIUM)) {
			event.setEffectCount(3);
			event.setEffect1(new FireResistanceAlchemyEffect(2, 1));
			event.setEffect2(new FireAbsorptionAlchemyEffect(1, 1));
			event.setEffect3(new ResistanceAlchemyEffect(1, 1));
		}
		//蓝花美耳草
		if (itemStack.is(Items.AZURE_BLUET)) {
			event.setEffectCount(3);
			event.setEffect1(new BlindnessAlchemyEffect(2, 1));
			event.setEffect2(new DarknessAlchemyEffect(1, 1));
			event.setEffect3(new InstantDamageAlchemyEffect(1, 1));
		}
		//郁金香
		if (itemStack.is(Items.PINK_TULIP) || itemStack.is(Items.WHITE_TULIP) || itemStack.is(Items.RED_TULIP) || itemStack.is(Items.ORANGE_TULIP)) {
			event.setEffectCount(3);
			event.setEffect1(new WeaknessAlchemyEffect(2, 1));
			event.setEffect2(new MiningFatigueAlchemyEffect(1, 1));
			event.setEffect3(new HungerAlchemyEffect(1, 1));
		}
		//滨菊
		if (itemStack.is(Items.OXEYE_DAISY)) {
			event.setEffectCount(3);
			event.setEffect1(new RegenerationAlchemyEffect(2, 1));
			event.setEffect2(new LevitationAlchemyEffect(1, 1));
			event.setEffect3(new AbsorptionAlchemyEffect(1, 1));
		}
		//矢车菊
		if (itemStack.is(Items.CORNFLOWER)) {
			event.setEffectCount(3);
			event.setEffect1(new JumpBoostAlchemyEffect(2, 1));
			event.setEffect2(new SlowFallingAlchemyEffect(1, 1));
			event.setEffect3(new InvisbilityAlchemyEffect(1, 1));
		}
		//铃兰
		if (itemStack.is(Items.LILY_OF_THE_VALLEY)) {
			event.setEffectCount(3);
			event.setEffect1(new PoisonAlchemyEffect(2, 1));
			event.setEffect2(new RegenerationAlchemyEffect(1, 1));
			event.setEffect3(new SlownessAlchemyEffect(1, 1));
		}
		//火把花 瓶子草
		if (itemStack.is(Items.TORCHFLOWER) || itemStack.is(Items.PITCHER_PLANT)) {
			event.setEffectCount(3);
			event.setEffect1(new NauseaAlchemyEffect(1, 1));
			event.setEffect2(new PruritusAlchemyEffect(3, 1));
			event.setEffect3(new FireResistanceAlchemyEffect(1, 1));
		}
		//向日葵
		if (itemStack.is(Items.SUNFLOWER)) {
			event.setEffectCount(3);
			event.setEffect1(new RegenerationAlchemyEffect(1, 1));
			event.setEffect2(new HealthBoostAlchemyEffect(1, 1));
			event.setEffect3(new AbsorptionAlchemyEffect(1, 1));
		}
		//丁香
		if (itemStack.is(Items.LILAC)) {
			event.setEffectCount(3);
			event.setEffect1(new JumpBoostAlchemyEffect(1, 1));
			event.setEffect2(new LuckAlchemyEffect(1, 1));
			event.setEffect3(new FogAlchemyEffect(1, 1));
		}
		//玫瑰丛
		if (itemStack.is(Items.ROSE_BUSH)) {
			event.setEffectCount(3);
			event.setEffect1(new NightVisionAlchemyEffect(1, 1));
			event.setEffect2(new BleedingAlchemyEffect(1, 1));
			event.setEffect3(new SlownessAlchemyEffect(1, 1));
		}
		//牡丹
		if (itemStack.is(Items.PEONY)) {
			event.setEffectCount(3);
			event.setEffect1(new GlowingAlchemyEffect(1, 1));
			event.setEffect2(new HasteAlchemyEffect(1, 1));
			event.setEffect3(new SlownessAlchemyEffect(1, 1));
		}
		//红蘑菇
		if (itemStack.is(Items.RED_MUSHROOM)) {
			event.setEffectCount(3);
			event.setEffect1(new HungerAlchemyEffect(1, 1));
			event.setEffect2(new NauseaAlchemyEffect(1, 2));
			event.setEffect3(new InstantDamageAlchemyEffect(2, 1));
		}
		//棕蘑菇
		if (itemStack.is(Items.BROWN_MUSHROOM)) {
			event.setEffectCount(3);
			event.setEffect1(new SaturationAlchemyEffect(1, 1));
			event.setEffect2(new InvisiblePassageAlchemyEffect(1, 2));
			event.setEffect3(new InstantHealthAlchemyEffect(2, 1));
		}
		//诡异菌
		if (itemStack.is(Items.WARPED_FUNGUS)) {
			event.setEffectCount(3);
			event.setEffect1(new WeaknessAlchemyEffect(2, 1));
			event.setEffect2(new ResistanceAlchemyEffect(1, 1));
			event.setEffect3(new DarknessAlchemyEffect(1, 1));
		}
		//绯红菌
		if (itemStack.is(Items.CRIMSON_FUNGUS)) {
			event.setEffectCount(3);
			event.setEffect1(new FireResistanceAlchemyEffect(1, 1));
			event.setEffect2(new StrengthAlchemyEffect(1, 1));
			event.setEffect3(new DeadlyPoisonAlchemyEffect(2, 1));
		}
		//下界疣
		if (itemStack.is(Items.NETHER_WART)) {
			event.setEffectCount(3);
			event.setEffect1(new HungerAlchemyEffect(2, 1));
			event.setEffect2(new StrengthAlchemyEffect(1, 2));
			event.setEffect3(new RegenerationAlchemyEffect(2, 1));
		}
		//灵魂沙 灵魂土
		if (itemStack.is(Items.SOUL_SAND) || itemStack.is(Items.SOUL_SOIL)) {
			event.setEffectCount(3);
			event.setEffect1(new DarknessAlchemyEffect(1, 2));
			event.setEffect2(new WitherAlchemyEffect(1, 1));
			event.setEffect3(new FogAlchemyEffect(1, 2));
		}
		//冰
		if (itemStack.is(Items.ICE) || itemStack.is(Items.PACKED_ICE) || itemStack.is(Items.BLUE_ICE)) {
			event.setEffectCount(3);
			event.setEffect1(new SlownessAlchemyEffect(2, 1));
			event.setEffect2(new FreezeAbsorptionAlchemyEffect(2, 1));
			event.setEffect3(new MiningFatigueAlchemyEffect(2, 1));
		}
	}
}