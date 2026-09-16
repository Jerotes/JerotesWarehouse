package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
	public static final String SPELL_SUBCATEGORY = "SpellSubcategory";

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
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
		if (!livingEntity.isShiftKeyDown()) {
			super.onUseTick(level, livingEntity, itemStack, count);
			return;
		}
		int n2 = this.getUseDuration(itemStack) - count;
		if (n2 % 1 == 0) {
			if (!(livingEntity instanceof Player player)) {
				return;
			}
			if (SpellType.getCastCooldownJerotes(player) > 0) {
				return;
			}
			livingEntity.swing(InteractionHand.MAIN_HAND, true);
			if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
				Entity target = player;
				int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();

				MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));

				if (Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) != null &&
						(Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) instanceof LivingEntity || magicSpellStart.canTargetNonLivingEntity())) {
					if (magicSpellStart.getMagicType() != MagicType.SELF)
						target = Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable());
				}
				if (magicSpellStart.getMagicType() == MagicType.TARGET && livingEntity.distanceTo(target) > magicSpellStart.getSpellDistance() && magicSpellStart.canUseTargetNone()) {
					target = livingEntity;
				}
				if (target == null) {
					target = player;
				}

				MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));
				boolean CanUse = magicSpell.canUse();
				boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
				boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
				boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
				if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
					if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
						//法术列表
						SpellListByString.getSpell(trueLevel, livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).spellUse(getSubcategoryMagicItem(itemStack));
					}
				}

			}
			player.awardStat(Stats.ITEM_USED.get(this));
		}
		super.onUseTick(level, livingEntity, itemStack, count);
	}
	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		if (!livingEntity.isShiftKeyDown()) {
			if (!(livingEntity instanceof Player player)) {
				return;
			}
			if (SpellType.getCastCooldownJerotes(player) > 0) {
				return;
			}
			livingEntity.swing(InteractionHand.MAIN_HAND, true);
			if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
				Entity target = player;
				int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();

				MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));

				if (Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) != null &&
						(Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable()) instanceof LivingEntity || magicSpellStart.canTargetNonLivingEntity())) {
					if (magicSpellStart.getMagicType() != MagicType.SELF)
						target = Main.getTargetedEntity(player, 256, false, magicSpellStart.mustTargetPickable());
				}
				if (magicSpellStart.getMagicType() == MagicType.TARGET && livingEntity.distanceTo(target) > magicSpellStart.getSpellDistance() && magicSpellStart.canUseTargetNone()) {
					target = livingEntity;
				}
				if (target == null) {
					target = player;
				}

				MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));
				boolean CanUse = magicSpell.canUse();
				boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
				boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
				boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
				if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
					if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
						//法术列表
						SpellListByString.getSpell(trueLevel, livingEntity, target, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).spellUse(getSubcategoryMagicItem(itemStack));
					}
				}

			}
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
			int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();
			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));
			MagicSpell.MagicTooltip(list, magicSpellStart, trueLevel(itemStack), null, itemStack);
		}
		list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
	}
	public MutableComponent getDisplayName() {
		return Component.translatable(this.getDescriptionId() + ".desc");
	}

	@Override
	public List<SpellTypeInterface> getMainSpellTypeMagicItem(ItemStack itemStack) {
		int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();
		List<SpellTypeInterface> spellList = new ArrayList<>();
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))
				&& SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getMagicType2() == MagicType.MAIN) {
			spellList.add(SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));
		}
		return spellList;
	}
	@Override
	public List<SpellTypeInterface> getAddSpellTypeMagicItem(ItemStack itemStack) {
		int trueLevel = SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();
		List<SpellTypeInterface> spellList = new ArrayList<>();
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))
				&& SpellListByString.getSpell(trueLevel, null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getMagicType2() == MagicType.ADD) {
			spellList.add(SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack)));
		}
		return spellList;
	}
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		player.startUsingItem(interactionHand);
		return InteractionResultHolder.consume(itemStack);

	}

	public boolean canChangeSubcategoryMagicItem(ItemStack itemStack) {
		return true;
	}

	public int trueLevel(ItemStack itemStack) {
		return SpellListByString.getSpell(getSpellLevelMagicItem(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellLevel();
	}

	public boolean isFoil(ItemStack itemStack) {
		return !this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack)) || super.isFoil(itemStack);
	}

	@Override
	public String getDescriptionId(ItemStack itemStack) {
		return !this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))
				? "spell." + SpellListByString.getSpell(getSpellLevelMagicItem(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellModId() + "." + SpellListByString.getSpell(getSpellLevelMagicItem(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellId()
				: super.getDescriptionId(itemStack);
	}

	public boolean hasSpellMagicItem() {
		return true;
	}
	public String getSpellIdMagicItem(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getString(SPELL).isEmpty()) {
			if (stack.hasCustomHoverName()) {
				return stack.getHoverName().getString();
			}
		}
		return tag.getString(SPELL);
	}
	@Override
	public int getSpellLevelMagicItem(ItemStack itemStack) {
		CompoundTag tag = itemStack.getOrCreateTag();
		return tag.getInt(SPELL_LEVEL);
	}
	@Override
	public int getSubcategoryMagicItem(ItemStack itemStack) {
		CompoundTag tag = itemStack.getOrCreateTag();
		return tag.getInt(SPELL_SUBCATEGORY);
	}
	@Override
	public ItemStack setSubcategoryMagicItem(ItemStack itemStack, int n) {
		ItemStack stack = itemStack;
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString(FlawlessMagicScroll.SPELL, getSpellIdMagicItem(itemStack));
		compoundTag.putInt(FlawlessMagicScroll.SPELL_LEVEL, getSpellLevelMagicItem(itemStack));
		compoundTag.putInt(FlawlessMagicScroll.SPELL_SUBCATEGORY, n);
		stack.setTag(compoundTag);
		return stack;
	}
	@Override
	public boolean isMeleeMagicItem(ItemStack itemStack) {
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).isMelee();
		}
		return false;
	}
	@Override
	public boolean isHelpMagicItem(ItemStack itemStack) {
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).isHelp();
		}
		return false;
	}

	@Override
	public void damageMagicItem(LivingEntity livingEntity, ItemStack itemStack) {
	}

	@Override
	public float getSpellDistanceMagicItem(ItemStack itemStack) {
		if (!this.getSpellIdMagicItem(itemStack).isEmpty() && SpellRegistry.spellExists(this.getSpellIdMagicItem(itemStack))) {
			return SpellListByString.getSpell(trueLevel(itemStack), null, null, SpellRegistry.getSpellTypeById(this.getSpellIdMagicItem(itemStack))).getSpellDistance();
		}
		return 0;
	}


	public ItemStack setSpellJavelin(ItemStack self, ItemStack magicItem) {
		ItemStack stack = self;
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString(FlawlessMagicScroll.SPELL, magicItem.getItem() instanceof MagicItem magicItem1 ? magicItem1.getSpellIdMagicItem(magicItem) : "");
		compoundTag.putInt(FlawlessMagicScroll.SPELL_LEVEL, magicItem.getItem() instanceof MagicItem magicItem1 ? magicItem1.getSpellLevelMagicItem(magicItem) : 1);
		if (magicItem.getItem() instanceof MagicItem magicItem1 && magicItem1.canChangeSubcategoryMagicItem(magicItem)) {
			compoundTag.putInt(FlawlessMagicScroll.SPELL_SUBCATEGORY, magicItem1.getSubcategoryMagicItem(magicItem));
		}
		stack.setTag(compoundTag);
		return stack;
	}
}
