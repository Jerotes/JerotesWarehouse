package com.jerotes.jerotes.util;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesAvoidDamageEvent;
import com.jerotes.jerotes.forge.JerotesMeleeDamageFromMainHandIsOffHandEvent;
import com.jerotes.jerotes.init.JerotesDamageTypeTags;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AttackFind {
    //伤害类型
    public static DamageSource findDamageType(Entity entity, ResourceKey<DamageType> damageTypeResourceKey) {
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeResourceKey));
    }
    public static DamageSource findDamageType(Entity entity, ResourceKey<DamageType> damageTypeResourceKey, Entity entity1, Entity entity2) {
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeResourceKey), entity1, entity2);
    }
    public static DamageSource findDamageType(Entity entity, ResourceKey<DamageType> damageTypeResourceKey, Entity entity1) {
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeResourceKey), entity1);
    }
    //是否无击退
    public static boolean noKnockback(DamageSource damagesource) {
        return damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:no_knockback")));
    }
    //抵挡魔法
    public static boolean canSelfResistMagic(DamageSource damageSource, float amount, LivingEntity entity, int selfLevel) {
        if (!(entity instanceof SpellUseEntity spellUse))
            return false;
        if (!MagicResistance(damageSource))
            return false;
        if (damageSource.getEntity() instanceof SpellUseEntity ||
                damageSource.getDirectEntity() instanceof SpellUseEntity ||
                damageSource.getEntity() instanceof MagicAbout ||
                damageSource.getDirectEntity() instanceof MagicAbout) {
            int otherLevl = getOtherLevl(damageSource);
            return selfLevel <= otherLevl;
        }
        else {
            int spelllevel = Math.max(0, spellUse.getSpellLevel() - 1);
            float maxResistance = spelllevel * spelllevel * 10;
            float afterDamage = amount * Math.max(0.05f, ((100f - spelllevel * spelllevel)/100f));
            if (Float.isNaN(afterDamage) || Float.isInfinite(afterDamage)) {
                return false;
            }
            return afterDamage <= maxResistance;
        }
    }
    public static boolean canSelfResistMagic(DamageSource damageSource, float amount, LivingEntity entity) {
        if (!(entity instanceof SpellUseEntity spellUse))
            return false;
        return canSelfResistMagic(damageSource, amount, entity, spellUse.getSpellLevel());
    }
    public static int getOtherLevl(DamageSource damageSource) {
        int ownerSpellUseLevel = damageSource.getEntity() instanceof SpellUseEntity spellUse1 ? spellUse1.getSpellLevel() : 1;
        int magicSpellUseLevel = damageSource.getDirectEntity() instanceof SpellUseEntity spellUse1 ? spellUse1.getSpellLevel() : 1;
        int ownerMagicAboutLevel = damageSource.getEntity() instanceof MagicAbout magicAbout ? magicAbout.getSpellLevel() : 1;
        int magicMagicAboutLevel = damageSource.getDirectEntity() instanceof MagicAbout magicAbout ? magicAbout.getSpellLevel() : 1;
        int otherLevl = Math.max(Math.max(ownerSpellUseLevel, magicSpellUseLevel),Math.max(ownerMagicAboutLevel, magicMagicAboutLevel));
        return otherLevl;
    }

    //是否近战攻击
    public static boolean isMeleeDamage(DamageSource damageSource) {
        return (damageSource.getEntity() == damageSource.getDirectEntity() ||
                damageSource.getEntity() != null && damageSource.getDirectEntity() == null)
                && damageSource.is(JerotesDamageTypeTags.IS_MELEE);
    }
    public static boolean MeleeDamageFromMainHandNotOffHand(LivingEntity livingEntity) {
        //event
        JerotesMeleeDamageFromMainHandIsOffHandEvent event = new JerotesMeleeDamageFromMainHandIsOffHandEvent(livingEntity);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isOffHand();
    }
    //是否魔法抵抗
    public static boolean MagicResistance(DamageSource damagesource) {
        return  damagesource.is(JerotesDamageTypeTags.IS_MAGIC)
                || (damagesource.getDirectEntity() != null && damagesource.getDirectEntity().getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:magic_shoots"))))
                || (damagesource.getEntity() != null && damagesource.getEntity().getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:magic_shoots"))))
                || damagesource.getDirectEntity() instanceof MagicAbout || damagesource.getEntity() instanceof MagicAbout
                || damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud
                || damagesource.getEntity() instanceof Warden && damagesource.is(DamageTypes.SONIC_BOOM);
    }
    //是否纯粹魔法
    public static boolean isTrueMagic(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_TRUE_MAGIC);
    }
    //是否力场
    public static boolean isForceDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_FORCE);
    }
    //是否火焰
    public static boolean isFireDamage(DamageSource damagesource) {
        return damagesource.is(DamageTypeTags.IS_FIRE);
    }
    //是否冰冻/冻伤
    public static boolean isFreezeDamage(DamageSource damagesource) {
        return isColdDamage(damagesource);
    }
    public static boolean isColdDamage(DamageSource damagesource) {
        return damagesource.is(DamageTypeTags.IS_FREEZING);
    }
    //是否闪电
    public static boolean isLightningDamage(DamageSource damagesource) {
        return damagesource.is(DamageTypeTags.IS_LIGHTNING);
    }
    //是否腐蚀/强酸
    public static boolean isAcidDamage(DamageSource damagesource) {
        return isCorrosionDamage(damagesource);
    }
    public static boolean isCorrosionDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_CORROSION) || damagesource.is(JerotesDamageTypes.ACID);
    }
    //是否雷鸣/音波
    public static boolean isThunderDamage(DamageSource damagesource) {
        return isSonicDamage(damagesource);
    }
    public static boolean isSonicDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_THUNDER);
    }
    //是否中毒
    public static boolean isPoisonDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_POISON);
    }
    //是否心灵/精神
    public static boolean isMentalDamage(DamageSource damagesource) {
        return isPsychicDamage(damagesource);
    }
    public static boolean isPsychicDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_PSYCHIC);
    }
    //是否光耀/正能量
    public static boolean isPositiveDamage(DamageSource damagesource) {
        return isRadiantDamage(damagesource);
    }
    public static boolean isRadiantDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_RADIANT);
    }
    //是否黯蚀/负能量
    public static boolean isNegativeDamage(DamageSource damagesource) {
        return isNecroticDamage(damagesource);
    }
    public static boolean isNecroticDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_NECROTIC);
    }
    //是否溺水/水
    public static boolean isWaterDamage(DamageSource damagesource) {
        return isDrownDamage(damagesource);
    }
    public static boolean isDrownDamage(DamageSource damagesource) {
        return damagesource.is(DamageTypeTags.IS_DROWNING);
    }
    //是否流血
    public static boolean isBleedDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_BLEED);
    }
    //是否凋零
    public static boolean isWitherDamage(DamageSource damagesource) {
        return damagesource.is(JerotesDamageTypeTags.IS_WITHER);
    }

    //削减
    public static void MaxHealthReduceSet(LivingEntity livingEntity, float count) {
        if (livingEntity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity) {
            jerotesChangeLivingEntity.setMaxHealthReduceCountJerotes(count);
        }
    }
    //继续削减
    public static void MaxHealthReduceAdd(LivingEntity livingEntity, float count) {
        if (livingEntity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity) {
            jerotesChangeLivingEntity.setMaxHealthReduceCountJerotes(jerotesChangeLivingEntity.getMaxHealthReduceCountJerotes() + count);
        }
    }

    //判断主仆
    public static LivingEntity getOwnerTrue(Entity entity) {
        if (entity instanceof OwnableEntity ownable && ownable.getOwner() != null)
            return ownable.getOwner();
        return null;
    }
    //不能攻击
    public static boolean FindCanNotAttack(LivingEntity attacker, Entity victim) {
        if (victim == null)
            return true;
        if (attacker == null)
            return true;
        if ((attacker.distanceToSqr(victim)) > Double.MAX_VALUE)
            return true;
        //对自身
        if (victim == attacker && attacker instanceof Mob mob && mob.getTarget() != attacker)
            return true;
        if (victim == attacker && !(attacker instanceof Mob))
            return true;
        if (attacker instanceof Mob mob && mob.getTarget() == victim)
            return false;
        if (victim instanceof Mob mob && mob.getTarget() == attacker)
            return false;
        //对信任的
        if (victim instanceof LivingEntity livingVictim && (Main.isTrusted(livingVictim, attacker, true)
                || Main.isTrusted(attacker, livingVictim, true)))
            return true;
        {
            boolean victimHasOwner =  getOwnerTrue(victim) != null;
            boolean attackerHasOwner = getOwnerTrue(attacker) != null;
            //对主人的主人对仆从的仆从
            if (victim instanceof LivingEntity livingEntity && hasMasterServantRelation(attacker, livingEntity))
                return true;
            //对主人 对仆从
            if (attackerHasOwner && getOwnerTrue(attacker) != null && getOwnerTrue(attacker) == victim)
                return true;
            if (victimHasOwner && getOwnerTrue(victim) != null && getOwnerTrue(victim) == attacker)
                return true;
            //
            if (victimHasOwner && getOwnerTrue(victim) != null
                    && (attacker.isAlliedTo(getOwnerTrue(victim)) || EntityFactionFind.isFaction(attacker, getOwnerTrue(victim))))
                return true;
            if (attackerHasOwner && getOwnerTrue(attacker) != null && victim instanceof LivingEntity livingVictim
                    && (victim.isAlliedTo(getOwnerTrue(attacker)) || EntityFactionFind.isFaction(livingVictim, getOwnerTrue(attacker))))
                return true;
            if (attackerHasOwner && getOwnerTrue(attacker) != null && victimHasOwner && getOwnerTrue(victim) != null
                    && (getOwnerTrue(attacker).isAlliedTo(getOwnerTrue(victim)) || EntityFactionFind.isFaction(getOwnerTrue(victim), getOwnerTrue(attacker))))
                return true;
            //共同主人
            if (victimHasOwner && attackerHasOwner && getOwnerTrue(victim) != null && getOwnerTrue(attacker) != null &&
                    getOwnerTrue(victim) == getOwnerTrue(attacker))
                return true;
            //不攻击主人信任的 不被主人信任的攻击
            if (attackerHasOwner && getOwnerTrue(attacker) != null && victim instanceof LivingEntity livingVictim && Main.isTrusted(getOwnerTrue(attacker), livingVictim, true))
                return true;
            if (victimHasOwner && getOwnerTrue(victim) != null &&
                    Main.isTrusted(getOwnerTrue(victim), attacker, true))
                return true;
            if (victimHasOwner && getOwnerTrue(victim) != null && attackerHasOwner && getOwnerTrue(attacker) != null &&
                    Main.isTrusted(getOwnerTrue(victim), getOwnerTrue(attacker), true))
                return true;
            if (victimHasOwner && getOwnerTrue(victim) != null && attackerHasOwner && getOwnerTrue(attacker) != null &&
                    Main.isTrusted(getOwnerTrue(attacker), getOwnerTrue(victim), true))
                return true;
        }
        //玩家阵营-双方非目标
        if (attacker.isAlliedTo(victim))
            return true;
        //生物阵营-双方非目标
        if (victim instanceof LivingEntity livingEntity && EntityFactionFind.isFaction(attacker, livingEntity))
            return true;
        //无法攻击-对方非目标
        if (victim instanceof LivingEntity livingEntity && !attacker.canAttack(livingEntity) || !attacker.canAttackType(victim.getType()))
            return true;
        //盔甲架
        return victim instanceof ArmorStand armorStand && armorStand.isMarker();
    }
    //a的主人是不是b的仆从
    public static boolean SomeOwner(LivingEntity needOwner, LivingEntity needServant) {
        if (getOwnerTrue(needServant) != null && getOwnerTrue(needServant) == needOwner)
            return true;
        if (getOwnerTrue(needServant) != null)
            return SomeOwner(needOwner, getOwnerTrue(needServant));
        return false;
    }
    public static boolean hasMasterServantRelation(LivingEntity entityA, LivingEntity entityB) {
        return SomeOwner(entityA, entityB) || SomeOwner(entityB, entityA);
    }

    //不能攻击除非
    public static boolean FindCanNotAttack(LivingEntity attacker, Entity victim, Entity canNotAttack) {
        if (victim == null)
            return true;
        //并非
        if (canNotAttack == victim)
            return true;
        //对自身
        if (attacker == victim)
            return true;
        //阵营
        if (attacker.isAlliedTo(victim) && (!(victim instanceof Mob) || (victim instanceof Mob mob && mob.getTarget() != attacker)) && (!(attacker instanceof Mob) || (attacker instanceof Mob mob && mob.getTarget() != victim)))
            return true;
        //盔甲架
        if (victim instanceof ArmorStand armorStand && armorStand.isMarker())
            return true;
        return false;
    }
    //驯服生物可攻击选择
    public static boolean wantsToAttack(Mob mob, LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (!(mob instanceof OwnableEntity ownable)) {
            return false;
        }
        if (ownable.getOwner() == null) {
            return false;
        }
        if (!(mob.canAttackType(livingEntity.getType()) && mob.canAttack(livingEntity))) {
            return false;
        }
        if (ownable.getOwner() == livingEntity) {
            return false;
        }
        if (!MainConfig.TamedMobAttackCreeperAndGhast && (livingEntity instanceof Creeper || livingEntity instanceof Ghast)) {
            return false;
        }
        if (FindCanNotAttack(mob, livingEntity)) {
            return false;
        }
        //玩家能否互相攻击
        if (livingEntity instanceof Player player && livingEntity2 instanceof Player player2 && !(player2.canHarmPlayer(player))) {
            return false;
        }
        if (livingEntity instanceof OwnableEntity ownableTarget && ownableTarget.getOwner() != null && (ownableTarget.getOwner() == mob || ownableTarget.getOwner() == ownable.getOwner() || ownableTarget.getOwnerUUID() == ownable.getOwnerUUID() || ownableTarget.getOwnerUUID() == mob.getUUID())) {
            return false;
        }
        if (livingEntity instanceof OwnableEntity ownableTarget && ownableTarget.getOwner() != null && (ownableTarget.getOwner().isAlliedTo(livingEntity2) || ownableTarget.getOwner().isAlliedTo(mob))) {
            return false;
        }
        if (livingEntity.isAlliedTo(livingEntity2) || livingEntity.isAlliedTo(livingEntity2)) {
            return false;
        }
        return true;
    }

    //范围攻击范围
    public static float reachAttackReach(LivingEntity user, ItemStack weapon, float playerReach, float reach, float baseReachEliminate, float sweepEnchantmentAdd) {
        float reachs = (((user instanceof Player || user instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.otherAttackReachAsPlayer()) ? playerReach : reach) + ((user.getAttribute(ForgeMod.ENTITY_REACH.get()) != null) ? (float) Math.max(0, user.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - baseReachEliminate) : 0));
        reachs += sweepEnchantmentAdd * weapon.getEnchantmentLevel(Enchantments.SWEEPING_EDGE);
        return reachs;
    }
    public static boolean shouldCritAttack(LivingEntity attacker, Entity target) {
        return attacker.fallDistance > 0.0F && !attacker.onGround() && !attacker.onClimbable() && !attacker.isInWater() && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger() && target instanceof LivingEntity;
    }

        //非常规攻击触发效果
    public static void attackBegin(LivingEntity attacker, Entity hurt) {
        int i = EnchantmentHelper.getFireAspect(attacker);
        if (i > 0) {
            hurt.setSecondsOnFire(i * 4);
        }
    }
    //攻击
    public static boolean attackAfter(LivingEntity attacker, Entity hurt, float damageMulti, float knockbackMulti, boolean useCustomNumber, float customNumber) {
        if (!hurt.isAlive()) {
            return false;
        }
        float f = 0;
        float f1 = 0;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            f = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        };
        if (attacker.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
            f1 = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        }
        if (hurt instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(),livingEntity.getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(attacker);
        }
        if (useCustomNumber) {
            f = customNumber;
        }
        if (f <= 0) {
            return false;
        }
        boolean bl;
        if (attacker instanceof Player player) {
            if (useCustomNumber) {
                bl = hurt.hurt(attacker.damageSources().playerAttack(player), f);
            }
            else {
                bl = hurt.hurt(attacker.damageSources().playerAttack(player), f * damageMulti);
            }
        }
        else if (attacker instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.attackDamageSourceAsPlayer()) {
            DamageSource damageSources = new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), attacker);
            if (useCustomNumber) {
                bl = hurt.hurt(damageSources, f);
            }
            else {
                bl = hurt.hurt(damageSources, f * damageMulti);
            }
        }
        else {
            if (useCustomNumber) {
                bl = hurt.hurt(attacker.damageSources().mobAttack(attacker), f);
            }
            else {
                bl = hurt.hurt(attacker.damageSources().mobAttack(attacker), f * damageMulti);
            }
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackMulti), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
                attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            if (hurt instanceof Player player) {
                maybeDisableShield(player, attacker.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY, attacker);
            }
            attacker.doEnchantDamageEffects(attacker, hurt);
            attacker.setLastHurtMob(hurt);
        }
        attackOther(attacker, hurt);
        return bl;
    }
    public static boolean attackAfterCustomDamage(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageMulti, float knockbackMulti, boolean useCustomNumber, float customNumber) {
        if (!hurt.isAlive()) {
            return false;
        }
        float f = 0;
        float f1 = 0;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            f = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        };
        if (attacker.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
            f1 = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        }
        if (hurt instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(),livingEntity.getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(attacker);
        }
        if (useCustomNumber) {
            f = customNumber;
        }
        if (f <= 0) {
            return false;
        }
        boolean bl;
        if (useCustomNumber) {
            bl = hurt.hurt(damageSource, f);
        }
        else {
            bl = hurt.hurt(damageSource, f * damageMulti);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackMulti), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
                attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            if (hurt instanceof Player player) {
                maybeDisableShield(player, attacker.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY, attacker);
            }
            attacker.doEnchantDamageEffects(attacker, hurt);
            attacker.setLastHurtMob(hurt);
        }
        attackOther(attacker, hurt);
        return bl;
    }
    public static boolean attackAfterCustomDamageNoEnchantAbout(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageMulti, float knockbackMulti, boolean useCustomNumber, float customNumber) {
        if (!hurt.isAlive()) {
            return false;
        }
        float f = 0;
        float f1 = 0;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            f = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        };
        if (attacker.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
            f1 = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        }
        if (useCustomNumber) {
            f = customNumber;
        }
        if (f <= 0) {
            return false;
        }
        boolean bl;
        if (useCustomNumber) {
            bl = hurt.hurt(damageSource, f);
        }
        else {
            bl = hurt.hurt(damageSource, f * damageMulti);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackMulti), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
                attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            if (hurt instanceof Player player) {
                maybeDisableShield(player, attacker.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY, attacker);
            }
            attacker.setLastHurtMob(hurt);
        }
        attackOther(attacker, hurt);
        return bl;
    }
    public static boolean attackAfterCustomDamageFalse(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageMulti, float knockbackMulti, boolean useCustomNumber, float customNumber) {
        if (!hurt.isAlive()) {
            return false;
        }
        float f = 0;
        float f1 = 0;
        if (attacker.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            f = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        };
        if (attacker.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
            f1 = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        }
        if (useCustomNumber) {
            f = customNumber;
        }
        if (f <= 0) {
            return false;
        }
        boolean bl;
        if (useCustomNumber) {
            bl = hurt.hurt(damageSource, f);
        }
        else {
            bl = hurt.hurt(damageSource, f * damageMulti);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackMulti), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
                attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            if (hurt instanceof LivingEntity livingEntityHurt && attacker instanceof Mob mob) {
                ItemStack itemStack = livingEntityHurt.isUsingItem() ? livingEntityHurt.getUseItem() : ItemStack.EMPTY;
                if (!mob.getMainHandItem().isEmpty() && !itemStack.isEmpty() && livingEntityHurt.getMainHandItem().getItem().canDisableShield(mob.getMainHandItem(), itemStack, livingEntityHurt, mob) && itemStack.getItem() instanceof ShieldItem) {
                    float chance = 0.25f + (float)EnchantmentHelper.getBlockEfficiency(mob) * 0.05f;
                    if (mob.getRandom().nextFloat() < chance) {
                        if (livingEntityHurt instanceof Player player) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        }
                        else if (livingEntityHurt instanceof UseShieldEntity useShieldEntity) {
                            useShieldEntity.disableShield();
                        }
                        mob.level().broadcastEntityEvent(livingEntityHurt, (byte)30);
                    }
                }
            }
            attacker.doEnchantDamageEffects(attacker, hurt);
            attacker.setLastHurtMob(hurt);
        }
        attackOther(attacker, hurt);
        return bl;
    }


    //额外行为
    public static void attackOther(LivingEntity attacker, Entity hurt) {
        if (!(attacker.getUseItem().getItem() instanceof ItemToolBasePike)) {
            ItemToolBaseSpearBase.jerotesLungeForwardMaybe(attacker);
        }
    }
    //破盾
    public  static void maybeDisableShield(Player player, ItemStack itemStack, ItemStack itemStack2, LivingEntity attacker) {
        if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem().canDisableShield(itemStack2,itemStack2,player,attacker) && itemStack2.getItem() instanceof ShieldItem) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(attacker) * 0.05F;
            if (attacker.getRandom().nextFloat() < f) {
                player.getCooldowns().addCooldown(itemStack2.getItem(), 100);
                attacker.level().broadcastEntityEvent(player, (byte)30);
            }
        }
    }
    //无抵抗击退
    public  static void JerotesKnockback(Entity entity, double d, double d2, double d3, float f) {
        if (entity instanceof LivingEntity livingEntity) {
            net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(livingEntity, (float) d, d2, d3);
            if(event.isCanceled()) return;
            d = event.getStrength();
            d2 = event.getRatioX();
            d3 = event.getRatioZ();
        }
        d *= 1.0D - f;
        if (!(d <= 0.0D)) {
            entity.hasImpulse = true;
            Vec3 vec3 = entity.getDeltaMovement();
            Vec3 vec31 = (new Vec3(d2, 0.0D, d3)).normalize().scale(d);
            entity.setDeltaMovement(vec3.x / 2.0D - vec31.x, entity.onGround() ? Math.min(0.4D, vec3.y / 2.0D + d) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    public static boolean SameFactionAvoidDamage(Entity attacker, LivingEntity hurt) {
        return SameFactionAvoidDamage(attacker, hurt, true);
    }
    //阵营免伤
    public static boolean SameFactionAvoidDamage(Entity attacker, LivingEntity hurt, boolean shouldJerotesEntity) {
        if (!MainConfig.SameFactionAvoidDamage && shouldJerotesEntity)
            return false;
        if (attacker == null || hurt == null)
            return false;
        if (!(attacker instanceof Mob)
//                || !(hurt instanceof Mob)
        ) {
            if (shouldJerotesEntity) {
                return false;
            }
        }
        if (!(attacker instanceof Mob mobAttacker && mobAttacker.getTarget() == hurt) &&
                !(hurt instanceof Mob mobHurt && mobHurt.getTarget() == attacker) &&
                (attacker.getTeam() == null && hurt.getTeam() == null || attacker.isAlliedTo(hurt) || hurt.isAlliedTo(attacker))) {
            if (attacker instanceof LivingEntity livingEntity && EntityFactionFind.isFaction(livingEntity, hurt) && (hurt instanceof JerotesEntity && attacker instanceof JerotesEntity || !shouldJerotesEntity || MainConfig.AffectsNonThisModEntities)) {
                return true;
            }
            //event
            JerotesAvoidDamageEvent event = new JerotesAvoidDamageEvent(hurt, hurt.damageSources().sonicBoom(attacker), attacker);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return true;
            }
            //仆从主人
            if ((attacker instanceof JerotesEntity || hurt instanceof JerotesEntity || !shouldJerotesEntity || MainConfig.AffectsNonThisModEntities)) {
                if (attacker instanceof OwnableEntity || hurt instanceof OwnableEntity) {
                    if (attacker instanceof OwnableEntity ownable && ownable.getOwner() == hurt)
                        return true;
                    else if (hurt instanceof OwnableEntity ownable && ownable.getOwner() == attacker)
                        return true;
                    else  if (hurt instanceof OwnableEntity ownable && attacker instanceof OwnableEntity ownable2 && ownable.getOwner() == ownable2.getOwner())
                        return true;
                    else if (attacker instanceof LivingEntity livingEntity && hasMasterServantRelation(livingEntity, hurt))
                        return true;
                }
            }
            //铜刻类型生物
            if (EntityFactionFind.isCarved(hurt.getType())) {
                if (EntityFactionFind.isCarved(attacker.getType()) || attacker instanceof AbstractVillager || attacker instanceof IronGolem)
                    return true;
            }


            if (attacker instanceof JerotesEntity || hurt instanceof JerotesEntity || !shouldJerotesEntity || MainConfig.AffectsNonThisModEntities) {
                //信任生物不误伤信任者的宠物
                if (attacker instanceof LivingEntity livingEntity && hurt instanceof OwnableEntity ownable && ownable.getOwner() != null
                        && Main.isTrusted(livingEntity, ownable.getOwner(), true)) {
                    return true;
                }
                //信任生物不误伤信任者
                if (attacker instanceof LivingEntity livingEntity && Main.isTrusted(livingEntity, hurt, true)) {
                    return true;
                }
                //信任者不误伤信任生物
                if (attacker instanceof LivingEntity livingEntity && Main.isTrusted(hurt, livingEntity, true)) {
                    return true;
                }
                //信任者的宠物不误伤信任生物
                if (attacker instanceof OwnableEntity ownable && ownable.getOwner() != null && Main.isTrusted(hurt, ownable.getOwner(), true)) {
                    return true;
                }
            }


            //村民类生物
            if (hurt instanceof AbstractVillager) {
                if (EntityFactionFind.isCarved(attacker.getType()))
                    return true;
            }
            //铁傀儡类生物
            if (hurt instanceof IronGolem) {
                if (EntityFactionFind.isCarved(attacker.getType()))
                    return true;
            }
        }
        return false;
    }




    public static boolean canRideAbout(Mob mount, @Nullable LivingEntity owner, @Nullable LivingEntity rider, LivingEntity newRider) {
        if (mount instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity && !jerotesChangeLivingEntity.canAddPassengerJerotes(newRider)) {
            return false;
        }
        return canRideAboutEnemy(mount, owner, rider, newRider);
    }
    public static boolean canRideAboutEnemy(Mob mount, @Nullable LivingEntity owner, @Nullable LivingEntity rider, LivingEntity newRider) {
        //敌人
        boolean enemy = nowEnemy(mount, newRider);
        //不是同一阵营
        boolean notSameFaction = mount.getTeam() != null && newRider.getTeam() != null && mount.getTeam() != newRider.getTeam();
        //主人的敌人
        boolean ownerEnemy = nowEnemy(owner, newRider);
        //不是主人同一阵营
        boolean notOwnerSameFaction = owner != null && owner.getTeam() != null && newRider.getTeam() != null && owner.getTeam() != newRider.getTeam();
        //骑手敌人
        boolean RiderEnemy = nowEnemy(rider, newRider);
        //不是骑手同一阵营
        boolean notRiderSameFaction = rider != null && rider.getTeam() != null && newRider.getTeam() != null && rider.getTeam() != newRider.getTeam();
        if (!enemy && !notSameFaction && !ownerEnemy && !notOwnerSameFaction && !RiderEnemy && !notRiderSameFaction) {
            return true;
        }
        return false;
    }

    public static boolean nowEnemy(LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (livingEntity == null || livingEntity2 == null)
            return false;
        if (FindCanNotAttack(livingEntity, livingEntity2))
            return false;
        if (SameFactionAvoidDamage(livingEntity, livingEntity2))
            return false;
        if (livingEntity instanceof Mob mob && mob.getTarget() == livingEntity2)
            return true;
        if (livingEntity2 instanceof Mob mob && mob.getTarget() == livingEntity)
            return true;
        if (livingEntity.getLastHurtByMob() == livingEntity2)
            return true;
        if (livingEntity.getLastHurtMob() == livingEntity2)
            return true;
        if (livingEntity2.getLastHurtByMob() == livingEntity)
            return true;
        if (livingEntity2.getLastHurtMob() == livingEntity)
            return true;
        return false;
    }

    public static void individualAttackJerotes(Mob mob, double angleThreshold, int n) {
        LivingEntity tureHurt = null;
        List<LivingEntity> list = mob.level().getEntitiesOfClass(LivingEntity.class, mob instanceof JerotesEntity jerotes && jerotes.getAttackBoundingBox() != null ? jerotes.getAttackBoundingBox().inflate(5) : mob.getBoundingBox().inflate(5));
        list.removeIf(livingEntity1 -> !mob.isWithinMeleeAttackRange(livingEntity1));
        if (list.isEmpty())
            return;
        for (LivingEntity hurt : list) {
            if (hurt == null) continue;
            if (AttackFind.FindCanNotAttack(mob, hurt)) continue;
            if (!mob.hasLineOfSight(hurt)) continue;
            if (!Main.canSeeAngle(mob, hurt.getEyePosition(), angleThreshold)) continue;
            if (tureHurt == null || tureHurt.distanceTo(mob) > hurt.distanceTo(mob)) {
                tureHurt = hurt;
            }
        }
        if (tureHurt == null)
            return;
        if (mob instanceof ControlVehicleEntity controlVehicleEntity) {
            controlVehicleEntity.individualAttackJerotes(tureHurt, n);
            controlVehicleEntity.individualAttackJerotes(tureHurt);
        } else {
            mob.doHurtTarget(tureHurt);
        }
    }
    public static void individualAttackJerotes(Mob mob, double angleThreshold) {
        individualAttackJerotes(mob, angleThreshold, 0);
    }
}

