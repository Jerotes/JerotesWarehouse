package com.jerotes.jerotes.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.entity.*;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.item.ItemSpecialAttack;
import com.jerotes.jerotes.item.ItemSpecialEffect;
import com.jerotes.jerotes.item.ItemSpecialInHand;
import com.jerotes.jerotes.item.MeleeItem;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemToolBaseSpearBase extends TieredItem implements ItemSpecialEffect, MeleeItem, Vanishable, ItemSpecialAttack, ItemSpecialInHand {
    public final float swingTimes;
    public final float hitboxMargin;
    public final int contactCooldownTicks;
    public final int delayTicks;
    public final Optional<Condition> dismountConditions;
    public final Optional<Condition> knockbackConditions;
    public final Optional<Condition> damageConditions;
    public final float forwardMovement;
    public final float damageMultiplier;
    public final SoundEvent sound;
    public final SoundEvent hitSound;
    public final SoundEvent sound2;
    public final SoundEvent hitSound2;
    public final float minRange;
    public final float maxRange;
    public final float mobFactor;
    public final float minCreativeRange;
    public final float maxCreativeRange;
    public final float hitboxMargin2;
    public final boolean dealsKnockback;
    public final boolean dismounts;
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ItemToolBaseSpearBase(Tier tier, Properties properties, float damage, float speed, float swingTimes, float hitboxMargin, int contactCooldownTicks, int delayTicks, Optional<Condition> dismountConditions, Optional<Condition> knockbackConditions, Optional<Condition> damageConditions, float forwardMovement, float damageMultiplier, SoundEvent sound, SoundEvent hitSound, SoundEvent sound2, SoundEvent hitSound2,
                                 float minRange, float maxRange, float minCreativeRange, float maxCreativeRange, float hitboxMargin2, float mobFactor, boolean dealsKnockback, boolean dismounts) {
        super(tier, properties.defaultDurability(tier.getUses()));
        this.swingTimes = swingTimes;
        this.hitboxMargin = hitboxMargin;
        this.contactCooldownTicks = contactCooldownTicks;
        this.delayTicks = delayTicks;
        this.dismountConditions = dismountConditions;
        this.knockbackConditions = knockbackConditions;
        this.damageConditions = damageConditions;
        this.forwardMovement = forwardMovement;
        this.damageMultiplier = damageMultiplier;
        this.sound = sound;
        this.hitSound = hitSound;
        this.sound2 = sound2;
        this.hitSound2 = hitSound2;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.minCreativeRange = minCreativeRange;
        this.maxCreativeRange = maxCreativeRange;
        this.hitboxMargin2 = hitboxMargin2;
        this.mobFactor = mobFactor;
        this.dealsKnockback = dealsKnockback;
        this.dismounts = dismounts;
        this.attackDamage = (float)damage + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", speed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
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
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        makeSound(player);
        return InteractionResultHolder.consume(itemStack);
    }
    public void onUseTickSpecial(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
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
        if (!(entity instanceof Player) && !(entity instanceof UseSpearSpecialEntity useSpearSpecialEntity && useSpearSpecialEntity.isGetMotionLikePlayer()) && entity.isPassenger()) {
            entity = entity.getRootVehicle();
        }
        Vec3 vec3 = getKnownMovement(entity).scale(20.0);
        if (entity.onGround()) {
            return vec3.with(Direction.Axis.Y, 0.0);
        }
        return vec3;
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return false;
    }
    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public void makeHitSound(Entity entity) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                hitSound, entity.getSoundSource(), 1.0f, 1.0f);
    }
    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public void makeHitSound2(Entity entity) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                hitSound2, entity.getSoundSource(), 1.0f, 1.0f);
    }

    @OnlyIn(Dist.CLIENT)
    public void makeLocalHitSound(Entity entity) {
        Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(hitSound, entity.getSoundSource(), 1.0f, 1.0f, entity, RandomSource.create().nextLong()));;
    }

    public int computeDamageUseDuration() {
        return this.delayTicks + this.damageConditions.map(Condition::maxDurationTicks).orElse(0);
    }

    public void damageEntities(ItemStack itemStack, int n, LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        int n2 = itemStack.getUseDuration() - n;
        if (n2 < this.delayTicks) {
            return;
        }
        n2 -= this.delayTicks;
        Vec3 vec3 = livingEntity.getLookAngle();
        double d = vec3.dot(getMotion(livingEntity));
        float f = livingEntity instanceof Player ? 1.0f : 0.2f;
        f = livingEntity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useSpearAsPlayer() ? 0.4f : f;
        //float f2 = livingEntity instanceof Player ? 1.0f : 0.5f;
        if (livingEntity instanceof UseSpearSpecialEntity useSpearSpecialEntity) {
            f = useSpearSpecialEntity.getNeedSpeed();
           // f2 = useSpearSpecialEntity.getNeedReach();
        }
        boolean bl = false;
        for (EntityHitResult entityHitResult : getHitEntitiesAlong(livingEntity,
                this,
                this.hitboxMargin, entity -> canHitEntity(livingEntity, entity))) {
            Entity entity2 = entityHitResult.getEntity();
            if (entity2 instanceof PartEntity<?> partEntity) {
                entity2 = partEntity.getParent();
            }
            if (entity2 instanceof LivingEntity living && living instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity &&
                    jerotesChangeLivingEntity.wasRecentlyStabbedJerotes(entity2, this.contactCooldownTicks)) {
                continue;
            }
            double d2 = vec3.dot(getMotion(entity2));
            double d3 = Math.max(0.0, d - d2);
            if (livingEntity instanceof UseSpearSpecialEntity useSpearSpecialEntity) {
                d3 *= useSpearSpecialEntity.getDamageMultiple();
            }
            boolean bl2 = this.dismountConditions.isPresent() && this.dismountConditions.get().test(n2, d, d3, f);
            boolean bl3 = this.knockbackConditions.isPresent() && this.knockbackConditions.get().test(n2, d, d3, f);
            boolean bl4 = this.damageConditions.isPresent() && this.damageConditions.get().test(n2, d, d3, f);
            if (livingEntity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity) {
                jerotesChangeLivingEntity.rememberStabbedEntityJerotes(entity2);
            }
            float fs = (float)d2 + (float)Mth.floor(d3 * (double)this.damageMultiplier);
            bl |= stabAttack(equipmentSlot, entity2, fs, bl4, bl3, bl2, livingEntity);
        }
        if (bl) {
            livingEntity.level().broadcastEntityEvent(livingEntity, (byte)2);
            //this.makeHitSound(livingEntity);
        }
    }

    public boolean stabAttack(EquipmentSlot equipmentSlot, Entity entity, float f, boolean bl, boolean bl2, boolean bl3, LivingEntity self) {
        Object object = self.level();
        if (!(object instanceof ServerLevel serverLevel)) {
            return false;
        }
        object = self.getItemBySlot(equipmentSlot);
        DamageSource damageSource = new DamageSource(self.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.SPEAR), self);
        float f2 = f;
        if (entity instanceof LivingEntity livingEntity) {
            f2 += EnchantmentHelper.getDamageBonus((ItemStack)object, livingEntity.getMobType());
        }
        Vec3 vec3 = entity.getDeltaMovement();
        boolean bl4 = bl2;
        boolean bl5 = bl && entity.hurt(damageSource, f2);
        bl4 |= bl5;
        if (bl2) {
            causeExtraKnockback(self, entity, 0.4f + getKnockback(self, entity, damageSource), vec3);
        }
        if (bl3 && entity.isPassenger() && !(entity instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(self, livingEntity))) {
            bl4 = true;
            entity.stopRiding();
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (self instanceof Player player) {
                ((ItemStack) object).hurtEnemy(livingEntity, player);
            }
        }
        if (bl5) {
            if (entity instanceof LivingEntity livingEntity) {
                EnchantmentHelper.doPostHurtEffects(livingEntity, self);
            }
            EnchantmentHelper.doPostDamageEffects( self, entity);
            AttackFind.attackBegin(self, entity);
        }
        if (!bl4) {
            return false;
        }
        self.setLastHurtMob(entity);
        if (self instanceof JerotesEntity && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && self.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK) || self instanceof Player) {
            if (self.getItemBySlot(equipmentSlot).isDamageableItem()) {
                self.getItemBySlot(equipmentSlot).hurtAndBreak(1, self,
                        e -> self.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
        }
        //self.playAttackSound();
        return true;
    }
    public static void causeExtraKnockback(LivingEntity self, Entity entity, float f, Vec3 vec3) {
        if (self instanceof Player || self instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useSpearAsPlayer() || self instanceof UseSpearSpecialEntity useSpearSpecialEntity && useSpearSpecialEntity.isCauseExtraKnockbackPlayer()) {
            if (f > 0.0f) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.knockback(f, Mth.sin(self.getYRot() * ((float)Math.PI / 180)), -Mth.cos(self.getYRot() * ((float)Math.PI / 180)));
                } else {
                    entity.push(-Mth.sin(self.getYRot() * ((float)Math.PI / 180)) * f, 0.1, Mth.cos(self.getYRot() * ((float)Math.PI / 180)) * f);
                }
                self.setDeltaMovement(self.getDeltaMovement().multiply(0.6, 1.0, 0.6));
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
        float f = (float)self.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        Level level = self.level();
        if (level instanceof ServerLevel serverLevel) {
            return self.getMainHandItem().getEnchantmentLevel(Enchantments.KNOCKBACK) / 2f;
        }
        return f / 2.0f;
    }
    public static Collection<EntityHitResult> getHitEntitiesAlong(LivingEntity livingEntity, ItemToolBaseSpearBase itemToolBaseSpearBase, float f3, Predicate<Entity> predicate) {
        Vec3 vec3 = getHeadLookAngle(livingEntity);
        Vec3 vec32 = livingEntity.getEyePosition();
        Vec3 vec33 = vec32.add(vec3.scale(itemToolBaseSpearBase.effectiveMinRange(livingEntity)));
        double d = getKnownMovement(livingEntity).dot(vec3);
        Vec3 vec34 = vec32.add(vec3.scale((double)itemToolBaseSpearBase.effectiveMaxRange(livingEntity) + Math.max(0.0, d)));
        return getHitEntitiesAlong(vec33, livingEntity, predicate, vec34, f3, ClipContext.Block.COLLIDER).map(blockHitResult -> List.of(), collection -> collection);
    }
    public static Vec3 getHeadLookAngle(Entity entity) {
        return calculateViewVector(entity.getXRot(), entity.getYHeadRot());
    }

    public static Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * 0.017453292F;
        float f1 = -p_20173_ * 0.017453292F;
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }




    public static Either<BlockHitResult, Collection<EntityHitResult>> getHitEntitiesAlong(Vec3 vec3, Entity entity, Predicate<Entity> predicate, Vec3 vec32, float f, ClipContext.Block block) {
        AABB aABB;
        Collection<EntityHitResult> collection;
        Level level = entity.level();
        BlockHitResult blockHitResult = clipIncludingBorder(new ClipContext(vec3, vec32, block, ClipContext.Fluid.NONE, entity), level);
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            vec32 = blockHitResult.getLocation();
        }
        if (!(collection = getManyEntityHitResult(level, entity, vec3, vec32, aABB = AABB.ofSize(vec3, f, f, f).expandTowards(vec32.subtract(vec3)).inflate(1.0), predicate, f)).isEmpty()) {
            return Either.right(collection);
        }
        return Either.left(blockHitResult);
    }
    public static Collection<EntityHitResult> getManyEntityHitResult(Level level, Entity entity, Vec3 vec32, Vec3 vec33, AABB aABB, Predicate<Entity> predicate, float f) {
        List<EntityHitResult> arrayList = new ArrayList<>();

        if (level == null || level.isClientSide() || entity == null || vec32 == null || vec33 == null) {
            return arrayList;
        }

        List<Entity> entities;
        try {
            entities = new ArrayList<>(level.getEntities(entity, aABB, predicate));
        } catch (Exception e) {
            return arrayList;
        }

        for (Entity entity2 : entities) {
            if (entity2 == null || entity2.isRemoved() || !entity2.isAlive()) {
                continue;
            }

            try {
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
    public static BlockHitResult clipIncludingBorder(ClipContext clipContext, Level level) {
        BlockHitResult blockHitResult = level.clip(clipContext);
        WorldBorder worldBorder = level.getWorldBorder();
        if (worldBorder.isWithinBounds(BlockPos.containing(clipContext.getFrom())) && !worldBorder.isWithinBounds(BlockPos.containing(blockHitResult.getLocation()))) {
            Vec3 vec3 = blockHitResult.getLocation().subtract(clipContext.getFrom());
            Direction direction = getApproximateNearest(vec3.x, vec3.y, vec3.z);
            Vec3 vec32 = clampVec3ToBound(blockHitResult.getLocation(), worldBorder);
            return new BlockHitResult(vec32, direction, BlockPos.containing(vec32), false);
        }
        return blockHitResult;
    }

    public static Vec3 clampVec3ToBound(Vec3 vec3, WorldBorder worldBorder) {
        return clampVec3ToBound(vec3.x, vec3.y, vec3.z, worldBorder);
    }

    public static Vec3 clampVec3ToBound(double d, double d2, double d3, WorldBorder worldBorder) {
        return new Vec3(Mth.clamp(d, worldBorder.getMinX(), worldBorder.getMaxX() - (double)1.0E-5f), d2, Mth.clamp(d3, worldBorder.getMinZ(), worldBorder.getMaxZ() - (double)1.0E-5f));
    }

    public static Direction getApproximateNearest(double d, double d2, double d3) {
        return getApproximateNearest((float)d, (float)d2, (float)d3);
    }
    public static Direction getApproximateNearest(float f, float f2, float f3) {
        Direction direction = Direction.NORTH;
        float f4 = Float.MIN_VALUE;
        for (Direction direction2 : Direction.values()) {
            float f5 = f * (float)direction2.getStepX() + f2 * (float)direction2.getStepY() + f3 * (float)direction2.getStepZ();
            if (!(f5 > f4)) continue;
            f4 = f5;
            direction = direction2;
        }
        return direction;
    }

    public record Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
      //  public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App) ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_duration_ticks").forGetter(Condition::maxDurationTicks), (App)Codec.FLOAT.optionalFieldOf("min_speed", (Object)Float.valueOf(0.0f)).forGetter(Condition::minSpeed), (App)Codec.FLOAT.optionalFieldOf("min_relative_speed", (Object)Float.valueOf(0.0f)).forGetter(Condition::minRelativeSpeed)).apply((Applicative)instance, Condition::new));
      //  public static final StreamCodec<ByteBuf, Condition> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Condition::maxDurationTicks, ByteBufCodecs.FLOAT, Condition::minSpeed, ByteBufCodecs.FLOAT, Condition::minRelativeSpeed, Condition::new);

        public boolean test(int n, double d, double d2, double d3) {
            return n <= this.maxDurationTicks && d >= (double)this.minSpeed * d3 && d2 >= (double)this.minRelativeSpeed * d3;
        }

        public static Optional<Condition> ofAttackerSpeed(int n, float f) {
            return Optional.of(new Condition(n, f, 0.0f));
        }

        public static Optional<Condition> ofRelativeSpeed(int n, float f) {
            return Optional.of(new Condition(n, 0.0f, f));
        }
    }


    public static boolean canHitEntity(Entity entity, Entity entity2) {
        LivingEntity livingEntity;
        if (!entity2.canBeHitByProjectile()) {
            return false;
        }
        if (entity instanceof LivingEntity living && AttackFind.FindCanNotAttack(living, entity2)) return false;
        if (!Main.hasLineOfSightEntity(entity, entity2)) {
            return false;
        }
        if (entity2 instanceof Player player) {
            livingEntity = (Player)entity2;
            if (entity instanceof Player && !(player = (Player)entity).canHarmPlayer((Player)livingEntity)) {
                return false;
            }
        }
        return !entity.isPassengerOfSameVehicle(entity2);
    }

    public void jerotesSpecialAttackClient(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            player.resetAttackStrengthTicker();
            ItemToolBaseSpearBase.jerotesLungeForwardMaybe(player);
            this.makeSound2(player);
        }
    }
    public void jerotesSpecialAttack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        attack(livingEntity, equipmentSlot);
    }

    public void attack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        float f = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null ? (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0f;
        boolean bl = false;
        for (EntityHitResult entityHitResult : getHitEntitiesAlong(livingEntity, this, this.hitboxMargin2, entity -> canHitEntity(livingEntity, entity))) {
            bl |= stabAttack(equipmentSlot, entityHitResult.getEntity(), f, true, this.dealsKnockback, this.dismounts, livingEntity);
        }
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.resetAttackStrengthTicker();
        }
        if (!(livingEntity instanceof Player && livingEntity.level().isClientSide())) {
            jerotesLungeForwardMaybe(livingEntity);
        }
        if (bl) {
            this.makeHitSound2(livingEntity);
        }
        this.makeSound2(livingEntity);
        livingEntity.swing(InteractionHand.MAIN_HAND, false);
    }

    public float effectiveMinRange(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.isSpectator()) {
                return 0.0f;
            }
            return player.isCreative() ? this.minCreativeRange : this.minRange;
        }
        if (entity instanceof Mob mob && entity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useSpearAsPlayer()) {
            if (mob.isSpectator()) {
                return 0.0f;
            }
            return jerotesPlayerBaseEntity.getAbilities() != null && jerotesPlayerBaseEntity.getAbilities().instabuild ? this.minCreativeRange : this.minRange;
        }
        return this.minRange * this.mobFactor;
    }
    public float effectiveMaxRange(Entity entity) {
        if (entity instanceof Player player) {
            return (player.isCreative() ? this.maxCreativeRange : this.maxRange) + (player.getAttribute(ForgeMod.ENTITY_REACH.get()) != null ? (float) Math.max(0, player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0);
        }
        if (entity instanceof Mob mob && entity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useSpearAsPlayer()) {
            return (jerotesPlayerBaseEntity.getAbilities() != null && jerotesPlayerBaseEntity.getAbilities().instabuild ? this.maxCreativeRange : this.maxRange) +
                    ((mob.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, mob.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0);
        }
        return this.maxRange * this.mobFactor + ((entity instanceof LivingEntity livingEntity && livingEntity.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, livingEntity.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 3) : 0);
    }

    public static void jerotesLungeForwardMaybe(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        jerotesDoLungeEffects(level, livingEntity);
    }
    public static void jerotesDoLungeEffects(Level level, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack mainHandItem = livingEntity.getMainHandItem();
            int lungeLevel = mainHandItem.getEnchantmentLevel(JerotesEnchantments.LUNGE.get());
            if (livingEntity instanceof Player player && player.getFoodData().getFoodLevel() <= 7.0f) {
                return;
            }
            if (lungeLevel > 0) {
                if (livingEntity.isPassenger() ||
                        livingEntity.isFallFlying() ||
                        livingEntity.isInWater()) {
                    return;
                }
                if (entity instanceof JerotesEntity && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && entity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK) || entity instanceof Player) {
                    if (mainHandItem.isDamageableItem()) {
                        mainHandItem.hurtAndBreak(1, livingEntity,
                                e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    }
                }
                //消耗饱食度
                if (livingEntity instanceof Player player) {
                    float exhaustion = 4.0f * lungeLevel;
                    player.causeFoodExhaustion(exhaustion);
                }
                //突进
                float strength = 0.458f * lungeLevel;
                RushAttack(livingEntity, strength);
                //声音
                SoundEvent sound = JerotesSounds.LUNGE_1;
                if (lungeLevel > 1) {
                    sound = JerotesSounds.LUNGE_2;
                }
                if (lungeLevel > 2) {
                    sound = JerotesSounds.LUNGE_3;
                }
                level.playSound(
                        null,
                        livingEntity.getX(),
                        livingEntity.getY(),
                        livingEntity.getZ(),
                        sound,
                        livingEntity.getSoundSource(),
                        1.0F,
                        1.0F
                );
            }
        }
    }

    public static boolean RushAttack(LivingEntity livingEntity, float strength) {
        Vec3 vec3 = livingEntity.getLookAngle();
        livingEntity.push(vec3.x * strength, 0.0, vec3.z * strength);
        return true;
    }
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() instanceof ItemToolBaseSpearBase) {
            if (enchantment == Enchantments.FIRE_ASPECT ||
                    enchantment == Enchantments.KNOCKBACK ||
                    enchantment == Enchantments.MOB_LOOTING ||
                    enchantment instanceof DamageEnchantment) {
                return true;
            }
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }




    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.NONE;
    }

    public boolean otherAnimSpear() {
        return false;
    }

    @Override
    public void specialInHandLayer(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            if (entityModel.attackTime > 0.0 && livingEntity.getMainArm() == humanoidArm) {
                SpearAnimations.thirdPersonAttackItem(entityModel.attackTime, poseStack, livingEntity);
            }
            float f;
            if ((f = livingEntity.getTicksUsingItem()) != 0.0f) {
                if (humanoidArm == HumanoidArm.RIGHT &&
                        (livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntity.getMainArm() == HumanoidArm.RIGHT || livingEntity.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntity.getMainArm() != HumanoidArm.RIGHT)
                ) {
                    SpearAnimations.thirdPersonUseItem(entityModel.attackTime, poseStack, f, humanoidArm, itemStack, livingEntity, Minecraft.getInstance().getPartialTick());
                }
                if (humanoidArm == HumanoidArm.LEFT &&
                        (livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntity.getMainArm() == HumanoidArm.LEFT || livingEntity.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntity.getMainArm() != HumanoidArm.LEFT)
                ) {
                    SpearAnimations.thirdPersonUseItem(entityModel.attackTime, poseStack, f, humanoidArm, itemStack, livingEntity, Minecraft.getInstance().getPartialTick());
                }
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (!otherAnimSpear()) {
            consumer.accept(new SpearClient());
        }
        else {
            super.initializeClient(consumer);
        }
    }

    public static class SpearClient implements IClientItemExtensions{
        public static final HumanoidModel.ArmPose SPEAR_POSE = HumanoidModel.ArmPose.create("JEROTES_SPEAR", false, (model, entity, arm) -> {});
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            if (stack.getItem() instanceof ItemToolBaseSpearBase) {
                //使用哪个手用哪个手抬
                if (entity.isUsingItem()) {
                    if (entity.getUsedItemHand() == hand)
                        return SPEAR_POSE;
                    else return HumanoidModel.ArmPose.EMPTY;
                }
                //主副手矛则为主不抬副台
                if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
                    if (InteractionHand.MAIN_HAND != hand)
                        return SPEAR_POSE;
                    else return HumanoidModel.ArmPose.EMPTY;
                }
                //哪个手矛哪个手抬
                return SPEAR_POSE;
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
            if (player.isUsingItem() && player.getUseItem() == itemInHand) {
                this.applyItemArmTransform(poseStack, arm, 0);
                float useTicks = itemInHand.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
                SpearAnimations.firstPersonUse(player instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity ? jerotesChangeLivingEntity.getTicksSinceLastKineticHitFeedbackJerotes(partialTick) : 0, poseStack, useTicks, arm, itemInHand);
                return true;
            }
            else if (swingProcess > 0.0F) {
                this.applyItemArmTransform(poseStack, arm, 0);
                SpearAnimations.firstPersonAttack(swingProcess, poseStack, dir, arm);
                return true;
            }
            else {
                this.applyItemArmTransform(poseStack, arm, equipProcess);
            }
            return true;
        }
    }

    @Override
    public int swingTimes() {
        float f = 20.0f * this.swingTimes;
        return (int)f;
    }
}