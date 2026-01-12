package com.jerotes.jerotes.compat.tacz;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TaczAnimate {
    public static void setRotationAnglesHead(LivingEntity living, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float limbSwingAmount) {
        ItemStack itemStack = living.getMainHandItem();
        ItemStack itemStacks = living.getOffhandItem();
        boolean bl3 = living.getFallFlyingTicks() > 4;
        boolean bl4 = living.isVisuallySwimming();
        ModelPart mainHand;
        ModelPart offHand;
        if (!(living instanceof Mob mob && mob.isLeftHanded())) {
            mainHand = rightArm;
            offHand = leftArm;
        } else {
            mainHand = leftArm;
            offHand = rightArm;
        }
        if (ModList.get().isLoaded("tacz")) {
            try {
                Class<?> thirdPersonManagerClass = Class.forName("com.tacz.guns.client.animation.third.InnerThirdPersonManager");
                Method setRotationAnglesHeadMethod = thirdPersonManagerClass.getDeclaredMethod(
                        "setRotationAnglesHead",
                        LivingEntity.class,
                        ModelPart.class,
                        ModelPart.class,
                        ModelPart.class,
                        ModelPart.class,
                        float.class
                );
                setRotationAnglesHeadMethod.invoke(null, living, rightArm, leftArm, body, head, limbSwingAmount);
            } catch (ClassNotFoundException e) {
                JerotesWarehouse.LOGGER.warn("Tacz animation class not found, using fallback animation");
                setFallbackAnimation(mainHand, offHand, head);
            } catch (NoSuchMethodException e) {
                JerotesWarehouse.LOGGER.warn("Tacz animation method not found, using fallback animation");
                setFallbackAnimation(mainHand, offHand, head);
            } catch (IllegalAccessException | InvocationTargetException e) {
                JerotesWarehouse.LOGGER.error("Failed to invoke Tacz animation method: {}", e.getMessage());
                setFallbackAnimation(mainHand, offHand, head);
            } catch (Exception e) {
                JerotesWarehouse.LOGGER.error("Unexpected error in Tacz animation: {}", e.getMessage());
                setFallbackAnimation(mainHand, offHand, head);
            }
        } else {
            setFallbackAnimation(mainHand, offHand, head);
        }
    }
    private static void setFallbackAnimation(ModelPart rightArm, ModelPart leftArm, ModelPart head) {
        //使用弩手臂动画
        if (rightArm != null && leftArm != null) {
            rightArm.yRot = -0.1f + head.yRot;
            leftArm.yRot = 0.1f + head.yRot + 0.4f;
            rightArm.xRot = -1.5707964f + head.xRot;
            leftArm.xRot = -1.5707964f + head.xRot;
        }
    }
}