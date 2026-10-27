package com.jerotes.jerotes.item.Tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.Interface.ItemSpecialAttack;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ToolActions.*;

public class ItemToolBaseTwoHandedAxe extends AxeItem implements ItemTwoHanded, ItemSpecialEffect, MeleeItem, ItemSpecialAttack {
    protected static final UUID BASE_ENTITY_REACH_UUID = UUID.fromString("c0b279fb-83be-442f-b062-c577d26065d3");
    protected static final UUID BASE_BLOCK_REACH_UUID = UUID.fromString("2ab44e2a-0eb7-4825-8a10-88709f6e2219");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final float attackDamageBaseline;

    public ItemToolBaseTwoHandedAxe(Tier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.attackDamageBaseline = damage + tier.getAttackDamageBonus();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", speed, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BASE_ENTITY_REACH_UUID, "Tool modifier", 0.5, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BASE_BLOCK_REACH_UUID, "Tool modifier", 0.5, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }
    @Override
    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!player.getOffhandItem().isEmpty()) {
            return InteractionResultHolder.fail(itemStack);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    //双手武器-斧
    public static final Set<ToolAction> TWO_HANDED_AXE_ACTIONS = of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF, SWORD_SWEEP);
    private static Set<ToolAction> of(ToolAction... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return TWO_HANDED_AXE_ACTIONS.contains(toolAction);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
            return this.isMeleeWeapon();
        }
        if (enchantment instanceof SweepingEdgeEnchantment) {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getBlockReduction() {
        return 50;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.two_handed_axe").withStyle(ChatFormatting.YELLOW));
        list.add(Component.translatable("item.jerotes.two_handed", this.getBlockReduction()).withStyle(ChatFormatting.YELLOW));
    }






    public void jerotesSpecialAttackClient(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            player.resetAttackStrengthTicker();
            ItemStack itemStack = player.getMainHandItem();
            boolean crit = AttackFind.shouldCritAttack(player, player);
            setAttackType(itemStack, crit ? 4 : Main.randomReach(player.getRandom(), 1, 3));
            setCritAttack(itemStack, crit);
        }
    }
    public static final String ATTACK_TYPE = "AttackType";
    public static final String CRIT_ATTACK = "CritAttack";
    public int getAttackType(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        return compoundtag.getInt(ATTACK_TYPE);
    }
    public static void setAttackType(ItemStack itemStack, int n) {
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        compoundtag.putInt(ATTACK_TYPE,  n);
        itemStack.setTag(compoundtag);
    }
    public boolean isCritAttack(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        return compoundtag.getBoolean(CRIT_ATTACK);
    }
    public static void setCritAttack(ItemStack itemStack, boolean bl) {
        CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
        compoundtag.putBoolean(CRIT_ATTACK,  bl);
        itemStack.setTag(compoundtag);
    }

    public void jerotesSpecialAttack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        boolean crit = AttackFind.shouldCritAttack(livingEntity, livingEntity);
        attack(livingEntity.level(), livingEntity, equipmentSlot, crit);
    }
    public void attack(Level level, LivingEntity livingEntity, EquipmentSlot equipmentSlot, boolean jump) {
        if (!livingEntity.isSilent()) {
            livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                    jump ? SoundEvents.PLAYER_ATTACK_CRIT : SoundEvents.PLAYER_ATTACK_SWEEP, livingEntity.getSoundSource(), 1.0f, 1.0f);
        }
        //攻击
        float reachs = AttackFind.reachAttackReach(livingEntity, livingEntity.getItemBySlot(equipmentSlot), 3f, 3f, 3.0F, 0.5F);
        List<LivingEntity> list = livingEntity.level().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(reachs, reachs, reachs));
        for (LivingEntity hurt : list) {
            if (hurt == null || hurt.distanceTo(livingEntity) > reachs * 4) continue;
            if (!hurt.isAlive()) continue;
            if (livingEntity == hurt) continue;
            //if (AttackFind.FindCanNotAttack(livingEntity, hurt)) continue;
            if (jump && !Main.canSeeAngle(livingEntity, hurt.getEyePosition(), 22.5) && !(hurt.getBoundingBox().intersects(livingEntity.getBoundingBox()))) continue;
            if ((!Main.canSee(hurt, livingEntity) || !hurt.hasLineOfSight(livingEntity)) && !(hurt.getBoundingBox().intersects(livingEntity.getBoundingBox()))) continue;
            if (!hurt.hasLineOfSight(livingEntity)) continue;
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.BYPASSES_COOLDOWN_MELEE, livingEntity);
            AttackFind.attackBegin(livingEntity, hurt);
            boolean bl = AttackFind.attackAfterCustomDamage(livingEntity, hurt, damageSource, jump ? 1.5f : 1f, 1f, false, 0f);
            if (bl) {
                if (jump) {
                    if (livingEntity.level() instanceof ServerLevel serverLevel) {
                        serverLevel.getChunkSource().broadcastAndSend(livingEntity, new ClientboundAnimatePacket(hurt, 4));
                    }
                    if (livingEntity.level().isClientSide()) {
                        Minecraft.getInstance().particleEngine.createTrackingEmitter(hurt, ParticleTypes.CRIT);
                    }
                }
                float f1 = EnchantmentHelper.getDamageBonus(livingEntity.getMainHandItem(), hurt.getMobType());
                if (f1 > 0.0F) {
                    if (livingEntity.level() instanceof ServerLevel serverLevel) {
                        serverLevel.getChunkSource().broadcastAndSend(livingEntity, new ClientboundAnimatePacket(hurt, 5));
                    }
                    if (livingEntity.level().isClientSide()) {
                        Minecraft.getInstance().particleEngine.createTrackingEmitter(hurt, ParticleTypes.ENCHANTED_HIT);
                    }
                }
                if (livingEntity.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, hurt.getX(), hurt.getY(0.5), hurt.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }

        if (livingEntity.level() instanceof ServerLevel) {
            if (livingEntity.getItemBySlot(equipmentSlot).getItem() instanceof ItemToolBaseTwoHandedSword itemToolBaseTwoHandedSword &&
                    itemToolBaseTwoHandedSword.getAttackType(livingEntity.getItemBySlot(equipmentSlot)) == 0 && !livingEntity.isUsingItem() &&
                    (livingEntity instanceof JerotesEntity && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null &&
                            livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK) || livingEntity instanceof Player)) {
                if (livingEntity.getItemBySlot(equipmentSlot).isDamageableItem()) {
                    livingEntity.getItemBySlot(equipmentSlot).hurtAndBreak(1, livingEntity,
                            e -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }
    }




    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equipProcess) {
                int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                poseStack.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProcess * -0.6F), (double)-0.72F);
            }
            //第一人称
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                                   ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                int dir = arm == HumanoidArm.RIGHT ? 1 : -1;
                if (player.isUsingItem() && player.getUseItem() == itemInHand) {
                    this.applyItemArmTransform(poseStack, arm, 0);
                    return true;
                }
                else if (swingProcess > 0.0F) {
                    HitResult hitResult = Minecraft.getInstance().hitResult;
                    boolean miningBlock = hitResult != null && hitResult.getType() == HitResult.Type.BLOCK;
                    if (getAttackType(itemInHand) == 0) {
                        setAttackType(itemInHand, miningBlock ? 4 : Main.randomReach(player.getRandom(), 1, 3));
                    }
                    if (swingProcess >= 0.98) {
                        setAttackType(itemInHand, 0);
                    }
                    this.applyItemArmTransform(poseStack, arm, 0);
                    firstPersonAttack(swingProcess, poseStack, dir, arm, getAttackType(itemInHand));
                    return true;
                }
                this.applyItemArmTransform(poseStack, arm, equipProcess);
                return true;
            }
        });
    }

    public static void firstPersonAttack(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm, int type) {
        if (type == 1) {
            firstPersonSwing(f, poseStack, n, humanoidArm, 1);
        }
        if (type == 2) {
            firstPersonSwing(f, poseStack, n, humanoidArm, -1f);
        }
        if (type == 3) {
            firstPersonSwing(f, poseStack, n, humanoidArm, 0.2f);
        }
        if (type == 4) {
            firstPersonStab(f, poseStack, n, humanoidArm);
        }
    }

    public static void firstPersonSwing(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm, float base) {
        //准备
        if (f > 0 && f <= 0.1) {
            float fs = f / 0.1f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 * fs * base));
            poseStack.mulPose(Axis.XP.rotationDegrees(20 * fs));
            poseStack.translate(0, 0.4 * fs, -0.65 * fs);
        }
        else if (f > 0.1 && f <= 0.15) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 * base));
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.translate(0, 0.4, -0.65);
        }
        //攻击
        else if (f > 0.15 && f <= 0.3) {
            float fs = (f - 0.15f) / 0.15f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 * base));
            poseStack.mulPose(Axis.XP.rotationDegrees(20 - (140 * fs)));
            poseStack.translate(0, 0.4 + (-0.1 * base * fs), -0.65 - 0.5 * fs);
        }
        else if (f > 0.3 && f <= 0.8) {
            float fs = 1 - (f - 0.3f) / 0.5f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 * base));
            poseStack.mulPose(Axis.XP.rotationDegrees(-120 - (20 * fs)));
            poseStack.translate(0, 0.4 + (-0.1 * base), -1.15);
        }
        //收回
        else if (f > 0.8) {
            float fs = 1 - (f - 0.8f) / 0.2f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 * fs * base));
            poseStack.mulPose(Axis.XP.rotationDegrees(-140 * fs));
            poseStack.translate(0, (0.4 + (-0.1 * base)) * fs, -1.15 * fs);
        }
    }
    public static void firstPersonStab(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm) {
        //蓄力后拉
        if (f > 0 && f <= 0.1) {
            float fs = f / 0.1f;
            poseStack.translate(0, 0.05 * fs, 0.3 * fs);
            poseStack.mulPose(Axis.XP.rotationDegrees(-20 * fs));
        }
        else if (f > 0.1 && f <= 0.15) {
            poseStack.translate(0, 0.05, 0.3);
            poseStack.mulPose(Axis.XP.rotationDegrees(-20));
        }
        //向前突刺
        else if (f > 0.15 && f <= 0.3) {
            float fs = (f - 0.15f) / 0.15f;
            poseStack.translate(0, 0.05 - (0.15 * fs), 0.3 - (1.45 * fs));
            poseStack.mulPose(Axis.XP.rotationDegrees(20 - (140 * fs)));
        }
        //刺出定格，慢慢往后收一点
        else if (f > 0.3 && f <= 0.8) {
            float fs = (f - 0.3f) / 0.5f;
            poseStack.translate(0, -0.1, -1.15 + (0.2 * fs));
            poseStack.mulPose(Axis.XP.rotationDegrees(-120 + (10 * fs)));
        }
        //收回
        else if (f > 0.8) {
            float fs = 1 - (f - 0.8f) / 0.2f;
            poseStack.translate(0, -0.1 * fs, (-1.15 + 0.2) * fs);
            poseStack.mulPose(Axis.XP.rotationDegrees((-120 + 10) * fs));
        }
    }

    public static float progress(float f, float f2, float f3) {
        return Mth.clamp(Mth.inverseLerp(f, f2, f3), 0.0f, 1.0f);
    }

    @Override
    public int swingTimes() {
        float f = 10;
        return (int)f;
    }
}

