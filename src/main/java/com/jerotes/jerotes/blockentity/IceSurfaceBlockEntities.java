package com.jerotes.jerotes.blockentity;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.init.JerotesBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IceSurfaceBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JerotesWarehouse.MODID);

    public static final RegistryObject<BlockEntityType<IceSurfaceBlockEntity>> ICE_SURFACE =
            REGISTRY.register("ice_surface", () -> BlockEntityType.Builder
                    .of(IceSurfaceBlockEntity::new, JerotesBlocks.ICE_SURFACE.get())
                    .build(null));
}
