package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.layer.JerotesSnowGolemHeadLayer;
import com.jerotes.jerotes.client.model.Modeljerotes_snow_golem;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesSnowGoelmEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JerotesSnowGoelmRenderer extends MobRenderer<JerotesSnowGoelmEntity, Modeljerotes_snow_golem<JerotesSnowGoelmEntity>> {
	private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

	public JerotesSnowGoelmRenderer(EntityRendererProvider.Context p_174188_) {
		super(p_174188_, new Modeljerotes_snow_golem<>(p_174188_.bakeLayer(Modeljerotes_snow_golem.LAYER_LOCATION)), 0.5F);
		this.addLayer(new JerotesSnowGolemHeadLayer(this, p_174188_.getBlockRenderDispatcher(), p_174188_.getItemRenderer()));
	}

	public ResourceLocation getTextureLocation(JerotesSnowGoelmEntity p_115012_) {
		return SNOW_GOLEM_LOCATION;
	}
}