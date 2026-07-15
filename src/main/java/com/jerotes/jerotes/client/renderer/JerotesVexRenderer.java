package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modeljerotes_vex;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class JerotesVexRenderer extends MobRenderer<JerotesVexEntity, Modeljerotes_vex> {
	private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
	private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

	public JerotesVexRenderer(EntityRendererProvider.Context p_174435_) {
		super(p_174435_, new Modeljerotes_vex(p_174435_.bakeLayer(Modeljerotes_vex.LAYER_LOCATION)), 0.3F);
		this.addLayer(new ItemInHandLayer<>(this, p_174435_.getItemInHandRenderer()));
	}

	protected int getBlockLightLevel(JerotesVexEntity p_116298_, BlockPos p_116299_) {
		return 15;
	}

	public ResourceLocation getTextureLocation(JerotesVexEntity p_116292_) {
		return p_116292_.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
	}
}