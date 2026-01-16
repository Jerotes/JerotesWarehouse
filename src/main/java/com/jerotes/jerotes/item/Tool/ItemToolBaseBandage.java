package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemToolBaseBandage extends Item {
    public static final String TAG_TARGET = "Target";
    public ItemToolBaseBandage(Properties properties) {
        super(properties);
    }

    public float getHealth() {
        return 8.0f;
    }
    public int getUseTime() {
        return 80;
    }
    public void addEffectAbout(LivingEntity livingEntity, LivingEntity livingEntity2) {
    }

    public static boolean isTarget(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag == null) return false;
        if (!compoundtag.contains(TAG_TARGET)) return false;
        compoundtag.getUUID(TAG_TARGET);
        return true;
    }
    public static UUID getTarget(ItemStack itemStack) {
        if (!isTarget(itemStack)) return null;
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        return compoundtag.getUUID(TAG_TARGET);
    }
    public static Entity FindTarget(ItemStack itemStack, ServerLevel serverLevel) {
        return serverLevel.getEntity(Objects.requireNonNull(getTarget(itemStack)));
    }
    public static void setTarget(ItemStack itemStack, LivingEntity livingEntity) {
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        compoundtag.putUUID(TAG_TARGET, livingEntity.getUUID());
        itemStack.setTag(compoundtag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (Main.getTargetedEntity(player, 3) != null && Main.getTargetedEntity(player, 3) instanceof LivingEntity livingEntity2) {
            setTarget(itemStack, livingEntity2);
        }
        else {
            setTarget(itemStack, player);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
        if (level instanceof ServerLevel serverLevel) {
            if (!(isTarget(itemStack)) || !FindTarget(itemStack, serverLevel).isAlive() || FindTarget(itemStack, serverLevel).distanceTo(livingEntity) > 6) {
                if (livingEntity instanceof Player player) {
                    player.getCooldowns().addCooldown(this, 40);
                }
                livingEntity.stopUsingItem();
                CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
                if (isTarget(itemStack)) {
                    compoundtag.remove(TAG_TARGET);
                }
                itemStack.setTag(compoundtag);
                return;
            }
            for (int i = 0; i < 3; ++i) {
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), FindTarget(itemStack, serverLevel).getRandomX(0.5), FindTarget(itemStack, serverLevel).getRandomY(), FindTarget(itemStack, serverLevel).getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
            }
        }
        int n2 = this.getUseDuration(itemStack) - count;
        if (n2 == this.getUseTime()) {
            livingEntity.playSound(JerotesSoundEvents.USE_BANDAGE);
        }
        super.onUseTick(level, livingEntity, itemStack, count);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
        int n2 = this.getUseDuration(itemStack) - n;
        if (n2 < this.getUseTime()) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            if (FindTarget(itemStack, serverLevel) == null || !FindTarget(itemStack, serverLevel).isAlive() || FindTarget(itemStack, serverLevel).distanceTo(livingEntity) > 6) {
                return;
            }
            if (FindTarget(itemStack, serverLevel) != null && FindTarget(itemStack, serverLevel) instanceof LivingEntity livingEntity1) {
                livingEntity1.heal(this.getHealth());
                livingEntity1.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.BANDAGE_CAN_REMOVE).forEach(effect -> {
                    if (livingEntity1.hasEffect(effect.get())) {
                        if (!livingEntity1.level().isClientSide()) {
                            livingEntity1.removeEffect(effect.get());
                        }
                    }
                });
                for (int i = 0; i < 3; ++i) {
                    serverLevel.sendParticles(ParticleTypes.HEART, livingEntity1.getRandomX(0.5), livingEntity1.getRandomY(), livingEntity1.getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
                }
                this.addEffectAbout(livingEntity1, livingEntity);
            }
            if (livingEntity instanceof Player player) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                player.getCooldowns().addCooldown(this, 40);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        if (isTarget(itemStack)) {
            compoundtag.remove(TAG_TARGET);
        }
        itemStack.setTag(compoundtag);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BRUSH;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.bandage", this.getHealth(), this.getUseTime() / 20).withStyle(ChatFormatting.YELLOW));
    }
}

