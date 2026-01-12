package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.util.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final protected Minecraft minecraft;

    @Inject(method = "renderSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void renderSpyglassOverlay(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (this.minecraft.player != null && this.minecraft.player.isScoping() &&
        Main.isOtherScoping(this.minecraft.player)) {
            ci.cancel();
        }
    }
}