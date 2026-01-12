package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class SpellFindUseEvent {



    public static int GetMainSpellUseCoolDownTick(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).MainSpellUseCoolDownTick;
    }
    public static int GetMainSpellUseCoolDownTickMax(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).MainSpellUseCoolDownTickMax;
    }
    public static int GetAddSpellUseCoolDownTick(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).AddSpellUseCoolDownTick;
    }
    public static int GetAddSpellUseCoolDownTickMax(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).AddSpellUseCoolDownTickMax;
    }

    public static int GetMainMainSpellTargetLevel(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel;
    }
    public static int GetAddSpellTargetLevel(Player player) {
        return player.getCapability(JerotesPlayerData.CAPABILITY, null)
                .orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTargetLevel;
    }

    @SubscribeEvent
    public static void SpellTick(LivingEvent.LivingTickEvent event) {
        LivingEntity caster = event.getEntity();
        if (caster == null)
            return;
        //整体计时
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_main_spell_cooldown", true);
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_add_spell_cooldown", true);
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_spell_cooldown", true);
        //法术间隔
        if (caster instanceof Player player) {
            if (GetMainSpellUseCoolDownTick(player) > GetMainSpellUseCoolDownTickMax(player)) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setMainSpellUseCoolDownTickMax(GetMainSpellUseCoolDownTick(player));
                });
            }
            if (GetAddSpellUseCoolDownTick(player) > GetAddSpellUseCoolDownTickMax(player)) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setAddSpellUseCoolDownTickMax(GetAddSpellUseCoolDownTick(player));
                });
            }
            if (GetMainSpellUseCoolDownTick(player) > 0) {
                if (!player.level().isClientSide()) {
                    (player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(player);
                }
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setMainSpellUseCoolDownTick(GetMainSpellUseCoolDownTick(player) - 1);
                });
            }
            else {
                if (GetMainSpellUseCoolDownTickMax(player) != 0){
                    if (!player.level().isClientSide()) {
                        (player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(player);
                    }
                    player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                        capability.setMainSpellUseCoolDownTickMax(0);
                    });
                }
            }
            if (GetAddSpellUseCoolDownTick(player) > 0) {
                if (!player.level().isClientSide()) {
                    (player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(player);
                }
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setAddSpellUseCoolDownTick(GetAddSpellUseCoolDownTick(player) - 1);
                });
            }
            else {
                if (GetAddSpellUseCoolDownTickMax(player) != 0) {
                    if (!player.level().isClientSide()) {
                        (player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables())).syncPlayerVariables(player);
                    }
                    player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                        capability.setAddSpellUseCoolDownTickMax(0);
                    });
                }
            }
        }

        //火焰吸收
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_fire_absorption", true);
        //冰霜吸收
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_freeze_absorption", true);
        //闪电吸收
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_lightning_absorption", true);
        //魔法飞弹
        if (caster.getPersistentData().getDouble("jerotes_magic_missile") > 0) {
            caster.getPersistentData().putDouble("jerotes_magic_missile", caster.getPersistentData().getDouble("jerotes_magic_missile") - 1);
            caster.getPersistentData().putDouble("jerotes_spell_cooldown", Math.max(2, caster.getPersistentData().getDouble("jerotes_spell_cooldown")));
            if (caster.getPersistentData().getDouble("jerotes_magic_missile") % 3 == 0) {
                if (caster.level() instanceof ServerLevel serverLevel) {
                    int spellLevelDamage = caster.getPersistentData().getInt("jerotes_magic_missile_spellLevelDamage");
                    UUID targetUUID = caster.getPersistentData().getUUID("jerotes_magic_missile_target");
                    if (!(serverLevel.getEntity(targetUUID) instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get()) && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= spellLevelDamage)) {
                        float spellLevelAccuracy = caster.getPersistentData().getFloat("jerotes_magic_missile_spellLevelAccuracy");
                        int count = caster.getPersistentData().getInt("jerotes_magic_missile_count");
                        float distance = caster.getPersistentData().getFloat("jerotes_magic_missile_distance");
                        MagicMissileEntity spell;
                        for (int i = 0; i < count; ++i) {
                            if (caster instanceof Mob mob && mob.getTarget() != null) {
                                mob.lookAt(mob.getTarget(), 360.0f, 360.0f);
                            }
                            double d2 = caster.getLookAngle().x;
                            double d3 = caster.getLookAngle().y;
                            double d4 = caster.getLookAngle().z;
                            spell = new MagicMissileEntity(spellLevelDamage, serverLevel, caster, d2, d3, d4);
                            spell.setPos(caster.getX(), caster.getY(0.7), caster.getZ());
                            spell.shootFromRotation(caster, caster.getXRot(), (float) (caster.getYRot() - ((count - 1) * distance) / 2 + i * distance), 0f, 1f, spellLevelAccuracy);
                            spell.setOwner(caster);
                            if (serverLevel.getEntity(targetUUID) != null && serverLevel.getEntity(targetUUID) != caster) {
                                spell.setTarget(serverLevel.getEntity(targetUUID));
                            }
                            serverLevel.addFreshEntity(spell);
                        }
                        if (!caster.isSilent()) {
                            caster.playSound(JerotesSounds.MAGIC_MAGIC_MISSILE,
                                    1.0f, 1.0F);
                        }
                    }
                }
            }
        }
        else if (caster.getPersistentData().get("jerotes_magic_missile") != null) {
            Main.persistentDataRemove(caster, "jerotes_magic_missile");
            Main.persistentDataRemove(caster, "jerotes_magic_missile_spellLevelDamage");
            Main.persistentDataRemove(caster, "jerotes_magic_missile_target");
            Main.persistentDataRemove(caster, "jerotes_magic_missile_spellLevelAccuracy");
            Main.persistentDataRemove(caster, "jerotes_magic_missile_count");
            Main.persistentDataRemove(caster, "jerotes_magic_missile_distance");
        }
    }
    @SubscribeEvent
    public static void CasterDeath(LivingDeathEvent event) {
        LivingEntity caster = event.getEntity();
        if (caster == null)
            return;
        //法术间隔
        Main.persistentDataRemove(caster, "jerotes_main_spell_cooldown");
        Main.persistentDataRemove(caster, "jerotes_add_spell_cooldown");
        Main.persistentDataRemove(caster, "jerotes_main_spell_cooldown_max");
        Main.persistentDataRemove(caster, "jerotes_add_spell_cooldown_max");
        //火焰吸收
        Main.persistentDataRemove(caster, "jerotes_fire_absorption");
        //冰霜吸收
        Main.persistentDataRemove(caster, "jerotes_freeze_absorption");
        //闪电吸收
        Main.persistentDataRemove(caster, "jerotes_lightning_absorption");
        SpellType.stops(caster, 10, true);
    }
}