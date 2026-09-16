package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.BestowCurseEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
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

    public static LivingEntity getTargetSpellUseTarget(Level level, UUID uuid) {
        if (!level.isClientSide()){
            return uuid == null ? null : Main.getLivingEntityByUUID(level, uuid);
        } else {
            return null;
        }
    }
    public static void TargetSpellUse(LivingEntity living) {
        if (living.level() instanceof ServerLevel) {
            String magic = Main.getJerotesPersistentData(living).getString("jerotes_using_magic");
            int level = (int) Main.getJerotesPersistentData(living).getDouble("jerotes_using_magic_level");
            LivingEntity livingEntity = getTargetSpellUseTarget(living.level(), Main.getJerotesPersistentData(living).getUUID("jerotes_using_magic_target"));
            if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
                int trueLevel = SpellListByString.getSpell(level, living, livingEntity, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();
                SpellListByString.getSpell(trueLevel, living, livingEntity, SpellRegistry.getSpellTypeById(magic)).spellFindUse();
            }
        }
    }

    @SubscribeEvent
    public static void SpellTick(LivingEvent.LivingTickEvent event) {
        LivingEntity caster = event.getEntity();
        if (caster == null)
            return;
        //整体计时
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_main_spell_cooldown", true);
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_add_spell_cooldown", true);
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_spell_tick", true);
        if (Main.getJerotesPersistentData(caster).getDouble("jerotes_spell_tick") == 5) {
            TargetSpellUse(caster);
        }
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
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_cold_absorption", true);
        //闪电吸收
        Main.persistentDataDoubleReduceToZero(caster, "jerotes_lightning_absorption", true);
    }


    @SubscribeEvent
    public static void CasterAttack(LivingAttackEvent event) {
        //攻击后停止
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity caster) {
            if (caster.level() instanceof ServerLevel serverLevel) {
                if (!SpellType.getConcentrationSpellTypeJerotes(caster).isEmpty()) {
                    if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(caster)) instanceof ConcentrationSpellEntity concentrationSpellEntity && concentrationSpellEntity.attackEndConcentration()) {
                        concentrationSpellEntity.endConcentration();
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void CasterHurt(LivingHurtEvent event) {
        if (event != null && event.getEntity() != null) {
            LivingEntity caster = event.getEntity();
            DamageSource damagesource = event.getSource();
            if (damagesource == null || caster == null)
                return;
            if (event.getAmount() > caster.getMaxHealth()/5) {
                if (!SpellType.getConcentrationSpellTypeJerotes(caster).isEmpty()) {
                    SpellType.endConcentration(caster);
                }
            }
        }
    }


    @SubscribeEvent
    public static void BestowCurse(LivingHurtEvent event) {
        if (event != null && event.getEntity() != null) {
            LivingEntity hurt = event.getEntity();
            DamageSource damagesource = event.getSource();
            if (damagesource == null || hurt == null || damagesource.getEntity() == null)
                return;
            if (event.getAmount() <= 0)
                return;
            if (event.getEntity().hasEffect(JerotesMobEffects.CURSED_ADDITIONAL_DAMAGE.get())) {
                if (damagesource.is(JerotesDamageTypes.CURSED_ADDITIONAL_DAMAGE))
                    return;
                List<BestowCurseEntity> list = hurt.level().getEntitiesOfClass(BestowCurseEntity.class, hurt.getBoundingBox().inflate(12, 12, 12));
                for (BestowCurseEntity bestowCurseEntity : list) {
                    if (bestowCurseEntity == null) continue;
                    if (bestowCurseEntity.getOwner() == damagesource.getEntity() || bestowCurseEntity.getOwner() == damagesource.getDirectEntity()) {
                        float scale = 1;
                        if (event.getAmount() > 0 && event.getAmount() < 3.5f) {
                            scale = Math.min(1, event.getAmount() / 3.5f);
                        }
                        hurt.invulnerableTime = 0;
                        DamageSource damageSource = AttackFind.findDamageType(hurt, JerotesDamageTypes.CURSED_ADDITIONAL_DAMAGE, bestowCurseEntity.getOwner());
                        hurt.hurt(damageSource, Main.rollDice(1, 8, hurt.getRandom()) * scale);
                        break;
                    }
                }
            }
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
        Main.persistentDataRemove(caster, "jerotes_cold_absorption");
        //闪电吸收
        Main.persistentDataRemove(caster, "jerotes_lightning_absorption");

        SpellType.stopCasting(caster, 999, true);

        if (!SpellType.getConcentrationSpellTypeJerotes(caster).isEmpty()) {
            SpellType.endConcentration(caster);
        }
    }



    //法术反制
    @SubscribeEvent
    public static void Counterspell(JerotesStopSpellEvent event) {
        Entity otherCaster = event.getOtherCaster();
        Entity otherMagic = event.getSpellEntity();
        Entity caster = event.getCaster();
        if (otherMagic instanceof MagicAboutEntity magicAbout && otherCaster != null && !magicAbout.isHelp()) {
            boolean bl = SpellUtil.SpellReaction(caster, otherCaster, otherMagic, magicAbout.getSpellLevel());
            event.setCanceled(bl);
        }
        //存在危害类
        if (event.getType() == 1) {
            boolean bl = SpellUtil.SpellReaction(caster, otherCaster, otherMagic, event.getLevel());
            event.setCanceled(bl);
        }
    }
}