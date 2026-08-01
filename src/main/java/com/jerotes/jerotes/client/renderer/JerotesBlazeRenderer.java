package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.layer.JerotesSnowGolemHeadLayer;
import com.jerotes.jerotes.client.model.Modeljerotes_snow_golem;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesBlazeEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesSnowGoelmEntity;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JerotesBlazeRenderer extends MobRenderer<JerotesBlazeEntity, BlazeModel<JerotesBlazeEntity>> {
	private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

	public JerotesBlazeRenderer(EntityRendererProvider.Context p_174188_) {
		super(p_174188_, new BlazeModel<>(p_174188_.bakeLayer(ModelLayers.BLAZE)), 0.5F);
	}

	protected int getBlockLightLevel(JerotesBlazeEntity p_113910_, BlockPos p_113911_) {
		return 15;
	}

	public ResourceLocation getTextureLocation(JerotesBlazeEntity p_115012_) {
		return BLAZE_LOCATION;
	}
}