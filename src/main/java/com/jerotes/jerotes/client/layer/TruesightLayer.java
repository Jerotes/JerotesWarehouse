package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class TruesightLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public TruesightLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || !player.hasEffect(JerotesMobEffects.TRUESIGHT.get())) {
            return;
        }
        int levels = Objects.requireNonNull(player.getEffect(JerotesMobEffects.TRUESIGHT.get())).getAmplifier();
        if (levels % 2 == 0) {
            if (!t.isInvisible()) {
                return;
            }
        }
        else {
            levels -= 1;
        }


        if (Objects.requireNonNull(player.getEffect(JerotesMobEffects.TRUESIGHT.get())).getAmplifier() < 1 && !t.isInvisible()) {
            return;
        }
        if ((levels + 1) * 30 < player.distanceTo(t)) {
            return;
        }
        EntityModel<T> model = this.getParentModel();

        poseStack.pushPose();
        poseStack.scale(1.05f, 1.05f, 1.05f);
        RenderType glowType = JerotesRenderType.glowing_outline();
        if (t instanceof Mob mob && !(mob.isAlliedTo(player))) {
            if (mob.isAggressive() || mob.getTarget() != null) {
                glowType = JerotesRenderType.glowing_outline_combat_angry();
            }
            else if (mob instanceof Enemy) {
                glowType = JerotesRenderType.glowing_outline_combat();
            }
        }
        if (t.isInvisible() || !player.hasLineOfSight(t)) {
            glowType = JerotesRenderType.glowing_outline_combat_less();
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(glowType);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }
}
