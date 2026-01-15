package com.jerotes.jerotes.client.sound;

import com.jerotes.jerotes.entity.Interface.BossEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;

public class BossMusicSoundInstance extends AbstractTickableSoundInstance {
	//有参考Mowzie's Mobs
	private BossEntity boss;
	private final SoundEvent soundEvent;

	public BossMusicSoundInstance(SoundEvent sound, BossEntity boss) {
		super(sound, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
		this.boss = boss;
		this.soundEvent = sound;
		this.attenuation = Attenuation.NONE;
		this.looping = true;
		this.delay = 0;
		this.x = ((Mob)boss).getX();
		this.y = ((Mob)boss).getY();
		this.z = ((Mob)boss).getZ();
	}

	public boolean canPlaySound() {
		return BossMusicPlayer.bossMusic == this;
	}

	public void tick() {
		if (((Mob)boss) == null || !((Mob)boss).isAlive() || ((Mob)boss).isSilent()) {
			boss = null;
			stop();
			BossMusicPlayer.bossMusic = null;
			Minecraft.getInstance().getMusicManager().stopPlaying();
		}
	}

	public void setBoss(BossEntity boss) {
		this.boss = boss;
	}

	public BossEntity getBoss() {
		return boss;
	}

	public SoundEvent getSoundEvent() {
		return soundEvent;
	}
}
