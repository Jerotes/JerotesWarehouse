package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.MagicType;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.jerotes.jerotes.spell.SpellTypeInterface;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public interface WizardEntity {
    default boolean stopUseMainSpellInGoal() {
        return false;
    }
    default boolean stopUseAddSpellInGoal() {
        return false;
    }
    default void SpellUseAfterAttack(String string, MagicType magicType, MagicType magicType2) {
    }
    default boolean isUseSelfNotStringSpellList() {
        return true;
    }
    default int getCombatStyle() {
        //1.常规行为 远近武器攻击 无武器时选择远程走位
        //2.法术站桩 远近武器攻击 优先选择站桩或靠近
        //3.法术走位 远武器攻击 优先选择远程走位
        //4.法术远离 远武器攻击 优先选择远离
        return 1;
    }

    //所有法术
    default List<SpellTypeInterface> MainSpellList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        if (SelfMainSpellList() != null && !SelfMainSpellList().isEmpty() && isUseSelfNotStringSpellList()) {
            spellList.addAll(SelfMainSpellList());
        }
        if (SelfMainSpellStringList() != null && !SelfMainSpellStringList().isEmpty()) {
            spellList.addAll(SelfMainSpellStringList());
        }
        if (this instanceof LivingEntity livingEntity) {
            if (livingEntity.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.getMainSpellType(livingEntity.getMainHandItem()).isEmpty()) {
                spellList.addAll(magicItem.getMainSpellType(livingEntity.getMainHandItem()));
            }
            if (livingEntity.getOffhandItem().getItem() instanceof MagicItem magicItem && !magicItem.getMainSpellType(livingEntity.getOffhandItem()).isEmpty()) {
                spellList.addAll(magicItem.getMainSpellType(livingEntity.getOffhandItem()));
            }
        }
        return spellList;
    }
    default List<SpellTypeInterface> AddSpellList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        if (SelfAddSpellList() != null && !SelfAddSpellList().isEmpty() && isUseSelfNotStringSpellList()) {
            spellList.addAll(SelfAddSpellList());
        }
        if (SelfAddSpellStringList() != null && !SelfAddSpellStringList().isEmpty()) {
            spellList.addAll(SelfAddSpellStringList());
        }
        if (this instanceof LivingEntity livingEntity) {
            if (livingEntity.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.getAddSpellType(livingEntity.getMainHandItem()).isEmpty()) {
                spellList.addAll(magicItem.getAddSpellType(livingEntity.getMainHandItem()));
            }
            if (livingEntity.getOffhandItem().getItem() instanceof MagicItem magicItem && !magicItem.getAddSpellType(livingEntity.getOffhandItem()).isEmpty()) {
                spellList.addAll(magicItem.getAddSpellType(livingEntity.getOffhandItem()));
            }
        }
        return spellList;
    }

    //自身法术
    default List<SpellTypeInterface> SelfMainSpellList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        return spellList;
    }
    default List<SpellTypeInterface> SelfAddSpellList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        return spellList;
    }
    default List<SpellTypeInterface> SelfMainSpellStringList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        if (this instanceof LivingEntity livingEntity) {
            ListTag listTag = Main.getJerotesPersistentData(livingEntity).getList("jerotes_main_spell_string_list", 8);
            if (!listTag.isEmpty()) {
                for (Tag tag : listTag) {
                    if (tag instanceof StringTag stringTag) {
                        String string = stringTag.getAsString();
                        spellList.add(SpellRegistry.getSpellTypeById(string));
                    }
                }
            }
        }
        return spellList;
    }
    default List<SpellTypeInterface> SelfAddSpellStringList() {
        List<SpellTypeInterface> spellList = new ArrayList<>();
        if (this instanceof LivingEntity livingEntity) {
            ListTag listTag = Main.getJerotesPersistentData(livingEntity).getList("jerotes_add_spell_string_list", 8);
            if (!listTag.isEmpty()) {
                for (Tag tag : listTag) {
                    if (tag instanceof StringTag stringTag) {
                        String string = stringTag.getAsString();
                        spellList.add(SpellRegistry.getSpellTypeById(string));
                    }
                }
            }
        }
        return spellList;
    }
}

