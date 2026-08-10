package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.EliteEntity;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.forge.JerotesUseSpellEvent;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MagicSpell {
	public int spellLevel;
	public LivingEntity caster;
	public Entity target;
	public MagicType magicType;
	public MagicType magicType2;
	public String spellId;
	public ParticleOptions spellDisplayParticle;
	public SoundEvent spellSound;
	public int subcategory = 0;
	public MagicSpell(int spellLevel, LivingEntity caster, Entity target, MagicType magicType, MagicType magicType2, String spellId, ParticleOptions spellDisplayParticle) {
		this.spellLevel = spellLevel;
		this.caster = caster;
		this.target = target;
		this.magicType = magicType;
		this.magicType2 = magicType2;
		this.spellId = spellId;
		this.spellDisplayParticle = spellDisplayParticle;
	}
	public MagicSpell(int spellLevel, LivingEntity caster, Entity target, MagicType magicType, MagicType magicType2, String spellId, ParticleOptions spellDisplayParticle, SoundEvent spellSound) {
		this(spellLevel, caster, target, magicType, magicType2, spellId, spellDisplayParticle);
		this.spellSound = spellSound;
	}

	public boolean spellUse(int subcategory) {
		if (subcategory > this.getSubcategoryReach())
			subcategory = 0;
		this.subcategory = subcategory;
		return spellUse();
	}
	public boolean spellUse() {
		if (!canUse()) {
			return false;
		}
		if (getCaster() != null && SpellType.getCastingTickJerotes(getCaster()) >= 0 && !SpellType.getLastSpellTypeJerotes(getCaster()).isEmpty()) {
			if (!(getMagicType2() == MagicType.ADD && getCastingTicks() <= 0)) {
				SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getLastSpellTypeJerotes(getCaster()));
				MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
				magicSpell.spellCastingStop(SpellType.getCastingTickJerotes(getCaster()));
			}
		}
		//结束正在蓄力的技能
		Main.getJerotesPersistentData(getCaster()).putString("jerotes_using_magic", "");
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_spell_tick", 0);
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_using_magic_level", 0);
		Main.getJerotesPersistentData(getCaster()).putUUID("jerotes_using_magic_target", UUID.fromString("00000000-0000-0000-0000-000000000000"));
		//图标
		if (getCaster().level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(getSpellDisplayParticle(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
			serverLevel.sendParticles(getSpellLevelParticle(), caster.getX(), caster.getBoundingBox().maxY + 1.0, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
		}
		//音效
		if (this.getSound() != null && getCaster() != null && canUse()) {
			getCaster().playSound(this.getSound(),
					EntityAndItemFind.isBoss(getCaster().getType()) ? 5f : (getCaster() instanceof EliteEntity ? 2f : 1f) * getSoundLevel(), 1.0F);
		}
		//效果
		getCaster().level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.REMOVE_AFTER_SPELL).forEach(effect -> {
			if (getCaster().hasEffect(effect.get())) {
				if (!getCaster().level().isClientSide()) {
					getCaster().removeEffect(effect.get());
				}
			}
		});
		//专注停止
		if (getCaster().level() instanceof ServerLevel serverLevel) {
			if (!SpellType.getConcentrationSpellTypeJerotes(getCaster()).isEmpty()) {
				if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(getCaster())) instanceof ConcentrationSpellEntity concentrationSpellEntity && concentrationSpellEntity.spellEndConcentration()) {
					concentrationSpellEntity.endConcentration();
				}
			}
		}
		//等级高了
		if (getBaseSpellLevel() > spellLevel) {
			spellLevel = getBaseSpellLevel();
		}
		//记录自身上次使用的法术
		SpellType.setLastSpellTypeJerotes(getCaster(), getSpellModId() + "_" + getSpellId());
		SpellType.setLastSpellLevelJerotes(getCaster(), spellLevel);
		if (getTarget() != null) {
			SpellType.setLastSpellTargetJerotes(getCaster(), getTarget().getUUID());
		}
		//
		SpellType.setCastCooldownJerotes(getCaster(), Math.max(2, SpellType.getCastCooldownJerotes(caster)));
		//正在使用法术 探查
		if (!canEffect()) {
			return false;
		}
//		if (this.getCastingTicks() > 0) {
//			Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_spell_tick", this.getCastingTicks() + 5);
//			Main.getJerotesPersistentData(getCaster()).putString("jerotes_using_magic", getSpellModId() + "_" + getSpellId());
//			Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_using_magic_level", spellLevel);
//			Main.getJerotesPersistentData(getCaster()).putUUID("jerotes_using_magic_target", getTarget().getUUID());
//			spellAfterUse();
//			return true;
//		}
		//如果施法专注应该停止之前的施法专注
		if (isConcentration()) {
			//避免刚准备切换就清除
			if (getCaster() instanceof MagicSpellLivingEntity magicSpellLivingEntity) {
				magicSpellLivingEntity.setConcentrationSpellCleanTickJerotes(20);
			}
			//之前的专注实体的影响，新专注增加需后续处理
			SpellType.endConcentration(getCaster());
			//开始专注新法术
			SpellType.setConcentrationSpellTypeJerotes(getCaster(), getSpellModId() + "_" + getSpellId());
			if (getCaster() instanceof Player player) {
				player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
					capability.setConcentrationSpellTarget(getSpellModId() + "_" + getSpellId());
				});
			}
			SpellType.startConcentrationMessage(caster, getSpellName());
		}
		//
		//施法
		boolean bl = spellFindUse();
		if (bl) {
			spellAfterUse();
		}
		return bl;
	}
	public boolean spellFindUse() {
		return false;
	}
	public void spellAfterUse() {
		if (this.getCaster() instanceof MagicSpellLivingEntity magicSpellLivingEntity) {
			magicSpellLivingEntity.setCastingMaxTickJerotes(getCastingTicks());
			magicSpellLivingEntity.setCastingTickJerotes(getCastingTicks() <= 0 ? -1 : getCastingTicks());
		}
	}
	public void spellCastingStop(int n) {
	}
//	//未使用·间隔特殊行为
//	public void spellCooldown() {
//	}
//	//未使用·最多使用次数
//	public void useCount() {
//	}
//	//未使用·后次使用限制
//	public boolean canAfterUse() {
//		return true;
//	}
	public boolean canUse() {
		if (getCaster() != null && SpellType.getCastCooldownJerotes(getCaster()) > 0) {
			return false;
		}
		if (getCaster() != null && SpellType.getCastingTickJerotes(getCaster()) >= 0 && !SpellType.getLastSpellTypeJerotes(getCaster()).isEmpty()) {
			SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getLastSpellTypeJerotes(getCaster()));
			MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
				return !magicSpell.isCastingCanNotStop();
		}
		return true;
	}
	public boolean isCastingCanNotStop() {
		return false;
	}
	public boolean canEffect() {
		JerotesUseSpellEvent event = new JerotesUseSpellEvent(this.getCaster(), this.getTarget(), this.getSpellLevel());
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return false;
		}
		this.caster = event.getCaster();
		this.target = event.getTarget();
		this.spellLevel = event.getLevel();

		//法术反制·被动影响
		if (canBeBreak()) {
			JerotesStopSpellEvent jerotesstopspellevent = new JerotesStopSpellEvent(getTarget(), this, null, getCaster(), this.isHelp() ? 3 : 1, getSpellLevel());
			MinecraftForge.EVENT_BUS.post(jerotesstopspellevent);
			if (jerotesstopspellevent.isCanceled()) {
				return false;
			}
		}
		//法术反制·主动影响
		if (canBeBreak()) {
			List<LivingEntity> list = getCaster().level().getEntitiesOfClass(LivingEntity.class, getCaster().getBoundingBox().inflate(32.0, 32, 32));
			list.removeIf(livingEntity -> !(livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())) || livingEntity == this.getCaster() || (livingEntity.hasEffect(JerotesMobEffects.SPELL_REACTION_LOCK.get())));
			list.removeIf(livingEntity -> getTarget() instanceof LivingEntity livingEntityTarget && SpellType.getCastingTickJerotes(livingEntityTarget) > -1);
			list.removeIf(livingEntity -> AttackFind.FindCanNotAttack(livingEntity, getCaster()));
			list.removeIf(livingEntity -> livingEntity instanceof Mob mob && mob.getTarget() != getCaster());
			list.removeIf(livingEntity -> livingEntity instanceof Player player && !(player.swinging && Main.getTargetedEntity(player,32) == getCaster() ));
			if (!list.isEmpty()) {
				for (LivingEntity target : list) {
					if (isHelp() && target == getTarget()) continue;
					JerotesStopSpellEvent jerotesstopspellevent = new JerotesStopSpellEvent(target, this, null, getCaster(), this.isHelp() ? 3 : 1, getSpellLevel());
					MinecraftForge.EVENT_BUS.post(jerotesstopspellevent);
					if (jerotesstopspellevent.isCanceled()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public int getCastingTicks() {
		return 0;
	}
	public boolean CastingAbout(int n) {
		return CastingAboutBase(n);
	}
	public boolean CastingAboutBase(int n) {
		return true;
	}


	public boolean canBeBreak() {
		return true;
	}
	public int getBaseSpellLevel() {
		return 1;
	}
	public int getSpellLevel() {
		return Math.max(getBaseSpellLevel(), spellLevel);
	}
	public int getSpellAccuracy() {
		if (getCaster() != null && getCaster() instanceof Player) {
			return 0;
		}
		if (getCaster() != null && getCaster() instanceof SpellUseEntity spellUse) {
			return Math.max(0, (-spellUse.getSpellLevel() + 3));
		}
		return Math.max(0, (-this.getSpellLevel() + 3));
	}
	public LivingEntity getCaster() {
		return caster;
	}
	public Entity getTarget() {
		return target;
	}
	public String getSpellModId() {
		return JerotesWarehouse.MODID;
	}
	public String getSpellId() {
		return spellId;
	}
	public Component getSpellName() {
		return Component.translatable("spell." + getSpellModId() + "." + getSpellId());
	}
	public Component getSpellDesc() {
		return Component.translatable("spell." + getSpellModId() + "." + getSpellId() + ".desc");
	}
	public ParticleOptions getSpellDisplayParticle() {
		return spellDisplayParticle;
	}
	public ParticleOptions getSpellLevelParticle() {
		if (getSpellLevel() <= 1)
			return JerotesParticleTypes.SPELL_LEVEL_I.get();
		if (getSpellLevel() == 2)
			return JerotesParticleTypes.SPELL_LEVEL_II.get();
		if (getSpellLevel() == 3)
			return JerotesParticleTypes.SPELL_LEVEL_III.get();
		if (getSpellLevel() == 4)
			return JerotesParticleTypes.SPELL_LEVEL_IV.get();
		if (getSpellLevel() == 5)
			return JerotesParticleTypes.SPELL_LEVEL_V.get();
		if (getSpellLevel() == 6)
			return JerotesParticleTypes.SPELL_LEVEL_VI.get();
		if (getSpellLevel() == 7)
			return JerotesParticleTypes.SPELL_LEVEL_VII.get();
		if (getSpellLevel() == 8)
			return JerotesParticleTypes.SPELL_LEVEL_VIII.get();
		if (getSpellLevel() >= 9)
			return JerotesParticleTypes.SPELL_LEVEL_IX.get();
		return JerotesParticleTypes.SPELL_LEVEL_I.get();
	}
	public boolean canTargetNonLivingEntity() {
		return false;
	}
	public boolean mustTargetPickable() {
		return true;
	}
	public MagicType getMagicType() {
		return magicType;
	}
	public MagicType getMagicType2() {
		return magicType2;
	}
	public SoundEvent getSound() {
		return spellSound;
	}
	public SpellSchool getSpellSchool() {
		return SpellSchool.EVOCATION;
	}
	public float getSpellCost() {
		return 1;
	}
	public float getSoundLevel() {
		return 1f;
	}
	public boolean isMelee() {
		return this.getMagicType() == MagicType.SELF;
	}
	public boolean isHelp() {
		return false;
	}
	public boolean isCombat() {
		return true;
	}
	public boolean isCantrip() {
		return false;
	}
	public boolean isConcentration() {
		return false;
	}
	public boolean canUseTargetNone() {
		return false;
	}
	public float shouldBeSelfChance() {
		return 0.8f;
	}
	public float getSpellDistance() {
		return isMelee() && getCaster() instanceof Player player ? (float) player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) : getSpellDistanceBase();
	}
	public float getSpellDistanceBase() {
		return 64;
	}
	public boolean canUseToEntity(Entity entity) {
		return true;
	}
	public boolean mobUseInCombat() {
		return true;
	}
	public ResourceLocation getDisplayResourceLocation() {
		return new ResourceLocation(this.getSpellModId(), "textures/particle/" + this.getSpellId() + "_display.png");
	}

	public boolean hasCustomSubcategoryReach() {
		return getCustomSubcategoryReachAdd() > 0;
	}
	public Component getSpellSubcategoryReachDesc(int n) {
		return Component.translatable("spell." + getSpellModId() + "." + getSpellId() + ".subcategory_" + n);
	}
	//子类别范围
	public int getSubcategoryReach() {
		return getBaseSubcategoryReachAdd() + getCustomSubcategoryReachAdd();
	}
	//默认增加子类别范围
	public int getBaseSubcategoryReachAdd() {
		return 0;
	}
	//法术自定义增加子类别范围
	public int getCustomSubcategoryReachAdd() {
		return 0;
	}
	//当前子类别
	public int getSubcategory() {
		return subcategory;
	}
	//当前法术自身的子类别
	public int getCustomSubcategory() {
		return getSubcategory() - getBaseSubcategoryReachAdd();
	}
	public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
		return 0;
	}

	public int playerUseCooldownTick(Player player) {
		if (getMagicType2() == MagicType.MAIN) {
			return 120;
		}
		if (getMagicType2() == MagicType.ADD) {
			return 120;
		}
		return 120;
	}


	public static void MagicTooltip(List<Component> list, MagicSpell magicSpell, int level) {
		MagicTooltip(list, magicSpell, level, null, null);
	}
	public static void MagicTooltip(List<Component> list, MagicSpell magicSpell, int level, Player player, ItemStack itemStack) {
		list.add(magicSpell.getSpellName().copy()
				.append(Component.translatable("spell.jerotes.spell_base", level).withStyle(ChatFormatting.DARK_PURPLE)));
		list.add(magicSpell.getSpellSchool().getDisplayName().copy().append(" ")
				.append(Component.translatable(magicSpell.getMagicType2() != MagicType.ADD ? "spell.jerotes.type.main" : "spell.jerotes.type.add").withStyle(ChatFormatting.DARK_PURPLE))
				.append(magicSpell.isHelp() ? " " : "")
				.append(!magicSpell.isHelp() ? Component.empty() : Component.translatable("spell.jerotes.spell_heal").withStyle(ChatFormatting.YELLOW))
				.append(magicSpell.isCantrip() ? " " : "")
				.append(!magicSpell.isCantrip() ? Component.empty() : Component.translatable("spell.jerotes.spell_cantrip").withStyle(ChatFormatting.LIGHT_PURPLE))
				.append(magicSpell.isConcentration() ? " " : "")
				.append(!magicSpell.isConcentration() ? Component.empty() : Component.translatable("spell.jerotes.spell_concentration").withStyle(ChatFormatting.AQUA))
		);
		list.add(magicSpell.getSpellDesc().copy()
				.withStyle(ChatFormatting.LIGHT_PURPLE));
		if (magicSpell.hasCustomSubcategoryReach()) {
			for (int i = 0; i <= magicSpell.getCustomSubcategoryReachAdd(); i++) {
				boolean bl = false;
				if (itemStack != null && itemStack.getItem() instanceof MagicItem magicItem && magicItem.getSubcategoryMagicItem(itemStack) == i) {
					bl = true;
				}
				if (itemStack == null && player != null &&
						player.getCapability(JerotesPlayerData.CAPABILITY, null)
						.orElse(new JerotesPlayerData.PlayerVariables()).SpellSubcategoryFind == i) {
					bl = true;
				}
				list.add(Component.translatable("spell.jerotes.subcategory", i).
						append(magicSpell.getSpellSubcategoryReachDesc(i).copy()
						.withStyle(bl ? ChatFormatting.AQUA : ChatFormatting.LIGHT_PURPLE).withStyle(bl ? ChatFormatting.UNDERLINE : ChatFormatting.LIGHT_PURPLE)
				));
			}
		}
		list.add(Component.translatable(magicSpell.isMelee() ? "spell.jerotes.spell_max_distance_touch" : "spell.jerotes.spell_max_distance", magicSpell.getSpellDistance())
				.withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}