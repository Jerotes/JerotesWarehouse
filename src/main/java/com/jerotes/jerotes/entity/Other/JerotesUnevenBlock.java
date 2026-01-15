package com.jerotes.jerotes.entity.Other;

import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class JerotesUnevenBlock extends BaseFallingBlock {
	private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Float> ANIM_V_Y = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> ADD_HEIGHT = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> START_TICK = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> STOP_TICK = SynchedEntityData.defineId(JerotesUnevenBlock.class, EntityDataSerializers.INT);
	//下落
	//朝向


	public JerotesUnevenBlock(EntityType<JerotesUnevenBlock> entityType, Level level) {
		super(entityType, level);
		this.setDuration(20);
		this.noPhysics = true;
	}

	public JerotesUnevenBlock(Level worldIn, BlockState blockState, float vy) {
		super(JerotesEntityType.JEROTES_UNEVEN_BLOCK.get(), worldIn);
		this.setMode(FallingMoveType.RENDER_MOVE);
		setBlockState(blockState);
		setAnimVY(vy);
		this.noPhysics = true;
	}

	public JerotesUnevenBlock(Level level, double px, double py, double pz, BlockState blockState, int duration) {
		super(JerotesEntityType.JEROTES_UNEVEN_BLOCK.get(), level);
		this.setMode(FallingMoveType.OVERALL_MOVE);
		this.setBlockState(blockState);
		this.setPos(px, py + (double) ((1.0F - this.getBbHeight()) / 2.0F), pz);
		this.setDuration(duration);
		this.xo = px;
		this.yo = py;
		this.zo = pz;
		this.setDeltaMovement(Vec3.ZERO);
		this.noPhysics = true;
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
	public boolean isInWall() {
		return false;
	}
	protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 1.1F;
	}
	public void lookAt(Vec3 vec3, float f, float f2) {
		double d0 = vec3.x() - this.getX();
		double d2 = vec3.z() - this.getZ();
		double d1 = vec3.y() - this.getY() + 0.5f;
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		float f3 = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
		float f4 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
		this.setXRot(this.rotlerp(this.getXRot(), f4, f2));
		this.setYRot(this.rotlerp(this.getYRot(), f3, f));
	}
	private float rotlerp(float f, float f2, float f3) {
		float f4 = Mth.wrapDegrees(f2 - f);
		if (f4 > f3) {
			f4 = f3;
		}
		if (f4 < -f3) {
			f4 = -f3;
		}
		return f + f4;
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
	public float getAddHeight() {
		return getEntityData().get(ADD_HEIGHT);
	}
	public void setAddHeight(float addHeight) {
		getEntityData().set(ADD_HEIGHT, addHeight);
	}
	public int getStartTick() {
		return getEntityData().get(START_TICK);
	}
	public void setStartTick(int startTick) {
		getEntityData().set(START_TICK, startTick);
	}
	public int getStopTick() {
		return getEntityData().get(STOP_TICK);
	}
	public void setStopTick(int stopTick) {
		getEntityData().set(STOP_TICK, stopTick);
	}
	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putFloat("AnimVY", getAnimVY());
		compoundTag.putInt("Duration", getDuration());
		compoundTag.putFloat("AddHeight", getAddHeight());
		compoundTag.putFloat("StartTick", getStartTick());
		compoundTag.putFloat("StopTick", getStopTick());
	}
	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.setAnimVY(compoundTag.getFloat("AnimVY"));
		this.setDuration(compoundTag.getInt("Duration"));
		this.setAddHeight(compoundTag.getFloat("AddHeight"));
		this.setStartTick(compoundTag.getInt("StartTick"));
		this.setStopTick(compoundTag.getInt("StopTick"));
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(MODE, FallingMoveType.RENDER_MOVE.toString());
		this.getEntityData().define(ANIM_V_Y, 1f);
		this.getEntityData().define(DURATION, 20);
		this.getEntityData().define(ADD_HEIGHT, 0.5f);
		this.getEntityData().define(START_TICK, 0);
		this.getEntityData().define(STOP_TICK, 15);
	}

	public void tick() {
		if (this.tickCount > this.getDuration()) {
			this.discard();
		}
		//上升
		int startTicks = Math.max(1, this.getStartTick() + 1);
		if (this.tickCount - 1 <= getStartTick()) {
			this.setPos(this.getPosition(0).add(0, (getAddHeight() / startTicks), 0));
		}
		//下落
		int stopTicks = Math.max(1, this.getDuration() - this.getStopTick());
		if (this.tickCount >= this.getStopTick()) {
			this.setPos(this.getPosition(0).add(0, -(getAddHeight() / stopTicks), 0));
		}
		super.tick();
	}
}