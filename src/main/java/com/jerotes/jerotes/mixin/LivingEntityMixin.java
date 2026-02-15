package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeStray;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.network.JerotesSpearRushAttackPacket;
import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements JerotesChangeLivingEntity {
    private static final EntityDataAccessor<Boolean> IS_TRUE_INVISIBLE_JEROTES = SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.BOOLEAN);

    protected LivingEntityMixin(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    public boolean isTrueInvisibleJerotes() {
        return this.getEntityData().get(IS_TRUE_INVISIBLE_JEROTES);
    }
    public void setTrueInvisibleJerotes(boolean bl) {
        this.getEntityData().set(IS_TRUE_INVISIBLE_JEROTES, bl);
    }

    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);

    @Shadow public abstract int getUseItemRemainingTicks();

    @Shadow protected int useItemRemaining;

    @Shadow protected abstract void completeUsingItem();

    @Shadow public abstract InteractionHand getUsedItemHand();

    @Shadow protected ItemStack useItem;

    @Shadow public abstract ItemStack getMainHandItem();


    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("IsTrueInvisibleJerotes", this.isTrueInvisibleJerotes());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setTrueInvisibleJerotes(compoundTag.getBoolean("IsTrueInvisibleJerotes"));
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(IS_TRUE_INVISIBLE_JEROTES, false);
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
    }

    public boolean canAddPassengerJerotes(Entity entity) {
        return this.canAddPassenger(entity);
    }

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
        if (((this.getTeam() == null && livingEntity.getTeam() == null) || this.getTeam() == livingEntity.getTeam())
                && EntityFactionFind.isFaction(self, livingEntity)
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
}