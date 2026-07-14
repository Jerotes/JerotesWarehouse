package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.EliteEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.forge.JerotesUseSpellEvent;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.init.JerotesMobEffects;
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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class MagicSpell {
	public int spellLevel;
	public LivingEntity caster;
	public Entity target;
	public MagicType magicType;
	public MagicType magicType2;
	public String spellId;
	public ParticleOptions spellDisplayParticle;
	public SoundEvent spellSound;
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

	public boolean spellUse() {
		if (!canUse()) {
			return false;
		}
		//结束正在蓄力的技能
		Main.getJerotesPersistentData(getCaster()).putString("jerotes_using_magic", "");
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_spell_tick", 0);
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_using_magic_level", 0);
		Main.getJerotesPersistentData(getCaster()).putUUID("jerotes_using_magic_target", UUID.fromString("00000000-0000-0000-0000-000000000000"));
		//图标
		if (getCaster().level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(getSpellDisplayParticle(), caster.getX(), caster.getBoundingBox().maxY + 0.5, caster.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
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
		//等级高了
		if (baseSpellLevel() > spellLevel) {
			spellLevel = baseSpellLevel();
		}
		//记录自身上次使用的法术
		Main.getJerotesPersistentData(getCaster()).putString("jerotes_last_magic", getSpellModId() + "_" + getSpellId());
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_last_magic_level", spellLevel);
		Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_spell_cooldown", Math.max(2, Main.getJerotesPersistentData(caster).getDouble("jerotes_spell_cooldown")));
		//正在使用法术 探查
		if (!canEffect()) {
			return false;
		}
		if (this.getUsingTicks() > 0) {
			Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_spell_tick", this.getUsingTicks() + 5);
			Main.getJerotesPersistentData(getCaster()).putString("jerotes_using_magic", getSpellModId() + "_" + getSpellId());
			Main.getJerotesPersistentData(getCaster()).putDouble("jerotes_using_magic_level", spellLevel);
			Main.getJerotesPersistentData(getCaster()).putUUID("jerotes_using_magic_target", getTarget().getUUID());
			spellAfterUse();
			return true;
		}
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
		if (getCaster() != null && Main.getJerotesPersistentData(getCaster()).getDouble("jerotes_spell_cooldown") > 0) {
			return false;
		}
		return true;
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
		if (canBeBreak() && !isHelp()) {
			if (getTarget() instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get()) && getCaster() != getTarget() && getCaster().distanceTo(getTarget()) <= 32) {
				//等级不如对方
				if (!(AttackFind.SameFactionAvoidDamage(getCaster(), livingEntity, false)) && Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get())).getAmplifier() + 1 >= this.getSpellLevel()) {
					//自身清除计时
					{
						SpellRegistry.getSpellTypeById(this.getSpellModId()+ "_" + this.getSpellId()).stop(getCaster(), Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get())).getAmplifier() + 1, false);
					}
					//去除对方buff
					if (!livingEntity.level().isClientSide()) {
						livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
					}
					livingEntity.swing(InteractionHand.OFF_HAND);
					SpellFind.Counterspell(livingEntity);
					return false;
				}
			}
		}
		//法术反制·主动影响
		if (canBeBreak()) {
			List<LivingEntity> list = getCaster().level().getEntitiesOfClass(LivingEntity.class, getCaster().getBoundingBox().inflate(32.0, 32, 32));
			list.removeIf(livingEntity -> !(livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())) || livingEntity == this.getCaster());
			list.removeIf(livingEntity -> AttackFind.FindCanNotAttack(livingEntity, getCaster()));
			list.removeIf(livingEntity -> livingEntity instanceof Mob mob && mob.getTarget() != getCaster());
			list.removeIf(livingEntity -> livingEntity instanceof Player player && !(player.swinging && Main.getTargetedEntity(player,32) == getCaster() ));
			if (!list.isEmpty()) {
				for (LivingEntity target : list) {
					if (isHelp() && target == getTarget()) continue;
					//等级不如对方
					if (!(AttackFind.SameFactionAvoidDamage(getCaster(), target, false)) && Objects.requireNonNull(target.getEffect(JerotesMobEffects.COUNTERSPELL.get())).getAmplifier() + 1 >= this.getSpellLevel()) {
						//去除对方buff
						if (!target.level().isClientSide()) {
							target.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
						}
						target.swing(InteractionHand.OFF_HAND);
						SpellFind.Counterspell(target);
						return false;
					}
				}
			}
		}
		return true;
	}
	public boolean canBeBreak() {
		return true;
	}
	public int baseSpellLevel() {
		return 1;
	}
	public int getUsingTicks() {
		return 0;
	}
	public int getSpellLevel() {
		return Math.max(baseSpellLevel(), spellLevel);
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
	public float getSoundLevel() {
		return 1f;
	}
	public boolean isMelee() {
		return false;
	}
	public boolean isHelp() {
		return false;
	}
	public boolean canUseTargetNone() {
		return false;
	}
	public float shouldBeSelf() {
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
		list.add(magicSpell.getSpellName().copy()
				.append(Component.translatable("spell.jerotes.spell_base", level).withStyle(ChatFormatting.DARK_PURPLE)));
		list.add(magicSpell.getSpellSchool().getDisplayName().copy().append(" ")
				.append(Component.translatable(magicSpell.getMagicType2() != MagicType.ADD ? "spell.jerotes.type.main" : "spell.jerotes.type.add").withStyle(ChatFormatting.DARK_PURPLE)));
		list.add(magicSpell.getSpellDesc().copy()
				.withStyle(ChatFormatting.LIGHT_PURPLE));
		list.add(Component.translatable("spell.jerotes.spell_max_distance", magicSpell.getSpellDistance())
				.withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}