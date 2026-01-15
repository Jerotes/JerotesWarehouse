package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.client.model.Modelspecial_action;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {

    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart body;

    @Shadow protected abstract HumanoidArm getAttackArm(T p_102857_);

    @Shadow protected abstract ModelPart getArm(HumanoidArm p_102852_);

    @Shadow protected abstract Iterable<ModelPart> bodyParts();

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void setupAnim(T entity, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_, CallbackInfo ci) {
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void poseRightArm(T entity, CallbackInfo ci) {
        boolean spear = Modelspecial_action.poseRightArm(((HumanoidModel<?>) (Object) this), entity);
        if (spear) {
            SpearAnimations.thirdPersonHandUse(this.rightArm, this.head, true, entity.getUseItem(), (HumanoidModel<?>) (Object) this, entity);
            ci.cancel();
        }
        //长枪
        if (entity.getMainHandItem().getItem() instanceof ItemToolBasePike) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                SpearAnimations.thirdPersonHandUse(this.rightArm, this.head, true, entity.getUseItem(), (HumanoidModel<?>) (Object) this, entity);
                if (entity.isUsingItem() && entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                    float f = 1 - Math.min(25, entity.getTicksUsingItem()) / 25f;
                    if (f > 0 && f <= 0.40) {
                        float fs = f / 0.40f;
                        this.rightArm.yRot += fs * 0.35f;
                    }
                    else if (f > 0.40 && f <= 0.5) {
                        float fs = (f - 0.40f) / 0.10f;
                        this.rightArm.yRot += 0.35f + fs * -0.55f;
                    }
                    else if (f > 0.5) {
                        float fs = 1 - (f - 0.5f) / 0.5f;
                        this.rightArm.yRot += fs * -0.2f;
                    }
                }
                ci.cancel();
            } else {
                ItemToolBasePike.animate(this.rightArm, this.leftArm, entity, true);
                 ci.cancel();
            }
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void poseLeftArm(T entity, CallbackInfo ci) {
        boolean spear = Modelspecial_action.poseLeftArm(((HumanoidModel<?>) (Object) this), entity);
        if (spear) {
            SpearAnimations.thirdPersonHandUse(this.leftArm, this.head, false, entity.getUseItem(), (HumanoidModel<?>) (Object) this, entity);
            ci.cancel();
        }
        if (entity.getMainHandItem().getItem() instanceof ItemToolBasePike) {
            if (entity.getMainArm() == HumanoidArm.LEFT) {
                SpearAnimations.thirdPersonHandUse(this.leftArm, this.head, false, entity.getUseItem(), (HumanoidModel<?>) (Object) this, entity);
                if (entity.isUsingItem() && entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                    float f = 1 - Math.min(25, entity.getTicksUsingItem()) / 25f;
                    if (f > 0 && f <= 0.40) {
                        float fs = f / 0.40f;
                        this.leftArm.yRot -= fs * 0.35f;
                    }
                    else if (f > 0.40 && f <= 0.5) {
                        float fs = (f - 0.40f) / 0.10f;
                        this.leftArm.yRot -= 0.35f + fs * -0.55f;
                    }
                    else if (f > 0.5) {
                        float fs = 1 - (f - 0.5f) / 0.5f;
                        this.leftArm.yRot -= fs * -0.2f;
                    }
                }
                ci.cancel();
            }
            else {
                ItemToolBasePike.animate(this.rightArm, this.leftArm, entity, false);
                ci.cancel();
            }
        }
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    private void setupAttackAnimation(T entity, float ageInTicks, CallbackInfo ci) {
        if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            if (!(this.attackTime <= 0.0F)) {
                HumanoidArm humanoidarm = this.getAttackArm(entity);
                ModelPart modelpart = this.getArm(humanoidarm);
                float f = this.attackTime;
                this.body.yRot = Mth.sin(Mth.sqrt(f) * 6.2831855F) * 0.2F;
                ModelPart modelPart;
                if (humanoidarm == HumanoidArm.LEFT) {
                    modelPart = this.body;
                    modelPart.yRot *= -1.0F;
                }

                this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
                this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
                this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
                this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
                modelPart = this.rightArm;
                modelPart.yRot += this.body.yRot;
                modelPart = this.leftArm;
                modelPart.yRot += this.body.yRot;
                modelPart = this.leftArm;
                modelPart.xRot += this.body.yRot;
                SpearAnimations.thirdPersonAttackHand((HumanoidModel<?>)(Object)this, (HumanoidModel<?>)(Object)this, entity);
            }
            ci.cancel();
        }
        if (entity.getMainHandItem().getItem() instanceof ItemToolBasePike) {
            if (!(this.attackTime <= 0.0F)) {
                HumanoidArm humanoidarm = this.getAttackArm(entity);
                ModelPart modelpart = this.getArm(humanoidarm);
                float f = this.attackTime;
                this.body.yRot = Mth.sin(Mth.sqrt(f) * 6.2831855F) * 0.2F;
                ModelPart modelPart;
                if (humanoidarm == HumanoidArm.LEFT) {
                    modelPart = this.body;
                    modelPart.yRot *= -1.0F;
                }

                this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
                this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
                this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
                this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
                modelPart = this.rightArm;
                modelPart.yRot += this.body.yRot;
                modelPart = this.leftArm;
                modelPart.yRot += this.body.yRot;
                modelPart = this.leftArm;
                modelPart.xRot += this.body.yRot;
                ItemToolBasePike.animateAttack((HumanoidModel<?>)(Object)this, (HumanoidModel<?>)(Object)this, entity);
            }
            ci.cancel();
        }
    }
}
