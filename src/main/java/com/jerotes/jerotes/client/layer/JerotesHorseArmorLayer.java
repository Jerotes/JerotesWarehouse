package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.client.model.Modeljerotes_horse;
import com.jerotes.jerotes.client.model.Modeljerotes_horse_armor;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.Interface.ItemBaseWarBeastArmor;
import com.jerotes.jerotes.item.Interface.ItemBeastArmor;
import com.jerotes.jerotes.item.ItemWarBeastArmor;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;

public class JerotesHorseArmorLayer extends RenderLayer<JerotesHorseEntity, Modeljerotes_horse<JerotesHorseEntity>> {
    private final Modeljerotes_horse<JerotesHorseEntity> model;

    public JerotesHorseArmorLayer(RenderLayerParent<JerotesHorseEntity, Modeljerotes_horse<JerotesHorseEntity>> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        this.model = new Modeljerotes_horse_armor<>(entityModelSet.bakeLayer(Modeljerotes_horse_armor.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, JerotesHorseEntity horse, float f, float f2, float f3, float f4, float f5, float f6) {
        if (EntityAndItemFind.isTrueInvisible(horse)) {
            return;
        }
        float f7;
        float f8;
        float f9;
        ItemStack itemStack = horse.getArmor();
        if (!(itemStack.getItem() instanceof HorseArmorItem horseArmorItem)) {
            return;
        }
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(horse, f, f2, f3);
        this.model.setupAnim(horse, f, f2, f4, f5, f6);
        if (horseArmorItem instanceof DyeableHorseArmorItem) {
            int n2 = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
            f9 = (float)(n2 >> 16 & 0xFF) / 255.0f;
            f7 = (float)(n2 >> 8 & 0xFF) / 255.0f;
            f8 = (float)(n2 & 0xFF) / 255.0f;
        }
        else {
            f9 = 1.0f;
            f7 = 1.0f;
            f8 = 1.0f;
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(horseArmorItem.getTexture()));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, f9, f7, f8, 1.0f);
        if (itemStack.getItem() instanceof ItemBeastArmor itemBeastArmor && itemStack.getItem() instanceof ItemBaseWarBeastArmor itemBaseWarBeastArmor && itemBeastArmor.hasOverlay()) {
            VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(itemBaseWarBeastArmor.getTextureOverlay()));
            this.model.renderToBuffer(poseStack, vertexConsumer2, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

