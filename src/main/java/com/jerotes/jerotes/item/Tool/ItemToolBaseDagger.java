package com.jerotes.jerotes.item.Tool;

import com.google.common.collect.*;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesPotions;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ToolActions.SWORD_DIG;

public class ItemToolBaseDagger extends TieredItem implements ItemSpecialEffect, MeleeItem {
    private static final Map<ItemToolBaseDagger, ItemToolBaseDagger> BY_ID = Maps.newIdentityHashMap();
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public ItemToolBaseDagger(Tier tier, int damage, float speed, Properties properties) {
        super(tier, properties);
        this.attackDamage = (float)damage + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)speed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        BY_ID.put(this, this);
    }

    public static Iterable<ItemToolBaseDagger> daggers() {
        return Iterables.unmodifiableIterable(BY_ID.values());
    }

    public float getDamage() {
        return this.attackDamage;
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }
    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        itemStack.hurtAndBreak(1, livingEntity2, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(2, livingEntity, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }

    public boolean canShiftKeyDownUse(LivingEntity livingEntity) {
        return true;
    }
    public boolean canShiftKeyDownDamage(LivingEntity livingEntity) {
        return true;
    }

    //匕首
    public static final Set<ToolAction> ONE_HANDED_BREAK_SHIELD_ACTIONS = of(SWORD_DIG);
    private static Set<ToolAction> of(ToolAction... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ONE_HANDED_BREAK_SHIELD_ACTIONS.contains(toolAction);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() instanceof ItemToolBaseDagger) {
            if (enchantment == Enchantments.SWEEPING_EDGE) {
                return false;
            }
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public void attackEffectUse(Entity self, Entity attackTo, ItemStack itemStack, boolean dealDamage) {
        if (attackTo instanceof LivingEntity livingEntity) {
            if (!self.level().isClientSide()) {
                List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);
                boolean heal = true;
                for (MobEffectInstance mobEffectInstance : list) {
                    if (!mobEffectInstance.getEffect().isBeneficial()) {
                        heal = false;
                    }
                }
                if (dealDamage || heal) {
                    for (MobEffectInstance mobEffectInstance : list) {
                        if (mobEffectInstance.getEffect().isInstantenous()) {
                            mobEffectInstance.getEffect().applyInstantenousEffect(self, self, livingEntity, mobEffectInstance.getAmplifier(), 0.5);
                            continue;
                        }
                        MobEffect mobEffect = new MobEffectInstance(mobEffectInstance).getEffect();
                        int amplifier = new MobEffectInstance(mobEffectInstance).getAmplifier();
                        int duration = new MobEffectInstance(mobEffectInstance).getDuration() / 5;
                        boolean bl = new MobEffectInstance(mobEffectInstance).isAmbient();
                        boolean bl2 = new MobEffectInstance(mobEffectInstance).isVisible();
                        if (self instanceof LivingEntity owner) {
                            livingEntity.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, bl, bl2), owner);
                        } else {
                            livingEntity.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, bl, bl2));
                        }
                    }
                    PotionUtils.setPotion(itemStack, Potions.WATER);
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        //药水
        if (usePotion(level, player, interactionHand)) {
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }
        return super.use(level, player, interactionHand);
    }
    public boolean usePotion(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        //药水
        if (itemStack.getItem() instanceof ItemToolBaseDagger && interactionHand == InteractionHand.MAIN_HAND) {
            if (player.getOffhandItem().getItem() instanceof PotionItem && !(PotionUtils.getPotion(player.getOffhandItem()) == JerotesPotions.WASTE.get()) && !(PotionUtils.getPotion(player.getOffhandItem()) == PotionUtils.getPotion(itemStack))) {
                PotionUtils.setPotion(itemStack, PotionUtils.getPotion(player.getOffhandItem()));
                if (!player.getAbilities().instabuild) {
                    PotionUtils.setPotion(player.getOffhandItem(), JerotesPotions.WASTE.get());
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREWING_STAND_BREW, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.dagger").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        List<MobEffectInstance> lists = PotionUtils.getMobEffects(itemStack);
        if (lists.isEmpty()) {
            PotionUtils.addPotionTooltip(itemStack, list, 0.2f);
        }
    }
}

