package com.jerotes.jerotes;

import com.jerotes.jerotes.client.CilentInit;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.event.RendererEvent;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JerotesWarehouse.MODID)
public class JerotesWarehouse
{
    public static final String MODID = "jerotes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final EquipmentSlot[] slot = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public JerotesWarehouse() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        JerotesItems.REGISTRY.register(modEventBus);
        JerotesMenus.REGISTRY.register(modEventBus);
        JerotesEntityType.REGISTRY.register(modEventBus);
        JerotesTabs.REGISTRY.register(modEventBus);
        JerotesMobEffects.REGISTRY.register(modEventBus);
        JerotesPotions.REGISTRY.register(modEventBus);
        JerotesParticleTypes.REGISTRY.register(modEventBus);
        JerotesEnchantments.REGISTRY.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MainConfig.CLIENT_SPEC);
        modEventBus.addListener(this::initClient);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
        event.enqueueWork(JerotesItems::setup);
        event.enqueueWork(JerotesPotions::init);
    }
    private void initClient(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        event.enqueueWork(() -> {
            CilentInit.clientInit();
            MinecraftForge.EVENT_BUS.register(new RendererEvent());
        });
    }
}
