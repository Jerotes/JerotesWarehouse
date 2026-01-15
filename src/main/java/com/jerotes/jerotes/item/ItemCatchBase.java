package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Interface.CatchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemCatchBase extends Item {
	private final Supplier<? extends EntityType<?>> entityTypeSupplier;

	public ItemCatchBase(Supplier<? extends EntityType<?>> entitySupplier, Properties properties) {
		super(properties);
		this.entityTypeSupplier = entitySupplier;
	}

	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		BlockPos blockpos = blockhitresult.getBlockPos();
		this.checkExtraContent(player, level, itemStack, blockpos);
		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.getAbilities().instabuild) {
			player.getItemInHand(interactionHand).shrink(1);
		}
		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
	}

	public EntityType<?> getEntityType() {
		return entityTypeSupplier.get();
	}

	public void checkExtraContent(@Nullable Player player, Level level, ItemStack itemStack, BlockPos blockPos) {
		if (level instanceof ServerLevel serverLevel) {
			this.spawnFish(serverLevel, itemStack, blockPos);
			serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, blockPos);
		}
	}

	private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
		Entity entity = getEntityType().spawn(serverLevel, stack, null, pos, MobSpawnType.BUCKET, true, false);
		if (entity instanceof CatchEntity) {
			CatchEntity catchEntity = (CatchEntity)entity;
			catchEntity.loadFromCatchTag(stack.getOrCreateTag());
			catchEntity.setFromCatch(true);
		}
	}

	public static boolean isBaby(ItemStack itemStack) {
		CompoundTag compoundtag = itemStack.getTag();
		return compoundtag != null &&
				((compoundtag.contains("Age") && compoundtag.getInt("Age") < 0)
						|| (compoundtag.contains("IsBaby") && compoundtag.getBoolean("IsBaby")));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
}
