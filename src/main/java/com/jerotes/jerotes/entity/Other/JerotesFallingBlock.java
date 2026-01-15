package com.jerotes.jerotes.entity.Other;

import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class JerotesFallingBlock extends Entity {
	private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Float> ANIM_V_Y = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.BLOCK_STATE);
	public float animY = 0;
	public float prevAnimY = 0;
	public static float DROP_FACTORS = 0.1f;

	public enum FallingMoveType {
		RENDER_MOVE,
		OVERALL_MOVE
	}

	public JerotesFallingBlock(EntityType<JerotesFallingBlock> entityType, Level level) {
		super(entityType, level);
		this.setDuration(20);
	}


	public JerotesFallingBlock(Level worldIn, BlockState blockState, float vy) {
		super(JerotesEntityType.JEROTES_FALLING_BLOCK.get(), worldIn);
		this.setMode(FallingMoveType.RENDER_MOVE);
		setBlockState(blockState);
		setAnimVY(vy);
	}

	public JerotesFallingBlock(Level level, double px, double py, double pz, BlockState blockState, int duration) {
		super(JerotesEntityType.JEROTES_FALLING_BLOCK.get(), level);
		this.setMode(FallingMoveType.OVERALL_MOVE);
		this.setBlockState(blockState);
		this.setPos(px, py + (double) ((1.0F - this.getBbHeight()) / 2.0F), pz);
		this.setDuration(duration);
		this.xo = px;
		this.yo = py;
		this.zo = pz;
		this.setDeltaMovement(Vec3.ZERO);
	}

	protected void defineSynchedData() {
		this.getEntityData().define(BLOCK_STATE, Blocks.DIRT.defaultBlockState());
		this.getEntityData().define(MODE, FallingMoveType.RENDER_MOVE.toString());
		this.getEntityData().define(ANIM_V_Y, 1F);
		this.getEntityData().define(DURATION, 20);
	}

	public void tick() {
		if (getMode() == FallingMoveType.RENDER_MOVE) setDeltaMovement(0, 0, 0);
		super.tick();
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, DROP_FACTORS / 2, 0.0D));
			}
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
			if ((this.onGround() && this.tickCount > this.getDuration()) || this.tickCount > 300) {
				this.discard();
			}
		} else {
			float animVY = getAnimVY();
			prevAnimY = animY;
			animY += animVY;
			setAnimVY(animVY - DROP_FACTORS);
			if (animY < -0.5) discard();
		}
	}


	@Override
	public void setDeltaMovement(double x, double y, double z) {
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			super.setDeltaMovement(x, y, z);
		}
	}

	protected void addAdditionalSaveData(CompoundTag compoundTag) {
		BlockState blockState = this.getBlockState();
		compoundTag.put("BlockState", NbtUtils.writeBlockState(blockState));
		compoundTag.putInt("Duration", getDuration());
		compoundTag.putInt("TickCount", tickCount);
		compoundTag.putFloat("VY", getEntityData().get(ANIM_V_Y));
	}

	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundTag.getCompound("BlockState")));
		setDuration(compoundTag.getInt("Duration"));
		tickCount = compoundTag.getInt("TickCount");
		setAnimVY(compoundTag.getFloat("VY"));
	}

	public boolean displayFireAnimation() {
		return false;
	}
	public BlockState getBlockState() {
		return this.entityData.get(BLOCK_STATE);
	}
	public void setBlockState(BlockState p_270267_) {
		this.entityData.set(BLOCK_STATE, p_270267_);
	}
	public FallingMoveType getMode() {
		String mode = this.entityData.get(MODE);
		if (mode.isEmpty()) return FallingMoveType.RENDER_MOVE;
		return FallingMoveType.valueOf(mode);
	}
	public void setMode(FallingMoveType type) {
		this.entityData.set(MODE, type.toString());
	}
	public float getAnimVY() {
		return getEntityData().get(ANIM_V_Y);
	}
	private void setAnimVY(float vy) {
		getEntityData().set(ANIM_V_Y, vy);
	}
	public int getDuration() {
		return getEntityData().get(DURATION);
	}
	public void setDuration(int duration) {
		getEntityData().set(DURATION, duration);
	}
}