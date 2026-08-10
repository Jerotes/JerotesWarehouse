package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.layer.JerotesIronGolemCrackinessLayer;
import com.jerotes.jerotes.client.layer.JerotesIronGolemFlowerLayer;
import com.jerotes.jerotes.client.model.Modeljerotes_iron_golem;
import com.jerotes.jerotes.entity.MagicSummoned.IronGolem.JerotesIronGoelmEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JerotesIronGoelmRenderer extends MobRenderer<JerotesIronGoelmEntity, Modeljerotes_iron_golem<JerotesIronGoelmEntity>> {
	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	public JerotesIronGoelmRenderer(EntityRendererProvider.Context p_174188_) {
		super(p_174188_, new Modeljerotes_iron_golem<>(p_174188_.bakeLayer(Modeljerotes_iron_golem.LAYER_LOCATION)), 0.7F);
		this.addLayer(new JerotesIronGolemCrackinessLayer(this));
		this.addLayer(new JerotesIronGolemFlowerLayer(this, p_174188_.getBlockRenderDispatcher()));
	}

	public ResourceLocation getTextureLocation(JerotesIronGoelmEntity p_115012_) {
		return GOLEM_LOCATION;
	}

	protected void setupRotations(JerotesIronGoelmEntity p_115014_, PoseStack p_115015_, float p_115016_, float p_115017_, float p_115018_) {
		super.setupRotations(p_115014_, p_115015_, p_115016_, p_115017_, p_115018_);
		if (!((double)p_115014_.walkAnimation.speed() < 0.01D)) {
			float f = 13.0F;
			float f1 = p_115014_.walkAnimation.position(p_115018_) + 6.0F;
			float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
			p_115015_.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
		}
	}
}