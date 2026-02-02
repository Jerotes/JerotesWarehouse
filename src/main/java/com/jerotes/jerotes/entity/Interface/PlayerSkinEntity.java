package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.util.PlayerName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface PlayerSkinEntity {
    PlayerName getUsername();

    @OnlyIn(Dist.CLIENT)
    boolean isTextureAvailable(MinecraftProfileTexture.Type type);
    GameProfile getProfile();
    ResourceLocation getTexture(MinecraftProfileTexture.Type type);
    @OnlyIn(Dist.CLIENT)
    SkinManager.SkinTextureCallback getSkinCallback();
    void setProfile(@Nullable GameProfile profile);
}

