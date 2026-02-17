package com.jerotes.jerotes.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MainConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    public static boolean TamedMobHurtByOwnerHasReduce;
    public static boolean TamedMobAttackCreeperAndGhast;
    public static boolean AffectsNonThisModEntities;

    public static int CorrosiveStopDurabilityValue;
    public static boolean ScreenShake;
    public static boolean ShieldCanBreakBaseInGameRule;
    public static boolean ArmorCanBreakBaseInGameRule;
    public static boolean MeleeCanBreakBaseInGameRule;
    public static boolean RangeCanBreakBaseInGameRule;
    public static boolean MagicCanBreakBaseInGameRule;
    public static boolean SameFactionAvoidDamage;
    public static boolean MobDestroyBlock;
    public static boolean TamedMobDestroyBlock;
    public static boolean MobSayQuestionMark;
    public static boolean MobHasShockAback;
    public static boolean MobUseCrossbowShrinkArrow;
    public static boolean MobUseBowShrinkArrow;
    public static boolean MobUseThrowShrinkItem;
    public static boolean MobUseOtherShrinkItem;
    public static boolean InventoryMobAboutItemstackInventoryTick;
    public static boolean MobManuallyControlCombatCameraChange;
    public static boolean ZombieCanUseSpearAndPikeWithoutTag;
    public static boolean RandomSkinMobHasUnderTexture;
    public static List<String> HumanCustomNameWide;
    public static List<String> HumanCustomNameSlim;

    public static List<String> HasPercentageDamage;
    public static List<String> HasDamageCap;
    public static List<String> HasDamageCooldownTick;
    public static double BaseBreakHurtCooldownMultiple;
    public static double BaseAttackPercentage;
    public static double BaseMagicAttackPercentage;
    public static double BaseDamageCap;
    public static double BaseDamageCooldownTick;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
        final Pair<ClientConfig, ForgeConfigSpec> pair1 = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = pair1.getLeft();
        CLIENT_SPEC = pair1.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave()
                .writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

    public static void bakeCommonConfig() {
        TamedMobHurtByOwnerHasReduce = COMMON.TamedMobHurtByOwnerHasReduce.get();
        TamedMobAttackCreeperAndGhast = COMMON.TamedMobAttackCreeperAndGhast.get();
        AffectsNonThisModEntities = COMMON.AffectsNonThisModEntities.get();

        CorrosiveStopDurabilityValue = COMMON.CorrosiveStopDurabilityValue.get();
        ScreenShake = COMMON.ScreenShake.get();
        ShieldCanBreakBaseInGameRule = COMMON.ShieldCanBreakBaseInGameRule.get();
        ArmorCanBreakBaseInGameRule = COMMON.ArmorCanBreakBaseInGameRule.get();
        MeleeCanBreakBaseInGameRule = COMMON.MeleeCanBreakBaseInGameRule.get();
        RangeCanBreakBaseInGameRule = COMMON.RangeCanBreakBaseInGameRule.get();
        MagicCanBreakBaseInGameRule = COMMON.MagicCanBreakBaseInGameRule.get();
        SameFactionAvoidDamage = COMMON.SameFactionAvoidDamage.get();
        MobSayQuestionMark = COMMON.MobSayQuestionMark.get();
        MobDestroyBlock = COMMON.MobDestroyBlock.get();
        TamedMobDestroyBlock = COMMON.TamedMobDestroyBlock.get();
        MobHasShockAback = COMMON.MobHasShockAback.get();
        MobUseCrossbowShrinkArrow = COMMON.MobUseCrossbowShrinkArrow.get();
        MobUseBowShrinkArrow = COMMON.MobUseBowShrinkArrow.get();
        MobUseThrowShrinkItem = COMMON.MobUseThrowShrinkItem.get();
        MobUseOtherShrinkItem = COMMON.MobUseOtherShrinkItem.get();
        InventoryMobAboutItemstackInventoryTick = COMMON.InventoryMobAboutItemstackInventoryTick.get();
                MobManuallyControlCombatCameraChange = COMMON.MobManuallyControlCombatCameraChange.get();
        ZombieCanUseSpearAndPikeWithoutTag = COMMON.ZombieCanUseSpearAndPikeWithoutTag.get();
        RandomSkinMobHasUnderTexture = COMMON.RandomSkinMobHasUnderTexture.get();
        HumanCustomNameWide = COMMON.HumanCustomNameWide.get();
        HumanCustomNameSlim = COMMON.HumanCustomNameSlim.get();

        HasPercentageDamage = COMMON.HasPercentageDamage.get();
        HasDamageCap = COMMON.HasDamageCap.get();
        HasDamageCooldownTick = COMMON.HasDamageCooldownTick.get();
        BaseBreakHurtCooldownMultiple = COMMON.BaseBreakHurtCooldownMultiple.get();
        BaseAttackPercentage = COMMON.BaseAttackPercentage.get();
        BaseMagicAttackPercentage = COMMON.BaseMagicAttackPercentage.get();
        BaseDamageCap = COMMON.BaseDamageCap.get();
        BaseDamageCooldownTick = COMMON.BaseDamageCooldownTick.get();
    }

    public static void bakeClientConfig() {
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getSpec() == MainConfig.COMMON_SPEC) {
            bakeCommonConfig();
        } else if (configEvent.getConfig().getSpec() == MainConfig.CLIENT_SPEC) {
            bakeClientConfig();
        }
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.BooleanValue TamedMobHurtByOwnerHasReduce;
        public final ForgeConfigSpec.BooleanValue TamedMobAttackCreeperAndGhast;
        public final ForgeConfigSpec.BooleanValue AffectsNonThisModEntities;

        public final ForgeConfigSpec.IntValue CorrosiveStopDurabilityValue;
        public final ForgeConfigSpec.BooleanValue ScreenShake;
        public final ForgeConfigSpec.BooleanValue ShieldCanBreakBaseInGameRule;
        public final ForgeConfigSpec.BooleanValue ArmorCanBreakBaseInGameRule;
        public final ForgeConfigSpec.BooleanValue MeleeCanBreakBaseInGameRule;
        public final ForgeConfigSpec.BooleanValue RangeCanBreakBaseInGameRule;
        public final ForgeConfigSpec.BooleanValue MagicCanBreakBaseInGameRule;
        public final ForgeConfigSpec.BooleanValue SameFactionAvoidDamage;
        public final ForgeConfigSpec.BooleanValue MobSayQuestionMark;
        public final ForgeConfigSpec.BooleanValue MobDestroyBlock;
        public final ForgeConfigSpec.BooleanValue TamedMobDestroyBlock;
        public final ForgeConfigSpec.BooleanValue MobHasShockAback;
        public final ForgeConfigSpec.BooleanValue MobUseCrossbowShrinkArrow;
        public final ForgeConfigSpec.BooleanValue MobUseBowShrinkArrow;
        public final ForgeConfigSpec.BooleanValue MobUseThrowShrinkItem;
        public final ForgeConfigSpec.BooleanValue MobUseOtherShrinkItem;
        public final ForgeConfigSpec.BooleanValue InventoryMobAboutItemstackInventoryTick;
        public final ForgeConfigSpec.BooleanValue MobManuallyControlCombatCameraChange;
        public final ForgeConfigSpec.BooleanValue ZombieCanUseSpearAndPikeWithoutTag;
        public final ForgeConfigSpec.BooleanValue RandomSkinMobHasUnderTexture;
        public final ForgeConfigSpec.ConfigValue<List<String>> HumanCustomNameWide;
        public final ForgeConfigSpec.ConfigValue<List<String>> HumanCustomNameSlim;

        public final ForgeConfigSpec.ConfigValue<List<String>> HasPercentageDamage;
        public final ForgeConfigSpec.ConfigValue<List<String>> HasDamageCap;
        public final ForgeConfigSpec.ConfigValue<List<String>> HasDamageCooldownTick;
        public final ForgeConfigSpec.DoubleValue BaseBreakHurtCooldownMultiple;
        public final ForgeConfigSpec.DoubleValue BaseAttackPercentage;
        public final ForgeConfigSpec.DoubleValue BaseMagicAttackPercentage;
        public final ForgeConfigSpec.DoubleValue BaseDamageCap;
        public final ForgeConfigSpec.DoubleValue BaseDamageCooldownTick;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push(" ");
            builder.pop();

            builder.push("Tamed Mob");
            TamedMobHurtByOwnerHasReduce = builder.comment("Tame Mob Hurt By Owner Has Reduce")
                    .define("驯服生物对主人对自身造成的伤害拥有减伤", true);
            TamedMobAttackCreeperAndGhast = builder.comment("Tame Mob Attack Creeper And Ghast")
                    .define("模组添加的驯服生物攻击苦力怕[Creeper]和恶魂[Ghast]", false);
            AffectsNonThisModEntities = builder.comment("Affects Non This Mod Entities")
                    .define("影响非本模组实体", false);
            builder.pop();

            builder.push("Other");
            CorrosiveStopDurabilityValue = builder.comment("Corrosive Stop Durability Value")
                    .defineInRange("腐蚀中止耐久值", 8, 0, Integer.MAX_VALUE);
            ScreenShake = builder.comment("Screen Shake")
                    .define("屏幕抖动", true);
            ShieldCanBreakBaseInGameRule = builder.comment("Shield Can Break Base In Game Rule")
                    .define("基础游戏规则盾牌物品损坏", true);
            ArmorCanBreakBaseInGameRule = builder.comment("Armor Can Break Base In Game Rule")
                    .define("基础游戏规则盔甲物品损坏", false);
            MeleeCanBreakBaseInGameRule = builder.comment("Melee Can Break Base In Game Rule")
                    .define("基础游戏规则近战武器损坏", false);
            RangeCanBreakBaseInGameRule = builder.comment("Range Can Break Base In Game Rule")
                    .define("基础游戏规则远程武器损坏", false);
            MagicCanBreakBaseInGameRule = builder.comment("Magic Can Break Base In Game Rule")
                    .define("基础游戏规则魔法武器损坏", true);
            SameFactionAvoidDamage = builder.comment("Same Faction Avoid Damage")
                    .define("同阵营免伤", true);
            MobSayQuestionMark = builder.comment("Mob Say Question Mark")
                    .define("生物说问号", true);
            MobDestroyBlock = builder.comment("Mob Destroy Block")
                    .define("生物破坏方块", true);
            TamedMobDestroyBlock = builder.comment("Tamed Mob Destroy Block")
                    .define("驯服生物破坏方块", true);
            MobHasShockAback = builder.comment("Mob Has Shock Aback")
                    .define("生物会因为震摄逃离", false);
            MobUseCrossbowShrinkArrow = builder.comment("Mob Use Crossbow Shrink Arrow")
                    .define("生物使用弩消耗箭矢", false);
            MobUseBowShrinkArrow = builder.comment("Mob Use Bow Shrink Arrow")
                    .define("生物使用弓消耗箭矢", false);
            MobUseThrowShrinkItem = builder.comment("Mob Use Throw Shrink Item")
                    .define("生物使用投掷消耗物品", false);
            MobUseOtherShrinkItem = builder.comment("Mob Use Other Shrink Item")
                    .define("生物使用其他消耗物品", false);
            InventoryMobAboutItemstackInventoryTick = builder.comment("Inventory Mob About Itemstack Inventory Tick")
                    .define("具有物品栏生物关于背包计时的物品", true);
            MobManuallyControlCombatCameraChange = builder.comment("Mob Manually Control Combat Camera Change")
                    .define("生物手动控制状态时摄像机变动 ", true);
            ZombieCanUseSpearAndPikeWithoutTag = builder.comment("Zombie Can Use Spear And Pike Without Tag")
                    .define("僵尸可以使用矛和长枪而不需要标签 ", true);
            RandomSkinMobHasUnderTexture = builder.comment("Random Skin Mob Has Under Texture")
                    .define("随机纹理生物拥有底层纹理", false);
            HumanCustomNameWide = builder.comment("Human Custom Name Wide")
                    .define("人类自定义名称宽体", Lists.newArrayList("Jerotes_"));
            HumanCustomNameSlim = builder.comment("Human Custom Name Slim")
                    .define("人类自定义名称细体", Lists.newArrayList("Sentnes", "Maid"));
            builder.pop();

            builder.push("Special Combat");
            HasPercentageDamage = builder.comment("Has Percentage Damage")
                    .define("拥有百分比伤害", Lists.newArrayList());
            HasDamageCap = builder.comment("Has Damage Cap")
                    .define("拥有限伤", Lists.newArrayList());
            HasDamageCooldownTick = builder.comment("Has Damage Cooldown Tick")
                    .define("拥有无敌帧", Lists.newArrayList());
            BaseBreakHurtCooldownMultiple = builder.comment("Base Break Hurt Cooldown Multiple")
                    .defineInRange("基础无敌帧破除倍数", 5.0, 0.0, Double.MAX_VALUE);
            BaseAttackPercentage = builder.comment("Base Attack Percentage : 5.0")
                    .defineInRange("基础百分比", 5.0, 0.0, Double.MAX_VALUE);
            BaseMagicAttackPercentage = builder.comment("Base Magic Attack Percentage : 5.0")
                    .defineInRange("基础魔法百分比", 5.0, 0.0, Double.MAX_VALUE);
            BaseDamageCap = builder.comment("Base Damage Cap Percentage")
                    .defineInRange("基础限伤百分比", 5, 0.0, Double.MAX_VALUE);
            BaseDamageCooldownTick = builder.comment("Base Damage Cooldown Tick")
                    .defineInRange("基础无敌帧帧数", 20, 0.0, Double.MAX_VALUE);
            builder.pop();
        }
    }

    public static class ClientConfig {
        public ClientConfig(ForgeConfigSpec.Builder builder) {
        }
    }
}