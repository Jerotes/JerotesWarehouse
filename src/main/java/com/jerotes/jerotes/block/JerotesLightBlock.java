package com.jerotes.jerotes.block;

import com.jerotes.jerotes.init.JerotesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.ToIntFunction;

public class JerotesLightBlock extends Block implements SimpleWaterloggedBlock {
	public static final int MAX_LEVEL = 15;
	public static final int MAX_DURATION = 200;
	public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
	public static final IntegerProperty DURATION = IntegerProperty.create("duration", 1, MAX_DURATION);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> {
		return state.getValue(LEVEL);
	};

	public JerotesLightBlock() {
		super(Properties.of()
				.replaceable()
				.noLootTable()
				.strength(0f, 0f)
				.noCollission()
				.noOcclusion()
				.hasPostProcess((bs, br, bp) -> true)
				.emissiveRendering((bs, br, bp) -> true)
				.lightLevel(LIGHT_EMISSION));
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(LEVEL, 15)
				.setValue(DURATION, 5)
				.setValue(WATERLOGGED, false));
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return !context.getItemInHand().isEmpty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (!state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		level.scheduleTick(pos, this, getDurationTicks(state.getValue(DURATION)));
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	public static int getDurationTicks(int duration) {
		return switch (duration) {
			case 0 -> 1;
			case 1 -> 5;
			case 2 -> 20;
			case 3 -> 40;
			case 4 -> 60;
			case 5 -> 120;
			case 6 -> 240;
			case 7 -> 360;
			case 8 -> 480;
			case 9 -> 600;
			case 10 -> 1200;
			case 11 -> 2400;
			case 12 -> 6000;
			case 13 -> 12000;
			case 14 -> 36000;
			default -> 1;
		};
	}
	public static int getDurationLevel(int ticks) {
		return switch (ticks) {
			case 1 -> 0;
			case 5 -> 1;
			case 20 -> 2;
			case 40 -> 3;
			case 60 -> 4;
			case 120 -> 5;
			case 240 -> 6;
			case 360 -> 7;
			case 480 -> 8;
			case 600 -> 9;
			case 1200 -> 10;
			case 2400 -> 11;
			case 6000 -> 12;
			case 12000 -> 13;
			case 36000 -> 14;
			default -> 0;
		};
	}
	public static boolean placeLight(Level level, BlockPos pos, int lightLevel, int durationTicks) {
		lightLevel = Mth.clamp(lightLevel, 0, MAX_LEVEL);
		BlockState oldState = level.getBlockState(pos);
		if (oldState.is(JerotesBlocks.JEROTES_LIGHT.get())) {
			int oldLevel = oldState.getValue(LEVEL);
			if (oldLevel > lightLevel) {
				return false;
			}
		}
		int duration = getDurationLevel(durationTicks);
		BlockState newState = JerotesBlocks.JEROTES_LIGHT.get().defaultBlockState()
				.setValue(LEVEL, lightLevel)
				.setValue(DURATION, duration);
		level.setBlock(pos, newState, 3);
		level.scheduleTick(pos, JerotesBlocks.JEROTES_LIGHT.get(), durationTicks);
		return true;
	}
	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LEVEL, DURATION, WATERLOGGED);
	}
}