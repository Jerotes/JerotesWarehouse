/*
 * Decompiled with CFR 0.152.
 */
package com.jerotes.jerotes.util;

import com.jerotes.jerotes.client.particle.SummonParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

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
                h, w,
                rBottom, gBottom, bBottom,
                rTop, gTop, bTop,
                alphaBottom, alphaTop
        );
        serverLevel.sendParticles(options, x, y, z, 0, 0, 0, 0, 0);
    }
    public static void summonParticle(ServerLevel serverLevel, Entity entity, double x, double y, double z, int colorBottom, int colorTop) {
        summonParticle(serverLevel, entity, 1.65, 1.65, x, y, z, colorBottom, colorTop);
    }


    //球粒子
    public static void sendBallParticles(Entity entity, SimpleParticleType simpleParticleType) {
        sendBallParticles(entity, simpleParticleType, false, 3.0f, 0.15f);
    }
    public static void sendBallParticles(Entity entity, SimpleParticleType simpleParticleType, boolean selfToOther, float radius, float speed) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            Vec3 playerPos = entity.position().add(0, entity.getY(0.5) - entity.getY(), 0);
            int particleCount = 40;

            for (int i = 0; i < particleCount; i++) {
                double angle = RandomSource.create().nextDouble() * Math.PI * 2;
                double inclination = RandomSource.create().nextDouble() * Math.PI;
                double offsetX = radius * Math.sin(inclination) * Math.cos(angle);
                double offsetY = radius * Math.cos(inclination);
                double offsetZ = radius * Math.sin(inclination) * Math.sin(angle);

                Vec3 particlePos;
                Vec3 velocity;

                if (selfToOther) {
                    particlePos = playerPos;
                    Vec3 direction = new Vec3(offsetX, offsetY, offsetZ).normalize();
                    velocity = direction.scale(speed);
                } else {
                    particlePos = playerPos.add(offsetX, offsetY, offsetZ);
                    Vec3 direction = playerPos.subtract(particlePos).normalize();
                    velocity = direction.scale(speed);
                }

                serverLevel.sendParticles(simpleParticleType,
                        particlePos.x, particlePos.y, particlePos.z,
                        0, velocity.x, velocity.y, velocity.z, 1.0f);
            }
        }
    }
}

