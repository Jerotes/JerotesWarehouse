package com.jerotes.jerotes.item;

import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AAExplorationEye extends Item {
	public static final String TAG_TARGET = "Target";

	public AAExplorationEye() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return InteractionResult.PASS;
	}

	public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		player.startUsingItem(interactionHand);
		CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
		if (isTarget(itemStack)) {
			compoundtag.remove(AAExplorationEye.TAG_TARGET);
			player.playSound(SoundEvents.CHAIN_BREAK);
		}
		itemStack.setTag(compoundtag);
		return InteractionResultHolder.consume(itemStack);
	}

	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity userEntity, int n) {
		if (!(userEntity instanceof Player player)) {
			return;
		}
		if (Main.getTargetedEntity(player, 256) != null && Main.getTargetedEntity(player, 256) instanceof LivingEntity userEntity2) {
			if (player instanceof ServerPlayer serverPlayer) {
				Main.openMobInventoryGui(serverPlayer, userEntity2, true, true, true, true, true, true, true, true);
			}
		}
		player.awardStat(Stats.ITEM_USED.get(this));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.BRUSH;
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemStack, LivingEntity userEntity2, LivingEntity userEntity3) {
		return true;
	}

	public static boolean isTarget(ItemStack itemStack) {
		CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag == null) return false;
        if (!compoundtag.contains(TAG_TARGET)) return false;
        compoundtag.getUUID(TAG_TARGET);
        return true;
	}

	public static UUID getTarget(ItemStack itemStack) {
		if (!isTarget(itemStack)) return null;
		CompoundTag compoundtag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
		return compoundtag.getUUID(TAG_TARGET);
	}

	public boolean isFoil(ItemStack itemStack) {
		return isTarget(itemStack) || super.isFoil(itemStack);
	}


	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity user) {
		boolean retval = super.onEntitySwing(itemstack, user);
		if (user instanceof Player player && Main.getTargetedEntity(player, 256) != null && Main.getTargetedEntity(player, 256) instanceof LivingEntity find) {
			//如果选择正常
			if (AAExplorationEye.isTarget(user.getMainHandItem())) {
				if (find.level() instanceof ServerLevel serverLevel &&
						AAExplorationEye.getTarget(user.getMainHandItem()) != null &&
						(serverLevel.getEntity(Objects.requireNonNull(AAExplorationEye.getTarget(user.getMainHandItem())))) instanceof LivingEntity lastFind) {
					if (find instanceof Mob mob && lastFind != mob) {
						mob.setTarget(lastFind);
					}
					if (lastFind instanceof Mob mob && find != mob) {
						mob.setTarget(find);
					}
					CompoundTag compoundtag = user.getMainHandItem().hasTag() ? user.getMainHandItem().getTag().copy() : new CompoundTag();
					compoundtag.remove(AAExplorationEye.TAG_TARGET);
					user.getMainHandItem().setTag(compoundtag);
					if (!find.isSilent()) {
						find.playSound(SoundEvents.CHAIN_PLACE);
					}
				}
				else {
					CompoundTag compoundtag = user.getMainHandItem().hasTag() ? user.getMainHandItem().getTag().copy() : new CompoundTag();
					compoundtag.putUUID(AAExplorationEye.TAG_TARGET, find.getUUID());
					user.getMainHandItem().setTag(compoundtag);
					if (!find.isSilent()) {
						find.playSound(SoundEvents.CHAIN_BREAK);
					}
				}
			}
			//如果选择不正常
			else {
				CompoundTag compoundtag = user.getMainHandItem().hasTag() ? user.getMainHandItem().getTag().copy() : new CompoundTag();
				compoundtag.putUUID(AAExplorationEye.TAG_TARGET, find.getUUID());
				user.getMainHandItem().setTag(compoundtag);
				if (!find.isSilent()) {
					find.playSound(SoundEvents.CHAIN_PLACE);
				}
			}
		}
		return retval;
	}
}