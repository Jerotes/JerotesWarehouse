package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;

public class PruritusMobEffect extends BaseMobEffectTick {
	public PruritusMobEffect() {
		super(MobEffectCategory.HARMFUL, -4276546);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.pruritus";
	}


	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int effectLevel = n + 1;
		if (livingEntity.getHealth() < (40 + effectLevel * 20) && !(livingEntity.getMobType() == MobType.UNDEAD) && !EntityFactionFind.isMachine(livingEntity)&& !EntityFactionFind.isConstruct(livingEntity)){
			if (livingEntity instanceof Mob mob) {
				mob.getNavigation().stop();
			}
			if (livingEntity.onGround() && livingEntity.getRandom().nextFloat() > 0.75f + Main.luck(livingEntity) * 0.1f && livingEntity.isAlive()) {
				RandomSource random = livingEntity.getRandom();

				float randomYaw = random.nextFloat() * 360.0f;
				float randomPitch = (random.nextFloat() - 0.5f) * 90.0f;

				float f3 = -Mth.sin(randomYaw * 0.017453292f) * Mth.cos(randomPitch * 0.017453292f);
				float f4 = -Mth.sin(randomPitch * 0.017453292f);
				float f5 = Mth.cos(randomYaw * 0.017453292f) * Mth.cos(randomPitch * 0.017453292f);

				float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
				f3 /= f6;
				f4 /= f6;
				f5 /= f6;

				float f7 = n * 0.02f * ((40 + effectLevel * 20) / livingEntity.getHealth());
				livingEntity.push(f3 * f7, f4 * f7, f5 * f7);
			}
		}
	}
}
