package com.jerotes.jerotes.util;

import com.google.common.hash.Hashing;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.PlayerSkinEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerSkin {
	//部分参考Player Mobs by Gustaf Järgren

	private static final Map<UUID, SkinType> SKIN_TYPE_CACHE = new Object2ObjectOpenHashMap<>();

	private static final Map<String, ResourceLocation> SKIN_RESOURCE_CACHE = new Object2ObjectOpenHashMap<>();
	private static final Map<String, ResourceLocation> CAPE_RESOURCE_CACHE = new Object2ObjectOpenHashMap<>();
	private static final Map<String, ResourceLocation> ELYTRA_RESOURCE_CACHE = new Object2ObjectOpenHashMap<>();
	public static final ResourceLocation HIDE_FEATURE = new ResourceLocation(JerotesWarehouse.MODID, "hide_feature");

	public static SkinType getPlayerSkinType(@Nullable GameProfile profile) {
		SkinType type = SkinType.DEFAULT;
		if (profile != null && profile.isComplete()) {
			if (SKIN_TYPE_CACHE.containsKey(profile.getId())) {
				type = SKIN_TYPE_CACHE.get(profile.getId());
			} else {
				Minecraft mc = Minecraft.getInstance();
				Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(profile);
				if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
					String stringType = map.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
					SKIN_TYPE_CACHE.put(profile.getId(), type = getType(stringType));
				} else {
					type = getType(DefaultPlayerSkin.getSkinModelName(profile.getId()));
				}
			}
		}
		return type;
	}

	private static SkinType getType(@Nullable String stringType) {
		return "slim".equals(stringType) ? SkinType.SLIM : SkinType.DEFAULT;
	}

	public static ResourceLocation getPlayerSkin(PlayerSkinEntity mob) {
		String lowerName = mob.getUsername().getSkinName().toLowerCase(Locale.ROOT);
		ResourceLocation location = SKIN_RESOURCE_CACHE.get(lowerName);
		if (location != null) {
			return location;
		}
		return getTexture(mob).orElse(DefaultPlayerSkin.getDefaultSkin());
	}

	@SuppressWarnings("deprecation")
	private static Optional<ResourceLocation> getTexture(PlayerSkinEntity entity) {
		if (entity.isTextureAvailable(MinecraftProfileTexture.Type.SKIN)) {
			return Optional.of(entity.getTexture(MinecraftProfileTexture.Type.SKIN));
		}
		GameProfile profile = entity.getProfile();
		if (profile != null && !profile.isComplete()) {
			return getDefault(profile);
		}
		if (profile != null && profile.getName() != null) {
			Minecraft mc = Minecraft.getInstance();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(profile);
			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				MinecraftProfileTexture profileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
				String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
				ResourceLocation location = SkinManager.getTextureLocation(MinecraftProfileTexture.Type.SKIN, s);
				if (mc.textureManager.getTexture(location, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()) {
					return Optional.of(location);
				} else {
					RenderSystem.recordRenderCall(() -> mc.getSkinManager().registerTexture(profileTexture, MinecraftProfileTexture.Type.SKIN, entity.getSkinCallback()));
				}
			}
		}
		return getDefault(profile);
	}

	private static Optional<ResourceLocation> getDefault(@Nullable GameProfile profile) {
		return Optional.of(profile != null && profile.isComplete() ? DefaultPlayerSkin.getDefaultSkin(profile.getId()) : DefaultPlayerSkin.getDefaultSkin());
	}

	public static void onResourceManagerReload(ResourceManager resourceManager) {
		SKIN_RESOURCE_CACHE.clear();

		var skins = resourceManager
				.listResources("skins", resourceLocation -> resourceLocation.getNamespace().equals(JerotesWarehouse.MODID) && resourceLocation.getPath().endsWith(".png"));
		Pattern skinPattern = Pattern.compile("skins/([a-z0-9_.-]*).png");

		for (ResourceLocation location : skins.keySet()) {
			Matcher matcher = skinPattern.matcher(location.getPath());
			if (matcher.find()) {
				String name = matcher.group(1);
				SKIN_RESOURCE_CACHE.put(name, location);
			}
		}

		parseHideableTexture(resourceManager, "capes", CAPE_RESOURCE_CACHE);
		parseHideableTexture(resourceManager, "elytra", ELYTRA_RESOURCE_CACHE);
	}

	private static void parseHideableTexture(ResourceManager resourceManager, String type, Map<String, ResourceLocation> cache) {
		cache.clear();
		Pattern pattern = Pattern.compile(type + "/([a-z0-9_.-]*).(png|txt)");

		var resources = resourceManager.listResources(type, resourceLocation -> resourceLocation.getNamespace().equals(JerotesWarehouse.MODID) &&
				(resourceLocation.getPath().endsWith(".png") || resourceLocation.getPath().endsWith(".txt")));

		for (ResourceLocation location : resources.keySet()) {
			Matcher matcher = pattern.matcher(location.getPath());
			if (matcher.find()) {
				String name = matcher.group(1);
				boolean ignore = Objects.equals(matcher.group(2), "txt");
				cache.put(name, ignore ? HIDE_FEATURE : location);
			}
		}
	}

	public static ResourceManagerReloadListener resourceManagerReloadListener() {
		return PlayerSkin::onResourceManagerReload;
	}

	public enum SkinType {
		DEFAULT,
		SLIM
	}

	public class ProfileUpdater {
		private static final Queue<PlayerSkinEntity> entities = new ArrayDeque<>();
		@Nullable
		private static Thread thread;

		public static void updateProfile(PlayerSkinEntity entity) {
			entities.add(entity);
			if (thread == null || thread.getState() == Thread.State.TERMINATED) {
				thread = new Thread(() -> {
					while (!entities.isEmpty()) {
						PlayerSkinEntity mob = entities.remove();
						if (mob != null) {
							SkullBlockEntity.updateGameprofile(mob.getProfile(), mob::setProfile);
						}
					}
				});
				thread.start();
			}
		}
	}
}