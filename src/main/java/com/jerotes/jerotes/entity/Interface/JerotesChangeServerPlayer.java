package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.phys.Vec3;

public interface JerotesChangeServerPlayer {
    Vec3 jerotesGetKnownMovement();
    void jerotesSetKnownMovement(Vec3 vec3);
}
