package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.ServerPlayerEntity;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Shadow @Nullable private Entity lastVehicle;

//    @Unique
//    private boolean jerotes1_20_4$receivedMovementThisTick;
//
//    @Unique
//    public void jerotes1_20_4$handleClientTickEnd() {
//        if (!this.jerotes1_20_4$receivedMovementThisTick) {
//            if (this.player instanceof ServerPlayerEntity serverPlayer) {
//                serverPlayer.jerotesSetKnownMovement(Vec3.ZERO);
//            }
//        }
//        this.jerotes1_20_4$receivedMovementThisTick = false;
//    }

    @Unique
    private void jerotes1_20_4$handlePlayerKnownMovement(Vec3 vec3) {
        if (vec3.lengthSqr() > (double)1.0E-5f) {
            this.player.resetLastActionTime();
        }
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.jerotesSetKnownMovement(vec3);
//            player.sendSystemMessage(Component.literal(
//                    String.valueOf(serverPlayer.jerotesGetKnownMovement().x()) + "," +
//                    String.valueOf(serverPlayer.jerotesGetKnownMovement().y()) + "," +
//                    String.valueOf(serverPlayer.jerotesGetKnownMovement().z())).withStyle(ChatFormatting.BLUE));
        }
        //this.jerotes1_20_4$receivedMovementThisTick = true;
    }
    @Unique
    private double jerotes1_20_4$handleMoveVehicleX;
    @Unique
    private double jerotes1_20_4$handleMoveVehicleY;
    @Unique
    private double jerotes1_20_4$handleMoveVehicleZ;
    @Unique
    private double jerotes1_20_4$handleMovePlayerX;
    @Unique
    private double jerotes1_20_4$handleMovePlayerY;
    @Unique
    private double jerotes1_20_4$handleMovePlayerZ;

    @Inject(method = "handleMoveVehicle", at = @At(value = "HEAD"))
    private void handleMoveVehicleHead(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket, CallbackInfo ci) {
        Entity entity = this.player.getRootVehicle();
        if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
            jerotes1_20_4$handleMoveVehicleX = entity.getX();
            jerotes1_20_4$handleMoveVehicleY = entity.getY();
            jerotes1_20_4$handleMoveVehicleZ = entity.getZ();
        }
    }
    @Inject(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;checkMovementStatistics(DDD)V"))
    private void handleMoveVehicle(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket, CallbackInfo ci) {
        Entity entity = this.player.getRootVehicle();
        if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
            double d = jerotes1_20_4$handleMoveVehicleX;
            double d2 = jerotes1_20_4$handleMoveVehicleY;
            double d3 = jerotes1_20_4$handleMoveVehicleZ;
            Vec3 vec3 = new Vec3(entity.getX() - d, entity.getY() - d2, entity.getZ() - d3);
            this.jerotes1_20_4$handlePlayerKnownMovement(vec3);
        }
    }
    @Inject(method = "handleMovePlayer", at = @At(value = "HEAD"))
    private void handleMovePlayerHead(ServerboundMovePlayerPacket serverboundMovePlayerPacket, CallbackInfo ci) {
        jerotes1_20_4$handleMovePlayerX = this.player.getX();
        jerotes1_20_4$handleMovePlayerY = this.player.getY();
        jerotes1_20_4$handleMovePlayerZ = this.player.getZ();
    }
    @Inject(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setOnGroundWithKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V"))
    private void handleMovePlayer(ServerboundMovePlayerPacket serverboundMovePlayerPacket, CallbackInfo ci) {
        double d4 = jerotes1_20_4$handleMovePlayerX;
        double d5 = jerotes1_20_4$handleMovePlayerY;
        double d6 = jerotes1_20_4$handleMovePlayerZ;
        Vec3 vec3 = new Vec3(this.player.getX() - d4, this.player.getY() - d5, this.player.getZ() - d6);
        this.jerotes1_20_4$handlePlayerKnownMovement(vec3);
    }
}