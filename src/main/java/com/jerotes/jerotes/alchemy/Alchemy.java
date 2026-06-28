package com.jerotes.jerotes.alchemy;

import com.jerotes.jerotes.alchemy.effect.AAAAlchemyEffect;
import com.jerotes.jerotes.alchemy.effect.AAANullAlchemyEffect;
import com.jerotes.jerotes.alchemy.forge.JerotesAlchemyMaterialConflictEvent;
import com.jerotes.jerotes.alchemy.forge.JerotesAlchemyMaterialEffectEvent;
import com.jerotes.jerotes.alchemy.forge.JerotesAlchemySpecialEvent;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

public class Alchemy {
    //多种材料不能一样
    //可能为特殊配方
    //完全不兼容的材料
    //不同材料不同等级持续时长
    //每种炼金材料具有多种不同炼金效果
    //注册药酿
    //加入游戏加载时获取物品属性包含

    //了解材料效果
    //看到材料效果
    //药剂配方记录
    //根据法术等级和制作次数得到完成效果

    //特殊配方
    public static boolean isSpecialResult(ItemStack material1, ItemStack material2, ItemStack material3, ItemStack material4, ItemStack material5) {
        JerotesAlchemySpecialEvent event = new JerotesAlchemySpecialEvent(material1, material2, material3, material4, material5);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isSpecial();
    }

    //结果类型
    public static int getResultType(int needCount, ItemStack material1, ItemStack material2, ItemStack material3, ItemStack material4, ItemStack material5) {
        int count = 0;
        if (!material1.isEmpty()) count += 1;
        if (!material2.isEmpty()) count += 1;
        if (!material3.isEmpty()) count += 1;
        if (!material4.isEmpty()) count += 1;
        if (!material5.isEmpty()) count += 1;
        //5-数量小于所需数
        if (count < needCount)
            return 5;
        JerotesAlchemyMaterialEffectEvent event1 = new JerotesAlchemyMaterialEffectEvent(material1);
        JerotesAlchemyMaterialEffectEvent event2 = new JerotesAlchemyMaterialEffectEvent(material2);
        JerotesAlchemyMaterialEffectEvent event3 = new JerotesAlchemyMaterialEffectEvent(material3);
        JerotesAlchemyMaterialEffectEvent event4 = new JerotesAlchemyMaterialEffectEvent(material4);
        JerotesAlchemyMaterialEffectEvent event5 = new JerotesAlchemyMaterialEffectEvent(material5);
        MinecraftForge.EVENT_BUS.post(event1);
        MinecraftForge.EVENT_BUS.post(event2);
        MinecraftForge.EVENT_BUS.post(event3);
        MinecraftForge.EVENT_BUS.post(event4);
        MinecraftForge.EVENT_BUS.post(event5);
        //1-含有超出上限的相同材料无法结合
        int material1MaxCount = event1.getMaxCount();
        int material2MaxCount = event2.getMaxCount();
        int material3MaxCount = event3.getMaxCount();
        int material4MaxCount = event4.getMaxCount();
        int material5MaxCount = event5.getMaxCount();
        ItemStack[] materials = {material1, material2, material3, material4, material5};
        int[] materialCounts = {1, 1, 1, 1, 1};
        for (int i = 0; i < materials.length; i++) {
            if (materials[i].isEmpty()) continue;
            for (int j = i + 1; j < materials.length; j++) {
                if (materials[j].isEmpty()) continue;
                if (ItemStack.isSameItem(materials[i], materials[j])) {
                    materialCounts[i]++;
                    materialCounts[j]++;
                }
            }
        }
        if (materialCounts[0] > material1MaxCount && !material1.isEmpty() ||
                materialCounts[1] > material2MaxCount && !material2.isEmpty() ||
                materialCounts[2] > material3MaxCount && !material3.isEmpty() ||
                materialCounts[3] > material4MaxCount && !material4.isEmpty() ||
                materialCounts[4] > material5MaxCount && !material5.isEmpty()) {
            return 1;
        }

        //0-含有非炼金材料无法结合
        boolean material1Not = event1.getEffectCount() <= 0 && !material1.isEmpty();
        boolean material2Not = event2.getEffectCount() <= 0 && !material2.isEmpty();
        boolean material3Not = event3.getEffectCount() <= 0 && !material3.isEmpty();
        boolean material4Not = event4.getEffectCount() <= 0 && !material4.isEmpty();
        boolean material5Not = event5.getEffectCount() <= 0 && !material5.isEmpty();
        if (material1Not || material2Not || material3Not || material4Not || material5Not)
            return 0;

        JerotesAlchemyMaterialConflictEvent event11 = new JerotesAlchemyMaterialConflictEvent(material1);
        JerotesAlchemyMaterialConflictEvent event12 = new JerotesAlchemyMaterialConflictEvent(material2);
        JerotesAlchemyMaterialConflictEvent event13 = new JerotesAlchemyMaterialConflictEvent(material3);
        JerotesAlchemyMaterialConflictEvent event14 = new JerotesAlchemyMaterialConflictEvent(material4);
        JerotesAlchemyMaterialConflictEvent event15 = new JerotesAlchemyMaterialConflictEvent(material5);
        MinecraftForge.EVENT_BUS.post(event11);
        MinecraftForge.EVENT_BUS.post(event12);
        MinecraftForge.EVENT_BUS.post(event13);
        MinecraftForge.EVENT_BUS.post(event14);
        MinecraftForge.EVENT_BUS.post(event15);

        //4-完全不兼容
        if (event11.isConflictWith(material2) && !material2.isEmpty() ||
                event11.isConflictWith(material3) && !material3.isEmpty() ||
                event11.isConflictWith(material4) && !material4.isEmpty() ||
                event11.isConflictWith(material5) && !material5.isEmpty() ||
                event12.isConflictWith(material1) && !material1.isEmpty() ||
                event12.isConflictWith(material3) && !material3.isEmpty() ||
                event12.isConflictWith(material4) && !material4.isEmpty() ||
                event12.isConflictWith(material5) && !material5.isEmpty() ||
                event13.isConflictWith(material1) && !material1.isEmpty() ||
                event13.isConflictWith(material2) && !material2.isEmpty() ||
                event13.isConflictWith(material4) && !material4.isEmpty() ||
                event13.isConflictWith(material5) && !material5.isEmpty() ||
                event14.isConflictWith(material1) && !material1.isEmpty() ||
                event14.isConflictWith(material2) && !material2.isEmpty() ||
                event14.isConflictWith(material3) && !material3.isEmpty() ||
                event14.isConflictWith(material5) && !material5.isEmpty() ||
                event15.isConflictWith(material1) && !material1.isEmpty() ||
                event15.isConflictWith(material2) && !material2.isEmpty() ||
                event15.isConflictWith(material3) && !material3.isEmpty() ||
                event15.isConflictWith(material4) && !material4.isEmpty()
        )
            return 4;
        //3-特殊配方
        if (isSpecialResult(material1, material2, material3, material4, material5))
            return 3;
        //2-可以结合（包括失败产物）
        return 2;
    }
    //结果等级
    public static int getResultLevel(Entity entity, int madeCount, int getResultType, int specialLevel,
                                     boolean spellLevelBonus, boolean madeCountBonus, boolean mustBeSpecialLevel) {
        int level = 1;
        //法术等级加成
        if (entity instanceof SpellUseEntity spellUse && spellLevelBonus)
            level += (int) (spellUse.getSpellLevel() / 2f);
        //制作次数加成
        if (madeCountBonus)
            level += madeCount;
        //特殊配方加成
        if (getResultType == 3)
            level = mustBeSpecialLevel ? specialLevel : Math.max(specialLevel, level);
        return level;
    }

    //生效效果
    private static boolean isValidAlchemyEffect(AAAAlchemyEffect effect) {
        return effect != null && !(effect instanceof AAANullAlchemyEffect);
    }
    public static List<AAAAlchemyEffect> findCommonEffectsWithMaxValues(
            ItemStack material1, ItemStack material2, ItemStack material3,
            ItemStack material4, ItemStack material5) {

        List<List<AAAAlchemyEffect>> allEffects = new ArrayList<>();
        if (!material1.isEmpty()) {
            List<AAAAlchemyEffect> effects1 = getValidEffectsFromMaterial(material1);
            allEffects.add(effects1);
        }
        if (!material2.isEmpty()) {
            List<AAAAlchemyEffect> effects2 = getValidEffectsFromMaterial(material2);
            allEffects.add(effects2);
        }
        if (!material3.isEmpty()) {
            List<AAAAlchemyEffect> effects3 = getValidEffectsFromMaterial(material3);
            allEffects.add(effects3);
        }
        if (!material4.isEmpty()) {
            List<AAAAlchemyEffect> effects4 = getValidEffectsFromMaterial(material4);
            allEffects.add(effects4);
        }
        if (!material5.isEmpty()) {
            List<AAAAlchemyEffect> effects5 = getValidEffectsFromMaterial(material5);
            allEffects.add(effects5);
        }
        if (allEffects.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, AAAAlchemyEffect> commonEffects = findCommonEffects(allEffects);
        return new ArrayList<>(commonEffects.values());
    }
    private static List<AAAAlchemyEffect> getValidEffectsFromMaterial(ItemStack material) {
        JerotesAlchemyMaterialEffectEvent event = new JerotesAlchemyMaterialEffectEvent(material);
        MinecraftForge.EVENT_BUS.post(event);

        List<AAAAlchemyEffect> validEffects = new ArrayList<>();
        AAAAlchemyEffect[] effects = {
                event.getEffect1(),
                event.getEffect2(),
                event.getEffect3(),
                event.getEffect4(),
                event.getEffect5()
        };

        for (AAAAlchemyEffect effect : effects) {
            if (isValidAlchemyEffect(effect)) {
                validEffects.add(effect);
            }
        }

        return validEffects;
    }
    private static Map<String, AAAAlchemyEffect> findCommonEffects(List<List<AAAAlchemyEffect>> allEffects) {
        Map<Class<? extends AAAAlchemyEffect>, List<AAAAlchemyEffect>> effectOccurrences = new HashMap<>();
        for (List<AAAAlchemyEffect> materialEffects : allEffects) {
            for (AAAAlchemyEffect effect : materialEffects) {
                Class<? extends AAAAlchemyEffect> effectClass = effect.getClass();
                effectOccurrences.computeIfAbsent(effectClass, k -> new ArrayList<>()).add(effect);
            }
        }
        Map<String, AAAAlchemyEffect> commonEffects = new HashMap<>();
        for (Map.Entry<Class<? extends AAAAlchemyEffect>, List<AAAAlchemyEffect>> entry : effectOccurrences.entrySet()) {
            List<AAAAlchemyEffect> effects = entry.getValue();
            //只保留出现2次及以上
            if (effects.size() >= 2) {
                //找出最高level和time
                AAAAlchemyEffect bestEffect = null;
                int maxLevel = -1;
                int maxTime = -1;
                for (AAAAlchemyEffect effect : effects) {
                    int level = effect.getLevel();
                    int time = effect.getTime();

                    if (level > maxLevel || (level == maxLevel && time > maxTime)) {
                        maxLevel = level;
                        maxTime = time;
                        bestEffect = effect;
                    }
                }
                if (bestEffect != null) {
                    //使用类名作为键，或者使用其他标识符
                    commonEffects.put(bestEffect.getClass().getSimpleName(), bestEffect);
                }
            }
        }

        return commonEffects;
    }


    public static ItemStack resultPotion(Entity entity, boolean sendMessage, ItemStack material1, ItemStack material2, ItemStack material3, ItemStack material4, ItemStack material5) {
        int resultType = getResultType(3, material1, material2, material3, material4, material5);
        //0-含有非炼金材料无法结合
        if (resultType == 0) {
            if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
                serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_0"));
            }
            return Items.AIR.getDefaultInstance();
        }
        //1-含有超出上限的相同材料无法结合
        if (resultType == 1) {
            if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
                serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_1"));
            }
            return Items.AIR.getDefaultInstance();
        }
        //5-数量小于所需数
        if (resultType == 5) {
            if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
                serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_5"));
            }
            return Items.AIR.getDefaultInstance();
        }
        //4-完全不兼容
        if (resultType == 4) {
            if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
                serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_4"));
            }
            //爆炸
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 2, Level.ExplosionInteraction.NONE);
            return Items.AIR.getDefaultInstance();
        }
        //2-可以结合（包括失败产物）
        //3-特殊配方
        if (resultType == 2 || resultType == 3) {
            if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
                if (resultType == 2) {
                    serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_2"));
                }
                if (resultType == 3) {
                    serverPlayer.sendSystemMessage(Component.translatable("alchemy.jerotes.message_3"));
                }
            }
            int madeCount = 0;
            int level = getResultLevel(entity, madeCount, resultType, 1, true, true, false);

            List<AAAAlchemyEffect> commonEffects = findCommonEffectsWithMaxValues(
                    material1, material2, material3, material4, material5);

            ItemStack itemStack = new ItemStack(Items.POTION);
            Collection<MobEffectInstance> collection = new ArrayList<>();
            Component component = Component.translatable("item.minecraft.potion");
            //处理共同效果
            for (AAAAlchemyEffect effect : commonEffects) {
                //应用最高等级和持续时间的效果
                collection.add(new MobEffectInstance(effect.getMobEffect(), effect.getMobEffectTick(effect.getTime(), level), effect.getMobEffectLevel(effect.getLevel(), level)));
//               if (entity instanceof ServerPlayer serverPlayer && sendMessage) {
//                   serverPlayer.sendSystemMessage(effect.getMobEffect().getDisplayName());
//               }
                component = Component.literal("").append(effect.getMobEffect().getDisplayName()).append(component);
            }
            PotionUtils.setCustomEffects(itemStack, collection);
            itemStack.setHoverName(Component.translatable("alchemy.jerotes.base").append(" ").append(component));
            return itemStack;
        }
        return Items.AIR.getDefaultInstance();
    }

    //记得让物品销毁，参考
    public static boolean discardItem(ItemStack itemStack) {
        JerotesAlchemyMaterialEffectEvent events = new JerotesAlchemyMaterialEffectEvent(itemStack);
        MinecraftForge.EVENT_BUS.post(events);
        if (!events.isDiscard())
            return false;
        if (events.getDiscardTime() <= 1) {
            itemStack.shrink(1);
            return true;
        }
        int n = itemStack.getTag() != null && itemStack.getTag().get("JerotesRemainingAlchemyUses") != null ? itemStack.getTag().getInt("JerotesRemainingAlchemyUses") : 0;
        if (events.getDiscardTime() - n <= 1) {
            itemStack.shrink(1);
            return true;
        }
        else {
            CompoundTag compoundtag = itemStack.getOrCreateTag();
            int ns = compoundtag.getInt("JerotesRemainingAlchemyUses");
            compoundtag.putInt("JerotesRemainingAlchemyUses", ns + 1);
            itemStack.setTag(compoundtag);
        }
        return false;
    }
}