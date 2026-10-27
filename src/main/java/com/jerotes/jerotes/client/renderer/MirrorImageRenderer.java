package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.MirrorImageLayer;
import com.jerotes.jerotes.client.model.Modelnull;
import com.jerotes.jerotes.client.renderer.Interface.JerotesChangeEntityRenderer;
import com.jerotes.jerotes.entity.Other.OtherSpell.MirrorImageEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class MirrorImageRenderer extends EntityRenderer<MirrorImageEntity> {

    public MirrorImageRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MirrorImageEntity mirror, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity owner = mirror.getOwner();
        if (owner == null || owner.isRemoved()) {
            return;
        }
        EntityRenderer<? super LivingEntity> ownerRenderer = this.entityRenderDispatcher.getRenderer(owner);
        if (ownerRenderer == null) {
            return;
        }
        if (ownerRenderer instanceof JerotesChangeEntityRenderer jerotesChangeEntityRenderer) {
            this.shadowRadius = mirror.isInvisible() ? 0 : jerotesChangeEntityRenderer.getJerotesShadowRadius();
            this.shadowStrength = mirror.isInvisible() ? 0 : jerotesChangeEntityRenderer.getJerotesShadowStrength();
        }
        float ownerYaw = Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
        poseStack.pushPose();
        ownerRenderer.render(owner, ownerYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MirrorImageEntity entity) {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
    }
}