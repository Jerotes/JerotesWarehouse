package com.jerotes.jerotes.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;

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