package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.entity.JerotesChangeLivingEntity;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
//    @Inject(method = "renderArmWithItem", at = @At("TAIL"))
//    private void renderArmWithItem(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
//                                         float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack,
//                                         MultiBufferSource buffer, int light, CallbackInfo ci) {
//        if (!(stack.getItem() instanceof ItemToolBaseSpearBase))
//            return;
//        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
//        if (player.isUsingItem() && player.getUseItem() == stack) {
//            float useTicks = player.getTicksUsingItem() + partialTick;
//            SpearAnimations.thirdPersonUseItem(player.attackAnim, poseStack, useTicks, arm, stack, player, partialTick);
//        }
//        if (player.attackAnim > 0.0F) {
//            SpearAnimations.thirdPersonAttackItem(player.attackAnim, poseStack, player);
//        }
//    }
}