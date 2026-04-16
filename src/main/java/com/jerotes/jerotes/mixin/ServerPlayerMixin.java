package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeServerPlayer;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements JerotesChangeServerPlayer {
    public ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Unique
    private Vec3 jerotesLastKnownClientMovement = Vec3.ZERO;
    @Unique
    public Vec3 jerotesGetKnownMovement() {
        Entity entity = this.getVehicle();
        if (entity != null && entity.getControllingPassenger() != this) {
            return ItemToolBaseSpearBase.getKnownMovement(entity);
        }
        return this.jerotesLastKnownClientMovement;
    }

    @Unique
    public void jerotesSetKnownMovement(Vec3 vec3) {
        this.jerotesLastKnownClientMovement = vec3;
    }
}