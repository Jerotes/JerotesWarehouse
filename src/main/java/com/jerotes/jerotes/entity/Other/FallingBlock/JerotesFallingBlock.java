package com.jerotes.jerotes.entity.Other.FallingBlock;

import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class JerotesFallingBlock extends BaseFallingBlock {
	//参考了Mowzie's Mobs
	private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Float> ANIM_V_Y = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(JerotesFallingBlock.class, EntityDataSerializers.FLOAT);

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

	@Override
	public void setDeltaMovement(double x, double y, double z) {
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			super.setDeltaMovement(x, y, z);
		}
	}
	public boolean displayFireAnimation() {
		return false;
	}

	public FallingMoveType getMode() {
		String mode = this.getEntityData().get(MODE);
		if (mode.isEmpty()) return FallingMoveType.RENDER_MOVE;
		return FallingMoveType.valueOf(mode);
	}
	public void setMode(FallingMoveType type) {
		this.getEntityData().set(MODE, type.toString());
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
	public float getGravity() {
		return getEntityData().get(GRAVITY);
	}
	public void setGravity(float gravity) {
		getEntityData().set(GRAVITY, gravity);
	}
	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putFloat("AnimVY", getAnimVY());
		compoundTag.putInt("Duration", getDuration());
		compoundTag.putFloat("Gravity", getGravity());
	}
	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.setAnimVY(compoundTag.getFloat("AnimVY"));
		this.setDuration(compoundTag.getInt("Duration"));
		this.setGravity(compoundTag.getInt("Gravity"));
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(MODE, FallingMoveType.RENDER_MOVE.toString());
		this.getEntityData().define(ANIM_V_Y, 1f);
		this.getEntityData().define(DURATION, 20);
		this.getEntityData().define(GRAVITY, 0.1f);
	}

	@Override
	public void tick() {
		if (getMode() == FallingMoveType.RENDER_MOVE) setDeltaMovement(0, 0, 0);
		super.tick();
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, getGravity() / 2, 0.0D));
			}
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
			if ((this.onGround() && this.tickCount > this.getDuration()) || this.tickCount > this.getDuration() * 20) {
				this.discard();
			}
		}
		else {
			float animVY = getAnimVY();
			prevAnimY = animY;
			animY += animVY;
			setAnimVY(animVY - getGravity());
			if (animY < -0.5) discard();
		}
	}
}