package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modeljerotes_abstract_zombie;
import com.jerotes.jerotes.client.model.Modeljerotes_vex;
import com.jerotes.jerotes.client.model.Modeljerotes_zombie;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.JerotesZombieEntity;
import com.jerotes.jerotes.entity.Mob.TestEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JerotesZombieRenderer extends HumanoidMobRenderer<JerotesZombieEntity, Modeljerotes_zombie<JerotesZombieEntity>> {
	private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

	public JerotesZombieRenderer(EntityRendererProvider.Context context) {
		super(context, new Modeljerotes_zombie<>(context.bakeLayer(Modeljerotes_zombie.LAYER_LOCATION)), 0.5F);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), context.getModelManager()));
	}

	public ResourceLocation getTextureLocation(JerotesZombieEntity jerotesZombieEntity) {
		return ZOMBIE_LOCATION;
	}

	protected boolean isShaking(JerotesZombieEntity jerotesZombieEntity) {
		return super.isShaking(jerotesZombieEntity) || jerotesZombieEntity.isUnderWaterConverting();
	}
}