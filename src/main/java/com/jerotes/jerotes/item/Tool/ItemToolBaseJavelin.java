package com.jerotes.jerotes.item.Tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Interface.JerotesItemThrownJavelinUse;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseJavelin extends TridentItem implements ItemSpecialEffect, JerotesItemThrownJavelinUse, MeleeItem {
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;
	public float throwTime = 10f;
	public ItemToolBaseJavelin(Properties properties, float f, float f2, float f3) {
		super(properties);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", f - 1f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", f2 - 4f, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
		this.throwTime = f3;
	}

	public ItemToolBaseJavelin(Properties properties, float f, float f2) {
		super(properties);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", f - 1f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", f2 - 4f, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
		this.throwTime = 10f;
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment instanceof DamageEnchantment ||
				enchantment instanceof FireAspectEnchantment ||
				enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON ||
				enchantment instanceof KnockbackEnchantment ||
				enchantment instanceof MeleeEnchantment) {
			return this.isMeleeWeapon();
		}
		if (enchantment instanceof SweepingEdgeEnchantment) {
			return this instanceof ItemTwoHanded;
		}
		if (enchantment instanceof ArrowPiercingEnchantment) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			return this.defaultModifiers;
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public boolean isJerotesThrownJavelin() {
		return true;
	}
	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
		return !player.isCreative();
	}

	@Override
	public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity3) {
		itemStack.hurtAndBreak(1, livingEntity3, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
		if ((double)blockState.getDestroySpeed(level, blockPos) != 0.0) {
			itemStack.hurtAndBreak(2, livingEntity2, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
			return InteractionResultHolder.fail(itemStack);
		}
		if (EnchantmentHelper.getRiptide(itemStack) > 0 && !player.isInWaterOrRain()) {
			return InteractionResultHolder.fail(itemStack);
		}
		player.startUsingItem(interactionHand);
		return InteractionResultHolder.consume(itemStack);
	}

	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		if (!(livingEntity instanceof Player)) {
			return;
		}
		Player player2 = (Player)livingEntity;
		int n2 = this.getUseDuration(itemStack) - n;
		if (n2 < throwTime) {
			return;
		}
		int n3 = EnchantmentHelper.getRiptide(itemStack);
		if (n3 > 0 && !player2.isInWaterOrRain()) {
			return;
		}
		if (!level.isClientSide()) {
			itemStack.hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(livingEntity.getUsedItemHand()));
			if (n3 == 0) {
				Projectile thrownJavelin = this.JerotesThrownJavelin(player2, itemStack);
				thrownJavelin.shootFromRotation(player2, player2.getXRot(), player2.getYRot(), 0.0f, getJavelinSpeed() + (float)n3 * 0.5f, 1.0f);
				if (thrownJavelin instanceof AbstractArrow baseJavelinEntity && player2.getAbilities().instabuild) {
					baseJavelinEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
				}
				//穿透
				if (thrownJavelin instanceof AbstractArrow baseJavelinEntity) {
					int np = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, itemStack);
					if (np > 0) {
						baseJavelinEntity.setPierceLevel((byte) np);
					}
				}
				level.addFreshEntity(thrownJavelin);
				level.playSound(null, thrownJavelin, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);
				if (!player2.getAbilities().instabuild) {
					player2.getInventory().removeItem(itemStack);
				}
			}
		}
		player2.awardStat(Stats.ITEM_USED.get(this));
		if (n3 > 0) {
			float f = player2.getYRot();
			float f2 = player2.getXRot();
			float f3 = -Mth.sin(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
			float f4 = -Mth.sin(f2 * 0.017453292f);
			float f5 = Mth.cos(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
			float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
			float f7 = 3.0f * ((1.0f + (float)n3) / 4.0f);
			player2.push(f3 *= f7 / f6, f4 *= f7 / f6, f5 *= f7 / f6);
			player2.startAutoSpinAttack(20);
			if (player2.onGround()) {
				float f8 = 1.1999999f;
				player2.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
			}
			SoundEvent soundEvent = n3 >= 3 ? SoundEvents.TRIDENT_RIPTIDE_3 : (n3 == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1);
			level.playSound(null, player2, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
		}
	}
	@Override
	public float getJavelinSpeed() {
		return 2.5F;
	}


	public boolean JerotesNormalThrownJavelin() {
		return true;
	}
	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.SPEAR;
	}

	public float getThrowingTime() {
		return throwTime;
	}
	public Projectile JerotesThrownJavelin(@Nullable LivingEntity livingEntity, ItemStack itemStack) {
		return null;
	}
	public float getThrowingDamage() {
		return 0f;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable("item.jerotes.javelin", getThrowingTime() / 20f, getThrowingDamage()).withStyle(ChatFormatting.YELLOW));
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
}


