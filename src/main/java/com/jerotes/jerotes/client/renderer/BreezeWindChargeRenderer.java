package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelwind_charge;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.BreezeWindChargeEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreezeWindChargeRenderer extends EntityRenderer<BreezeWindChargeEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID,
            "textures/entity/projectiles/wind_charge.png");
    private final Modelwind_charge model;

    public BreezeWindChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new Modelwind_charge(context.bakeLayer(Modelwind_charge.LAYER_LOCATION));
    }

    @Override
    public void render(BreezeWindChargeEntity windCharge, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float f3 = (float)windCharge.tickCount + f2;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.flow(TEXTURE_LOCATION, this.xOffset(f3) % 1.0f, 0.0f));
        this.model.setupAnim(windCharge, 0.0f, 0.0f, f3, 0.0f, 0.0f);
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.5f);
        super.render(windCharge, f, f2, poseStack, multiBufferSource, n);
    }

    protected float xOffset(float f) {
        return f * 0.03f;
    }

    @Override
    public ResourceLocation getTextureLocation(BreezeWindChargeEntity windCharge) {
        return TEXTURE_LOCATION;
    }
}

