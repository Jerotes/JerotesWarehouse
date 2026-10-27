package com.jerotes.jerotes.entity.Other.BreathAreaSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class ColorSprayAreaEntity extends BaseBreathAreaEntity implements TraceableEntity, OwnableEntity, MagicAbout {

	private static final int[] IRIDESCENCE = {
			0xFF2000, 0xFF8C00, 0xFFE000, 0x33DD44, 0x1E7BFF, 0x4B2BD8, 0xA020F0
	};

	public ColorSprayAreaEntity(EntityType<? extends ColorSprayAreaEntity> entityType, Level level) {
		super(entityType, level);
	}

	public ColorSprayAreaEntity(Level level, LivingEntity owner) {
		this(JerotesEntityType.COLOR_SPRAY_AREA.get(), level);
		this.setOwnerUUID(owner.getUUID());
	}

	@Override
	public boolean dealDamageTo(LivingEntity livingEntity) {
		LivingEntity owner = this.getOwner();
		if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == owner) {
			return false;
		}
		if (AttackFind.FindCanNotAttack(owner, livingEntity)) {
			return false;
		}
		if (EntityAndItemFind.savingSpellThrow(livingEntity)) {
			return false;
		}
		livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
				(6 + (getSpellLevel() - 1) * 3) * 20, 0), owner);
		this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
		return true;
	}

	@Override
	public float getMaxReach() {
		return 4.5f;
	}

	@Override
	public int getParticleCount() {
		return 60;
	}

	@Override
	public ParticleOptions coneParticle(float angleRad, float depth) {
		return new DustParticleOptions(iridescence(angleRad / Mth.TWO_PI), 1.0f);
	}

	@Override
	public float[] boltParticleColor(float angleRad, float depth) {
		Vector3f c = iridescence(angleRad / Mth.TWO_PI);
		return new float[]{c.x(), c.y(), c.z()};
	}

	@Override
	public int stripColor(float t) {
		Vector3f c = iridescence(t);
		return ((int) (c.x() * 255f) << 16) | ((int) (c.y() * 255f) << 8) | (int) (c.z() * 255f);
	}

	@Override
	public ParticleOptions getParticle() {
		return new DustParticleOptions(iridescence(0f), 1.0f);
	}

	@Override
	public int beamLightI() {
		return IRIDESCENCE[0];
	}

	@Override
	public int beamLightII() {
		return IRIDESCENCE[3];
	}

	@Override
	public int beamLightIII() {
		return IRIDESCENCE[6];
	}

	private static Vector3f iridescence(float t) {
		float x = (t % 1.0f + 1.0f) % 1.0f * IRIDESCENCE.length;
		int i = (int) x;
		float f = x - i;
		int a = IRIDESCENCE[i % IRIDESCENCE.length];
		int b = IRIDESCENCE[(i + 1) % IRIDESCENCE.length];
		return new Vector3f(
				Mth.lerp(f, (a >> 16) & 0xFF, (b >> 16) & 0xFF) / 255f,
				Mth.lerp(f, (a >> 8) & 0xFF, (b >> 8) & 0xFF) / 255f,
				Mth.lerp(f, a & 0xFF, b & 0xFF) / 255f
		);
	}
}
