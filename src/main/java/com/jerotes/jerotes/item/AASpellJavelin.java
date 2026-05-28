package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AASpellJavelin extends Item implements MagicItem {
	public static final String SPELL = "Spell";
	public static final String SPELL_LEVEL = "SpellLevel";

	public AASpellJavelin() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
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
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}
		if (Main.getJerotesPersistentData(player).getDouble("jerotes_spell_cooldown") > 0) {
			return;
		}
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			Entity target = player;
			int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), player, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
			if (Main.getTargetedEntity(player, 256) != null && Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
				if (magicSpellStart.getMagicType() != MagicType.SELF)
					target = livingEntity2;
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).spellUse();
				}
			}

		}
		player.awardStat(Stats.ITEM_USED.get(this));
	}
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellLevel();
			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack)));

			list.add(magicSpellStart.getSpellName().copy()
					.append(Component.translatable("spell.jerotes.spell_base", trueLevel(itemStack))).withStyle(ChatFormatting.DARK_PURPLE));
			list.add(magicSpellStart.getSpellDesc().copy()
					.withStyle(ChatFormatting.LIGHT_PURPLE));
			list.add(Component.translatable("spell.jerotes.spell_max_distance", magicSpellStart.getSpellDistance())
					.withStyle(ChatFormatting.LIGHT_PURPLE));
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

	public boolean isFoil(ItemStack itemStack) {
		return !this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack)) || super.isFoil(itemStack);
	}

	@Override
	public String getDescriptionId(ItemStack itemStack) {
		return !this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))
				? "spell." + SpellListByString.getSpell(getSpellLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellModId() + "." + SpellListByString.getSpell(getSpellLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellId()
				: super.getDescriptionId(itemStack);
	}

	public String getSpellId(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
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
	}

	@Override
	public float getSpellDistance(ItemStack itemStack) {
		if (!this.getSpellId(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellId(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellId(itemStack))).getSpellDistance();
		}
		return 0;
	}
}
