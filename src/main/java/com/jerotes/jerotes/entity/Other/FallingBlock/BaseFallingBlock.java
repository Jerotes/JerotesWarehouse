package com.jerotes.jerotes.entity.Other.FallingBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseFallingBlock extends Entity {
	public static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(BaseFallingBlock.class, EntityDataSerializers.BLOCK_STATE);

	public BaseFallingBlock(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public float animY;
	public float prevAnimY;
	public FallingMoveType getMode() {return FallingMoveType.OVERALL_MOVE;}
	public void setMode(FallingMoveType type) {}
	public BlockState getBlockState() {return this.entityData.get(BLOCK_STATE);}
	public void setBlockState(BlockState blockState) {this.entityData.set(BLOCK_STATE, blockState);}
	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {
		BlockState blockState = this.getBlockState();
		compoundTag.put("BlockState", NbtUtils.writeBlockState(blockState));
		compoundTag.putFloat("AnimY", animY);
		compoundTag.putFloat("PrevAnimY", prevAnimY);
		compoundTag.putInt("TickCount", tickCount);
	}
	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundTag.getCompound("BlockState")));
		this.animY = compoundTag.getFloat("AnimY");
		this.prevAnimY = compoundTag.getFloat("PrevAnimY");
		this.tickCount = compoundTag.getInt("TickCount");
	}
	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(BLOCK_STATE, Blocks.DIRT.defaultBlockState());
	}

	public enum FallingMoveType {
		RENDER_MOVE,
		OVERALL_MOVE
	}
}