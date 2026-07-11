package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.entity.Mob.MirrorImageEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.ShootTargetSpell.FireballEntity;
import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Breath.PoisonBreathEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.LightningBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.RayofEnfeeblementEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.RayofSicknessEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.*;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

import java.util.List;

public class SpellFind {
	//魔法飞弹
	public static boolean MagicMissile(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, int tickCount, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			MagicMissileEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				double d2 = caster.getLookAngle().x;
				double d3 = caster.getLookAngle().y;
				double d4 = caster.getLookAngle().z;
				spell = new MagicMissileEntity(spellLevelDamage, serverLevel, caster, d2, d3, d4);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				if (target != null && target != caster) {
					spell.setTarget(target);
				}
				serverLevel.addFreshEntity(spell);
		}
			if (!caster.level().isClientSide()) {
				Main.getJerotesPersistentData(caster).putDouble("jerotes_magic_missile", Main.getJerotesPersistentData(caster).getDouble("jerotes_magic_missile") - 3);
				Main.getJerotesPersistentData(caster).putUUID("jerotes_magic_missile_target", target != null ? target.getUUID() : null);
				Main.getJerotesPersistentData(caster).putInt("jerotes_magic_missile_spellLevelDamage", spellLevelDamage);
				Main.getJerotesPersistentData(caster).putDouble("jerotes_magic_missile", tickCount * 3 + Main.getJerotesPersistentData(caster).getDouble("jerotes_magic_missile"));
				Main.getJerotesPersistentData(caster).putFloat("jerotes_magic_missile_spellLevelAccuracy", spellLevelAccuracy);
				Main.getJerotesPersistentData(caster).putInt("jerotes_magic_missile_count", count);
				Main.getJerotesPersistentData(caster).putFloat("jerotes_magic_missile_distance", distance);
			}
		}
		return true;
	}
	//毒性吐息
	public static boolean PoisonBreath(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMaxDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PoisonBreathEntity breath;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				breath = new PoisonBreathEntity(spellLevelDamage, spellLevelMaxDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				breath.setOwner(caster);
				serverLevel.addFreshEntity(breath);
			}
		}
		return true;
	}
	//致病射线
	public static boolean RayofSickness(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			RayofSicknessEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new RayofSicknessEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//衰弱射线
	public static boolean RayofEnfeeblement(LivingEntity caster, Entity target, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			RayofEnfeeblementEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new RayofEnfeeblementEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//闪电束
	public static boolean LightningBolt(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			LightningBoltEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new LightningBoltEntity(spellLevelDamage, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//恶毒嘲笑
	public static boolean ViciousMockery(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ViciousMockeryEntity spell;
			spell = new ViciousMockeryEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//人类定身术
	public static boolean HoldPerson(LivingEntity caster, Entity target, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, int count) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			HoldPersonEntity spell;
			spell = new HoldPersonEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
			if (target != null) {
				int maxAttack = 1;
				//选择其他目标
				List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(12.0, 12.0, 12.0));
				list.removeIf(entity -> entity == caster || entity == target || AttackFind.FindCanNotAttack(caster, entity));
				for (LivingEntity findEntity : list) {
					if (findEntity == null) continue;
					if (!EntityFactionFind.isHumanoid(findEntity)) continue;
					if (findEntity.hasEffect(JerotesMobEffects.HOLD_MOB.get())
							&& findEntity.getEffect(JerotesMobEffects.HOLD_MOB.get()).getAmplifier() >= spellLevelMainEffectLevel - 1) continue;
					if (maxAttack > count) break;
					if ((findEntity instanceof Mob mob && mob.getTarget() == caster || caster instanceof Mob mob1 && mob1.getTarget() == findEntity)
							|| (findEntity instanceof Player && caster instanceof Enemy)
							|| (caster instanceof Player player && Main.getTargetedEntity(player, 32) == findEntity)
							|| (findEntity.getTeam() != null && caster.getTeam() != null && !findEntity.isAlliedTo(caster))
							|| (findEntity.isAlliedTo(target) && !findEntity.isAlliedTo(caster))) {
						spell = new HoldPersonEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
						spell.setPos(findEntity.getX(), findEntity.getEyeY(), findEntity.getZ());
						spell.setOwner(caster);
						spell.setTarget(findEntity);
						spell.setSize(Main.mobWidth(findEntity));
						serverLevel.addFreshEntity(spell);
						maxAttack += 1;
					}
				}
			}
		}
		return true;
	}
	//降咒
	public static boolean BestowCurse(LivingEntity caster, Entity target, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BestowCurseEntity spell;
			spell = new BestowCurseEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//魅影杀手
	public static boolean PhantasmalKiller(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PhantasmalKillerEntity spell;
			spell = new PhantasmalKillerEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//摄心目光
	public static boolean Eyebite(LivingEntity caster, Entity target, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			EyebiteEntity spell;
			spell = new EyebiteEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//火焰吸收
	public static boolean FireAbsorption(LivingEntity caster, float amount, double spellLevelPercentage, float spellLevelInterval) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (Main.getJerotesPersistentData(caster).getDouble("jerotes_fire_absorption") <= 0 || Main.getJerotesPersistentData(caster).getDouble("jerotes_fire_absorption_last_damage") < amount || Main.getJerotesPersistentData(caster).get("jerotes_fire_absorption") == null) {
				caster.heal((float) (amount * (spellLevelPercentage / 100)));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(ParticleTypes.FLAME, caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
				}
				Main.getJerotesPersistentData(caster).putDouble("jerotes_fire_absorption", Main.getJerotesPersistentData(caster).getDouble("jerotes_fire_absorption") + spellLevelInterval * 20);
				Main.getJerotesPersistentData(caster).putDouble("jerotes_fire_absorption_last_damage", amount);
				if (!caster.isInvisible()) {
					serverLevel.sendParticles(JerotesParticleTypes.FIRE_ABSORPTION_DISPLAY.get(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
				}
			}
		}
		return true;
	}
	//冰霜吸收
	public static boolean FreezeAbsorption(LivingEntity caster, float amount, double spellLevelPercentage, float spellLevelInterval) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (Main.getJerotesPersistentData(caster).getDouble("jerotes_freeze_absorption") <= 0 || Main.getJerotesPersistentData(caster).getDouble("jerotes_freeze_absorption_last_damage") < amount || Main.getJerotesPersistentData(caster).get("jerotes_freeze_absorption") == null) {
				caster.heal((float) (amount * (spellLevelPercentage / 100)));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
				}
				Main.getJerotesPersistentData(caster).putDouble("jerotes_freeze_absorption", Main.getJerotesPersistentData(caster).getDouble("jerotes_freeze_absorption") + spellLevelInterval * 20);
				Main.getJerotesPersistentData(caster).putDouble("jerotes_freeze_absorption_last_damage", amount);
				if (!caster.isInvisible()) {
					serverLevel.sendParticles(JerotesParticleTypes.FREEZE_ABSORPTION_DISPLAY.get(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
				}
			}
		}
		return true;
	}
	//闪电吸收
	public static boolean LightningAbsorption(LivingEntity caster, float amount, double spellLevelPercentage, float spellLevelInterval) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (Main.getJerotesPersistentData(caster).getDouble("jerotes_lightning_absorption") <= 0 || Main.getJerotesPersistentData(caster).getDouble("jerotes_lightning_absorption_last_damage") < amount || Main.getJerotesPersistentData(caster).get("jerotes_lightning_absorption") == null) {
				caster.heal((float) (amount * (spellLevelPercentage / 100)));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
				}
				Main.getJerotesPersistentData(caster).putDouble("jerotes_lightning_absorption", Main.getJerotesPersistentData(caster).getDouble("jerotes_lightning_absorption") + spellLevelInterval * 20);
				Main.getJerotesPersistentData(caster).putDouble("jerotes_lightning_absorption_last_damage", amount);
				if (!caster.isInvisible()) {
					serverLevel.sendParticles(JerotesParticleTypes.LIGHTNING_ABSORPTION_DISPLAY.get(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
				}
			}
		}
		return true;
	}
	//疗伤术
	public static boolean CureWounds(LivingEntity caster, LivingEntity target, int spellLevelHeal) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target.getMobType() != MobType.UNDEAD && !EntityFactionFind.isMachine(target) && !EntityFactionFind.isConstruct(target)) {
				target.heal(spellLevelHeal * Main.randomReach(caster.getRandom(), 1, 8));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(ParticleTypes.HEART, target.getRandomX(1), target.getRandomY(), target.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
				}
			}
		}
		return true;
	}
	//迷踪步
	public static boolean MistyStep(LivingEntity caster, int spellLevelDistance, int tryTime, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (!isPlayer) {
				double d = caster.getX() + (caster.getRandom().nextDouble() - 0.5) * spellLevelDistance;
				double d2 = caster.getY() + (double) (caster.getRandom().nextInt(spellLevelDistance) - spellLevelDistance / 2);
				double d3 = caster.getZ() + (caster.getRandom().nextDouble() - 0.5) * spellLevelDistance;
				BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(d, d2, d3);
				while (mutableBlockPos.getY() > serverLevel.getMinBuildHeight() && !serverLevel.getBlockState(mutableBlockPos).blocksMotion()) {
					mutableBlockPos.move(Direction.DOWN);
				}
				BlockState blockState = serverLevel.getBlockState(mutableBlockPos);
				boolean bl = blockState.blocksMotion();
				if ((!bl || !Main.hasLineOfSightPos(new Vec3(d, d2, d3), caster)) && tryTime > 0) {
					return SpellFind.MistyStep(caster, spellLevelDistance, tryTime - 1, false);
				}
				Vec3 vec3 = caster.position();
				boolean bl3 = caster.randomTeleport(d, d2, d3, true);
				if (bl3) {
					serverLevel.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(caster));
					if (caster.isPassenger()) {
						caster.stopRiding();
					}
					caster.resetFallDistance();
				} else if (tryTime > 0) {
					return SpellFind.MistyStep(caster, spellLevelDistance, tryTime - 1, false);
				}
				for (int i = 0; i < 8; ++i) {
					serverLevel.sendParticles(ParticleTypes.FLASH, caster.getRandomX(1.0), caster.getRandomY(), caster.getRandomZ(1.0), 0, 0.0, 0.0, 0.0, 0.0);
				}
			}
			else {
//				final double maxDistance = 30.0;
//				Vec3 startPos = caster.getEyePosition(1.0f);
//				Vec3 viewVector = caster.getViewVector(1.0f);
//				Vec3 endPos = startPos.add(viewVector.scale(maxDistance));
//				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
//						startPos, endPos,
//						ClipContext.Block.OUTLINE,
//						ClipContext.Fluid.NONE,
//						caster
//				));
//				Vec3 targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, maxDistance);
//				BlockPos safePos = Main.findSafePosition(serverLevel, targetPos);
//				if (safePos != null) {
//					boolean success = caster.randomTeleport(
//							safePos.getX() + 0.5,
//							safePos.getY(),
//							safePos.getZ() + 0.5,
//							true
//					);
//
//					if (success) {
//						return true;
//					}
//				}
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(30));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				Vec3 targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 30);
				boolean success = caster.randomTeleport(targetPos.x, targetPos.y, targetPos.z, true);
				if (success) {
					return true;
				}
			}
		}
		return true;
	}
	//冰霜吸收
	public static boolean Counterspell(LivingEntity caster) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (!caster.isInvisible()) {
				serverLevel.sendParticles(JerotesParticleTypes.COUNTERSPELL_DISPLAY.get(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
			}
		}
		return true;
	}
	//镜影术
	public static boolean MirrorImage(LivingEntity caster, int count, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PlayerTeam teams = (PlayerTeam) caster.getTeam();
			for (int i = 0; i < count; ++i) {
				BlockPos summonPos = caster.getOnPos().above();
				MirrorImageEntity mirrorImageEntity = JerotesEntityType.MIRROR_IMAGE.get().spawn(serverLevel, BlockPos.containing(summonPos.getX(), summonPos.getY(), summonPos.getZ()), MobSpawnType.MOB_SUMMONED);
				if (mirrorImageEntity != null) {
					mirrorImageEntity.setOrder(i + 1);
					mirrorImageEntity.setOwner(caster);
					mirrorImageEntity.setScale(Main.mobWidth(caster), Main.mobHeight(caster));
					mirrorImageEntity.spellLevelDamage = spellLevelDamage;
					//
					List<Mob> list = caster.level().getEntitiesOfClass(Mob.class, caster.getBoundingBox().inflate(32.0, 32.0, 32.0));
					list.removeIf(entity -> entity == caster || entity.getTarget() != caster);
					for (Mob findEntity : list) {
						if (findEntity == null || !findEntity.isAlive()) continue;
						if (EntityAndItemFind.targetBlindnessTrue(findEntity)|| findEntity.hasEffect(JerotesMobEffects.TRUESIGHT.get())) continue;
						findEntity.setTarget(mirrorImageEntity);
					}
					//
					if (teams != null) {
						serverLevel.getScoreboard().addPlayerToTeam(mirrorImageEntity.getStringUUID(), teams);
					}
				}
			}
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//匕首之云$法术
	public static boolean CloudOfDaggers(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(18));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 18);
			}
			//目标位置
			CloudOfDaggersEntity cloud = new CloudOfDaggersEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwner(caster);
			serverLevel.addFreshEntity(cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//火球术$法术
	public static boolean Fireball(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(45));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 45);
			}
			//目标位置
			FireballEntity cloud = new FireballEntity(serverLevel, caster);
			cloud.setStartPos(new Vec3(caster.getX(), caster.getY(0.7) - cloud.getBbHeight()/2, caster.getZ()));
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwner(caster);
			serverLevel.addFreshEntity(cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
}