/*
 * Decompiled with CFR 0.152.
 */
package com.jerotes.jerotes.util;

import com.jerotes.jerotes.client.particle.SummonParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ParticlesUse {
    public static void summonParticle(ServerLevel serverLevel, Entity entity, double xzScale, double yScale, double x, double y, double z, int colorBottom, int colorTop) {
        float rBottom = ((colorBottom >> 16) & 0xFF) / 255.0f;
        float gBottom = ((colorBottom >> 8)  & 0xFF) / 255.0f;
        float bBottom = (colorBottom & 0xFF) / 255.0f;

        float rTop = ((colorTop >> 16) & 0xFF) / 255.0f;
        float gTop = ((colorTop >> 8)  & 0xFF) / 255.0f;
        float bTop = (colorTop & 0xFF) / 255.0f;

        float alphaBottom = SummonParticleOptions.DEFAULT_ALPHA_BOTTOM;
        float alphaTop    = SummonParticleOptions.DEFAULT_ALPHA_TOP;

        float w = (float) (entity.getBbWidth() * xzScale);
        float h = (float) (entity.getBbHeight() * yScale);
        SummonParticleOptions options = SummonParticleOptions.withColorsAndSize(
                h * 1.2f, w * 1.2f,
                rBottom, gBottom, bBottom,
                rTop, gTop, bTop,
                alphaBottom, alphaTop
        );
        serverLevel.sendParticles(options, x, y, z, 0, 0, 0, 0, 0);
    }
    public static void summonParticle(ServerLevel serverLevel, Entity entity, double x, double y, double z, int colorBottom, int colorTop) {
        summonParticle(serverLevel, entity, 1.25f, 1.25f, x, y, z, colorBottom, colorTop);
    }
}

