package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CamelRenderer.class)
public abstract class CamelRendererMixin extends MobRenderer<Camel, CamelModel<Camel>> {

    public CamelRendererMixin(EntityRendererProvider.Context context, CamelModel<Camel> camelCamelModel, float f) {
        super(context, camelCamelModel, f);
    }

    @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
    public void getTextureLocation(Camel camel, CallbackInfoReturnable<ResourceLocation> cir) {
        if (camel instanceof JerotesChangeCamel jerotesChangeCamel && jerotesChangeCamel.isJerotesCamelHusk()) {
            cir.setReturnValue(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/camel/camel_husk.png"));
        }
    }
}