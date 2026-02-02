package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseWhip extends ItemToolBaseSword {
    public final int useTick;
    private final float damageMulti;
    private final float knockbackMulti;
    private final float reach;
    private final float playerReach;
    public ItemToolBaseWhip(Tier tier, int damage, float speed, Properties properties, int useTick, float damageMulti, float knockbackMulti, float reach, float playerReach) {
        super(tier, damage, speed, properties);
        this.useTick = useTick;
        this.damageMulti = damageMulti;
        this.knockbackMulti = knockbackMulti;
        this.reach = reach;
        this.playerReach = playerReach;
    }
    public ItemToolBaseWhip(Tier tier, int damage, float speed, Properties properties) {
        this(tier, damage, speed, properties, 20, 1.0f, 1.0f, 1.5f, 3.0f);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.SPEAR;
    }
    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (interactionHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemStack);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity user, int n) {
        int n2 = this.getUseDuration(itemStack) - n;
        if (n2 < this.useTick) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            if (!user.isSilent()) {
                user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, user.getSoundSource(), 10.0f, 1.0f);
            }
            float reachs = AttackFind.reachAttackReach(user, itemStack, playerReach, reach, 3f, 0.5f);
            List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(reachs, reachs, reachs));
            for (LivingEntity hurt : list) {
                if (hurt == null || hurt.distanceTo(user) > reachs * 4) continue;
                if (AttackFind.FindCanNotAttack(user, hurt)) continue;
                if (!Main.canSee(hurt, user)) continue;
                if (!hurt.hasLineOfSight(user)) continue;
                AttackFind.attackBegin(user, hurt);
                boolean bl2 = AttackFind.attackAfter(user, hurt, damageMulti, knockbackMulti, false, 0f);
                if (bl2) {
                    afterUseAttack(itemStack, level, user, hurt, list);
                }
            }
            double d0 = (-Mth.sin(user.getYRot() * ((float)Math.PI / 180F)));
            double d1 = Mth.cos(user.getYRot() * ((float)Math.PI / 180F));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, user.getX() + d0, user.getY(0.5) , user.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }
        itemStack.hurtAndBreak(2,  user, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }
    public void afterUseAttack(ItemStack itemStack, Level level, LivingEntity self, LivingEntity hurt, List<LivingEntity> list) {
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.whip").withStyle(ChatFormatting.YELLOW));
    }
}

