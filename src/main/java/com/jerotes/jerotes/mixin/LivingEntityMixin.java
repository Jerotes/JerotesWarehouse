package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeStray;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Mob.AddHandEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.network.JerotesSpearRushAttackPacket;
import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements JerotesChangeLivingEntity {
    @Unique
    private static final UUID MAX_HEALTH_REDUCE_UUID = UUID.fromString("e0dce003-920a-4d77-a4d0-47bbb9114a82");
    @Unique
    private static final EntityDataAccessor<Float> MAX_HEALTH_REDUCE_COUNT_JEROTES = SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Float> MAX_HEALTH_REDUCE_TICK_JEROTES = SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Boolean> IS_TRUE_INVISIBLE_JEROTES = SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Integer> ANESTHETIZED_TICK_JEROTES = SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.INT);

    protected LivingEntityMixin(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    public void setLastDamageSourceJerotes(DamageSource damageSource) {
        this.lastDamageSource = damageSource;
    }
    public boolean isTrueInvisibleJerotes() {
        return this.getEntityData().get(IS_TRUE_INVISIBLE_JEROTES);
    }
    public void setTrueInvisibleJerotes(boolean bl) {
        this.getEntityData().set(IS_TRUE_INVISIBLE_JEROTES, bl);
    }
    public int getCurrentSwingDurationJerotes() {
        return getCurrentSwingDuration();
    }
    public float getMaxHealthReduceCountJerotes() {
        return this.getEntityData().get(MAX_HEALTH_REDUCE_COUNT_JEROTES);
    }
    public void setMaxHealthReduceCountJerotes(float count) {
        this.getEntityData().set(MAX_HEALTH_REDUCE_COUNT_JEROTES, count);
        this.getEntityData().set(MAX_HEALTH_REDUCE_TICK_JEROTES, 120f);
        applyMaxHealthReductionJerotes(count);
    }
    public float getMaxHealthReduceTickJerotes() {
        return this.getEntityData().get(MAX_HEALTH_REDUCE_TICK_JEROTES);
    }
    public void setMaxHealthReduceTickJerotes(float tick) {
        this.getEntityData().set(MAX_HEALTH_REDUCE_TICK_JEROTES, tick);
    }
    public int getAnesthetizedTickJerotes() {
        return this.getEntityData().get(ANESTHETIZED_TICK_JEROTES);
    }
    public void setAnesthetizedTickJerotes(int tick) {
        this.getEntityData().set(ANESTHETIZED_TICK_JEROTES, tick);
    }


    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);

    @Shadow public abstract int getUseItemRemainingTicks();

    @Shadow protected int useItemRemaining;

    @Shadow protected abstract void completeUsingItem();

    @Shadow public abstract InteractionHand getUsedItemHand();

    @Shadow protected ItemStack useItem;

    @Shadow public abstract ItemStack getMainHandItem();


    @Shadow
    public abstract void setLastHurtByMob(@org.jetbrains.annotations.Nullable LivingEntity p_21039_);

    @Shadow
    public abstract boolean hurt(DamageSource p_21016_, float p_21017_);

    @Shadow
    protected abstract int getCurrentSwingDuration();


    @Inject(method = "spawnSoulSpeedParticle", at = @At("HEAD"), cancellable = true)
    private void spawnSoulSpeedParticle(CallbackInfo ci) {
        if (this.hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get()))
            ci.cancel();
    }

    @Shadow
    @Nullable
    private DamageSource lastDamageSource;
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("IsTrueInvisibleJerotes", this.isTrueInvisibleJerotes());
        compoundTag.putFloat("MaxHealthReduceCountJerotes", this.getMaxHealthReduceCountJerotes());
        compoundTag.putFloat("MaxHealthReduceTickJerotes", this.getMaxHealthReduceTickJerotes());
        compoundTag.putInt("AnesthetizedTickJerotes", this.getAnesthetizedTickJerotes());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setTrueInvisibleJerotes(compoundTag.getBoolean("IsTrueInvisibleJerotes"));
        this.setMaxHealthReduceCountJerotes(compoundTag.getFloat("MaxHealthReduceCountJerotes"));
        this.setMaxHealthReduceTickJerotes(compoundTag.getFloat("MaxHealthReduceTickJerotes"));
        this.setAnesthetizedTickJerotes(compoundTag.getInt("AnesthetizedTickJerotes"));
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(IS_TRUE_INVISIBLE_JEROTES, false);
        this.getEntityData().define(MAX_HEALTH_REDUCE_COUNT_JEROTES, 0f);
        this.getEntityData().define(MAX_HEALTH_REDUCE_TICK_JEROTES, 120f);
        this.getEntityData().define(ANESTHETIZED_TICK_JEROTES, 0);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    protected void tick(CallbackInfo ci) {
        if (this.isInvisible()) {
            boolean trueInvisible = EntityAndItemFind.isTrueInvisibleBuff(this);
            if (entityData.get(IS_TRUE_INVISIBLE_JEROTES) != trueInvisible) {
                if (!this.level().isClientSide())
                    this.entityData.set(IS_TRUE_INVISIBLE_JEROTES, trueInvisible);
            }
        }

        if (this.getMaxHealthReduceCountJerotes() > 0) {
            if (!this.level().isClientSide()) {
                this.setMaxHealthReduceTickJerotes(Math.max(this.getMaxHealthReduceTickJerotes() - 1, 0));
            }
            if (this.getMaxHealthReduceTickJerotes() <= 0) {
                if (!this.level().isClientSide()) {
                    this.setMaxHealthReduceCountJerotes(Math.max(this.getMaxHealthReduceCountJerotes() - 1, 0));
                    this.setMaxHealthReduceTickJerotes(120);
                }
            }
        }
    }
    public void applyMaxHealthReductionJerotes(float count) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        Objects.requireNonNull(livingEntity.getAttribute(Attributes.MAX_HEALTH)).removeModifier(MAX_HEALTH_REDUCE_UUID);
        if (count > 0) {
            Objects.requireNonNull(livingEntity.getAttribute(Attributes.MAX_HEALTH))
                    .addTransientModifier(new AttributeModifier(MAX_HEALTH_REDUCE_UUID, "Max Health Reduction", -count, AttributeModifier.Operation.ADDITION));
        }
    }

    @Inject(method = "setLastHurtByMob", at = @At("HEAD"), cancellable = true)
    public void setLastHurtByMob(LivingEntity livingEntity, CallbackInfo ci) {
        if (livingEntity instanceof AddHandEntity addHandEntity && addHandEntity.getOwner() != null) {
            setLastHurtByMob(addHandEntity.getOwner());
            ci.cancel();
        }
    }

    public boolean canAddPassengerJerotes(Entity entity) {
        return this.canAddPassenger(entity);
    }

    @Unique
    @Nullable
    protected Object2LongMap<Entity> recentKineticEnemiesJerotes;
    public boolean wasRecentlyStabbedJerotes(Entity entity, int n) {
        if (this.recentKineticEnemiesJerotes == null) {
            return false;
        }
        if (this.recentKineticEnemiesJerotes.containsKey((Object)entity)) {
            return this.level().getGameTime() - this.recentKineticEnemiesJerotes.getLong((Object)entity) < n;
        }
        return false;
    }
    public void rememberStabbedEntityJerotes(Entity entity) {
        if (this.recentKineticEnemiesJerotes != null) {
            this.recentKineticEnemiesJerotes.put(entity, this.level().getGameTime());
        }
    }

    @Inject(method = "startUsingItem", at = @At("TAIL"))
    private void startUsingItem(InteractionHand p_21159_, CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            if (this.useItem.getItem() instanceof ItemToolBaseSpearBase) {
                this.recentKineticEnemiesJerotes = new Object2LongOpenHashMap<>();
            }
        }
    }

    private long lastKineticHitFeedbackTimeJerotes = Integer.MIN_VALUE;
    public long getlastKineticHitFeedbackTimeJerotes() {
        return lastKineticHitFeedbackTimeJerotes;
    }
    public float getTicksSinceLastKineticHitFeedbackJerotes(float f) {
        if (this.lastKineticHitFeedbackTimeJerotes < 0L) {
            return 0.0f;
        }
        return (float)(this.level().getGameTime() - this.lastKineticHitFeedbackTimeJerotes) + f;
    }
    private void onKineticHitJerotes() {
        if (this.level().getGameTime() - this.lastKineticHitFeedbackTimeJerotes <= 10L) {
            return;
        }
        this.lastKineticHitFeedbackTimeJerotes = this.level().getGameTime();
        if (!(this.useItem.getItem() instanceof ItemToolBaseSpearBase kineticWeapon))
            return;
        kineticWeapon.makeLocalHitSound(this);
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte by, CallbackInfo ci) {
        if (by == 2) {
            this.onKineticHitJerotes();
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void updateUsingItem(ItemStack itemStack, CallbackInfo ci) {
        if (itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
            if (this.level().isClientSide()) {
//                itemToolBaseSpearBase.damageEntities(itemStack, this.getUseItemRemainingTicks(),
//                        ((LivingEntity)(Object)this),
//                        getUsedItemHand() == InteractionHand.MAIN_HAND ?
//                                EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                PacketHandler.sendToServer(new JerotesSpearRushAttackPacket(itemStack, getUseItemRemainingTicks(), ((LivingEntity)(Object)this).getId()));
            }
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void canAttack(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object) this;
        if ((((this.getTeam() == null && livingEntity.getTeam() == null) || this.getTeam() == livingEntity.getTeam())
                && EntityFactionFind.isFaction(self, livingEntity)) && (self instanceof JerotesEntity || livingEntity instanceof JerotesEntity)
        ) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void getCurrentSwingDuration(CallbackInfoReturnable<Integer> cir) {
       if (this.getMainHandItem().getItem() instanceof MeleeItem itemToolBaseSpearBase) {
           int n = itemToolBaseSpearBase.swingTimes();
           if (MobEffectUtil.hasDigSpeed((LivingEntity) (Object)this)) {
               cir.setReturnValue(n - (1 + MobEffectUtil.getDigSpeedAmplification((LivingEntity)(Object)this)));
           }
           else {
               cir.setReturnValue(this.hasEffect(MobEffects.DIG_SLOWDOWN) ? n + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : n);
           }
           cir.cancel();
       }
    }

    @Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    protected void getMobType(CallbackInfoReturnable<MobType> cir) {
        if (this instanceof JerotesChangeCamel jerotesChangeCamel && jerotesChangeCamel.isJerotesCamelHusk() && jerotesChangeCamel.isJerotesMobControlled())
            cir.setReturnValue(MobType.UNDEAD);
    }


    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof JerotesChangeStray jerotesChangeStray && jerotesChangeStray.isJerotesParched() && mobEffectInstance.getEffect() == MobEffects.WEAKNESS)
            cir.setReturnValue(false);
    }


//    @Redirect(
//            method = "hurt",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z",
//                    ordinal = 7
//            )
//    )
//    private boolean redirectExplosionCheck(DamageSource source, TagKey<DamageType> tag) {
//        if (tag == DamageTypeTags.IS_EXPLOSION)
//            return source.is(tag) || EntityAndItemFind.noKnockback(source);
//        return source.is(tag);
//    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void knockback(double strength, double x, double z, CallbackInfo ci) {
        if (Main.getJerotesPersistentData(this).getDouble("jerotes_no_knockback_tag") > 0) {
            Main.getJerotesPersistentData(this).putDouble("jerotes_no_knockback_tag", 0);
            ci.cancel();
        }
    }
}