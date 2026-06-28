package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface FactionEntity {
    //是同一阵营
    default boolean isFriendFaction(Entity entity) {
        return isFriendFactionBase(entity);
    }
    default boolean isFriendFactionBase(Entity entity) {
        return entity instanceof FactionEntity factions && !Collections.disjoint(getFactionTypeList(), factions.getFactionTypeList()) || helpSelfType() && this instanceof Entity entity2 && entity.getType() == entity2.getType();
    }
    default boolean helpSelfType() {
        return false;
    }
    //憎恨阵营
    default boolean isHateFaction(Entity entity) {
        return isHateFactionBase(entity);
    }
    default boolean isHateFactionBase(Entity entity) {
        return false;
    }
    //协助所有同伴
    default boolean helpAllSameFaction() {
        return true;
    }
    //被所有同伴协助
    default boolean helpByAllSameFaction() {
        return true;
    }
    //同类协助
    default boolean helpSameType() {
        return true;
    }
    //特例协助
    default boolean canBeHelp(Entity entity) {
        return true;
    }
    //阵营
    default String getFirstFactionTypeName() {
        return "";
    }
    default String getMobTypeNameModId() {
        if (this instanceof LivingEntity entity && EntityFactionFind.getTrueFaction(entity) != null) {
            String s = EntityFactionFind.getTrueFaction(entity);
            if (s.contains("raider") || s.contains("piglin") || s.contains("illager") || s.contains("witch") || s.contains("wither_skeleton") || s.contains("villager")) {
                return "jerotes";
            }
        }
        return "";
    }

    default List<String> getFactionTypeUntilTame() {
        List<String> list = new ArrayList<>();
        list.add(getFirstFactionTypeName());
        return list;
    }
    default List<String> getFactionTypeListEvenThoughTame() {
        return new ArrayList<>();
    }
    default List<String> getFactionTypeList() {
        List<String> list = getFactionTypeListEvenThoughTame();
        if (this instanceof OwnableEntity ownable && ownable.getOwner() == null || !(this instanceof OwnableEntity ownable)) {
            list.addAll(getFactionTypeUntilTame());
        }
        if (this instanceof LivingEntity living) {
            for (Tag tag : Main.getJerotesPersistentData(living).getList("jerotes_mob_faction", 8)) {
                list.add(tag.getAsString());
            }
        }
        list.removeIf(String::isEmpty);
        return list;
    }
}
