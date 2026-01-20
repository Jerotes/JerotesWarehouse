package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseChainsaw extends ItemToolBaseAxe {
    private final float damageBase;
    private final float knockbackBase;
    private final int cooldownTick;
    private final float reach;
    private final float playerReach;
    public ItemToolBaseChainsaw(Tier tier, int damage, float speed, Properties properties, float damageBase, float knockbackBase, int cooldownTick, float reach, float playerReach) {
        super(tier, damage, speed, properties);
        this.damageBase = damageBase;
        this.knockbackBase = knockbackBase;
        this.cooldownTick = cooldownTick;
        this.reach = reach;
        this.playerReach = playerReach;
    }
    public ItemToolBaseChainsaw(Tier tier, int damage, float speed, Properties properties) {
        this(tier, damage, speed, properties, 0.2f, 0.1f, 40, 1.5f, 3.0f);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }
    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 320;
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
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
        if (livingEntity.getTicksUsingItem() == count) {
            livingEntity.stopUsingItem();
            this.releaseUsing(itemStack, level, livingEntity, count);
            if (livingEntity instanceof Player player) {
                player.getCooldowns().addCooldown(this, cooldownTick*2);
                itemStack.hurtAndBreak(1, player, livingEntitys -> livingEntitys.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
        }
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            if (!livingEntity.isSilent()) {
                livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ITEM_BREAK, livingEntity.getSoundSource(), 10.0f, 1.0f);
            }
            //攻击
            float reachs = (((livingEntity instanceof Player || livingEntity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.otherAttackReachAsPlayer()) ? playerReach : reach) + ((livingEntity.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, livingEntity.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0));
            List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(reachs, reachs, reachs));
            for (LivingEntity hurt : list) {
                if (hurt == null || hurt.distanceTo(livingEntity) > reachs * 4) continue;
                if (!hurt.isAlive()) continue;
                if (AttackFind.FindCanNotAttack(livingEntity, hurt)) continue;
                if (!Main.canSee(hurt, livingEntity)) continue;
                if (!hurt.hasLineOfSight(livingEntity)) continue;
                DamageSource damageSources = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.BYPASSES_COOLDOWN_MELEE), livingEntity);
                AttackFind.attackBegin(livingEntity, hurt);
                boolean bl2 = AttackFind.attackAfterCustomDamage(livingEntity, hurt, damageSources, damageBase, knockbackBase, false, 0f);
                if (bl2) {
                    afterUseAttack(itemStack, level, livingEntity, hurt, list);
                }
            }
        }
    }
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity3) {
        itemStack.hurtAndBreak(1, livingEntity3, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }
    public void afterUseAttack(ItemStack itemStack, Level level, LivingEntity self, LivingEntity hurt, List<LivingEntity> list) {
        if (!hurt.level().isClientSide) {
            hurt.addEffect(new MobEffectInstance(JerotesMobEffects.BLEEDING.get(), 60, 0, false, false), self);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, hurt.getX(), hurt.getY(0.5), hurt.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
        }
    }
    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
        if (livingEntity instanceof Player player2) {
            player2.awardStat(Stats.ITEM_USED.get(this));
        }
        int n2 = this.getUseDuration(itemStack) - n;
        if (n2 < this.getUseDuration(itemStack) / 8) {
            return;
        }
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, cooldownTick);
            itemStack.hurtAndBreak(2, player, livingEntitys -> livingEntitys.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.chainsaw", damageBase).withStyle(ChatFormatting.YELLOW));
    }
}

