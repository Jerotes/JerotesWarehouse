package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.MirrorImageLayer;
import com.jerotes.jerotes.client.model.Modelnull;
import com.jerotes.jerotes.entity.Mob.MirrorImageEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MirrorImageRenderer extends MobRenderer<MirrorImageEntity, Modelnull<MirrorImageEntity>> {
    private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
    public MirrorImageRenderer(EntityRendererProvider.Context context) {
        super(context, new Modelnull(context.bakeLayer(Modelnull.LAYER_LOCATION)), 0f);
        this.addLayer(new MirrorImageLayer<>(this));
    }

    @Override
    protected float getFlipDegrees(MirrorImageEntity entity) {
        return 0.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(MirrorImageEntity entity) {
        return LOCATION;
    }
}