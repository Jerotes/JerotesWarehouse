package com.jerotes.jerotes.client.sound;

import com.jerotes.jerotes.entity.Interface.BossEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class BossMusicPlayer {
	//有参考Mowzie's Mobs
	public static BossMusicSoundInstance bossMusic;

	public static void playBossMusic(BossEntity boss) {
		SoundEvent music = boss.getBossMusic();
		if (music != null && boss instanceof Mob bossMob && bossMob.isAlive()) {
			Player player = Minecraft.getInstance().player;
			Minecraft.getInstance().getMusicManager().stopPlaying();
			if (bossMusic != null) {
				float volume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
				if (volume <= 0) {
					bossMusic = null;
				}
				else if (bossMusic.getBoss() == bossMob && !(player != null && bossMob.canAttack(player) && bossMob.canAttackType(player.getType()) && bossMob.distanceTo(player) < 50 * 50)) {
					bossMusic.setBoss(null);
				}
				else if (bossMusic.getBoss() == null && bossMusic.getSoundEvent() == music) {
					bossMusic.setBoss(boss);
				}
			}
			else {
				if (player != null && bossMob.canAttack(player) && bossMob.distanceTo(player) < 50 * 50) {
					bossMusic = new BossMusicSoundInstance(boss.getBossMusic(), boss);
				}
			}
			if (bossMusic != null && !Minecraft.getInstance().getSoundManager().isActive(bossMusic)) {
				Minecraft.getInstance().getSoundManager().play(bossMusic);
			}
		}
	}

	public static void stopBossMusic(BossEntity boss) {
		if (bossMusic != null && bossMusic.getBoss() == boss) {
			bossMusic.setBoss(null);
		}
	}
}