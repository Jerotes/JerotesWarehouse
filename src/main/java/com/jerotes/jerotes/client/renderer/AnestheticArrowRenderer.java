package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.arrow.AnestheticArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AnestheticArrowRenderer extends ArrowRenderer<AnestheticArrowEntity> {
    public static final ResourceLocation ARROW_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/anesthetic_arrow.png");

    public AnestheticArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AnestheticArrowEntity arrow) {
        return ARROW_LOCATION;
    }
}

