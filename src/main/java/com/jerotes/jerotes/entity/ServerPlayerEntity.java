package com.jerotes.jerotes.entity;

import net.minecraft.world.phys.Vec3;

public interface ServerPlayerEntity {
    Vec3 jerotesGetKnownMovement();
    void jerotesSetKnownMovement(Vec3 vec3);
}
