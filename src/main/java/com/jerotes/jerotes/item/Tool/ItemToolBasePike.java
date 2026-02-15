package com.jerotes.jerotes.item.Tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.entity.Mob.TestEntity;
import com.jerotes.jerotes.entity.Interface.ServerPlayerEntity;
import com.jerotes.jerotes.entity.Interface.UseSpearSpecialEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.Interface.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Ease;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemToolBasePike extends TieredItem implements MeleeItem, ItemSpecialEffect, Vanishable, ItemSpecialAttack, ItemTwoHanded, ItemSpecialInHand {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    protected static final UUID BASE_ENTITY_REACH_UUID = UUID.fromString("c0b279fb-83be-442f-b062-c577d26065d3");
    protected static final UUID BASE_BLOCK_REACH_UUID = UUID.fromString("2ab44e2a-0eb7-4825-8a10-88709f6e2219");
    private final float attackDamage;

    public final float selfSpeedDamage;
    public final float targetSpeedDamage;
    public final float specialDamageBase;
    public final float swingTimes;
    public final SoundEvent sound;
    public final SoundEvent hitSound;
    public final SoundEvent sound2;
    public final float minRange;
    public final float maxRange;
    public final float minCreativeRange;
    public final float maxCreativeRange;
    public final float hitboxMargin;
    public final float mobFactor;

    public ItemToolBasePike(Tier tier, Properties properties, float damage, float speed, float swingTimes, float specialDamageBase, float selfSpeedDamage, float targetSpeedDamage, float reach, SoundEvent sound, SoundEvent hitSound, SoundEvent sound2,
                            float minRange, float maxRange, float minCreativeRange, float maxCreativeRange, float hitboxMargin, float mobFactor) {
        super(tier, properties.defaultDurability(tier.getUses()));
        this.selfSpeedDamage = selfSpeedDamage;
        this.targetSpeedDamage = targetSpeedDamage;
        this.specialDamageBase = specialDamageBase;
        this.swingTimes = swingTimes;
        this.sound = sound;
        this.hitSound = hitSound;
        this.sound2 = sound2;
        this.minRange = minRange;
        this.maxRange = maxRange - reach;
        this.minCreativeRange = minCreativeRange;
        this.maxCreativeRange = maxCreativeRange - reach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
        this.attackDamage = (float)damage + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", speed, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BASE_ENTITY_REACH_UUID, "Tool modifier", reach, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BASE_BLOCK_REACH_UUID, "Tool modifier", reach, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public int getBlockReduction() {
        return 50;
    }
    public void makeSound(Entity entity) {
        if (entity instanceof Player player) {
            entity.level().playSound(player, entity.getX(), entity.getY(), entity.getZ(),
                    sound, entity.getSoundSource(), 1.0f, 1.0f);
        }
        else {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    sound, entity.getSoundSource(), 1.0f, 1.0f);
        }
    }
    public void makeSound2(Entity entity) {
        if (entity instanceof Player player) {
            entity.level().playSound(player, entity.getX(), entity.getY(), entity.getZ(),
                    sound2, entity.getSoundSource(), 1.0f, 1.0f);
        }
        else {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    sound2, entity.getSoundSource(), 1.0f, 1.0f);
        }
    }
    public void makeHitSound(Entity entity) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                hitSound, entity.getSoundSource(), 1.0f, 1.0f);
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
        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.pike", selfSpeedDamage, targetSpeedDamage, specialDamageBase).withStyle(ChatFormatting.YELLOW));
        list.add(Component.translatable("item.jerotes.two_handed_pike").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
        if (livingEntity.getTicksUsingItem() == count) {
            livingEntity.stopUsingItem();
            this.releaseUsing(itemStack, level, livingEntity, count);
        }
        if (livingEntity.getTicksUsingItem() == 10) {
            specialAttack(livingEntity.level(), livingEntity);
            if (livingEntity instanceof JerotesEntity && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK) || livingEntity instanceof Player) {
                if (itemStack.isDamageableItem()) {
                    itemStack.hurtAndBreak(1, livingEntity,
                            e -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
            if (livingEntity instanceof Player player) {
                player.getCooldowns().addCooldown(this, 40);
            }
        }
        super.onUseTick(level, livingEntity, itemStack, count);
    }
    public void specialAttack(Level level, LivingEntity livingEntity) {
        makeSound2(livingEntity);
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            //攻击
            List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class,
                    livingEntity.getBoundingBox().inflate(effectiveMaxRange(livingEntity) * 2f));
            for (LivingEntity hurt : list) {
                if (hurt == null || hurt.distanceTo(livingEntity) > effectiveMaxRange(livingEntity) * 0.7f) continue;
                if (!hurt.isAlive()) continue;
                if (AttackFind.FindCanNotAttack(livingEntity, hurt)) continue;
                if (!Main.canSeeAngle(livingEntity, hurt.getEyePosition(), 80)) continue;
                if (!hurt.hasLineOfSight(livingEntity)) continue;
                AttackFind.attackBegin(livingEntity, hurt);
                causeExtraKnockback(livingEntity, hurt, 1.0f, hurt.getDeltaMovement());
                AttackFind.attackAfter(livingEntity, hurt, specialDamageBase
                        , 2f, false, 0f);
                if (!EntityAndItemFind.isNoSpecialKnockback(hurt.getType())) {
                    if (Main.mobSizeSmall(hurt) || Main.mobSizeMedium(hurt) || Main.mobSizeLarge(hurt)) {
                        double d = 0.0;
                        if (hurt.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                            d = Math.max(livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 1.0);
                        }
                        double d3 = Math.max(0, 1 - d) + 0.2f;
                        float base = (0.15f + 1 / livingEntity.distanceTo(hurt) / 3f) * 0.25f;
                        hurt.setOnGround(false);
                        hurt.setDeltaMovement(hurt.getDeltaMovement().add(
                                -(livingEntity.getX() - hurt.getX()) * d3 * base,
                                -(livingEntity.getY() - hurt.getY()) * d3 * base + 0.125f * d3 * base,
                                -(livingEntity.getZ() - hurt.getZ()) * d3 * base
                        ));
                    }
                }
            }

            Vec3 lookDir = livingEntity.getLookAngle().normalize();
            Vec3 entityPos = livingEntity.position().add(lookDir.scale(0.35));
            double radius = 1.325;
            int particles = 80;
            double heightOffset = livingEntity.getY(0.8) - livingEntity.getY();

            float yaw = livingEntity.getYRot();
            float pitch = livingEntity.getXRot();
            for (int i = 0; i < particles; i++) {
                double angle = Math.toRadians(-90 + (i * 180.0 / particles));
                double zOffset = radius * Math.cos(angle);
                double xOffset = radius * Math.sin(angle);
                double cosYaw = Math.cos(Math.toRadians(yaw));
                double sinYaw = Math.sin(Math.toRadians(yaw));
                double rotatedX = xOffset * cosYaw - zOffset * sinYaw;
                double rotatedZ = xOffset * sinYaw + zOffset * cosYaw;
                double finalYOffset = heightOffset + (Math.sin(Math.toRadians(-pitch)) * zOffset);
                Vec3 particlePos = entityPos.add(rotatedX, finalYOffset, rotatedZ);
                double speedX = rotatedX * 0.07 + (Math.random() * 0.02 - 0.01);
                double speedY = lookDir.y * 0.07 + (Math.random() * 0.02 - 0.01);
                double speedZ = rotatedZ * 0.07 + (Math.random() * 0.02 - 0.01);

                serverLevel.sendParticles(ParticleTypes.CRIT,
                        particlePos.x, particlePos.y, particlePos.z,
                        0, speedX, speedY, speedZ, 1);
            }
        }
    }

    @Override
    public void specialInHandLayer(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            if (entityModel.attackTime > 0.0 && livingEntity.getMainArm() == humanoidArm) {
                ItemToolBasePike.thirdPersonAttackItem(entityModel.attackTime, poseStack, livingEntity);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 40;
    }

    public void jerotesSpecialAttackClient(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            player.resetAttackStrengthTicker();
            ItemToolBaseSpearBase.jerotesLungeForwardMaybe(player);
            this.makeSound(player);
        }
    }
    public void jerotesSpecialAttack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        attack(livingEntity, equipmentSlot);
    }
    public boolean jerotesSpecialAttackNeed(LivingEntity livingEntity) {
        return !(livingEntity instanceof Player player && player.getAttackStrengthScale(5F) < 1) && livingEntity.getOffhandItem().isEmpty();
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() instanceof ItemToolBasePike) {
            if (enchantment == Enchantments.FIRE_ASPECT ||
                    enchantment == Enchantments.KNOCKBACK ||
                    enchantment == Enchantments.MOB_LOOTING ||
                    enchantment instanceof DamageEnchantment) {
                return true;
            }
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public void attack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        float f = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null ? (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0f;
        boolean bl = false;
        int order = 0;
        for (EntityHitResult entityHitResult : getHitEntitiesAlong(livingEntity, this, hitboxMargin, entity -> ItemToolBaseSpearBase.canHitEntity(livingEntity, entity))) {
            bl |= stabAttack(equipmentSlot, entityHitResult.getEntity(), f, true, true, true, livingEntity, order);
            order ++;
        }
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.resetAttackStrengthTicker();
        }
        if (!(livingEntity instanceof Player && livingEntity.level().isClientSide())) {
            ItemToolBaseSpearBase.jerotesLungeForwardMaybe(livingEntity);
        }
        if (bl) {
            this.makeHitSound(livingEntity);
        }
        this.makeSound(livingEntity);
       // livingEntity.swing(InteractionHand.MAIN_HAND, false);
    }
    public static void animate(ModelPart p_102087_, ModelPart p_102088_, LivingEntity p_102089_, boolean p_102090_) {
        ModelPart $$4 = p_102090_ ? p_102087_ : p_102088_;
        ModelPart $$5 = p_102090_ ? p_102088_ : p_102087_;
        $$4.yRot = p_102090_ ? -0.8F : 0.8F;
        $$4.xRot = -0.97079635F;
        $$5.xRot = $$4.xRot;
        float $$8 = 0.5f;
        $$5.yRot = Mth.lerp($$8, 0.4F, 0.85F) * (float)(p_102090_ ? 1 : -1);
        $$5.xRot = Mth.lerp($$8, $$5.xRot, -1.5707964F);
    }
    public static <T extends HumanoidModel<?>> void animateAttack(HumanoidModel<?> humanoidModel, T t, LivingEntity livingEntity) {
        float f = livingEntity.getAttackAnim(Minecraft.getInstance().getPartialTick());
        float f2 = Ease.inOutSine(SpearAnimations.progress(f, 0.0f, 0.05f));
        float f3 = Ease.inQuad(SpearAnimations.progress(f, 0.05f, 0.2f));
        float f4 = Ease.inOutExpo(SpearAnimations.progress(f, 0.4f, 1.0f));
        {
            HumanoidArm humanoidArms = livingEntity.getMainArm();
            HumanoidArm humanoidArm = livingEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidArms : humanoidArms.getOpposite();
            humanoidModel.rightArm.yRot -= humanoidModel.body.yRot;
            humanoidModel.leftArm.yRot -= humanoidModel.body.yRot;
            SpearAnimations.getArm(humanoidModel, (HumanoidArm)humanoidArm).xRot += (90.0f * f2 - 120.0f * f3 + 30.0f * f4) * ((float)Math.PI / 180);
        }
        {
            HumanoidArm humanoidArms = livingEntity.getMainArm().getOpposite();
            HumanoidArm humanoidArm = livingEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidArms : humanoidArms.getOpposite();
            humanoidModel.rightArm.yRot -= humanoidModel.body.yRot;
            humanoidModel.leftArm.yRot -= humanoidModel.body.yRot;
            SpearAnimations.getArm(humanoidModel, (HumanoidArm)humanoidArm).xRot += (90.0f * f2 - 120.0f * f3 + 30.0f * f4) * ((float)Math.PI / 180) * 0.2f;
        }
    }
    public static void thirdPersonAttackItem(float f, PoseStack poseStack, LivingEntity livingEntity) {
        if (f <= 0.0f) {
            return;
        }
        //后拉
        if (f > 0 && f <= 0.05) {
            float fs = f / 0.05f;
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0f * fs));
        }
        //前刺
        else if (f > 0.05 && f <= 0.2) {
            float fs = (f - 0.05f) / 0.2f;
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0f - 40f * fs));
        }
        else if (f > 0.2 && f <= 0.4) {
            float fs = 1;
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0f - 40f * fs));
        }
        //收回
        else if (f > 0.4) {
            float fs = 1 - (f - 0.4f) / 0.6f;
            poseStack.mulPose(Axis.XP.rotationDegrees(-20.0f * fs));
        }
    }

    public boolean stabAttack(EquipmentSlot equipmentSlot, Entity entity, float f, boolean bl, boolean bl2, boolean bl3, LivingEntity self, int order) {

        //相对速度
        Vec3 attackerVelocity = getMotion(self).multiply(selfSpeedDamage, selfSpeedDamage, selfSpeedDamage);
        Vec3 targetVelocity = getMotion(entity).multiply(targetSpeedDamage, targetSpeedDamage, targetSpeedDamage);
        Vec3 relativeVelocity = attackerVelocity.subtract(targetVelocity);
        double relativeSpeed = relativeVelocity.length();
        //计算攻击方向上的相对速度分量（用于攻击加成）
        Vec3 attackDirection = self.getLookAngle().normalize();
        double attackRelativeSpeed = relativeVelocity.dot(attackDirection);

        //计算防御方向上的相对速度分量（用于反作用力）
        Vec3 defenseDirection = attackDirection.reverse(); //攻击的反方向
        double defenseRelativeSpeed = relativeVelocity.dot(defenseDirection);

        if (!(self.level() instanceof ServerLevel)) {
            return false;
        }
        ItemStack equipment = self.getItemBySlot(equipmentSlot);
        //攻击
        DamageSource damageSource = new DamageSource(self.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.SPEAR), self);
        float f2 = f;
        if (entity instanceof LivingEntity livingEntity) {
            f2 += EnchantmentHelper.getDamageBonus(equipment, livingEntity.getMobType());
        }
        double speedDamageMultiplier = Math.min(16.0, attackRelativeSpeed / 7.5);
        f2 *= (float) (1.0f + speedDamageMultiplier * 0.5f);
        Vec3 vec3 = entity.getDeltaMovement();
        boolean bl4 = bl2;
        boolean bl5 = bl && entity.hurt(damageSource, f2);

        if (bl5 && entity instanceof Player player && self instanceof TestEntity testEntity) {
            player.sendSystemMessage(Component.literal("Attack Base" + (float) (1.0f + speedDamageMultiplier * 0.5f)).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("Attack Relative Speed" + attackRelativeSpeed).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("Defense Relative Speed" + defenseRelativeSpeed).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("Distance" + player.distanceTo(testEntity)).withStyle(ChatFormatting.RED));
        }
        if (bl5 && entity instanceof TestEntity testEntity && self instanceof Player player) {
            player.sendSystemMessage(Component.literal("Attack Base" + (float) (1.0f + speedDamageMultiplier * 0.5f)).withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("Attack Relative Speed" + attackRelativeSpeed).withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("Defense Relative Speed" + defenseRelativeSpeed).withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("Distance" + player.distanceTo(testEntity)).withStyle(ChatFormatting.GREEN));
        }

        bl4 |= bl5;
        //旧速度
        Vec3 oldTarget = entity.getDeltaMovement();
        Vec3 oldSelf = self.getDeltaMovement();
        if (bl5) {
            if (entity instanceof LivingEntity livingEntity) {
                EnchantmentHelper.doPostHurtEffects(livingEntity, self);
            }
            EnchantmentHelper.doPostDamageEffects( self, entity);
            AttackFind.attackBegin(self, entity);
        }

        if (attackRelativeSpeed > 4) {
            entity.setSprinting(false);
            self.setSprinting(false);
            //下马
            if (bl3 && entity.isPassenger() && entity.getVehicle() != null && !(entity instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(self, livingEntity))) {
                bl4 = true;
                entity.getVehicle().setSprinting(false);
                entity.stopRiding();
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (self instanceof Player player) {
                equipment.hurtEnemy(livingEntity, player);
            }
        }
        //修改实体速度
        //目标减速更多，根据相对速度调整减速系数
        //相对速度越大，目标减速越多
        double targetSlowdown = Math.max(0.3, 0.6 - attackRelativeSpeed * 0.02);
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(targetSlowdown, targetSlowdown, targetSlowdown));
        if (attackRelativeSpeed > 10 && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, false, false));
        }
        //击退
        if (bl2) {
            //基于相对速度的击退
            //基础击退 + 相对速度带来的额外击退
            float baseKnockback = 0.4f + getKnockback(self, entity, damageSource);
            //每10m/s增加1.0击退
            float speedKnockback = (float)(attackRelativeSpeed * 0.1);
            //最大击退2.0
            float totalKnockback = Math.min(baseKnockback + speedKnockback, 2.0f);

            causeExtraKnockback(self, entity, totalKnockback, oldTarget);
        }

        //攻击者的反作用力
        if (attackRelativeSpeed > 0) {
            //基础反作用力
            double recoil = defenseRelativeSpeed * 0.25;
            //如果武器损坏，反作用力增加
            if (equipment.isDamageableItem() && equipment.getDamageValue() > equipment.getMaxDamage() * 0.8) {
                recoil *= 1.5;
            }
            recoil *= 1 - Math.min(0.9, order * 0.15);
            //反作用力 攻击反方向 轻微的水平减速，保持垂直运动
            recoil = Math.min(recoil, 1.0);
            Vec3 recoilVector = defenseDirection.scale(-recoil * 0.25);
            self.hurtMarked = true;
            self.setOnGround(false);
            self.setDeltaMovement(self.getDeltaMovement().multiply(1f - recoil, 1f - recoil, 1f - recoil).
                    add(recoilVector).multiply(0.9, 0.3, 0.9));
        }
        //如果目标速度远大于攻击者速度，攻击者会受到推力
        if (oldTarget.length() > oldSelf.length() * 8) {
            double pushFactor = Math.min(0.15, (oldTarget.length() - oldSelf.length() * 8) * 0.01);
            self.hurtMarked = true;
            self.setOnGround(false);
            self.setDeltaMovement(self.getDeltaMovement().add(oldTarget.normalize().scale(pushFactor)));
        }

        if (!bl4) {
            return false;
        }
        self.setLastHurtMob(entity);
        if (self instanceof JerotesEntity && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && self.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK) || self instanceof Player) {
            if (equipment.isDamageableItem()) {
                //根据相对速度调整耐久消耗
                int durabilityLoss = 1;
                if (attackRelativeSpeed > 10) {
                    durabilityLoss = (int) attackRelativeSpeed;
                }
                equipment.hurtAndBreak(durabilityLoss, self,
                        e -> self.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
        }
        //self.playAttackSound();
        return true;
    }
    public static Vec3 getKnownMovement(Entity entity) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            return serverPlayer.jerotesGetKnownMovement();
        }
        LivingEntity livingEntity = entity.getControllingPassenger();
        if (livingEntity instanceof Player player) {
            if (entity.isAlive()) {
                return getKnownMovement(player);
            }
        }
        return entity.getDeltaMovement();
    }
    public static Vec3 getMotion(Entity entity) {
        if (!(entity instanceof Player) && !(entity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.usePikeAsPlayer()) && !(entity instanceof UseSpearSpecialEntity useSpearSpecialEntity && useSpearSpecialEntity.isJerotesSpearGetMotionLikePlayer()) && entity.isPassenger()) {
            entity = entity.getRootVehicle();
        }

        Vec3 vec3 = getKnownMovement(entity).scale(20.0);
        if (entity.onGround()) {
            vec3 = vec3.multiply(0.6, 0, 0.6);
        }
        double mass = 1.0;
        if (entity instanceof LivingEntity) {
            if (Main.mobSizeGiant(entity)) {
                mass = 4;
            }
            if (Main.mobSizeLarge(entity)) {
                mass = 2;
            }
            if (Main.mobSizeMedium(entity)) {
                mass = 1;
            }
            if (Main.mobSizeSmall(entity)) {
                mass = 0.2;
            }
            if (EntityAndItemFind.isNoSpecialKnockback(entity.getType())) {
                mass = 5;
            }
        }
        return vec3.scale(mass);
    }
    public static void causeExtraKnockback(LivingEntity self, Entity entity, float f, Vec3 vec3) {
        if (self instanceof Player || self instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.usePikeAsPlayer() || self instanceof UseSpearSpecialEntity useSpearSpecialEntity && useSpearSpecialEntity.isJerotesSpearCauseExtraKnockbackPlayer()) {
            if (f > 0.0f) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.knockback(f, Mth.sin(self.getYRot() * ((float)Math.PI / 180)), -Mth.cos(self.getYRot() * ((float)Math.PI / 180)));
                    entity.push(0, 0.05, 0);
                } else {
                    entity.push(-Mth.sin(self.getYRot() * ((float)Math.PI / 180)) * f, 0.1, Mth.cos(self.getYRot() * ((float)Math.PI / 180)) * f);
                }
                self.setDeltaMovement(self.getDeltaMovement().multiply(0.8, 1.0, 0.8));
                self.setSprinting(false);
            }
            if (entity instanceof ServerPlayer serverPlayer && entity.hurtMarked) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
                entity.hurtMarked = false;
                entity.setDeltaMovement(vec3);
            }
        }
        else {
            if (f > 0.0f && entity instanceof LivingEntity livingEntity) {
                livingEntity.knockback(f, Mth.sin(self.getYRot() * ((float) Math.PI / 180)), -Mth.cos(self.getYRot() * ((float) Math.PI / 180)));
                self.setDeltaMovement(self.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
        }
    }
    protected float getKnockback(LivingEntity self, Entity entity, DamageSource damageSource) {
        float f = (float)self.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 1;
        Level level = self.level();
        if (level instanceof ServerLevel serverLevel) {
            return self.getMainHandItem().getEnchantmentLevel(Enchantments.KNOCKBACK);
        }
        return f;
    }

    public static Collection<EntityHitResult> getHitEntitiesAlong(LivingEntity livingEntity, ItemToolBasePike itemToolBasePike, float f3, Predicate<Entity> predicate) {
        Vec3 vec3 = ItemToolBaseSpearBase.getHeadLookAngle(livingEntity);
        Vec3 vec32 = livingEntity.getEyePosition();
        Vec3 vec33 = vec32.add(vec3.scale(itemToolBasePike.effectiveMinRange(livingEntity)));
        double d = ItemToolBaseSpearBase.getKnownMovement(livingEntity).dot(vec3);
        Vec3 vec34 = vec32.add(vec3.scale((double)itemToolBasePike.effectiveMaxRange(livingEntity) + Math.max(0.0, d)));
        return getHitEntitiesAlong(vec33, livingEntity, itemToolBasePike, predicate, vec34, f3, ClipContext.Block.COLLIDER, itemToolBasePike.maxRange/2).map(blockHitResult -> List.of(), collection -> collection);
    }
    public static Either<BlockHitResult, Collection<EntityHitResult>> getHitEntitiesAlong(Vec3 vec3, Entity entity, ItemToolBasePike itemToolBasePike, Predicate<Entity> predicate,
                                                                                          Vec3 vec32, float f, ClipContext.Block block, float maxAdd) {
        AABB aABB;
        Collection<EntityHitResult> collection;
        Level level = entity.level();
        BlockHitResult blockHitResult = ItemToolBaseSpearBase.clipIncludingBorder(new ClipContext(vec3, vec32, block, ClipContext.Fluid.NONE, entity), level);
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            vec32 = blockHitResult.getLocation();
        }
        if (!(collection = getManyEntityHitResult(level, entity, itemToolBasePike, vec3, vec32, aABB = AABB.ofSize(vec3, f, f, f).expandTowards(vec32.subtract(vec3)).inflate(1.0), predicate, f, maxAdd)).isEmpty()) {
            return Either.right(collection);
        }
        return Either.left(blockHitResult);
    }
    public static Collection<EntityHitResult> getManyEntityHitResult(Level level, Entity entity, ItemToolBasePike itemToolBasePike, Vec3 vec32, Vec3 vec33, AABB aABB, Predicate<Entity> predicate, float f, float maxAdd) {
        List<EntityHitResult> arrayList = new ArrayList<>();
        if (level == null || level.isClientSide() || entity == null || vec32 == null || vec33 == null) {
            return arrayList;
        }
        List<Entity> entities;
        try {
            entities = new ArrayList<>(level.getEntities(entity, aABB.inflate(maxAdd), predicate));
        } catch (Exception e) {
            return arrayList;
        }
        for (Entity entity2 : entities) {
            if (entity2 == null || entity2.isRemoved() || !entity2.isAlive()) {
                continue;
            }
            try {
                //相对速度
                Vec3 attackerVelocity = getMotion(entity).multiply(itemToolBasePike.selfSpeedDamage, itemToolBasePike.selfSpeedDamage, itemToolBasePike.selfSpeedDamage);
                Vec3 targetVelocity = getMotion(entity2).multiply(itemToolBasePike.targetSpeedDamage, itemToolBasePike.targetSpeedDamage, itemToolBasePike.targetSpeedDamage);
                Vec3 relativeVelocity = attackerVelocity.subtract(targetVelocity);
                Vec3 attackDirection = entity.getLookAngle().normalize();
                double attackRelativeSpeed = relativeVelocity.dot(attackDirection);


                //是否能触及
                boolean canAttack = level.getEntities(entity, aABB.inflate(attackRelativeSpeed), predicate).contains(entity2);

                if (!canAttack)
                    continue;
                AABB aABB2 = entity2.getBoundingBox().inflate(f);
                if (aABB2.contains(vec32)) {
                    arrayList.add(new EntityHitResult(entity2, vec32));
                    continue;
                }
                Optional<Vec3> optional = aABB2.clip(vec32, vec33);
                optional.ifPresent(vec3 -> arrayList.add(new EntityHitResult(entity2, vec3)));
            } catch (Exception e) {
                // log.debug("Error processing entity in getManyEntityHitResult", e);
            }
        }
        return arrayList;
    }
    public float effectiveMinRange(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.isSpectator()) {
                return 0.0f;
            }
            return player.isCreative() ? this.minCreativeRange : this.minRange;
        }
        if (entity instanceof Mob mob && entity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.usePikeAsPlayer()) {
            if (mob.isSpectator()) {
                return 0.0f;
            }
            return jerotesPlayerBaseEntity.getAbilities() != null && jerotesPlayerBaseEntity.getAbilities().instabuild ? this.minCreativeRange : this.minRange;
        }
        return this.minRange * this.mobFactor;
    }
    public float effectiveMaxRange(Entity entity) {
        if (entity instanceof Player player) {
            return (player.isCreative() ? this.maxCreativeRange : this.maxRange) + (player.getAttribute(ForgeMod.ENTITY_REACH.get()) != null ? (float) Math.max(0, player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3): 0);
        }
        if (entity instanceof Mob mob && entity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.usePikeAsPlayer()) {
            return (jerotesPlayerBaseEntity.getAbilities() != null && jerotesPlayerBaseEntity.getAbilities().instabuild ? this.maxCreativeRange : this.maxRange) +
                    ((mob.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, mob.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0);
        }
        return this.maxRange * this.mobFactor + ((entity instanceof LivingEntity livingEntity && livingEntity.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, livingEntity.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0);
    }

  @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new PikeClient());
    }
    public static class PikeClient implements IClientItemExtensions{
        public static final HumanoidModel.ArmPose PIKE_POSE = HumanoidModel.ArmPose.create("JEROTES_PIKE", true, (model, entity, arm) -> {});
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            if (stack.getItem() instanceof ItemToolBaseSpearBase) {
                return PIKE_POSE;
            }
            return HumanoidModel.ArmPose.EMPTY;
        }

        private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equipProcess) {
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            poseStack.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProcess * -0.6F), (double)-0.72F);
        }

        //第一人称
        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                               ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            int dir = arm == HumanoidArm.RIGHT ? 1 : -1;

            if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                this.applyItemArmTransform(poseStack, arm, 0);
                float f = 1 - Math.min(25, player.getTicksUsingItem()) / 25f;
                if (f > 0 && f <= 0.40) {
                    float fs = f / 0.40f;
                    poseStack.mulPose(Axis.YP.rotationDegrees(-40.0f * fs * dir));
                }
                else if (f > 0.40 && f <= 0.5) {
                    float fs = (f - 0.40f) / 0.10f;
                    poseStack.mulPose(Axis.YP.rotationDegrees(-40.0f * dir + 80f * fs * dir));
                }
                else if (f > 0.5) {
                    float fs = 1 - (f - 0.5f) / 0.5f;
                    poseStack.mulPose(Axis.YP.rotationDegrees(40.0f * dir * fs * dir));
                }
            }
            else if (swingProcess > 0.0F && player.getOffhandItem().isEmpty()) {
                this.applyItemArmTransform(poseStack, arm, 0);
                firstPersonAttack(swingProcess, poseStack, dir, arm);
                return true;
            }
            else {
                this.applyItemArmTransform(poseStack, arm, equipProcess);
            }
            return true;
        }
    }

    public static void firstPersonAttack(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm) {
        //后拉
        if (f > 0 && f <= 0.05) {
            float fs = f / 0.05f;
            poseStack.mulPose(Axis.XP.rotationDegrees(-20.0f * fs));
            poseStack.translate(0, 0, 6 * fs);
        }
        //前刺
        else if (f > 0.05 && f <= 0.15) {
            float fs = (f - 0.05f) / 0.1f;
            poseStack.mulPose(Axis.XP.rotationDegrees(-20.0f + 40 * fs));
            poseStack.translate(0, 0, 6 - 7 * fs);
        }
        else if (f > 0.15 && f <= 0.5) {
            float fs = (f - 0.15f) / 0.35f;
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0f - 10 * fs));
            poseStack.translate(0, 0, -1 + 0.25 * fs);
        }
        //收回
        else if (f > 0.5) {
            float fs = 1 - (f - 0.5f) / 0.5f;
            poseStack.mulPose(Axis.XP.rotationDegrees(10.0f * fs));
            poseStack.translate(0, 0, -0.75 * fs);
        }
    }

    @Override
    public int swingTimes() {
        float f = 20.0f * this.swingTimes;
        return (int)f;
    }
}

