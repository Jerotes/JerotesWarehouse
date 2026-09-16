package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JerotesBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, JerotesWarehouse.MODID);
	public static final RegistryObject<Block> REDSTONE_LASER = REGISTRY.register("redstone_laser", RedstoneLaserBlock::new);
	public static final RegistryObject<Block> JEROTES_LIGHT = REGISTRY.register("jerotes_light", JerotesLightBlock::new);
}
