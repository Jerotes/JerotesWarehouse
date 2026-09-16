package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.block.RedstoneLaserBlock;
import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.Other.BreathAreaSpell.PoisonBreathAreaEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.MirrorImageEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.*;
import com.jerotes.jerotes.entity.Other.ShootTargetSpell.FireballEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Breath.PoisonBreathEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicBeam.SunbeamEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.*;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray.FlamesSprayEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray.FrostbiteSprayEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray.SparksSprayEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.WitchBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.*;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.*;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.inventory.SafeEnchantmentMenu;
import com.jerotes.jerotes.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

import java.lang.reflect.Method;
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
		}
		return true;
	}
	//毒性吐息
	public static boolean PoisonBreath(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (subcategory == 0) {
				if (!isPlayer && target != caster && caster instanceof Mob mob && target != null) {
					mob.getLookControl().setLookAt(target, 360.0f, 360.0f);
					mob.lookAt(target, 360.0f, 360.0f);
				}
				//目标位置
				Vec3 look = caster.getLookAngle().normalize();
				Vec3 targetPos = caster.position()
						.add(0, caster.getBbHeight() * 0.25 + 2 / 16f, 0)
						.add(look.scale(2.25));

				PoisonBreathAreaEntity cloud = new PoisonBreathAreaEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelDamage);
				cloud.setPos(targetPos.x, targetPos.y, targetPos.z);
				cloud.setYRot(caster.getYRot());
				cloud.setXRot(caster.getXRot());
				cloud.setOwner(caster);
				cloud.setTarget(caster);
				serverLevel.addFreshEntity(cloud);
				serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
			}
			if (subcategory == 1) {
				PoisonBreathEntity breath;
				for (int i = 0; i < count; ++i) {
					if (!isPlayer && caster instanceof Mob mob && target != null) {
						mob.lookAt(target, 360.0f, 360.0f);
					}
					breath = new PoisonBreathEntity(spellLevelDamage, 6, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
					breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
					breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
					breath.setOwner(caster);
					serverLevel.addFreshEntity(breath);
				}
			}
		}
		return true;
	}
	//烈焰人火球
	public static boolean BlazeFireball(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BlazeFireballEntity fireball;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				fireball = new BlazeFireballEntity(spellLevelDamage, spellLevelFireTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				fireball.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				fireball.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				fireball.setOwner(caster);
				serverLevel.addFreshEntity(fireball);
			}
		}
		return true;
	}
	//旋风人风弹
	public static boolean BreezeWindCharge(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelExplode, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BreezeWindChargeEntity fireball;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				fireball = new BreezeWindChargeEntity(spellLevelDamage, spellLevelExplode, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				fireball.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				fireball.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				fireball.setOwner(caster);
				serverLevel.addFreshEntity(fireball);
			}
		}
		return true;
	}
	//恶魂火球术
	public static boolean GhastFireball(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelExplode, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int spellSubcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			GhastFireballEntity fireball;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				fireball = new GhastFireballEntity(spellLevelDamage, spellLevelFireTime, spellLevelExplode, spellSubcategory, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				fireball.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				fireball.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				fireball.setOwner(caster);
				serverLevel.addFreshEntity(fireball);
			}
		}
		return true;
	}
	//末影龙火球术
	public static boolean EnderdragonFireball(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			EnderdragonFireballEntity fireball;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				fireball = new EnderdragonFireballEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				fireball.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				fireball.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				fireball.setOwner(caster);
				serverLevel.addFreshEntity(fireball);
			}
		}
		return true;
	}
	//火焰箭
	public static boolean FireBolt(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FireBoltEntity breath;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				breath = new FireBoltEntity(spellLevelDamage, spellLevelFireTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				breath.setOwner(caster);
				serverLevel.addFreshEntity(breath);

				Vec3 pos = breath.position();
				for (int i2 = 0; i2 < 40; i2++) {
					double dx = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dy = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dz = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					serverLevel.sendParticles(JerotesParticleTypes.FLAME.get(),
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
		}
		return true;
	}
	//寒冰箭
	public static boolean FrostBolt(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFreezeTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FrostBoltEntity breath;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				breath = new FrostBoltEntity(spellLevelDamage, spellLevelFreezeTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				breath.setOwner(caster);
				serverLevel.addFreshEntity(breath);

				Vec3 pos = breath.position();
				for (int i2 = 0; i2 < 40; i2++) {
					double dx = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dy = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dz = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					serverLevel.sendParticles(JerotesParticleTypes.FROST.get(),
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
		}
		return true;
	}
	//电击箭
	public static boolean ShockBolt(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ShockBoltEntity breath;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				breath = new ShockBoltEntity(spellLevelDamage, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				breath.setOwner(caster);
				serverLevel.addFreshEntity(breath);

				Vec3 pos = breath.position();
				for (int i2 = 0; i2 < 40; i2++) {
					double dx = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dy = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dz = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					serverLevel.sendParticles(JerotesParticleTypes.SHOCK.get(),
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
		}
		return true;
	}
	//喷涌火舌$法术
	public static boolean FlamesSpray(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelAccuracy, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FlamesSprayEntity spell;
			if (!isPlayer && target != caster && caster instanceof Mob mob && target != null) {
				mob.getLookControl().setLookAt(target, 360.0f, 360.0f);
				mob.lookAt(target, 360.0f, 360.0f);
			}
			double d2 = caster.getLookAngle().x;
			double d3 = caster.getLookAngle().y;
			double d4 = caster.getLookAngle().z;
			spell = new FlamesSprayEntity(spellLevelDamage, spellLevelFireTime, serverLevel, caster, d2, d3, d4);
			spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
			spell.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0f, 1f, spellLevelAccuracy);
			spell.setOwner(caster);
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//喷涌霜噬$法术
	public static boolean FrostbiteSpray(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFreezeTime, float spellLevelAccuracy, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FrostbiteSprayEntity spell;
			if (!isPlayer && target != caster && caster instanceof Mob mob && target != null) {
				mob.getLookControl().setLookAt(target, 360.0f, 360.0f);
				mob.lookAt(target, 360.0f, 360.0f);
			}
			double d2 = caster.getLookAngle().x;
			double d3 = caster.getLookAngle().y;
			double d4 = caster.getLookAngle().z;
			spell = new FrostbiteSprayEntity(spellLevelDamage, spellLevelFreezeTime, serverLevel, caster, d2, d3, d4);
			spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
			spell.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0f, 1f, spellLevelAccuracy);
			spell.setOwner(caster);
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//喷涌电花$法术
	public static boolean SparksSpray(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			SparksSprayEntity spell;
			if (!isPlayer && target != caster && caster instanceof Mob mob && target != null) {
				mob.getLookControl().setLookAt(target, 360.0f, 360.0f);
				mob.lookAt(target, 360.0f, 360.0f);
			}
			double d2 = caster.getLookAngle().x;
			double d3 = caster.getLookAngle().y;
			double d4 = caster.getLookAngle().z;
			spell = new SparksSprayEntity(spellLevelDamage, serverLevel, caster, d2, d3, d4);
			spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
			spell.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0f, 1f, spellLevelAccuracy);
			spell.setOwner(caster);
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//巫术箭
	public static boolean WitchBolt(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelSpeed, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			WitchBoltEntity spear;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spear = new WitchBoltEntity(spellLevelDamage, serverLevel, caster);
				spear.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0.0f, spellLevelSpeed, spellLevelAccuracy);
				spear.setOwner(caster);
				serverLevel.addFreshEntity(spear);
				SpellType.startConcentration(caster, spear);

				Vec3 pos = spear.position();
				for (int i2 = 0; i2 < 40; i2++) {
					double dx = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dy = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dz = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					serverLevel.sendParticles(JerotesParticleTypes.SHOCK.get(),
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
		}
		return true;
	}
	//冰刃
	public static boolean IceKnife(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFreezeTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			IceKnifeEntity breath;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				breath = new IceKnifeEntity(spellLevelDamage, spellLevelFreezeTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				breath.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				breath.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				breath.setOwner(caster);
				serverLevel.addFreshEntity(breath);

				Vec3 pos = breath.position();
				for (int i2 = 0; i2 < 40; i2++) {
					double dx = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dy = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					double dz = (caster.getRandom().nextDouble() - 0.5) * 2.0;
					serverLevel.sendParticles(JerotesParticleTypes.FROST.get(),
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
		}
		return true;
	}
	//火焰射线
	public static boolean RayofFire(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			RayofFireEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new RayofFireEntity(spellLevelDamage, spellLevelFireTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//冷冻射线
	public static boolean RayofFrost(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFreezeTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			RayofFrostEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new RayofFrostEntity(spellLevelDamage, spellLevelFreezeTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//闪电射线
	public static boolean RayofLightning(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			RayofLightningEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new RayofLightningEntity(spellLevelDamage, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//魔能爆
	public static boolean EldritchBlast(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			EldritchBlastEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new EldritchBlastEntity(spellLevelDamage, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
				if (!caster.isSilent()) {
					caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.GENERIC_EXPLODE,
							caster.getSoundSource(), 1.0f, 1.5F + caster.getRandom().nextFloat() * 0.5f);
				}
			}
		}
		return true;
	}
	//灼热射线
	public static boolean ScorchingRay(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelFireTime, float spellLevelAccuracy, int count, float distance, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ScorchingRayEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new ScorchingRayEntity(spellLevelDamage, spellLevelFireTime, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
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
	//解离术
	public static boolean Disintegrate(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int spellSubcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			DisintegrateEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new DisintegrateEntity(spellLevelDamage, spellSubcategory, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//死亡一指
	public static boolean FingerOfDeath(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int spellSubcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FingerOfDeathEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new FingerOfDeathEntity(spellLevelDamage, spellSubcategory, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
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
	//连锁闪电
	public static boolean ChainLightning(LivingEntity caster, Entity target, int spellLevelDamage, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int spellSubcategory, boolean spellIsFirst) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ChainLightningEntity spell;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				spell = new ChainLightningEntity(spellLevelDamage, spellSubcategory, spellIsFirst, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				spell.setPos(caster.getX(), caster.getY(0.7) - spell.getBbHeight()/2, caster.getZ());
				spell.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				spell.setOwner(caster);
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//阳炎射线$法术
	public static boolean Sunbeam(LivingEntity caster, int spellLevelDamage, int spellLevelSubcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			SunbeamEntity baseBeamEntity = new SunbeamEntity(JerotesEntityType.SUNBEAM.get(), serverLevel, spellLevelDamage, spellLevelSubcategory);
			baseBeamEntity.setPos(caster.getX(), caster.getY(0.75), caster.getZ());
			baseBeamEntity.setLightLockX((float) caster.getX());
			baseBeamEntity.setLightLockY((float) (caster.getY(0.75)));
			baseBeamEntity.setLightLockZ((float) caster.getZ());
			baseBeamEntity.setOwner(caster);
			serverLevel.addFreshEntity(baseBeamEntity);
			SpellType.startConcentration(caster, baseBeamEntity);
		}
		return true;
	}
	//虫群孳生
	public static boolean Infestation(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			InfestationEntity spell;
			spell = new InfestationEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//电爪
	public static boolean ShockingGrasp(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ShockingGraspEntity spell;
			spell = new ShockingGraspEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//守卫者光束
	public static boolean GuardianLaser(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			GuardianLaserEntity spell;
			spell = new GuardianLaserEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//恶毒嘲笑
	public static boolean ViciousMockery(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ViciousMockeryEntity spell;
			spell = new ViciousMockeryEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//隐形术
	public static boolean Invisibility(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			InvisibilityEntity spell = new InvisibilityEntity(serverLevel, caster);
			spell.setSpellLevelDamage(spellLevelDamage);
			spell.setPos(target.getX(), target.getY() + 2 / 16f, target.getZ());
			spell.setOwnerUUID(caster.getUUID());
			spell.setTarget(target);
			spell.setSize(Main.mobWidth(target));
			spell.setTargetHeight(Main.mobHeight(target));
			serverLevel.addFreshEntity(spell);
			SpellType.startConcentration(caster, spell);
		}
		return true;
	}
	//隐形通道
	public static boolean InvisiblePassage(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			InvisiblePassageEntity spell = new InvisiblePassageEntity(serverLevel, caster);
			spell.setSpellLevelDamage(spellLevelDamage);
			spell.setPos(target.getX(), target.getY() + 2 / 16f, target.getZ());
			spell.setOwnerUUID(caster.getUUID());
			spell.setTarget(target);
			spell.setSize(Main.mobWidth(target));
			spell.setTargetHeight(Main.mobHeight(target));
			serverLevel.addFreshEntity(spell);
			SpellType.startConcentration(caster, spell);
		}
		return true;
	}
	//人类定身术
	public static boolean HoldPerson(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, int tickCount, boolean first) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			HoldMobEntity spell = null;
			if (target instanceof LivingEntity livingEntity) {
				//感知判定
				if (!EntityAndItemFind.savingSpellThrow(livingEntity)) {
					spell = new HoldMobEntity(serverLevel, caster);
					spell.setSpellLevelDamage(spellLevelDamage);
					spell.setPos(livingEntity.getX(), livingEntity.getY() + 2 / 16f, livingEntity.getZ());
					spell.setOwnerUUID(caster.getUUID());
					spell.setTarget(livingEntity);
					spell.setSize(Main.mobWidth(livingEntity));
					spell.setTargetHeight(Main.mobHeight(livingEntity));
					serverLevel.addFreshEntity(spell);
					DamageSource damageSource = new DamageSource(caster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.MAGIC_EFFECT), spell, caster);
					livingEntity.hurt(damageSource, 0f);
				}
			}
			
			HoldPersonMainEntity cloud = new HoldPersonMainEntity(serverLevel, caster);
			if (target != null) {
				cloud.setPos(target.getX(), target.getY(), target.getZ());
				cloud.setOwner(caster);
				cloud.setSize(Main.mobWidth(target));
				if (spell != null) {
					cloud.add(spell.getUUID());
				}
			}
			serverLevel.addFreshEntity(cloud);
			SpellType.startConcentration(caster, cloud);
			if (caster instanceof Player) {
				if (first) {
					if (tickCount > 1) {
						if (!caster.level().isClientSide()) {
							Main.getJerotesPersistentData(caster).putDouble("jerotes_hold_person", Main.getJerotesPersistentData(caster).getDouble("jerotes_hold_person") - 3);
							Main.getJerotesPersistentData(caster).putInt("jerotes_hold_person_spellLevelDamage", spellLevelDamage);
							Main.getJerotesPersistentData(caster).putInt("jerotes_hold_person_spellLevelMainEffectTime", spellLevelMainEffectTime);
							Main.getJerotesPersistentData(caster).putInt("jerotes_hold_person_spellLevelMainEffectLevel", spellLevelMainEffectLevel);
							Main.getJerotesPersistentData(caster).putDouble("jerotes_hold_person", tickCount - 1 + Main.getJerotesPersistentData(caster).getDouble("jerotes_hold_person"));
						}
					}
				}
			}
			else {
				if (target instanceof LivingEntity livingEntity) {
					int maxAttack = 1;
					//选择其他目标
					List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(12.0, 12.0, 12.0));
					list.removeIf(entity -> entity == caster || entity == target || AttackFind.FindCanNotAttack(caster, entity));
					for (LivingEntity findEntity : list) {
						if (findEntity == null) continue;
						if (!EntityFactionFind.isHumanoid(findEntity)) continue;
						if (AttackFind.SameFactionAvoidDamage(findEntity, caster, false)) continue;
						if (findEntity.hasEffect(JerotesMobEffects.HOLD_MOB.get())
								&& findEntity.getEffect(JerotesMobEffects.HOLD_MOB.get()).getAmplifier() >= spellLevelMainEffectLevel - 1) continue;
						if (maxAttack > tickCount) break;
						if ((findEntity instanceof Mob mob && mob.getTarget() == caster || caster instanceof Mob mob1 && mob1.getTarget() == findEntity)
								|| (AttackFind.SameFactionAvoidDamage(findEntity, livingEntity, false) && !AttackFind.SameFactionAvoidDamage(findEntity, caster, false))
								|| (findEntity.isAlliedTo(target) && !findEntity.isAlliedTo(caster))) {
							spell = new HoldMobEntity(serverLevel, caster);
							spell.setSpellLevelDamage(spellLevelDamage);
							spell.setPos(findEntity.getX(), findEntity.getY() + 2 / 16f, findEntity.getZ());
							spell.setOwnerUUID(caster.getUUID());
							spell.setTarget(findEntity);
							spell.setSize(Main.mobWidth(findEntity));
							spell.setTargetHeight(Main.mobHeight(findEntity));
							serverLevel.addFreshEntity(spell);
							DamageSource damageSource = new DamageSource(caster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.MAGIC_EFFECT), spell, caster);
							findEntity.hurt(damageSource, 0f);
							if (EntityFactionFind.isHumanoid(findEntity)) {
								findEntity.addEffect(new MobEffectInstance(JerotesMobEffects.HOLD_MOB.get(), 20 * spellLevelMainEffectTime - 1, spellLevelMainEffectLevel - 1), caster);
							}
							
							
							maxAttack += 1;
							cloud.add(spell.getUUID());
						}
					}
				}
			}
		}
		return true;
	}
	//降咒术
	public static boolean BestowCurse(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (spellLevelDamage < 5) {
				ConcentrationBestowCurseEntity spell = new ConcentrationBestowCurseEntity(serverLevel, caster);
				spell.setSpellLevelDamage(spellLevelDamage);
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwnerUUID(caster.getUUID());
				spell.setTarget(target);
				spell.setSubcategory(subcategory);
				spell.setSize(Main.mobWidth(target));
				spell.setTargetHeight(Main.mobHeight(target));
				serverLevel.addFreshEntity(spell);
				SpellType.startConcentration(caster, spell);
			}
			else {
				BestowCurseEntity spell = new BestowCurseEntity(serverLevel, caster);
				spell.setSpellLevelDamage(spellLevelDamage);
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwnerUUID(caster.getUUID());
				spell.setTarget(target);
				spell.setSubcategory(subcategory);
				spell.setSize(Main.mobWidth(target));
				spell.setTargetHeight(Main.mobHeight(target));
				serverLevel.addFreshEntity(spell);
			}
		}
		return true;
	}
	//解咒术
	public static boolean RemoveCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (subcategory == 0) {
				if (!(target instanceof LivingEntity))
					target = caster;
			}
			if (subcategory == 0) {
				if (target == caster && caster instanceof Player player) {
					if (Main.getTargetedEntity(player, 20, true) != null && Main.getTargetedEntity(player, 20, true) instanceof LivingEntity targets) {
						target = targets;
					}
				}
			}
			if (target == null)
				target = caster;
			RemoveCurseEntity spell;
			spell = new RemoveCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, subcategory, serverLevel, caster, 0, 0, 0);
			spell.setPos(target.getX(), target.getY(), target.getZ());
			spell.setOwner(caster);
			spell.setTarget(target);
			spell.setSize(Main.mobWidth(target));
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//失明诅咒
	public static boolean BlindnessCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BlindnessCurseEntity spell;
			spell = new BlindnessCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//黑暗诅咒
	public static boolean DarknessCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			DarknessCurseEntity spell;
			spell = new DarknessCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//疲劳诅咒
	public static boolean FatigueCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FatigueCurseEntity spell;
			spell = new FatigueCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//魅影杀手
	public static boolean PhantasmalKiller(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PhantasmalKillerEntity spell = new PhantasmalKillerEntity(serverLevel, caster);
			spell.setSpellLevelDamage(spellLevelDamage);
			spell.setPos(target.getX(), target.getY() + 2 / 16f, target.getZ());
			spell.setOwnerUUID(caster.getUUID());
			spell.setTarget(target);
			spell.setSize(Main.mobWidth(target));
			spell.setTargetHeight(Main.mobHeight(target));
			serverLevel.addFreshEntity(spell);
			SpellType.startConcentration(caster, spell);
		}
		return true;
	}
	//摄心目光
	public static boolean Eyebite(LivingEntity caster, Entity target, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			EyebiteEntity spell;
			spell = new EyebiteEntity(spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
				spell.setOwner(caster);
				spell.setTarget(target);
				spell.setSize(Main.mobWidth(target));
			}
			serverLevel.addFreshEntity(spell);
		}
		return true;
	}
	//重伤术
	public static boolean Harm(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			HarmEntity spell;
			spell = new HarmEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
			if (target != null) {
				spell.setPos(target.getX(), target.getY(), target.getZ());
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
					serverLevel.sendParticles(JerotesParticleTypes.FLAME.get(), caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
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
	public static boolean ColdAbsorption(LivingEntity caster, float amount, double spellLevelPercentage, float spellLevelInterval) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (Main.getJerotesPersistentData(caster).getDouble("jerotes_cold_absorption") <= 0 || Main.getJerotesPersistentData(caster).getDouble("jerotes_cold_absorption_last_damage") < amount || Main.getJerotesPersistentData(caster).get("jerotes_cold_absorption") == null) {
				caster.heal((float) (amount * (spellLevelPercentage / 100)));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(JerotesParticleTypes.FROST.get(), caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
				}
				Main.getJerotesPersistentData(caster).putDouble("jerotes_cold_absorption", Main.getJerotesPersistentData(caster).getDouble("jerotes_cold_absorption") + spellLevelInterval * 20);
				Main.getJerotesPersistentData(caster).putDouble("jerotes_cold_absorption_last_damage", amount);
				if (!caster.isInvisible()) {
					serverLevel.sendParticles(JerotesParticleTypes.COLD_ABSORPTION_DISPLAY.get(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
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
					serverLevel.sendParticles(JerotesParticleTypes.SHOCK.get(), caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
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
	//生命修补
	public static boolean LifeMend(LivingEntity caster, LivingEntity target, int spellLevelHeal) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			LifeMendEntity cloud = new LifeMendEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelHeal);
			cloud.setPos(target.getX(), target.getY() + 2/16f, target.getZ());
			cloud.setOwnerUUID(caster.getUUID());
			cloud.setTarget(target);
			cloud.setSize(Main.mobWidth(target));
			cloud.setTargetHeight(Main.mobHeight(target));
			serverLevel.addFreshEntity(cloud);
		}
		return true;
	}
	//疗伤术
	public static boolean CureWounds(LivingEntity caster, LivingEntity target, int spellLevelHeal) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target.getMobType() != MobType.UNDEAD && !EntityFactionFind.isMachine(target) && !EntityFactionFind.isConstruct(target)) {
				CureWoundsEntity cloud = new CureWoundsEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelHeal);
				cloud.setPos(target.getX(), target.getY() + 2/16f, target.getZ());
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(target);
				cloud.setSize(Main.mobWidth(target));
				cloud.setTargetHeight(Main.mobHeight(target));
				serverLevel.addFreshEntity(cloud);
			}
		}
		return true;
	}
	//修补术
	public static boolean Mending(LivingEntity caster, Entity target, int spellLevelHeal, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (subcategory == 0) {
				if (!(target instanceof LivingEntity))
					target = caster;
			}
			if (target == null)
				target = caster;
			if (!(subcategory == 1 && (target instanceof LivingEntity livingEntity && !EntityFactionFind.isMachine(livingEntity) && !EntityFactionFind.isConstruct(livingEntity)))) {
				MendingEntity cloud = new MendingEntity(serverLevel, caster);
				cloud.setSubcategory(subcategory);
				cloud.setSpellLevelDamage(spellLevelHeal);
				cloud.setPos(target.getX(), target.getY() + 2/16f, target.getZ());
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(target);
				cloud.setSize(Main.mobWidth(target));
				cloud.setTargetHeight(Main.mobHeight(target));
				serverLevel.addFreshEntity(cloud);
			}
		}
		return true;
	}
	//羽落术
	public static boolean FeatherFall(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (!(subcategory == 2)) {
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
				AABB searchBox = new AABB(targetPos.x - 2.25, targetPos.y - 2.25, targetPos.z - 2.25,
						targetPos.x + 2.25, targetPos.y + 2.25, targetPos.z + 2.25);

				ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelDamage);
				cloud.setPos(targetPos.x, targetPos.y - 1, targetPos.z);
				cloud.setColorI(0x826d72);
				cloud.setColorII(0xede2c9);
				cloud.setColorIII(0xb2a68e);
				cloud.setColorIV(0xfffceb);
				cloud.setSize(4.5f);
				cloud.setTargetHeight(4.5f);
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(null);
				serverLevel.addFreshEntity(cloud);
				int particleCount = 80;
				for (int i = 0; i < particleCount; i++) {
					double px = searchBox.minX + serverLevel.random.nextDouble() * (searchBox.maxX - searchBox.minX);
					double py = searchBox.minY + serverLevel.random.nextDouble() * (searchBox.maxY - searchBox.minY);
					double pz = searchBox.minZ + serverLevel.random.nextDouble() * (searchBox.maxZ - searchBox.minZ);
					double vx = (serverLevel.random.nextDouble() - 0.5) * 2;
					double vy = (serverLevel.random.nextDouble() - 0.5) * 0.5;
					double vz = (serverLevel.random.nextDouble() - 0.5) * 2;
					serverLevel.sendParticles(JerotesParticleTypes.FEATHER.get(), px, py, pz,
							1, vx, vy, vz, 0.05);
				}

				List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, searchBox);
				for (LivingEntity findEntity : list) {
					if (findEntity == null) continue;
					if (AttackFind.SameFactionAvoidDamage(findEntity, caster, false) || subcategory == 1) continue;
					findEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), caster);
				}
			}
			else {
				if (target instanceof LivingEntity findEntity) {
					int particleCount = 40;
					for (int i = 0; i < particleCount; i++) {
						double px = findEntity.getX() + (serverLevel.random.nextDouble() - 0.5) * 2.0;
						double py = findEntity.getY() + serverLevel.random.nextDouble() * 1.5;
						double pz = findEntity.getZ() + (serverLevel.random.nextDouble() - 0.5) * 2.0;
						double vx = (serverLevel.random.nextDouble() - 0.5) * 2;
						double vy = (serverLevel.random.nextDouble() - 0.5) * 0.5;
						double vz = (serverLevel.random.nextDouble() - 0.5) * 2;
						serverLevel.sendParticles(JerotesParticleTypes.FEATHER.get(), px, py, pz,
								1, vx, vy, vz, 0.05);
					}
					ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
					cloud.setSpellLevelDamage(spellLevelDamage);
					cloud.setPos(findEntity.getX(), findEntity.getY(), findEntity.getZ());
					cloud.setColorI(0x826d72);
					cloud.setColorII(0xede2c9);
					cloud.setColorIII(0xb2a68e);
					cloud.setColorIV(0xfffceb);
					cloud.setSize(Main.mobWidth(findEntity));
					cloud.setTargetHeight(Main.mobHeight(findEntity));
					cloud.setOwnerUUID(caster.getUUID());
					cloud.setTarget(null);
					serverLevel.addFreshEntity(cloud);
					findEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), caster);
				}
			}
		}
		return true;
	}
	//黑暗视觉
	public static boolean Darkvision(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target instanceof LivingEntity findEntity) {
				ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelDamage);
				cloud.setPos(findEntity.getX(), findEntity.getY(), findEntity.getZ());
				cloud.setColorI(0x848df2);
				cloud.setColorII(0x2d3f9e);
				cloud.setColorIII(0xfff489);
				cloud.setColorIV(0xd6b747);
				cloud.setSize(Main.mobWidth(findEntity));
				cloud.setTargetHeight(Main.mobHeight(findEntity));
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(null);
				serverLevel.addFreshEntity(cloud);
				findEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * spellLevelMainEffectTime, spellLevelMainEffectLevel - 1), caster);
			}
		}
		return true;
	}
	//迷踪步
	public static boolean MistyStep(LivingEntity caster, int spellLevelDistance, int tryTime, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			caster.resetFallDistance();
			for (int i = 0; i < 8; ++i) {
				serverLevel.sendParticles(ParticleTypes.FLASH,
						caster.getRandomX(1.0),
						caster.getRandomY(),
						caster.getRandomZ(1.0),
						0, 0.0, 0.0, 0.0, 0.0);
			}
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
			}
			else {
				{
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
					}
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(spellLevelDistance));
				Vec3 targetPos;

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = hitResult.getLocation();
				caster.teleportTo(targetPos.x, targetPos.y, targetPos.z);
				return true;
			}
		}
		return true;
	}
	//末影步
	public static boolean EnderStep(LivingEntity caster, int spellLevelDistance, int tryTime, boolean isPlayer) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			caster.resetFallDistance();
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
					return SpellFind.EnderStep(caster, spellLevelDistance, tryTime - 1, false);
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
					return SpellFind.EnderStep(caster, spellLevelDistance, tryTime - 1, false);
				}
			}
			else {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(spellLevelDistance));
				Vec3 targetPos = caster.getPosition(0);

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = hitResult.getLocation();
				boolean success = caster.randomTeleport(targetPos.x, targetPos.y, targetPos.z, true);
				if (success) {
					return true;
				}
				else {
					serverLevel.broadcastEntityEvent(caster, (byte)46);
					caster.teleportTo(targetPos.x, targetPos.y, targetPos.z);
				}
			}
		}
		return true;
	}
	//法术反制
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
			List<MirrorImageMainEntity> list = caster.level().getEntitiesOfClass(MirrorImageMainEntity.class, caster.getBoundingBox().inflate(32, 32, 32));
			list.removeIf(mirrorImageEntity -> mirrorImageEntity.getOwner() != caster);
			for (MirrorImageMainEntity mirrorImageEntity : list) {
				mirrorImageEntity.discard();
			}
			List<MirrorImageEntity> list2 = caster.level().getEntitiesOfClass(MirrorImageEntity.class, caster.getBoundingBox().inflate(32, 32, 32));
			list2.removeIf(mirrorImageEntity -> mirrorImageEntity.getOwner() != caster);
			for (MirrorImageEntity mirrorImageEntity : list2) {
				mirrorImageEntity.discard();
			}

			PlayerTeam teams = (PlayerTeam) caster.getTeam();

			MirrorImageMainEntity cloud = new MirrorImageMainEntity(serverLevel, caster);
			cloud.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
			cloud.setOwner(caster);
			cloud.setTarget(caster);
			cloud.setSize(Main.mobWidth(caster));

			for (int i = 0; i < count; ++i) {
				BlockPos summonPos = caster.getOnPos().above();
				MirrorImageEntity mirrorImageEntity = JerotesEntityType.MIRROR_IMAGE.get().spawn(serverLevel, BlockPos.containing(summonPos.getX(), summonPos.getY(), summonPos.getZ()), MobSpawnType.MOB_SUMMONED);
				if (mirrorImageEntity != null) {
					Main.conjureAbout(caster, mirrorImageEntity);
					mirrorImageEntity.setOrder(i + 1);
					mirrorImageEntity.setOwner(caster);
					mirrorImageEntity.setScale(Main.mobWidth(caster), Main.mobHeight(caster));
					mirrorImageEntity.spellLevelDamage = spellLevelDamage;
					//
					List<Mob> list3 = caster.level().getEntitiesOfClass(Mob.class, caster.getBoundingBox().inflate(32.0, 32.0, 32.0));
					list3.removeIf(entity -> entity == caster || entity.getTarget() != caster);
					for (Mob findEntity : list3) {
						if (findEntity == null || !findEntity.isAlive()) continue;
						if (EntityAndItemFind.targetBlindnessTrue(findEntity)|| findEntity.hasEffect(JerotesMobEffects.TRUESIGHT.get())) continue;
						findEntity.setTarget(mirrorImageEntity);
					}
					//
					if (teams != null) {
						serverLevel.getScoreboard().addPlayerToTeam(mirrorImageEntity.getStringUUID(), teams);
					}
					cloud.add(mirrorImageEntity.getUUID());
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

			//直接无损移动
			if (!SpellType.getConcentrationSpellTypeJerotes(caster).isEmpty()) {
				if (SpellType.getConcentrationSpellTypeJerotes(caster).equals("jerotes_cloud_of_daggers") &&
						spellLevelDamage == SpellType.getConcentrationSpellLevelJerotes(caster) &&
						serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(caster)) instanceof CloudOfDaggersEntity cloudOfDaggers &&
						cloudOfDaggers.position().distanceTo(targetPos) <= 9) {
					cloudOfDaggers.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
					return false;
				}
			}

			//目标位置
			CloudOfDaggersEntity cloud = new CloudOfDaggersEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwnerUUID(caster.getUUID());
			serverLevel.addFreshEntity(cloud);
			SpellType.startConcentration(caster, cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//创造篝火$法术
	public static boolean CreateBonfire(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			BlockPos blockPos = new BlockPos(
					Mth.floor(targetPos.x),
					Mth.floor(targetPos.y),
					Mth.floor(targetPos.z)
			);
			if (caster instanceof Player) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(18));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				blockPos = hitResult.getBlockPos();
			}
			//目标位置
			MagicBonfireEntity cloud = new MagicBonfireEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(blockPos.getCenter().add(0,0.5f,0));
			if (serverLevel.getBlockState(blockPos).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
				cloud.setSoulFire(true);
			}
			cloud.setOwnerUUID(caster.getUUID());
			serverLevel.addFreshEntity(cloud);
			SpellType.startConcentration(caster, cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//疫病虫群$法术
	public static boolean InsectPlague(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(90));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 90);
			}
			//目标位置
			InsectPlagueEntity cloud = new InsectPlagueEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwnerUUID(caster.getUUID());
			serverLevel.addFreshEntity(cloud);
			SpellType.startConcentration(caster, cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//植物滋长$法术
	public static boolean PlantGrowth(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			int reach = 30;
			if (subcategory == 0) {
				reach = 9;
			}
			else if (subcategory == 1) {
				reach = 18;
			}
			else if (subcategory == 2) {
				reach = 30;
			}
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(90));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 90);
			}
			ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y - 2, targetPos.z);
			cloud.setColorI(0x487f4f);
			cloud.setColorII(0x67a124);
			cloud.setColorIII(0x1d3822);
			cloud.setColorIV(0x528919);
			cloud.setSize(5);
			cloud.setTargetHeight(5);
			cloud.setOwnerUUID(caster.getUUID());
			cloud.setTarget(null);
			serverLevel.addFreshEntity(cloud);
			//目标位置
			AABB aABB = new AABB(targetPos.x + reach, targetPos.y + reach, targetPos.z + reach, targetPos.x - reach, targetPos.y - reach, targetPos.z - reach).move(0, -2, 0);
			for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
				if (blockPos.getCenter().distanceTo(targetPos) < 36) {
					BlockState blockState = serverLevel.getBlockState(blockPos);
					if (blockState.is(BlockTags.DIRT) &&
							serverLevel.getBlockState(blockPos.above()).getFluidState().isEmpty() &&
							serverLevel.getBlockState(blockPos.above()).isAir() &&
							caster.getRandom().nextFloat() < 0.10f + (spellLevelDamage * 0.06f)) {
						BlockPos abovePos = blockPos.above();
						serverLevel.setBlock(abovePos, Blocks.SWEET_BERRY_BUSH.defaultBlockState(), 3);
					}
					if (blockState.is(BlockTags.SAND) &&
							serverLevel.getBlockState(blockPos.above()).getFluidState().isEmpty() &&
							serverLevel.getBlockState(blockPos.above()).isAir() &&
							serverLevel.getBlockState(blockPos.above().south()).isAir() &&
							serverLevel.getBlockState(blockPos.above().north()).isAir() &&
							serverLevel.getBlockState(blockPos.above().east()).isAir() &&
							serverLevel.getBlockState(blockPos.above().west()).isAir() &&
							caster.getRandom().nextFloat() < 0.10f + (spellLevelDamage * 0.06f)) {
						serverLevel.setBlock(blockPos.above(), Blocks.CACTUS.defaultBlockState(), 3);
						BlockPos abovePos = blockPos.above().above();

						if (serverLevel.getBlockState(abovePos).getFluidState().isEmpty() &&
								serverLevel.getBlockState(abovePos).isAir() &&
								serverLevel.getBlockState(abovePos.south()).isAir() &&
								serverLevel.getBlockState(abovePos.north()).isAir() &&
								serverLevel.getBlockState(abovePos.east()).isAir() &&
								serverLevel.getBlockState(abovePos.west()).isAir() &&
								caster.getRandom().nextFloat() < 0.80f + (spellLevelDamage * 0.04f)) {
							serverLevel.setBlock(abovePos, Blocks.CACTUS.defaultBlockState(), 3);
							abovePos = blockPos.above().above().above();

							if (serverLevel.getBlockState(abovePos).getFluidState().isEmpty() &&
									serverLevel.getBlockState(abovePos).isAir() &&
									serverLevel.getBlockState(abovePos.south()).isAir() &&
									serverLevel.getBlockState(abovePos.north()).isAir() &&
									serverLevel.getBlockState(abovePos.east()).isAir() &&
									serverLevel.getBlockState(abovePos.west()).isAir() &&
									caster.getRandom().nextFloat() < 0.40f + (spellLevelDamage * 0.03f)) {
								serverLevel.setBlock(abovePos, Blocks.CACTUS.defaultBlockState(), 3);
								abovePos = blockPos.above().above().above().above();

								if (serverLevel.getBlockState(abovePos).getFluidState().isEmpty() &&
										serverLevel.getBlockState(abovePos).isAir() &&
										serverLevel.getBlockState(abovePos.south()).isAir() &&
										serverLevel.getBlockState(abovePos.north()).isAir() &&
										serverLevel.getBlockState(abovePos.east()).isAir() &&
										serverLevel.getBlockState(abovePos.west()).isAir() &&
										caster.getRandom().nextFloat() < 0.10f + (spellLevelDamage * 0.02f)) {
									serverLevel.setBlock(abovePos, Blocks.CACTUS.defaultBlockState(), 3);
								}
							}
						}
					}
				}
			}
			for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
				if (blockPos.getCenter().distanceTo(targetPos) < 36) {
					if (BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), serverLevel, blockPos) || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), serverLevel, blockPos, null)) {
						if (!serverLevel.isClientSide()) {
							serverLevel.levelEvent(1505, blockPos, 0);
						}
					}
				}
			}
		}
		return true;
	}
	//植物凋零$法术
	public static boolean DiminishPlants(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			int reach = 30;
			if (subcategory == 0) {
				reach = 9;
			}
			else if (subcategory == 1) {
				reach = 18;
			}
			else if (subcategory == 2) {
				reach = 30;
			}
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(90));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 90);
			}
			ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y - 2, targetPos.z);
			cloud.setColorI(0x513d24);
			cloud.setColorII(0x946428);
			cloud.setColorIII(0xa37d48);
			cloud.setColorIV(0xba9c68);
			cloud.setSize(5);
			cloud.setTargetHeight(5);
			cloud.setOwnerUUID(caster.getUUID());
			cloud.setTarget(null);
			serverLevel.addFreshEntity(cloud);
			//目标位置
			AABB aABB = new AABB(targetPos.x + reach, targetPos.y + reach, targetPos.z + reach, targetPos.x - reach, targetPos.y - reach, targetPos.z - reach).move(0, -2, 0);

			for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
				if (blockPos.getCenter().distanceTo(targetPos) < 36) {
					if (serverLevel.getBlockState(blockPos).is(BlockTags.FLOWERS) ||
							serverLevel.getBlockState(blockPos).getBlock() instanceof BushBlock ||
							serverLevel.getBlockState(blockPos).getBlock() instanceof LeavesBlock ||
							serverLevel.getBlockState(blockPos).getBlock() instanceof IPlantable) {
						if (new ItemStack(serverLevel.getBlockState(blockPos).getBlock()).getRarity() == Rarity.EPIC) continue;
						BlockState blockState = serverLevel.getBlockState(blockPos);
						float block = blockState.getDestroySpeed(serverLevel, blockPos);
						if (block >= 2 || block < 0f) continue;
						if (!ForgeEventFactory.onEntityDestroyBlock(caster, blockPos, blockState)) continue;
						if (!ForgeHooks.canEntityDestroy(serverLevel, blockPos, caster)) continue;
						serverLevel.destroyBlock(blockPos, !serverLevel.getBlockState(blockPos).is(BlockTags.REPLACEABLE_BY_TREES), caster);
					}
				}
			}
		}
		return true;
	}
	//火球术$法术
	public static boolean Fireball(LivingEntity caster, LivingEntity target, int spellLevelDamage, int subcategory) {
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
			cloud.setSubcategory(subcategory);
			cloud.setStartPos(new Vec3(caster.getX(), caster.getY(0.7), caster.getZ()));
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwner(caster);
			serverLevel.addFreshEntity(cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//冰风暴$法术
	public static boolean IceStorm(LivingEntity caster, LivingEntity target, int spellLevelDamage, int subcategory) {
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
			IceStormEntity cloud = new IceStormEntity(serverLevel, caster);
			cloud.setSubcategory(subcategory);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y + 2/16f, targetPos.z);
			cloud.setOwner(caster);
			serverLevel.addFreshEntity(cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//雷鸣波$法术
	public static boolean Thunderwave(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (caster instanceof Mob mob && target != null) {
				mob.lookAt(target, 360.0f, 360.0f);
			}
			//目标位置
			Vec3 look = caster.getLookAngle().normalize();
			Vec3 targetPos = caster.position()
					.add(0, caster.getBbHeight() * 0.25 + 2 / 16f, 0)
					.add(look.scale(2.25));

			ThunderwaveEntity cloud = new ThunderwaveEntity(serverLevel, caster);

			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y, targetPos.z);
			cloud.setYRot(caster.getYRot());
			cloud.setXRot(caster.getXRot());
			cloud.setOwner(caster);
			cloud.setTarget(caster);
			serverLevel.addFreshEntity(cloud);
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//召唤恼鬼$法术
	public static boolean ConjureVexs(LivingEntity caster, int countMin, int countMax, int summonDistance) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PlayerTeam teams = (PlayerTeam) caster.getTeam();
			int count = countMin;
			if (countMin < countMax) {
				count = Main.randomReach(caster.getRandom(), countMin, countMax);
			}
			for (int i = 0; i < count; ++i) {
				BlockPos summonPos = Main.findSpawnPositionNearFillOnBlock(caster, summonDistance);
				JerotesVexEntity jerotesVexEntity = JerotesEntityType.JEROTES_VEX.get().spawn(serverLevel, BlockPos.containing(summonPos.getX(), summonPos.getY(), summonPos.getZ()), MobSpawnType.MOB_SUMMONED);
				if (jerotesVexEntity != null) {
					Main.conjureAbout(caster, jerotesVexEntity);
					//jerotesVexEntity.setMaxLifeTick(20 * (30 + caster.getRandom().nextInt(90)));
					jerotesVexEntity.setMaxLifeTick(20 * 30);
					jerotesVexEntity.setNotPersistent(true);
					jerotesVexEntity.setLifeTick(0);
					jerotesVexEntity.setTame(true);
					jerotesVexEntity.setOwnerUUID(caster.getUUID());
					jerotesVexEntity.setOrderedToSit(false);
					if (teams != null) {
						serverLevel.getScoreboard().addPlayerToTeam(jerotesVexEntity.getStringUUID(), teams);
					}
					if (caster instanceof Mob mob && mob.getTarget() != null) {
						jerotesVexEntity.setTarget(mob.getTarget());
					}
					ParticlesUse.summonParticle(serverLevel, jerotesVexEntity, jerotesVexEntity.getX(), jerotesVexEntity.getY(), jerotesVexEntity.getZ(), 0x7992aa, 0xa2bcd5);
				}
			}
			serverLevel.gameEvent(GameEvent.ENTITY_PLACE, new BlockPos((int) caster.getX(), (int) caster.getY(), (int) caster.getZ()), GameEvent.Context.of(caster));
		}
		return true;
	}
	//线形唤魔者尖牙$法术
	public static boolean LinearEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			boolean sameEntity = caster == target;
			double baseY = sameEntity ? caster.getY() : Math.min(target.getY(), caster.getY());
			double maxY = sameEntity ? caster.getY() + 1.0 : Math.max(target.getY(), caster.getY()) + 1.0;
			float yRotRad = sameEntity ?
					(caster.getYRot() + 90) * ((float)Math.PI / 180F) :
					(float)Mth.atan2(target.getZ() - caster.getZ(), target.getX() - caster.getX());
			for (int i = 0; i < 16; ++i) {
				double offset = 1.25 * (i + 1);
				double x = caster.getX() + (double) Mth.cos(yRotRad) * offset;
				double z = caster.getZ() + (double) Mth.sin(yRotRad) * offset;
				LinearEvokerFang(caster, target, spellLevelDamage, x, z, baseY, maxY, yRotRad, i);
			}
		}
		return true;
	}
	static void LinearEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage, double d, double d2, double d3, double d4, float f, int delay) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			PlayerTeam teams = (PlayerTeam) caster.getTeam();
			BlockPos blockPos = BlockPos.containing(d, d4, d2);
			boolean bl = false;
			double d5 = 0.0;
			do {
				BlockState blockState;
				VoxelShape voxelShape;
				BlockPos blockPos2 = blockPos.below();
				BlockState blockState2 = caster.level().getBlockState(blockPos2);
				if (!blockState2.isFaceSturdy(caster.level(), blockPos2, Direction.UP)) continue;
				if (!caster.level().isEmptyBlock(blockPos) && !(voxelShape = (blockState = caster.level().getBlockState(blockPos)).getCollisionShape(caster.level(), blockPos)).isEmpty()) {
					d5 = voxelShape.max(Direction.Axis.Y);
				}
				bl = true;
				break;
			} while ((blockPos = blockPos.below()).getY() >= Mth.floor(d3) - 1);
			if (bl) {
				JerotesEvokerFangEntity evokerFangEntity = new JerotesEvokerFangEntity(caster.level(), d, (double) blockPos.getY() + d5, d2, f, delay, caster);
				evokerFangEntity.setSpellLevelDamage(spellLevelDamage);
				evokerFangEntity.setOwner(caster);
				if (teams != null) {
					serverLevel.getScoreboard().addPlayerToTeam(evokerFangEntity.getStringUUID(), teams);
				}
				caster.level().addFreshEntity(evokerFangEntity);
			}
		}
	}

	//环形唤魔者尖牙$法术
	public static boolean CircularEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			int n;
			for (n = 0; n < 5; ++n) {
				float f2 = 4 + (float) n * (float) Math.PI * 2.0f / 5.0f;
				SpellFind.CircularEvokerFang(caster, target, spellLevelDamage, caster.getX() + (double) Mth.cos(f2) * 1.5, caster.getZ() + (double) Mth.sin(f2) * 1.5, caster.getY(), caster.getY() + 1, f2, 0);
			}
			for (n = 0; n < 8; ++n) {
				float f2 = 4 + (float) n * (float) Math.PI * 2.0f / 8.0f + 1.2566371f;
				SpellFind.CircularEvokerFang(caster, target, spellLevelDamage, caster.getX() + (double) Mth.cos(f2) * 2.5, caster.getZ() + (double) Mth.sin(f2) * 2.5, caster.getY(), caster.getY() + 1, f2, 3);
			}
		}
		return true;
	}
	static void CircularEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage, double d, double d2, double d3, double d4, float f, int delay) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BlockPos blockPos = BlockPos.containing(d, d4, d2);
			PlayerTeam teams = (PlayerTeam) caster.getTeam();
			boolean bl = false;
			double d5 = 0.0;
			do {
				BlockState blockState;
				VoxelShape voxelShape;
				BlockPos blockPos2 = blockPos.below();
				BlockState blockState2 = caster.level().getBlockState(blockPos2);
				if (!blockState2.isFaceSturdy(caster.level(), blockPos2, Direction.UP)) continue;
				if (!caster.level().isEmptyBlock(blockPos) && !(voxelShape = (blockState = caster.level().getBlockState(blockPos)).getCollisionShape(caster.level(), blockPos)).isEmpty()) {
					d5 = voxelShape.max(Direction.Axis.Y);
				}
				bl = true;
				break;
			} while ((blockPos = blockPos.below()).getY() >= Mth.floor(d3) - 1);
			if (bl) {
				JerotesEvokerFangEntity evokerFangEntity = new JerotesEvokerFangEntity(caster.level(), d, (double)blockPos.getY() + d5, d2, f, delay, caster);
				evokerFangEntity.setSpellLevelDamage(spellLevelDamage);
				evokerFangEntity.setOwner(caster);
				if (teams != null) {
					serverLevel.getScoreboard().addPlayerToTeam(evokerFangEntity.getStringUUID(), teams);
				}
				caster.level().addFreshEntity(evokerFangEntity);
			}
		}
	}
	//律令死亡
	public static boolean PowerWordKill(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target != null) {
				Vec3 vec3 = caster.getEyePosition();
				Vec3 vec32 = target.getEyePosition().subtract(vec3);
				Vec3 vec33 = vec32.normalize();
				for (int i = 1; i < Mth.floor(vec32.length()) * 6 + 1; ++i) {
					Vec3 vec34 = vec3.add(vec33.scale(i / 6f));
					serverLevel.sendParticles(ParticleTypes.ENCHANT, vec34.x, vec34.y, vec34.z, 1, 0.0, 0.0, 0.0, 0.0);
				}
			}
			PowerWordKillEntity cloud = new PowerWordKillEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(target.getX(), target.getY() + 2/16f, target.getZ());
			cloud.setOwnerUUID(caster.getUUID());
			cloud.setTarget(target);
			cloud.setSize(Main.mobWidth(target));
			cloud.setTargetHeight(Main.mobHeight(target));
			serverLevel.addFreshEntity(cloud);
		}
		return true;
	}
	//探知眼
	public static boolean ProbingEye(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target != null) {
				if (caster instanceof Player player) {
					if (target instanceof LivingEntity livingEntity2) {
						if (player instanceof ServerPlayer serverPlayer) {
							Main.openMobInventoryGui(serverPlayer, livingEntity2, false, false, false, false, false, false, false, false);
						}
					}
				}
			}
		}
		return true;
	}
	//元素契约术
	public static boolean ElementalContract(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target instanceof LivingEntity livingEntity && livingEntity instanceof ContractableEntity contractableEntity && contractableEntity.isCanContract()) {
				if (EntityFactionFind.isElemental(livingEntity)) {
					if (target instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() == caster && !contractableEntity.isNotPersistent()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.no_contract_required");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						return contractableEntity.contractUse(caster);
					}
					if (spellLevelDamage >= contractableEntity.contractShouldLevel()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.contract");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(), 0xc4a881, 0xffffff);
						return contractableEntity.contractUse(caster);
					}
					else {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.can_not_contract_level");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
			}
			if (caster instanceof Player player) {
				Component component = Component.translatable("spell.jerotes.message.can_not_contract_type");
				player.sendSystemMessage(component);
				player.displayClientMessage(component, true);
			}
		}
		return false;
	}
	//妖精契约术
	public static boolean FeyContract(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target instanceof LivingEntity livingEntity && livingEntity instanceof ContractableEntity contractableEntity && contractableEntity.isCanContract()) {
				if (EntityFactionFind.isFey(livingEntity)) {
					if (target instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() == caster && !contractableEntity.isNotPersistent()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.no_contract_required");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						return contractableEntity.contractUse(caster);
					}
					if (spellLevelDamage >= contractableEntity.contractShouldLevel()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.contract");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(), 0xc4a881, 0xffffff);
						return contractableEntity.contractUse(caster);
					}
					else {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.can_not_contract_level");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
			}
			if (caster instanceof Player player) {
				Component component = Component.translatable("spell.jerotes.message.can_not_contract_type");
				player.sendSystemMessage(component);
				player.displayClientMessage(component, true);
			}
		}
		return false;
	}
	//构造契约术
	public static boolean ConstructContract(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target instanceof LivingEntity livingEntity && livingEntity instanceof ContractableEntity contractableEntity && contractableEntity.isCanContract()) {
				if (EntityFactionFind.isConstruct(livingEntity)) {
					if (target instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() == caster && !contractableEntity.isNotPersistent()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.no_contract_required");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						return contractableEntity.contractUse(caster);
					}
					if (spellLevelDamage >= contractableEntity.contractShouldLevel()) {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.contract");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
						ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(), 0xc4a881, 0xffffff);
						return contractableEntity.contractUse(caster);
					}
					else {
						if (caster instanceof Player player) {
							Component component = Component.translatable("spell.jerotes.message.can_not_contract_level");
							player.sendSystemMessage(component);
							player.displayClientMessage(component, true);
						}
					}
				}
			}
			if (caster instanceof Player player) {
				Component component = Component.translatable("spell.jerotes.message.can_not_contract_type");
				player.sendSystemMessage(component);
				player.displayClientMessage(component, true);
			}
		}
		return false;
	}
	//造水术$法术
	public static boolean CreateWater(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(9));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 9);
			}


			if (subcategory == 5) {
				if (target != null && target != caster) {
					targetPos = target.position();
				}
				ThrownPotion potion = new ThrownPotion(serverLevel, targetPos.x, targetPos.y + 0.2f, targetPos.z);
				ItemStack waterBottle = new ItemStack(Items.SPLASH_POTION);
				PotionUtils.setPotion(waterBottle, Potions.WATER);
				potion.setItem(waterBottle);
				potion.setOwner(caster);
				BlockPos pos = BlockPos.containing(targetPos);
				BlockHitResult hitResult = new BlockHitResult(
						targetPos,
						Direction.UP,
						pos.below(),
						false
				);
				try {
					Method onHitMethod = ThrownPotion.class.getDeclaredMethod("onHit", HitResult.class);
					onHitMethod.setAccessible(true);
					onHitMethod.invoke(potion, hitResult);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelDamage);
				cloud.setPos(targetPos.x, targetPos.y, targetPos.z);
				cloud.setColorI(0x9edbff);
				cloud.setColorII(0x58a1e9);
				cloud.setColorIII(0x2c4173);
				cloud.setColorIV(0x232b5c);
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(null);
				int xzreach = 0;
				int yreach = 0;
				int move = 1;
				if (subcategory == 4) {
					xzreach = 1;
				}
				if (subcategory == 1 && spellLevelDamage >= 3) {
					xzreach = 1;
				}
				if (subcategory == 2 && spellLevelDamage >= 3) {
					xzreach = 1;
				}
				if (subcategory == 2 && spellLevelDamage >= 5) {
					yreach = 1;
					move = 2;
				}
				cloud.setSize(1 + xzreach * 2);
				cloud.setTargetHeight(1 + yreach * 2);
				serverLevel.addFreshEntity(cloud);

				//目标位置
				AABB aABB = new AABB(targetPos.x + xzreach, targetPos.y + yreach, targetPos.z + xzreach, targetPos.x - xzreach, targetPos.y - yreach, targetPos.z - xzreach).move(0, move, 0);

				AABB aABB2 = new AABB(targetPos.x, targetPos.y, targetPos.z, targetPos.x, targetPos.y, targetPos.z).move(0, move - 1, 0);
				boolean bl = false;
				for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB2.minX), Mth.floor(aABB2.minY), Mth.floor(aABB2.minZ), Mth.floor(aABB2.maxX), Mth.floor(aABB2.maxY), Mth.floor(aABB2.maxZ))) {
					BlockState state = serverLevel.getBlockState(blockPos);
					Block block = state.getBlock();
					if (state.getBlock() instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
						if (!state.getValue(BlockStateProperties.WATERLOGGED)) {
							bl = true;
						}
					}
					if (block == Blocks.CAULDRON) {
						bl = true;
					} else if (block == Blocks.WATER_CAULDRON) {
						int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
						if (currentLevel < 3) {
							bl = true;
						}
					}
				}
				if (bl) {
					aABB = new AABB(targetPos.x + xzreach, targetPos.y + yreach, targetPos.z + xzreach, targetPos.x - xzreach, targetPos.y - yreach, targetPos.z - xzreach).move(0, move - 1, 0);
				}
				for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
					BlockState state = serverLevel.getBlockState(blockPos);
					Block block = state.getBlock();
					if (serverLevel.dimensionType().ultraWarm()) {
						int i = blockPos.getX();
						int j = blockPos.getY();
						int k = blockPos.getZ();
						serverLevel.playSound(caster, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
								2.6F + (serverLevel.random.nextFloat() - serverLevel.random.nextFloat()) * 0.8F);

						for(int l = 0; l < 8; ++l) {
							serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
						}
					}
					else {
						if (subcategory == 3 || subcategory == 4) {
							if (state.isAir()) {
								serverLevel.setBlock(blockPos, Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 1), 3);
							} else if (block == Blocks.CAULDRON) {
								serverLevel.setBlock(blockPos, Blocks.WATER_CAULDRON.defaultBlockState()
										.setValue(LayeredCauldronBlock.LEVEL, 1), 3);
							} else if (block == Blocks.WATER_CAULDRON) {
								int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
								if (currentLevel < 3) {
									serverLevel.setBlock(blockPos, state.setValue(LayeredCauldronBlock.LEVEL, currentLevel + 1), 3);
								}
							}
						} else {
							if (state.isAir()) {
								serverLevel.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
							} else if (state.getBlock() instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
								if (!state.getValue(BlockStateProperties.WATERLOGGED)) {
									serverLevel.setBlock(blockPos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
								}
							} else if (block == Blocks.CAULDRON) {
								serverLevel.setBlock(blockPos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 3);
							} else if (block == Blocks.WATER_CAULDRON) {
								int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
								if (currentLevel < 3) {
									serverLevel.setBlock(blockPos, state.setValue(LayeredCauldronBlock.LEVEL, 3), 3);
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	//枯水术$法术
	public static boolean DestroyWater(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			Vec3 targetPos = caster.getPosition(0);
			if (target != null) {
				targetPos = new Vec3(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
			}
			if (caster instanceof Player && (target == null || target == caster)) {
				Vec3 startPos = caster.getEyePosition(1.0f);
				Vec3 viewVector = caster.getViewVector(1.0f);
				Vec3 endPos = startPos.add(viewVector.scale(9));

				BlockHitResult hitResult = serverLevel.clip(new ClipContext(
						startPos, endPos,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.WATER,
						caster
				));
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 9, 0.2f).add(0,-1,0);
			}
			ColorMagicWindEntity cloud = new ColorMagicWindEntity(serverLevel, caster);
			cloud.setSpellLevelDamage(spellLevelDamage);
			cloud.setPos(targetPos.x, targetPos.y, targetPos.z);
			cloud.setColorI(0x9edbff);
			cloud.setColorII(0x58a1e9);
			cloud.setColorIII(0x2c4173);
			cloud.setColorIV(0x232b5c);
			cloud.setOwnerUUID(caster.getUUID());
			cloud.setTarget(null);
			int xzreach = 0;
			int yreach = 0;
			int move = 1;
			if (subcategory == 1 && spellLevelDamage >= 3) {
				xzreach = 1;
			}
			if (subcategory == 2 && spellLevelDamage >= 3) {
				xzreach = 1;
			}
			if (subcategory == 2 && spellLevelDamage >= 5) {
				yreach = 1;
				move = 0;
			}
			cloud.setSize(1 + xzreach * 2);
			cloud.setTargetHeight(1 + yreach * 2);
			serverLevel.addFreshEntity(cloud);

			//目标位置
			AABB aABB = new AABB(targetPos.x + xzreach, targetPos.y + yreach, targetPos.z + xzreach, targetPos.x - xzreach, targetPos.y - yreach, targetPos.z - xzreach).move(0, move, 0);

			AABB aABB2 = new AABB(targetPos.x, targetPos.y, targetPos.z, targetPos.x, targetPos.y, targetPos.z).move(0, move - 1, 0);
			boolean bl = false;
			for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB2.minX), Mth.floor(aABB2.minY), Mth.floor(aABB2.minZ), Mth.floor(aABB2.maxX), Mth.floor(aABB2.maxY), Mth.floor(aABB2.maxZ))) {
				BlockState state = serverLevel.getBlockState(blockPos);
				Block block = state.getBlock();
				if (state.getBlock() instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
					if (!state.getValue(BlockStateProperties.WATERLOGGED)) {
						bl = true;
					}
				}
				if (block == Blocks.CAULDRON) {
					bl = true;
				} else if (block == Blocks.WATER_CAULDRON) {
					int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
					if (currentLevel < 3) {
						bl = true;
					}
				}
			}
			if (bl) {
				aABB = new AABB(targetPos.x + xzreach, targetPos.y + yreach, targetPos.z + xzreach, targetPos.x - xzreach, targetPos.y - yreach, targetPos.z - xzreach).move(0, move - 1, 0);
			}
			for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
				BlockState state = serverLevel.getBlockState(blockPos);
				Block block = state.getBlock();
				if (block == Blocks.WATER) {
					serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
				}
				else if (block instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
					if (state.getValue(BlockStateProperties.WATERLOGGED)) {
						serverLevel.setBlock(blockPos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
					}
				}
				else if (block == Blocks.WATER_CAULDRON) {
					serverLevel.setBlock(blockPos, Blocks.CAULDRON.defaultBlockState(), 3);
				}
			}
		}
		return true;
	}
	//凋灵之首
	public static boolean WitherSkull(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelExplode, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			JerotesWitherSkullEntity witherSkull;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				witherSkull = new JerotesWitherSkullEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, spellLevelExplode, subcategory, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				witherSkull.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				witherSkull.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				witherSkull.setOwner(caster);
				serverLevel.addFreshEntity(witherSkull);
			}
		}
		return true;
	}
	//危险凋灵之首
	public static boolean DangerousWitherSkull(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, float spellLevelExplode, float spellLevelAccuracy, int count, float distance, boolean isPlayer, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			JerotesWitherSkullEntity witherSkull;
			for (int i = 0; i < count; ++i) {
				if (!isPlayer && caster instanceof Mob mob && target != null) {
					mob.lookAt(target, 360.0f, 360.0f);
				}
				witherSkull = new JerotesWitherSkullEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, spellLevelExplode, subcategory, serverLevel, caster, caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z);
				witherSkull.setDangerous(true);
				witherSkull.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
				witherSkull.shootFromRotation(caster, caster.getXRot(), (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
				witherSkull.setOwner(caster);
				serverLevel.addFreshEntity(witherSkull);
			}
		}
		return true;
	}
	//唤魔者伎俩
	public static boolean Evokercraft(LivingEntity caster, Entity target, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (subcategory == 0 || subcategory == 1 || subcategory == 2) {
				if (!(target instanceof LivingEntity livingEntity) || !EntityAndItemFind.hasColor(livingEntity)) {
					if (caster instanceof Player player) {
						Component component = Component.translatable("spell.jerotes.message.no_target_available");
						player.sendSystemMessage(component);
						player.displayClientMessage(component, true);
					}
					return false;
				}
				DyeColor dyeColor = DyeColor.WHITE;
				if (subcategory == 0) {
					ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(),
							11743532, 16711680);
					dyeColor = DyeColor.RED;
				}
				if (subcategory == 1) {
					ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(),
							14602026, 16776960);
					dyeColor = DyeColor.YELLOW;
				}
				if (subcategory == 2) {
					ParticlesUse.summonParticle(serverLevel, target, target.getX(), target.getY(), target.getZ(),
							2437522, 255);
					dyeColor = DyeColor.BLUE;
				}
				EntityAndItemFind.setColor(livingEntity, dyeColor);
			}

			if (subcategory == 3 || subcategory == 4 || subcategory == 5) {
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
				BlockPos blockPos = new BlockPos(
						Mth.floor(targetPos.x),
						Mth.floor(targetPos.y),
						Mth.floor(targetPos.z)
				);
				DyeColor dyeColor = DyeColor.WHITE;
				if (subcategory == 3) {
					ParticlesUse.summonParticle(serverLevel, caster, blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f,
							11743532, 16711680);
					dyeColor = DyeColor.RED;
				}
				if (subcategory == 4) {
					ParticlesUse.summonParticle(serverLevel, caster, blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f,
							14602026, 16776960);
					dyeColor = DyeColor.YELLOW;
				}
				if (subcategory == 5) {
					ParticlesUse.summonParticle(serverLevel, caster, blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f,
							2437522, 255);
					dyeColor = DyeColor.BLUE;
				}
				BlockState oldState = serverLevel.getBlockState(blockPos);
				Block newBlock = DyeBlock.getDyedVariant(oldState.getBlock(), dyeColor);


				if (newBlock == null || oldState.getBlock() == newBlock) {
					if (caster instanceof Player player) {
						Component component = Component.translatable("spell.jerotes.message.no_target_available");
						player.sendSystemMessage(component);
						player.displayClientMessage(component, true);
					}
					return false;
				}
				if (oldState.getBlock() instanceof BedBlock) {
					Direction dir = oldState.getValue(BedBlock.FACING);
					BedPart part = oldState.getValue(BedBlock.PART);

					BlockPos headPos = (part == BedPart.HEAD) ? blockPos : blockPos.relative(dir);
					BlockPos footPos = (part == BedPart.FOOT) ? blockPos : blockPos.relative(dir.getOpposite());

					BlockState headState = serverLevel.getBlockState(headPos);
					BlockState footState = serverLevel.getBlockState(footPos);

					int flags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

					BlockState state1 = newBlock.defaultBlockState()
							.setValue(BedBlock.FACING, headState.getValue(BedBlock.FACING))
							.setValue(BedBlock.PART, headState.getValue(BedBlock.PART))
							.setValue(BedBlock.OCCUPIED, headState.getValue(BedBlock.OCCUPIED));

					BlockState state2 = newBlock.defaultBlockState()
							.setValue(BedBlock.FACING, footState.getValue(BedBlock.FACING))
							.setValue(BedBlock.PART, footState.getValue(BedBlock.PART))
							.setValue(BedBlock.OCCUPIED, footState.getValue(BedBlock.OCCUPIED));

					serverLevel.setBlock(headPos, Blocks.AIR.defaultBlockState(), flags);
					serverLevel.setBlock(footPos, Blocks.AIR.defaultBlockState(), flags);
					serverLevel.setBlock(headPos, state1, 18);
					serverLevel.setBlock(footPos, state2, 18);
				} else {
					serverLevel.setBlock(blockPos, newBlock.withPropertiesOf(oldState), 3);
				}
			}
		}
		return true;
	}
	//监守者音爆
	public static boolean WardenSonicBoom(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target == null) {
				if (!(caster instanceof Player player))
					return false;
				if (Main.getTargetedEntity(player, 20) != null && Main.getTargetedEntity(player, 20) instanceof LivingEntity targets) {
					target = targets;
				}
			}
			if (!(target instanceof LivingEntity livingTarget) || target == null || target == caster) {
				if (caster instanceof Player player) {
					Component component = Component.translatable("spell.jerotes.message.no_target_available");
					player.sendSystemMessage(component);
					player.displayClientMessage(component, true);
				}
				return false;
			}
			double dx = target.getX() - caster.getX();
			double dz = target.getZ() - caster.getZ();
			double dy = target.getY() - caster.getY() - 1.6;
			if (dx * dx + dz * dz > 15.0 * 15.0 || Math.abs(dy) > 20.0) {
				return false;
			}
			if (caster instanceof Mob mob) {
				mob.getLookControl().setLookAt(target.position());
			}
			serverLevel.broadcastEntityEvent(caster, (byte)62);
			caster.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
			Vec3 start = caster.position().add(0.0, Main.mobHeight(caster) * (1.6f/2.9f), 0.0);
			Vec3 end = livingTarget.getEyePosition();
			Vec3 dir = end.subtract(start);
			Vec3 norm = dir.normalize();
			int steps = Mth.floor(dir.length()) + 7;
			for (int i = 1; i < steps; i++) {
				Vec3 pos = start.add(norm.scale(i));
				serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
			}
			//caster.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
			float damage = 10 + (spellLevelDamage - 2) * 5;
			livingTarget.hurt(serverLevel.damageSources().sonicBoom(caster), damage);
			double knockbackResist = livingTarget.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
			double horiz = 2.5 * (1.0 - knockbackResist);
			double vert = 0.5 * (1.0 - knockbackResist);
			livingTarget.push(norm.x() * horiz, norm.y() * vert, norm.z() * horiz);
			return true;
		}
		return true;
	}
	//红石激光
	public static boolean RedstoneLaser(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (!(caster.level() instanceof ServerLevel serverLevel)) {
			return true;
		}
		Vec3 targetPos = caster.getPosition(0);
		BlockPos blockPos = BlockPos.containing(targetPos);
		if (caster instanceof Player) {
			Vec3 startPos = caster.getEyePosition(1.0f);
			Vec3 viewVector = caster.getViewVector(1.0f);
			Vec3 endPos = startPos.add(viewVector.scale(45));
			BlockHitResult hitResult = serverLevel.clip(new ClipContext(
					startPos, endPos,
					ClipContext.Block.OUTLINE,
					ClipContext.Fluid.NONE,
					caster
			));

			if (hitResult.getType() == HitResult.Type.BLOCK) {
				blockPos = hitResult.getBlockPos();
			} else {
				targetPos = Main.adjustPositionForSolidHit(hitResult, startPos, viewVector, 45);
				blockPos = BlockPos.containing(targetPos);
			}
		}

		BlockState blockState = serverLevel.getBlockState(blockPos);
		Block block = blockState.getBlock();
		boolean bl = false;

		if (!blockState.isAir()) {
			BlockPos placePos = null;
			Direction placedDir = null;
			Direction preferredDir = null;
			Direction avoidedDir = null;

			if (block instanceof RedstoneTorchBlock) {
				preferredDir = Direction.DOWN;
			} else if (blockState.hasProperty(BlockStateProperties.FACING)) {
				avoidedDir = blockState.getValue(BlockStateProperties.FACING);
			} else if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
				avoidedDir = blockState.getValue(HorizontalDirectionalBlock.FACING);
			}

			if (preferredDir != null) {
				BlockPos candidate = blockPos.relative(preferredDir);
				if (serverLevel.getBlockState(candidate).isAir()) {
					placePos = candidate;
					placedDir = preferredDir;
				}
			}

			if (placePos == null) {
				for (Direction dir : Direction.values()) {
					if (dir == avoidedDir) continue;
					BlockPos candidate = blockPos.relative(dir);
					if (serverLevel.getBlockState(candidate).isAir()) {
						placePos = candidate;
						placedDir = dir;
						break;
					}
				}
			}

			if (placePos == null) {
				for (Direction dir : Direction.values()) {
					BlockPos candidate = blockPos.relative(dir);
					if (serverLevel.getBlockState(candidate).isAir()) {
						placePos = candidate;
						placedDir = dir;
						break;
					}
				}
			}

			if (placePos != null) {
				serverLevel.setBlock(placePos,
						JerotesBlocks.REDSTONE_LASER.get().defaultBlockState()
								.setValue(RedstoneLaserBlock.FACING, placedDir),
						3);
				serverLevel.updateNeighborsAt(blockPos, block);
				bl = true;
			}
		}

		if (bl) {
			Vec3 start = caster.position().add(0, Main.mobHeight(caster) * 0.7f, 0);
			Vec3 end = blockPos.getCenter();
			Vec3 dirVec = end.subtract(start);
			Vec3 norm = dirVec.normalize();
			int steps = Mth.floor(dirVec.length() * 6) + 1;
			for (int i = 1; i < steps; ++i) {
				Vec3 point = start.add(norm.scale(i / 6f));
				serverLevel.sendParticles(DustParticleOptions.REDSTONE, point.x, point.y, point.z, 1, 0, 0, 0, 0);
			}
		} else {
			if (caster instanceof Player player) {
				Component component = Component.translatable("spell.jerotes.message.no_target_available");
				player.sendSystemMessage(component);
				player.displayClientMessage(component, true);
			}
		}
		return true;
	}
	//即刻锻造
	public static boolean InstantForge(LivingEntity caster, int spellLevelDamage, int subcategory) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (caster instanceof Player player) {
				//工作台
				if (subcategory == 0) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new CraftingMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.crafting")
					));
					return true;
				}
				//锻造台
				else if (subcategory == 1) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new SmithingMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.upgrade")
					));
					return true;
				}
				//切石机
				else if (subcategory == 2) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new StonecutterMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.stonecutter")
					));
					return true;
				}
				//制图台
				else if (subcategory == 3) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new CartographyTableMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.cartography_table")
					));
					return true;
				}
				//织布机
				else if (subcategory == 4) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new LoomMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.loom")
					));
					return true;
				}
				//砂轮（2环及以上）
				else if (subcategory == 5 && spellLevelDamage >= 2) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new GrindstoneMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.grindstone_title")
					));
					return true;
				}
				//铁砧（3环及以上）
				else if (subcategory == 6 && spellLevelDamage >= 3) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new AnvilMenu(id, inv, ContainerLevelAccess.NULL),
							Component.translatable("container.repair")
					));
					return true;
				}
				//附魔台（5环及以上）
				else if (subcategory == 7 && spellLevelDamage >= 5) {
					player.openMenu(new SimpleMenuProvider(
							(id, inv, p) -> new SafeEnchantmentMenu(id, p),
							Component.translatable("container.enchant")
					));
					return true;
				}
			}
		}
		return false;
	}
	//末影箱
	public static boolean EnderChest(LivingEntity caster, int spellLevelDamage) {
		if (!(caster instanceof Player player)) {
			return false;
		}
		if (player.level().isClientSide) {
			return true;
		}
		player.openMenu(new SimpleMenuProvider(
				(id, inventory, p) -> ChestMenu.threeRows(id, inventory, player.getEnderChestInventory()),
				Component.translatable("container.enderchest")
		));
		player.awardStat(Stats.OPEN_ENDERCHEST);
		return true;
	}
	//锁定瞄准
	public static boolean LockAim(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (target != null) {
				Vec3 vec3 = caster.getEyePosition();
				Vec3 vec32 = target.getEyePosition().subtract(vec3);
				Vec3 vec33 = vec32.normalize();
				for (int i = 1; i < Mth.floor(vec32.length()) * 6 + 1; ++i) {
					Vec3 vec34 = vec3.add(vec33.scale(i / 6f));
					serverLevel.sendParticles(ParticleTypes.POOF, vec34.x, vec34.y, vec34.z, 1, 0.0, 0.0, 0.0, 0.0);
				}
				LockAimEntity cloud = new LockAimEntity(serverLevel, caster);
				cloud.setSpellLevelDamage(spellLevelDamage);
				cloud.setPos(target.getX(), target.getY() + 2/16f, target.getZ());
				cloud.setOwnerUUID(caster.getUUID());
				cloud.setTarget(target);
				serverLevel.addFreshEntity(cloud);
				SpellType.startConcentration(caster, cloud);
				return true;
			}
			if (caster instanceof Player player) {
				Component component = Component.translatable("spell.jerotes.message.no_target_available");
				player.sendSystemMessage(component);
				player.displayClientMessage(component, true);
			}
			return false;
		}
		return true;
	}
}