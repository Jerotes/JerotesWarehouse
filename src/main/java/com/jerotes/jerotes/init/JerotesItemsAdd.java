package com.jerotes.jerotes.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public interface JerotesItemsAdd {
	static BlockState getBlockState(BlockSource blockSource) {
		return blockSource.getBlockState();
	}
	static BlockPos getPos(BlockSource blockSource) {
		return blockSource.getPos();
	}
	static ServerLevel getLevel(BlockSource blockSource) {
		return blockSource.getLevel();
	}
	static Vec3 getCenter(BlockSource blockSource) {
		return blockSource.getPos().getCenter();
	}
}
