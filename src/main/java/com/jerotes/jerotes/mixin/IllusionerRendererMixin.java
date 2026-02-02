package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IllusionerRenderer.class)
public abstract class IllusionerRendererMixin extends IllagerRenderer<Illusioner> {

    protected IllusionerRendererMixin(EntityRendererProvider.Context p_174182_, IllagerModel<Illusioner> p_174183_, float p_174184_) {
        super(p_174182_, p_174183_, p_174184_);
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(Illusioner p_114952_, float p_114953_, float p_114954_, PoseStack p_114955_, MultiBufferSource p_114956_, int p_114957_, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null && player.hasEffect(JerotesMobEffects.TRUESIGHT.get())) {
            super.render(p_114952_, p_114953_, p_114954_, p_114955_, p_114956_, p_114957_);
            ci.cancel();
        }
    }
}