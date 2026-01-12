package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.entity.Mob.MirrorImageEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class MirrorImageLayer<T extends MirrorImageEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public MirrorImageLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T self, float f, float f2, float f3, float f4, float f5, float f6) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = minecraft.shouldEntityAppearGlowing(self) && self.isInvisible();
        if (self.isInvisible() && !bl) {
            return;
        }
        if (self.getOwner() == null) {
            return;
        }
    }
}
