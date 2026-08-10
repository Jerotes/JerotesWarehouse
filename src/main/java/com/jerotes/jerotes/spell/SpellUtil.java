package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.item.AAExplorationEye;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class SpellUtil {
    //法术反应
    public static boolean SpellReaction(Entity caster, Entity otherCaster, Entity otherMagic, int otherSpellLevel) {
        //boolean 判定是否执行清除等
        //法术反制
        if (CounterspellCanUse(caster)) {
            return Counterspell(caster, otherCaster, otherMagic, CounterspellLevel(caster), otherSpellLevel);
        }
        return false;
    }
    //法术反制前提
    public static boolean CounterspellCanUse(Entity caster) {
        return caster instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get());
    }
    public static int CounterspellLevel(Entity caster) {
        return caster instanceof LivingEntity livingEntity ? livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 : 0;
    }
    //法术反制-实现
    public static boolean Counterspell(Entity caster, Entity otherCaster, Entity otherMagic, int counterSpellLevel, int otherSpellLevel) {
        if (!CounterspellCanUse(caster)) {
            return false;
        }
        if (caster == otherCaster ||
                caster instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.SPELL_REACTION_LOCK.get()) ||
                (otherCaster instanceof LivingEntity otherCasterLivingEntity && AttackFind.SameFactionAvoidDamage(caster, otherCasterLivingEntity, false))) {
            return false;
        }
        boolean bl;
        int DC = 10 + otherSpellLevel;
        //直接等级判定
        if (counterSpellLevel >= otherSpellLevel) {
            bl = true;
        }
        //如果是玩家施展的反制，则强制要求过判定
        else if (caster instanceof Player player) {
            bl = (Main.rollDice(1, 20, player.getRandom()) + (caster instanceof SpellUseEntity useEntity ? useEntity.getSpellLevel() : 0)) >= DC;
        }
        //如果不是玩家，且配置文件无法判定，则超过等级直接判定失败
        else if (MainConfig.NoRollDice || !(MainConfig.HaveSavingThrow || MainConfig.HaveSavingThrowOnlyPlayerAndLegendary && EntityAndItemFind.isLegendary(caster))) {
            bl = false;
        }
        //其他情况投骰子
        else {
            bl = (Main.rollDice(1, 20, caster.level().getRandom()) + (caster instanceof SpellUseEntity useEntity ? useEntity.getSpellLevel() : 0)) >= DC;
        }

        if (caster instanceof LivingEntity livingEntity) {
            if (!caster.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.OFF_HAND);
            SpellFind.Counterspell(livingEntity);
        }

        //直接打断-停止施法
        if (otherMagic == null && otherCaster instanceof LivingEntity livingEntity) {
            if (otherCaster instanceof MagicSpellLivingEntity magicSpellLivingEntity && magicSpellLivingEntity.getCastingTickJerotes() > -1) {
                SpellType.stopCasting(livingEntity, otherSpellLevel, false);
            }
        }

        //直接打断-停止施法
        if (caster.level() instanceof ServerLevel serverLevel) {
            //产生粒子 玩家-目标施法者/魔法
            {
                Entity castersTarget = otherMagic != null ? otherMagic : otherCaster;
                Vec3 start = caster.position().add(0.0, Main.mobHeight(caster) * 0.7f, 0.0);
                Vec3 end = castersTarget.position().add(0.0, Main.mobHeight(caster) * 0.7f, 0.0);
                Vec3 dir = end.subtract(start);
                Vec3 norm = dir.normalize();
                int steps = Mth.floor(dir.length()) * 6 + 1;
                for (int i = 1; i < steps; i++) {
                    Vec3 pos = start.add(norm.scale(i / 6f));
                    serverLevel.sendParticles(bl ? JerotesParticleTypes.COUNTERSPELL.get() : JerotesParticleTypes.COUNTERSPELL_FAIL.get(), pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
            if (otherMagic != null && otherCaster != null) {
                Vec3 start = otherMagic.position().add(0.0, Main.mobHeight(caster) * 0.7f, 0.0);
                Vec3 end = otherCaster.position().add(0.0, Main.mobHeight(caster) * 0.7f, 0.0);
                Vec3 dir = end.subtract(start);
                Vec3 norm = dir.normalize();
                int steps = Mth.floor(dir.length()) * 6 + 1;
                for (int i = 1; i < steps; i++) {
                    Vec3 pos = start.add(norm.scale(i / 6f));
                    serverLevel.sendParticles(bl ? JerotesParticleTypes.COUNTERSPELL.get() : JerotesParticleTypes.COUNTERSPELL_FAIL.get(), pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
        return bl;
    }
}