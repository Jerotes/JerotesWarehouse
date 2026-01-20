package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeMob;
import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camel.class)
public abstract class CamelMixin extends AbstractHorse implements JerotesChangeCamel, JerotesChangeMob {
    private static final EntityDataAccessor<Boolean> IS_JEROTES_CAMEL_HUSK = SynchedEntityData.defineId(CamelMixin.class, EntityDataSerializers.BOOLEAN);

    protected CamelMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    public boolean isJerotesMobControlled() {
        return this.getFirstPassenger() instanceof Mob;
    }
    public boolean isJerotesCamelHusk() {
        return this.getEntityData().get(IS_JEROTES_CAMEL_HUSK);
    }
    public void setJerotesCamelHusk(boolean bl) {
        this.getEntityData().set(IS_JEROTES_CAMEL_HUSK, bl);
    }
    public float chargeSpeedModifierJerotes() {
        if (isJerotesCamelHusk())
            return 4.0f;
        return 1.0f;
    }
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("IsJerotesCamelHusk", this.isJerotesCamelHusk());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setJerotesCamelHusk(compoundTag.getBoolean("IsJerotesCamelHusk"));
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(IS_JEROTES_CAMEL_HUSK, false);
    }

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    protected void isFood(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (isJerotesCamelHusk())
            cir.setReturnValue(itemStack.is(ItemTags.create(new ResourceLocation("camel_husk_food"))));
    }

    @Inject(method = "clampRotation", at = @At("HEAD"), cancellable = true)
    protected void clampRotation(Entity entity, CallbackInfo ci) {
        if (isJerotesCamelHusk())
            ci.cancel();
    }

    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    protected void canMate(Animal animal, CallbackInfoReturnable<Boolean> cir) {
        if (isJerotesCamelHusk())
            cir.setReturnValue(false);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    protected void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (isJerotesCamelHusk()) {
            if (!this.getPassengers().isEmpty() && isJerotesMobControlled()) {
                Entity entity = (Entity) this.getPassengers().get(0);
                if (entity instanceof LivingEntity livingEntity) {
                    cir.setReturnValue(livingEntity);
                }
            }
        }
    }


//    @Inject(method = "travel", at = @At("HEAD"))
//    protected void travel(Vec3 vec3, CallbackInfo ci) {
//        if (isJerotesCamelHusk()) {
//            if (isJerotesMobControlled()) {
//                Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
//                if (this.isVehicle() && this.getControllingPassenger() != null && entity != null) {
//                    if (entity != null) {
//                        this.setYRot(entity.getYRot());
//                        this.yRotO = this.getYRot();
//                        this.setXRot(entity.getXRot() * 0.5F);
//                        this.setRot(this.getYRot(), this.getXRot());
//                        this.yBodyRot = entity.getYRot();
//                        this.yHeadRot = entity.getYRot();
//                    }
//                }
//            }
//        }
//    }
}