package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;

import java.lang.reflect.Field;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JerotesSounds {
    public static final SoundEvent SPELL = createSoundEvent("spell");
    public static final SoundEvent BREATH = createSoundEvent("breath");
    public static final SoundEvent USE_SADDLE = createSoundEvent("use_saddle");
    public static final SoundEvent USE_CHEST = createSoundEvent("use_chest");
    public static final SoundEvent USE_BEAST_ARMOR = createSoundEvent("use_beast_armor");
    public static final SoundEvent USE_BANDAGE = createSoundEvent("use_bandage");
    public static final SoundEvent REPAIR_MOB = createSoundEvent("repair_mob");
    public static final SoundEvent TWOHANDED_BLOCK = createSoundEvent("twohanded_block");
    public static final SoundEvent BITE = createSoundEvent("bite");
    public static final SoundEvent TELEPORT = createSoundEvent("teleport");
    public static final SoundEvent ITEM_THROW = createSoundEvent("item_throw");
    public static final SoundEvent WIND = createSoundEvent("wind");
    public static final SoundEvent CLOAKING = createSoundEvent("cloaking");
    public static final SoundEvent WIND_BURST = createSoundEvent("wind_burst");
    public static final SoundEvent ATTACK_1 = createSoundEvent("attack_1");
    public static final SoundEvent LUNGE_1 = createSoundEvent("lunge_1");
    public static final SoundEvent LUNGE_2 = createSoundEvent("lunge_2");
    public static final SoundEvent LUNGE_3 = createSoundEvent("lunge_3");
    public static final SoundEvent SPEAR_WOOD_USE = createSoundEvent("spear_wood_use");
    public static final SoundEvent SPEAR_WOOD_HIT = createSoundEvent("spear_wood_hit");
    public static final SoundEvent SPEAR_WOOD_ATTACK = createSoundEvent("spear_wood_attack");
    public static final SoundEvent SPEAR_USE = createSoundEvent("spear_use");
    public static final SoundEvent SPEAR_HIT = createSoundEvent("spear_hit");
    public static final SoundEvent SPEAR_ATTACK = createSoundEvent("spear_attack");

    public static final SoundEvent MAGIC_MAGIC_MISSILE = createSoundEvent("magic_magic_missile");
    public static final SoundEvent MAGIC_POISON_BREATH = createSoundEvent("magic_poison_breath");
    public static final SoundEvent MAGIC_RAY_OF_SICKNESS = createSoundEvent("magic_ray_of_sickness");
    public static final SoundEvent MAGIC_RAY_OF_ENFEEBLEMENT = createSoundEvent("magic_ray_of_enfeeblement");
    public static final SoundEvent MAGIC_LIGHTNING_BOLT = createSoundEvent("magic_lightning_bolt");
    public static final SoundEvent MAGIC_VICIOUS_MOCKERY = createSoundEvent("magic_vicious_mockery");
    public static final SoundEvent MAGIC_HOLD_PERSON = createSoundEvent("magic_hold_person");
    public static final SoundEvent MAGIC_BESTOW_CURSE = createSoundEvent("magic_bestow_curse");
    public static final SoundEvent MAGIC_PHANTASMAL_KILLER = createSoundEvent("magic_phantasmal_killer");
    public static final SoundEvent MAGIC_EYEBITE = createSoundEvent("magic_eyebite");
    public static final SoundEvent MAGIC_FIRE_ABSORPTION = createSoundEvent("magic_fire_absorption");
    public static final SoundEvent MAGIC_FREEZE_ABSORPTION = createSoundEvent("magic_freeze_absorption");
    public static final SoundEvent MAGIC_LIGHTNING_ABSORPTION = createSoundEvent("magic_lightning_absorption");
    public static final SoundEvent MAGIC_CURE_WOUNDS = createSoundEvent("magic_cure_wounds");
    public static final SoundEvent MAGIC_MISTY_STEP = createSoundEvent("magic_misty_step");
    public static final SoundEvent MAGIC_MIRROR_IMAGE = createSoundEvent("magic_mirror_image");
    public static final SoundEvent MAGIC_INVISIBLE_PASSAGE = createSoundEvent("magic_invisible_passage");
    public static final SoundEvent MAGIC_COUNTERSPELL = createSoundEvent("magic_counterspell");
    public static final SoundEvent MAGIC_MAGIC_ABSORPTION = createSoundEvent("magic_magic_absorption");

    private static SoundEvent createSoundEvent(final String soundName) {
        final ResourceLocation soundID = new ResourceLocation(JerotesWarehouse.MODID, soundName);
        return SoundEvent.createVariableRangeEvent(soundID);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final NewRegistryEvent event) {
        try {
            for (Field f : JerotesSounds.class.getFields()) {
                Object obj = f.get(null);
                if (obj instanceof SoundEvent) {
                    ForgeRegistries.SOUND_EVENTS.register(((SoundEvent) obj).getLocation(), (SoundEvent) obj);
                } else if (obj instanceof SoundEvent[]) {
                    for (SoundEvent soundEvent : (SoundEvent[]) obj) {
                        ForgeRegistries.SOUND_EVENTS.register(soundEvent.getLocation(), soundEvent);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

