package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;

public class ShootRenderer<T extends Projectile> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public ShootRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = f;
        this.fullBright = bl;
    }

    public ShootRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, true);
    }

    @Override
    protected int getBlockLightLevel(T t, BlockPos blockPos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(t, blockPos);
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.scale(this.scale, this.scale, this.scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        this.itemRenderer.renderStatic(((ItemSupplier)t).getItem(), ItemDisplayContext.GROUND, n, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, ((Entity)t).level(), ((Entity)t).getId());
        poseStack.popPose();
        poseStack.translate(0, 0.125 - t.getBbHeight()/2, 0);
        super.render(t, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(Projectile tex) {
        return TEXTURE_LOCATION;
    }
}

