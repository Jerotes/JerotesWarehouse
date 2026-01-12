package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class JerotesPlayerSpawnEgg extends ForgeSpawnEggItem {
	public JerotesPlayerSpawnEgg() {
		super(JerotesEntityType.JEROTES_PLAYER, 0xffffff, 0xffffff, new Item.Properties());
	}

	public InteractionResult useOn(UseOnContext p_43223_) {
		Level level = p_43223_.getLevel();
		if (!(level instanceof ServerLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemstack = p_43223_.getItemInHand();
			BlockPos blockpos = p_43223_.getClickedPos();
			Direction direction = p_43223_.getClickedFace();
			BlockState blockstate = level.getBlockState(blockpos);
			if (blockstate.is(Blocks.SPAWNER)) {
				BlockEntity blockentity = level.getBlockEntity(blockpos);
				if (blockentity instanceof SpawnerBlockEntity) {
					SpawnerBlockEntity spawnerblockentity = (SpawnerBlockEntity)blockentity;
					EntityType<?> entitytype1 = this.getType(itemstack.getTag());
					spawnerblockentity.setEntityId(entitytype1, level.getRandom());
					blockentity.setChanged();
					level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
					level.gameEvent(p_43223_.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
					itemstack.shrink(1);
					return InteractionResult.CONSUME;
				}
			}

			BlockPos blockpos1;
			if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
				blockpos1 = blockpos;
			} else {
				blockpos1 = blockpos.relative(direction);
			}
			EntityType<?> entitytype = this.getType(itemstack.getTag());
			Entity mob = entitytype.spawn((ServerLevel)level, null, p_43223_.getPlayer(), blockpos1,
					MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
			if (mob != null) {
				if (p_43223_.getItemInHand().hasCustomHoverName()) {
					String string = p_43223_.getItemInHand().getHoverName().getString();
					if (string.equals("Bandit")) {
						mob.getPersistentData().putString("jerotes_mob_faction", "bandit");
					}
				}
				itemstack.shrink(1);
				level.gameEvent(p_43223_.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
			}

			return InteractionResult.CONSUME;
		}
	}

	private static Component removeAction(Component p_20141_) {
		MutableComponent mutablecomponent = p_20141_.plainCopy().setStyle(p_20141_.getStyle().withClickEvent((ClickEvent)null));
		Iterator var2 = p_20141_.getSiblings().iterator();

		while(var2.hasNext()) {
			Component component = (Component)var2.next();
			mutablecomponent.append(removeAction(component));
		}

		return mutablecomponent;
	}


	public Optional<Mob> spawnOffspringFromSpawnEgg(Player p_43216_, Mob p_43217_, EntityType<? extends Mob> p_43218_, ServerLevel p_43219_, Vec3 p_43220_, ItemStack p_43221_) {
		if (!this.spawnsEntity(p_43221_.getTag(), p_43218_)) {
			return Optional.empty();
		} else {
			Mob mob;
			if (p_43217_ instanceof AgeableMob) {
				mob = ((AgeableMob)p_43217_).getBreedOffspring(p_43219_, (AgeableMob)p_43217_);
			} else {
				mob = (Mob)p_43218_.create(p_43219_);
			}

			if (mob == null) {
				return Optional.empty();
			} else {
				((Mob)mob).setBaby(true);
				if (!((Mob)mob).isBaby()) {
					return Optional.empty();
				} else {
					((Mob)mob).moveTo(p_43220_.x(), p_43220_.y(), p_43220_.z(), 0.0F, 0.0F);
					p_43219_.addFreshEntityWithPassengers((Entity)mob);
					if (p_43221_.hasCustomHoverName()) {
						String string = p_43221_.getHoverName().getString();
						if (string.equals("Bandit")) {
							mob.getPersistentData().putString("jerotes_mob_faction", "bandit");
						}
						else {
							((Mob) mob).setCustomName(p_43221_.getHoverName());
						}
					}

					if (!p_43216_.getAbilities().instabuild) {
						p_43221_.shrink(1);
					}

					return Optional.of(mob);
				}
			}
		}
	}

}
