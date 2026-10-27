package com.jerotes.jerotes.blockentity;

import com.jerotes.jerotes.block.IceSurfaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

public class IceSurfaceBlockEntity extends BlockEntity {

    public static final int DEFAULT_MAX = 120;

    private int remaining = DEFAULT_MAX;
    private int max = DEFAULT_MAX;

    public IceSurfaceBlockEntity(BlockPos pos, BlockState state) {
        super(IceSurfaceBlockEntities.ICE_SURFACE.get(), pos, state);
    }

    public int getRemaining() {
        return this.remaining;
    }

    public int getMax() {
        return this.max;
    }

    public void merge(int newRemaining, int newMax) {
        this.remaining = Math.max(this.remaining, newRemaining);
        this.max = Math.min(this.max, newMax);
        this.refreshAge();
        this.setChanged();
    }

    public void init(int newMax) {
        this.remaining = newMax;
        this.max = newMax;
        this.refreshAge();
        this.setChanged();
    }

    private void refreshAge() {
        Level level = this.level;
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof IceSurfaceBlock)) {
            return;
        }
        int age = this.remaining >= this.max ? 0
                : 3 - Math.max(0, Math.min(3, (int) (3.0 * this.remaining / this.max)));
        int oldAge = state.getValue(IceSurfaceBlock.AGE);
        if (oldAge != age) {
            level.setBlock(this.worldPosition, state.setValue(IceSurfaceBlock.AGE, age), 2);
        }
    }

    private int meltSpeed() {
        Level level = this.level;
        if (level == null) {
            return 1;
        }
        if (!level.getFluidState(this.worldPosition).isEmpty()) {
            return 2;
        }
        double temperature = level.getBiome(this.worldPosition).value().getBaseTemperature();
        if (temperature > 1.5) {
            return 4;
        }
        if (temperature > 0.8) {
            return 2;
        }
        return 1;
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.remaining > 0) {
            this.remaining -= this.meltSpeed();
            if (this.remaining % 10 == 0) {
                this.refreshAge();
                this.setChanged();
            }
            if (this.remaining <= 0) {
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.FROSTED_ICE.defaultBlockState()),
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.1, this.worldPosition.getZ() + 0.5,
                            14, 0.45, 0.06, 0.45, 0.03);
                }
                this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Remaining", this.remaining);
        tag.putInt("Max", this.max);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.remaining = tag.getInt("Remaining");
        this.max = tag.getInt("Max");
    }
}
