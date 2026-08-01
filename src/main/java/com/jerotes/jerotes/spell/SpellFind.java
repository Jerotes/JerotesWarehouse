package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.Mob.MirrorImageEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.*;
import com.jerotes.jerotes.entity.Other.ShootTargetSpell.FireballEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Breath.PoisonBreathEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.FireBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.FrostBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.ShockBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.WitchBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.*;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.*;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

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
					serverLevel.sendParticles(ParticleTypes.FLAME,
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
					serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
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
					serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
							pos.x, pos.y, pos.z,
							1, dx, dy, dz, 0.05);
				}
			}
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
					serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
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
	//虫群孳生
	public static boolean Infestation(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			InfestationEntity spell;
			spell = new InfestationEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
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
	//电爪
	public static boolean ShockingGrasp(LivingEntity caster, Entity target, int spellLevelDamage) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			ShockingGraspEntity spell;
			spell = new ShockingGraspEntity(spellLevelDamage, serverLevel, caster, 0, 0, 0);
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
				cloud.setPos(target.getX(), target.getEyeY(), target.getZ());
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
	//降咒
	public static boolean BestowCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BestowCurseEntity spell;
			spell = new BestowCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
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
	//失明诅咒
	public static boolean BlindnessCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			BlindnessCurseEntity spell;
			spell = new BlindnessCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
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
	//黑暗诅咒
	public static boolean DarknessCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			DarknessCurseEntity spell;
			spell = new DarknessCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
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
	//疲劳诅咒
	public static boolean FatigueCurse(LivingEntity caster, Entity target, int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			FatigueCurseEntity spell;
			spell = new FatigueCurseEntity(spellLevelDamage, spellLevelMainEffectTime, spellLevelMainEffectLevel, serverLevel, caster, 0, 0, 0);
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
				spell.setPos(target.getX(), target.getEyeY(), target.getZ());
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
	public static boolean ColdAbsorption(LivingEntity caster, float amount, double spellLevelPercentage, float spellLevelInterval) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (Main.getJerotesPersistentData(caster).getDouble("jerotes_cold_absorption") <= 0 || Main.getJerotesPersistentData(caster).getDouble("jerotes_cold_absorption_last_damage") < amount || Main.getJerotesPersistentData(caster).get("jerotes_cold_absorption") == null) {
				caster.heal((float) (amount * (spellLevelPercentage / 100)));
				for (int i = 0; i < 2; ++i) {
					serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, caster.getRandomX(1), caster.getRandomY(), caster.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
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
					Main.conjureAbout(caster, mirrorImageEntity);
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
	public static boolean PlantGrowth(LivingEntity caster, LivingEntity target, int spellLevelDamage, int subcategory) {
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
	public static boolean DiminishPlants(LivingEntity caster, LivingEntity target, int spellLevelDamage, int subcategory) {
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
	public static boolean LinearEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage, int count, float summonDistance) {
		if (caster.level() instanceof ServerLevel serverLevel) {
			boolean sameEntity = caster == target;
			double baseY = sameEntity ? caster.getY() : Math.min(target.getY(), caster.getY());
			double maxY = sameEntity ? caster.getY() + 1.0 : Math.max(target.getY(), caster.getY()) + 1.0;
			float yRotRad = sameEntity ?
					(caster.getYRot() + 90) * ((float)Math.PI / 180F) :
					(float)Mth.atan2(target.getZ() - caster.getZ(), target.getX() - caster.getX());
			for (int i = 0; i < count; ++i) {
				double offset = summonDistance * (i + 1);
				double x = caster.getX() + (double) Mth.cos(yRotRad) * offset;
				double z = caster.getZ() + (double) Mth.sin(yRotRad) * offset;
				LinearEvokerFang(caster, target, spellLevelDamage, x, z, baseY, maxY, yRotRad);
			}
		}
		return true;
	}
	static void LinearEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage, double d, double d2, double d3, double d4, float f) {
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
				JerotesEvokerFangEntity evokerFangEntity = new JerotesEvokerFangEntity(caster.level(), d, (double) blockPos.getY() + d5, d2, f, 0, caster);
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
			boolean sameEntity = caster == target;
			//全圈
			int n;
			for (n = 0; n < 8; ++n) {
				float f2 = 4 + (float) n * (float) Math.PI * 2.0f / 8.0f;
				SpellFind.CircularEvokerFang(caster, target, spellLevelDamage, caster.getX() + (double) Mth.cos(f2) * 1.5, caster.getZ() + (double) Mth.sin(f2) * 1.5, caster.getY(), caster.getY() + 1, f2);
			}
			for (n = 0; n < 12; ++n) {
				float f2 = 4 + (float) n * (float) Math.PI * 2.0f / 12.0f;
				SpellFind.CircularEvokerFang(caster, target, spellLevelDamage, caster.getX() + (double) Mth.cos(f2) * 2.5, caster.getZ() + (double) Mth.sin(f2) * 2.5, caster.getY(), caster.getY() + 1, f2);
			}
			for (n = 0; n < 16; ++n) {
				float f2 = 4 + (float) n * (float) Math.PI * 2.0f / 16.0f;
				SpellFind.CircularEvokerFang(caster, target, spellLevelDamage, caster.getX() + (double) Mth.cos(f2) * 3.5, caster.getZ() + (double) Mth.sin(f2) * 3.5, caster.getY(), caster.getY() + 1, f2);
			}
		}
		return true;
	}
	static void CircularEvokerFang(LivingEntity caster, LivingEntity target, int spellLevelDamage, double d, double d2, double d3, double d4, float f) {
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
				JerotesEvokerFangEntity evokerFangEntity = new JerotesEvokerFangEntity(JerotesEntityType.JEROTES_EVOKER_FANGS.get(), caster.level());
				evokerFangEntity.setSpellLevelDamage(spellLevelDamage);
				evokerFangEntity.setYRot(f * (180F / (float)Math.PI));
				evokerFangEntity.setPos(d, (double)blockPos.getY() + d5, d2);
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
}