package com.jerotes.jerotes.client.animation;

import com.jerotes.jerotes.entity.JerotesChangeLivingEntity;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.util.Ease;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SpearAnimations {
    public static float progress(float f, float f2, float f3) {
        return Mth.clamp(Mth.inverseLerp(f, f2, f3), 0.0f, 1.0f);
    }


    public static <T extends HumanoidModel<?>> void thirdPersonHandUse(ModelPart modelPart, ModelPart modelPart2, boolean bl, ItemStack itemStack, T t, LivingEntity livingEntity) {
        int n = bl ? 1 : -1;

        float headYRotDeg = (float)(57.295776f * modelPart2.yRot);
        headYRotDeg = headYRotDeg % 360.0f;

        if (headYRotDeg > 180.0f) headYRotDeg -= 360.0f;
        else if (headYRotDeg < -180.0f) headYRotDeg += 360.0f;

        float headYRotRad = headYRotDeg * ((float)Math.PI / 180);

        float f = headYRotRad;
        modelPart.yRot = -0.1f * (float)n + f;
        modelPart.xRot = -1.5707964f + modelPart2.xRot + 0.8f;
        if (livingEntity.isFallFlying() || t.swimAmount > 0.0f) {
            modelPart.xRot -= 0.9599311f;
        }
        modelPart.yRot = (float)Math.PI / 180 * Mth.clamp((float)(57.295776f * modelPart.yRot), (float)-60.0f, (float)60.0f);
        modelPart.xRot = (float)Math.PI / 180 * Mth.clamp((float)(57.295776f * modelPart.xRot), (float)-120.0f, (float)30.0f);
        if (livingEntity.getTicksUsingItem() <= 0.0f || livingEntity.isUsingItem() && livingEntity.getUsedItemHand() != (bl ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)) {
            return;
        }
        if (!(itemStack.getItem() instanceof ItemToolBaseSpearBase kineticWeapon))
            return;
        UseParams useParams = UseParams.fromKineticWeapon(kineticWeapon, livingEntity.getTicksUsingItem());
        modelPart.yRot += (float)(-n) * useParams.swayScaleFast() * ((float)Math.PI / 180) * useParams.swayIntensity() * 1.0f;
        modelPart.zRot += (float)(-n) * useParams.swayScaleSlow() * ((float)Math.PI / 180) * useParams.swayIntensity() * 0.5f;
        modelPart.xRot += (float)Math.PI / 180 * (-40.0f * useParams.raiseProgressStart() + 30.0f * useParams.raiseProgressMiddle() + -20.0f * useParams.raiseProgressEnd() + 20.0f * useParams.lowerProgress() + 10.0f * useParams.raiseBackProgress() + 0.6f * useParams.swayScaleSlow() * useParams.swayIntensity());
    }

    public static void thirdPersonUseItem(float fs2, PoseStack poseStack, float f, HumanoidArm humanoidArm, ItemStack itemStack, LivingEntity livingEntity, float fs) {
        if (!(itemStack.getItem() instanceof ItemToolBaseSpearBase kineticWeapon))
            return;
        if (f == 0.0f) {
            return;
        }
        float f2 = Ease.inQuad(SpearAnimations.progress(livingEntity.getAttackAnim(Minecraft.getInstance().getPartialTick()), 0.05f, 0.2f));
        float f3 = Ease.inOutExpo(SpearAnimations.progress(livingEntity.getAttackAnim(Minecraft.getInstance().getPartialTick()), 0.4f, 1.0f));
        UseParams useParams = UseParams.fromKineticWeapon(kineticWeapon, f);
        int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        float f4 = 1.0f - Ease.outBack(1.0f - useParams.raiseProgress());
        float f5 = 0.125f;
        float f6 = SpearAnimations.hitFeedbackAmount(livingEntity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity ? jerotesChangeLivingEntity.getTicksSinceLastKineticHitFeedbackJerotes(fs) : 0);
        poseStack.translate(0.0, (double)(-f6) * 0.4, (double)(-kineticWeapon.forwardMovement * (f4 - useParams.raiseBackProgress()) + f6));
        poseStack.rotateAround(Axis.XN.rotationDegrees(70.0f * (useParams.raiseProgress() - useParams.raiseBackProgress()) - 40.0f * (f2 - f3)), 0.0f, -0.03125f, 0.125f);
        poseStack.rotateAround(Axis.YP.rotationDegrees((float)(n * 90) * (useParams.raiseProgress() - useParams.swayProgress() + 3.0f * f3 + f2)), 0.0f, 0.0f, 0.125f);
    }

    public static <T extends HumanoidModel<?>> void thirdPersonAttackHand(HumanoidModel<?> humanoidModel, T t, LivingEntity livingEntity) {
        float f = livingEntity.getAttackAnim(Minecraft.getInstance().getPartialTick());
        HumanoidArm humanoidArms = livingEntity.getMainArm();
        HumanoidArm humanoidArm = livingEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidArms : humanoidArms.getOpposite();
        humanoidModel.rightArm.yRot -= humanoidModel.body.yRot;
        humanoidModel.leftArm.yRot -= humanoidModel.body.yRot;
        humanoidModel.leftArm.xRot -= humanoidModel.body.yRot;
        float f2 = Ease.inOutSine(SpearAnimations.progress(f, 0.0f, 0.05f));
        float f3 = Ease.inQuad(SpearAnimations.progress(f, 0.05f, 0.2f));
        float f4 = Ease.inOutExpo(SpearAnimations.progress(f, 0.4f, 1.0f));
        getArm(humanoidModel, (HumanoidArm)humanoidArm).xRot += (90.0f * f2 - 120.0f * f3 + 30.0f * f4) * ((float)Math.PI / 180);
    }
    public static ModelPart getArm(HumanoidModel<?> humanoidModel, HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? humanoidModel.leftArm : humanoidModel.rightArm;
    }

    public static void thirdPersonAttackItem(float fs2, PoseStack poseStack, LivingEntity livingEntity) {
        if (fs2 <= 0.0f) {
            return;
        }
        float f = 0.0f;
        if (livingEntity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase kineticWeapon) {
            f = kineticWeapon.forwardMovement;
        }
        float f2 = 0.125f;
        float f3 = fs2;
        float f4 = Ease.inQuad(SpearAnimations.progress(f3, 0.05f, 0.2f));
        float f5 = Ease.inOutExpo(SpearAnimations.progress(f3, 0.4f, 1.0f));
        poseStack.rotateAround(Axis.XN.rotationDegrees(70.0f * (f4 - f5)), 0.0f, -0.125f, 0.125f);
        poseStack.translate(0.0f, f * (f4 - f5), 0.0f);
    }

    private static float hitFeedbackAmount(float f) {
        return 0.4f * (Ease.outQuart(SpearAnimations.progress(f, 1.0f, 3.0f)) - Ease.inOutSine(SpearAnimations.progress(f, 3.0f, 10.0f)));
    }

    public static void firstPersonUse(float f, PoseStack poseStack, float f2, HumanoidArm humanoidArm, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemToolBaseSpearBase kineticWeapon))
            return;
        UseParams useParams = UseParams.fromKineticWeapon(kineticWeapon, f2);
        int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((double)((float)n * (useParams.raiseProgress() * 0.15f + useParams.raiseProgressEnd() * -0.05f + useParams.swayProgress() * -0.1f + useParams.swayScaleSlow() * 0.005f)), (double)(useParams.raiseProgress() * -0.075f + useParams.raiseProgressMiddle() * 0.075f + useParams.swayScaleFast() * 0.01f), (double)useParams.raiseProgressStart() * 0.05 + (double)useParams.raiseProgressEnd() * -0.05 + (double)(useParams.swayScaleSlow() * 0.005f));
        poseStack.rotateAround(Axis.XP.rotationDegrees(-65.0f * Ease.inOutBack(useParams.raiseProgress()) - 35.0f * useParams.lowerProgress() + 100.0f * useParams.raiseBackProgress() + -0.5f * useParams.swayScaleFast()), 0.0f, 0.1f, 0.0f);
        poseStack.rotateAround(Axis.YN.rotationDegrees((float)n * (-90.0f * SpearAnimations.progress(useParams.raiseProgress(), 0.5f, 0.55f) + 90.0f * useParams.swayProgress() + 2.0f * useParams.swayScaleSlow())), (float)n * 0.15f, 0.0f, 0.0f);
        poseStack.translate(0.0f, -SpearAnimations.hitFeedbackAmount(f), 0.0f);
    }

    public static void firstPersonAttack(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm) {
        float f2 = Ease.inOutSine(SpearAnimations.progress(f, 0.0f, 0.05f));
        float f3 = Ease.outBack(SpearAnimations.progress(f, 0.05f, 0.2f));
        float f4 = Ease.inOutExpo(SpearAnimations.progress(f, 0.4f, 1.0f));
        poseStack.translate((float)n * 0.1f * (f2 - f3), -0.075f * (f2 - f4), 0.65f * (f2 - f3));
        poseStack.mulPose(Axis.XP.rotationDegrees(-70.0f * (f2 - f4)));
        poseStack.translate(0.0, 0.0, -0.25 * (double)(f4 - f3));
    }

    public record UseParams(float raiseProgress, float raiseProgressStart, float raiseProgressMiddle, float raiseProgressEnd, float swayProgress, float lowerProgress, float raiseBackProgress, float swayIntensity, float swayScaleSlow, float swayScaleFast) {
        public static UseParams fromKineticWeapon(ItemToolBaseSpearBase kineticWeapon, float f) {
            int n = kineticWeapon.delayTicks;
            int n2 = kineticWeapon.dismountConditions.map(ItemToolBaseSpearBase.Condition::maxDurationTicks).orElse(0) + n;
            int n3 = n2 - 20;
            int n4 = kineticWeapon.knockbackConditions.map(ItemToolBaseSpearBase.Condition::maxDurationTicks).orElse(0) + n;
            int n5 = n4 - 40;
            int n6 = kineticWeapon.damageConditions.map(ItemToolBaseSpearBase.Condition::maxDurationTicks).orElse(0) + n;
            float f2 = SpearAnimations.progress(f, 0.0f, n);
            float f3 = SpearAnimations.progress(f2, 0.0f, 0.5f);
            float f4 = SpearAnimations.progress(f2, 0.5f, 0.8f);
            float f5 = SpearAnimations.progress(f2, 0.8f, 1.0f);
            float f6 = SpearAnimations.progress(f, n3, n5);
            float f7 = Ease.outCubic(Ease.inOutElastic(SpearAnimations.progress(f - 20.0f, n5, n4)));
            float f8 = SpearAnimations.progress(f, n6 - 5, n6);
            float f9 = 2.0f * Ease.outCirc(f6) - 2.0f * Ease.inCirc(f8);
            float f10 = Mth.sin(f * 19.0f * ((float)Math.PI / 180)) * f9;
            float f11 = Mth.sin(f * 30.0f * ((float)Math.PI / 180)) * f9;
            return new UseParams(f2, f3, f4, f5, f6, f7, f8, f9, f10, f11);
        }
    }
}

