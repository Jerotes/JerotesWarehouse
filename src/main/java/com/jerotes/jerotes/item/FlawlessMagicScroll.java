package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.jerotes.jerotes.item.Tool.ItemToolBaseBandage.FindTarget;

public class FlawlessMagicScroll extends Item implements MagicItem {
	public static final String SPELL = "Spell";
	public static final String SPELL_LEVEL = "SpellLevel";

	public FlawlessMagicScroll() {
		super(new Properties().rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
		int n2 = this.getUseDuration(itemStack) - count;
		if (n2 == this.getUseTime()) {
			livingEntity.playSound(JerotesSoundEvents.SPELL);
		}
		super.onUseTick(level, livingEntity, itemStack, count);
	}
	public int getUseTime() {
		return 10;
	}
	public int getSchool(ItemStack itemStack) {
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			SpellSchool spellSchool = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellSchool();
			if (spellSchool != null) {
				if (spellSchool == SpellSchool.ABJURATION) return 1;     //防护系
				if (spellSchool == SpellSchool.CONJURATION) return 2;    //咒法系
				if (spellSchool == SpellSchool.DIVINATION) return 3;     //预言系
				if (spellSchool == SpellSchool.ENCHANTMENT) return 4;    //附魔系
				if (spellSchool == SpellSchool.EVOCATION) return 5;      //塑能系
				if (spellSchool == SpellSchool.ILLUSION) return 6;       //幻术系
				if (spellSchool == SpellSchool.NECROMANCY) return 7;     //死灵系
				if (spellSchool == SpellSchool.TRANSMUTATION) return 8;  //变化系
				if (spellSchool == SpellSchool.PSIONICS) return 9;       //灵能
				if (spellSchool == SpellSchool.SKILL) return 10;         //技巧
			}
		}
		return 0;
	}
	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		int n2 = this.getUseDuration(itemStack) - n;
		if (n2 < getUseTime()) {
			return;
		}
		if (Main.getJerotesPersistentData(livingEntity).getDouble("jerotes_spell_cooldown") > 0) {
			return;
		}
		livingEntity.swing(InteractionHand.MAIN_HAND, true);
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			Entity target = livingEntity;
			int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
			if (livingEntity instanceof Player player) {
				if (Main.getTargetedEntity(player, 256) != null &&
						Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
					if (magicSpellStart.getMagicType() != MagicType.SELF)
						target = livingEntity2;
				}
			}
			if (livingEntity instanceof Mob mob) {
				if (mob.getTarget() != null && !magicSpellStart.isHelp() && mob.distanceTo(mob.getTarget()) <= magicSpellStart.getSpellDistance()) {
					if (magicSpellStart.getMagicType() != MagicType.SELF)
						target = mob.getTarget();
				}
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != livingEntity || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && livingEntity.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					SpellListByString.getSpell(trueLevel, livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).spellUse();
					damageMagicItem(livingEntity, itemStack);
				}
			}
		}
		if (livingEntity instanceof Player player) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();
			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
			MagicSpell.MagicTooltip(list, magicSpellStart, trueLevel(itemStack));
		}
	}

	@Override
	public List<SpellTypeInterface> getMainSpellType(ItemStack itemStack) {
		int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();
		List<SpellTypeInterface> spellList = new ArrayList<>();
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))
				&& SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getMagicType2() == MagicType.MAIN) {
			spellList.add(SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
		}
		return spellList;
	}
	@Override
	public List<SpellTypeInterface> getAddSpellType(ItemStack itemStack) {
		int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();
		List<SpellTypeInterface> spellList = new ArrayList<>();
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))
				&& SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getMagicType2() == MagicType.ADD) {
			spellList.add(SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
		}
		return spellList;
	}
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		player.startUsingItem(interactionHand);
		return InteractionResultHolder.consume(itemStack);

	}

	public int trueLevel(ItemStack itemStack) {
		return SpellListByString.getSpell(getSpellLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();
	}

	@Override
	public String getDescriptionId(ItemStack itemStack) {
		return !this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))
				? "spell." + SpellListByString.getSpell(getSpellLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellModId() + "." + SpellListByString.getSpell(getSpellLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellId()
				: super.getDescriptionId(itemStack);
	}

	public String getSpellId(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getString(SPELL).isEmpty()) {
			if (stack.hasCustomHoverName()) {
				return stack.getHoverName().getString();
			}
		}
		return tag.getString(SPELL);
	}
	@Override
	public int getSpellLevel(ItemStack itemStack) {
		CompoundTag tag = itemStack.getOrCreateTag();
		return tag.getInt(SPELL_LEVEL);
	}
	@Override
	public boolean isMelee(ItemStack itemStack) {
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).isMelee();
		}
		return false;
	}
	@Override
	public boolean isHelp(ItemStack itemStack) {
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).isHelp();
		}
		return false;
	}

	@Override
	public void damageMagicItem(LivingEntity livingEntity, ItemStack itemStack) {
		if (livingEntity instanceof Player player) {
			if (!player.getAbilities().instabuild) {
				itemStack.shrink(1);
			}
		}
		else if (JerotesGameRules.JEROTES_MAGIC_CAN_BREAK != null && livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MAGIC_CAN_BREAK)) {
			itemStack.shrink(1);
		}
	}

	@Override
	public float getSpellDistance(ItemStack itemStack) {
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellDistance();
		}
		return 0;
	}
}
