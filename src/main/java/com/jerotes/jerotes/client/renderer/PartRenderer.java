package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Part.BasePartEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PartRenderer<T extends BasePartEntity> extends EntityRenderer<T> {
	private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
	public PartRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(T t) {
		return LOCATION;
	}
}
