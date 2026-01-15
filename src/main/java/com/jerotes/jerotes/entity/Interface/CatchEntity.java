package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.item.Tool.ItemToolBaseInsectNet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface CatchEntity {
    boolean fromCatch();

    void setFromCatch(boolean p_148834_);

    void saveToCatchTag(ItemStack p_148833_);

    void loadFromCatchTag(CompoundTag p_148832_);

    ItemStack getCatchItemStack();

    SoundEvent getPickupSound();

    /** @deprecated */
    @Deprecated
    static void saveDefaultDataToCatchTag(Mob mob, ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getOrCreateTag();
        if (mob.hasCustomName()) {
            itemStack.setHoverName(mob.getCustomName());
        }

        if (mob.isNoAi()) {
            compoundtag.putBoolean("NoAI", mob.isNoAi());
        }

        if (mob.isSilent()) {
            compoundtag.putBoolean("Silent", mob.isSilent());
        }

        if (mob.isNoGravity()) {
            compoundtag.putBoolean("NoGravity", mob.isNoGravity());
        }

        if (mob.hasGlowingTag()) {
            compoundtag.putBoolean("Glowing", mob.hasGlowingTag());
        }

        if (mob.isInvulnerable()) {
            compoundtag.putBoolean("Invulnerable", mob.isInvulnerable());
        }

        compoundtag.putFloat("Health", mob.getHealth());
    }

    /** @deprecated */
    @Deprecated
    static void loadDefaultDataFromCatchTag(Mob mob, CompoundTag compoundTag) {
        if (compoundTag.contains("NoAI")) {
            mob.setNoAi(compoundTag.getBoolean("NoAI"));
        }

        if (compoundTag.contains("Silent")) {
            mob.setSilent(compoundTag.getBoolean("Silent"));
        }

        if (compoundTag.contains("NoGravity")) {
            mob.setNoGravity(compoundTag.getBoolean("NoGravity"));
        }

        if (compoundTag.contains("Glowing")) {
            mob.setGlowingTag(compoundTag.getBoolean("Glowing"));
        }

        if (compoundTag.contains("Invulnerable")) {
            mob.setInvulnerable(compoundTag.getBoolean("Invulnerable"));
        }

        if (compoundTag.contains("Health", 99)) {
            mob.setHealth(compoundTag.getFloat("Health"));
        }

    }

    static <T extends LivingEntity & CatchEntity> Optional<InteractionResult> catchMobPickup(Player player, InteractionHand interactionHand, T t) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (itemstack.getItem() instanceof ItemToolBaseInsectNet && t.isAlive()) {
            t.playSound(t.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = t.getCatchItemStack();
            t.saveToCatchTag(itemstack1);
            if (!player.getInventory().add(itemstack1)) {
                player.drop(itemstack1, false);
            }
            Level level = t.level();
            if (!level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemstack1);
            }
            itemstack.hurtAndBreak(2, player, livingEntity -> livingEntity.broadcastBreakEvent(interactionHand));
            t.discard();
            return Optional.of(InteractionResult.sidedSuccess(level.isClientSide));
        } else {
            return Optional.empty();
        }
    }
}

