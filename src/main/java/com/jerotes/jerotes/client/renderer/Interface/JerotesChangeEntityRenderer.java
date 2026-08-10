package com.jerotes.jerotes.client.renderer.Interface;

import net.minecraft.nbt.CompoundTag;

public interface JerotesChangeEntityRenderer {
    default float getJerotesShadowRadius() {
        return 0.0f;
    }
    default float getJerotesShadowStrength() {
        return 1.0F;
    }
}
