package com.jerotes.jerotes.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

import java.util.*;

public class JerotesBossEvent extends ServerBossEvent {
	private final Mob mob;
	private final Set<ServerPlayer> unseenPlayers = new HashSet<>();
	private UUID id;

	public JerotesBossEvent(Mob mob, UUID uuid, BossBarColor bossBarColor, boolean bl) {
		super(Objects.requireNonNull(mob.getDisplayName()), bossBarColor, BossBarOverlay.PROGRESS);
		this.setVisible(true);
		this.setId(uuid);
		this.setDarkenScreen(bl);
		this.mob = mob;
	}

	public JerotesBossEvent(Mob mob, UUID uuid, BossBarColor bossBarColor, BossBarOverlay bossBarOverlay, boolean bl) {
		super(Objects.requireNonNull(mob.getDisplayName()), bossBarColor, bossBarOverlay);
		this.setVisible(true);
		this.setId(uuid);
		this.setDarkenScreen(bl);
		this.mob = mob;
	}

	public JerotesBossEvent(Mob mob, Component component, UUID uuid, BossBarColor bossBarColor, boolean bl) {
		super(component, bossBarColor, BossBarOverlay.PROGRESS);
		this.setVisible(true);
		this.setId(uuid);
		this.setDarkenScreen(bl);
		this.mob = mob;
	}

	public JerotesBossEvent(Mob mob, UUID uuid, BossBarColor bossBarColor, boolean bl, boolean bl2) {
		this(mob, uuid, bossBarColor, bl);
		this.setCreateWorldFog(bl2);
	}

	public JerotesBossEvent(Component component, UUID uuid, BossBarColor bossBarColor, boolean bl, boolean bl2) {
		super(component, bossBarColor, BossBarOverlay.PROGRESS);
		this.setVisible(true);
		this.setId(uuid);
		this.setDarkenScreen(bl);
		this.setCreateWorldFog(bl2);
		this.mob = null;
	}

	public JerotesBossEvent(Component component, UUID uuid, BossBarColor bossBarColor, boolean bl, boolean bl2, boolean bl3) {
		super(component, bossBarColor, BossBarOverlay.PROGRESS);
		this.setVisible(bl3);
		this.setId(uuid);
		this.setDarkenScreen(bl);
		this.setCreateWorldFog(bl2);
		this.mob = null;
	}

	public void setId(UUID uuid) {
		this.id = uuid;
	}

	public UUID getId() {
		return this.id;
	}

	public void update() {
		if (this.mob != null) {
			Iterator<ServerPlayer> it = this.unseenPlayers.iterator();
			while (it.hasNext()) {
				ServerPlayer player = it.next();
				if (this.mob.getSensing().hasLineOfSight(player)) {
					super.addPlayer(player);
					it.remove();
				}
			}
		}
	}

	public void addPlayer(ServerPlayer player) {
		if (this.mob != null) {
			if (this.mob.getSensing().hasLineOfSight(player)) {
				super.addPlayer(player);
			} else {
				this.unseenPlayers.add(player);
			}
		}
		else {
			super.addPlayer(player);
		}
	}

	public void removePlayer(ServerPlayer player) {
		super.removePlayer(player);
		this.unseenPlayers.remove(player);
	}
}