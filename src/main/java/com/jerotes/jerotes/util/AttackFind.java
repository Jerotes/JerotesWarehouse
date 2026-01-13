package com.jerotes.jerotes.util;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.*;
import com.jerotes.jerotes.forge.JerotesAvoidDamageEvent;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.tool.ItemToolBasePike;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
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


    //非常规攻击触发效果
    public static void attackBegin(LivingEntity attacker, Entity hurt) {
        int i = EnchantmentHelper.getFireAspect(attacker);
        if (i > 0) {
            hurt.setSecondsOnFire(i * 4);
        }
    }
    //攻击
    public static boolean attackAfter(LivingEntity attacker, Entity hurt, float damageBase, float knockbackBase, boolean useCustomNumber, float customNumber) {
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
                bl = hurt.hurt(attacker.damageSources().playerAttack(player), f * damageBase);
            }
        }
        else if (attacker instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.attackDamageSourceAsPlayer()) {
            DamageSource damageSources = new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), attacker);
            if (useCustomNumber) {
                bl = hurt.hurt(damageSources, f);
            }
            else {
                bl = hurt.hurt(damageSources, f * damageBase);
            }
        }
        else {
            if (useCustomNumber) {
                bl = hurt.hurt(attacker.damageSources().mobAttack(attacker), f);
            }
            else {
                bl = hurt.hurt(attacker.damageSources().mobAttack(attacker), f * damageBase);
            }
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackBase), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
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
    public static boolean attackAfterCustomDamage(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageBase, float knockbackBase, boolean useCustomNumber, float customNumber) {
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
            bl = hurt.hurt(damageSource, f * damageBase);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackBase), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
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
    public static boolean attackAfterCustomDamageNoEnchantAbout(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageBase, float knockbackBase, boolean useCustomNumber, float customNumber) {
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
            bl = hurt.hurt(damageSource, f * damageBase);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackBase), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
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
    public static boolean attackAfterCustomDamageFalse(LivingEntity attacker, Entity hurt, DamageSource damageSource, float damageBase, float knockbackBase, boolean useCustomNumber, float customNumber) {
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
            bl = hurt.hurt(damageSource, f * damageBase);
        }
        if (bl) {
            if (f1 > 0.0F && hurt instanceof LivingEntity livingEntity) {
                livingEntity.knockback((f1 * knockbackBase), Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
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

    //阵营免伤
    public static boolean SameFactionAvoidDamage(Entity attacker, LivingEntity hurt) {
        if (!MainConfig.SameFactionAvoidDamage)
            return false;
        if (!(attacker instanceof Mob mobAttacker) || !(hurt instanceof Mob mobHurt))
            return false;
        if (mobAttacker.getTarget() != mobHurt && mobHurt.getTarget() != mobAttacker && (mobAttacker.getTeam() == null && mobHurt.getTeam() == null || mobAttacker.isAlliedTo(mobHurt))) {
            if (EntityFactionFind.isFaction(mobHurt, mobAttacker) && (mobHurt instanceof JerotesEntity && mobAttacker instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities)) {
                return true;
            }
            //event
            JerotesAvoidDamageEvent event = new JerotesAvoidDamageEvent(hurt, hurt.damageSources().mobAttack(hurt), attacker);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return true;
            }
            //仆从主人
            if ((mobAttacker instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities) && mobAttacker instanceof OwnableEntity ownable && ownable.getOwner() == mobHurt) {
                return true;
            }
            //铜刻类型生物
            if (EntityFactionFind.isCarved(mobHurt.getType())) {
                if (EntityFactionFind.isCarved(mobAttacker.getType()) || mobAttacker instanceof AbstractVillager || mobAttacker instanceof IronGolem)
                    return true;
            }


            if (mobAttacker instanceof JerotesEntity || mobHurt instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities) {
                //信任生物不误伤信任者的宠物
                if (mobHurt instanceof OwnableEntity ownable && ownable.getOwner() != null
                        && Main.isTrusted(mobAttacker, ownable.getOwner(), true)) {
                    return true;
                }
                //信任生物不误伤信任者
                if (Main.isTrusted(mobAttacker, mobHurt, true)) {
                    return true;
                }
                //信任者不误伤信任生物
                if (Main.isTrusted(mobHurt, mobAttacker, true)) {
                    return true;
                }
                //信任者的宠物不误伤信任生物
                if (mobAttacker instanceof OwnableEntity ownable && ownable.getOwner() != null && Main.isTrusted(mobHurt, ownable.getOwner(), true)) {
                    return true;
                }
                //灾厄村民
                if (EntityFactionFind.isRaider(mobHurt) && EntityFactionFind.isRaider(mobAttacker)) {
                    return true;
                }
                //灾厄村民
                if (EntityFactionFind.isPiglin(mobHurt) && EntityFactionFind.isPiglin(mobAttacker)) {
                    return true;
                }
                //灾厄村民
                if (EntityFactionFind.isPiglin(mobHurt) && EntityFactionFind.isPiglin(mobAttacker)) {
                    return true;
                }
            }


            //村民类生物
            if (mobHurt instanceof AbstractVillager) {
                if (EntityFactionFind.isCarved(mobAttacker.getType()))
                    return true;
            }
            //铁傀儡类生物
            if (mobHurt instanceof IronGolem) {
                if (EntityFactionFind.isCarved(mobAttacker.getType()))
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

    public static void individualAttack(Mob mob, double angleThreshold, int n) {
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
            controlVehicleEntity.individualAttack(tureHurt, n);
            controlVehicleEntity.individualAttack(tureHurt);
        } else {
            mob.doHurtTarget(tureHurt);
        }
    }
    public static void individualAttack(Mob mob, double angleThreshold) {
        individualAttack(mob, angleThreshold, 0);
    }
}

