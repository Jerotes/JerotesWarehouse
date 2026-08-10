package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.client.renderer.Interface.JerotesChangeEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements JerotesChangeEntityRenderer {
    @Shadow
    protected float shadowRadius;

    @Shadow
    protected float shadowStrength;

    protected EntityRendererMixin() {
        super();
    }

    public float getJerotesShadowRadius() {
        return shadowRadius;
    }
    public float getJerotesShadowStrength() {
        return shadowStrength;
    }
}