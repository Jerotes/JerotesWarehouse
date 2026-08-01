package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.entity.MagicSummoned.Zombie.FingerOfDeathZombieEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.JerotesZombieEntity;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Objects;

public class FingerOfDeathEntity extends BaseRayEntity {
    public FingerOfDeathEntity(EntityType<? extends FingerOfDeathEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FingerOfDeathEntity(EntityType<? extends FingerOfDeathEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        this(entityType, level);
        this.moveTo(d, d2, d3, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (d7 != 0.0) {
            this.xPower = d4 / d7 * 0.1;
            this.yPower = d5 / d7 * 0.1;
            this.zPower = d6 / d7 * 0.1;
        }
    }

    public FingerOfDeathEntity(int spellLevelDamage, int spellSubcategory, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.FINGER_OF_DEATH.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellSubcategory = spellSubcategory;
        this.summonTod = d;
        this.summonTod2 = d2;
        this.summonTod3 = d3;
    }

    protected void hitEntity(Entity entity) {
        super.hitEntity(entity);
        if (!this.isUseful())
            return;
        if (this.level().isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            //体质判定
                Entity entity2 = this.getOwner();
                DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.NECROTIC, this, entity2);
                boolean bl = livingEntity.hurt(damageSource,
                        (Main.rollDice(7 + 2 * (spellLevelDamage - 7), 8, livingEntity.getRandom())
                                + 30) * (!EntityAndItemFind.savingSpellThrow(livingEntity) ? 1f : 0.5f)
                );
                if (bl && !livingEntity.isAlive()) {
                    if (this.spellSubcategory == 0) {
                        if (!EntityAndItemFind.isLoreUndying(livingEntity) && entity2 instanceof LivingEntity living) {
                            if (this.level() instanceof ServerLevel serverLevel) {
                                if (livingEntity instanceof Mob mob && EntityFactionFind.isHumanoid(livingEntity)) {
                                    //特效
                                    for (int n = 0; n < 20; ++n) {
                                        serverLevel.sendParticles(JerotesParticleTypes.FINGER_OF_DEATH.get(),
                                                livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0, 0, 0, 0.125f);
                                    }
                                    ParticlesUse.summonParticle(serverLevel, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                                            0x30153c, 0x6a3279);
                                    PlayerTeam teams = (PlayerTeam) living.getTeam();
                                    FingerOfDeathZombieEntity fingerOfDeathZombieEntity = mob.convertTo(JerotesEntityType.FINGER_OF_DEATH_ZOMBIE.get(), true);
                                    if (fingerOfDeathZombieEntity != null) {
                                        if (teams != null) {
                                            serverLevel.getScoreboard().addPlayerToTeam(fingerOfDeathZombieEntity.getStringUUID(), teams);
                                        }
                                        if (!this.isSilent()) {
                                            serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                                        }
                                        ForgeEventFactory.onLivingConvert(livingEntity, fingerOfDeathZombieEntity);
                                        fingerOfDeathZombieEntity.setBaby(livingEntity.isBaby());
                                        fingerOfDeathZombieEntity.setScale(livingEntity.getDimensions(Pose.STANDING).height/livingEntity.getScale());
                                        fingerOfDeathZombieEntity.tame(living);
                                        fingerOfDeathZombieEntity.setOrderedToSit(false);
                                        fingerOfDeathZombieEntity.setWander(false);
                                        if (living instanceof Mob mobs && mobs.getTarget() != null) {
                                            fingerOfDeathZombieEntity.setTarget(mobs.getTarget());
                                        }
                                        //属性
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.MAX_HEALTH) != null &&
                                                mob.getAttribute(Attributes.MAX_HEALTH) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MAX_HEALTH)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.MAX_HEALTH));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null &&
                                                mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_DAMAGE)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_KNOCKBACK) != null &&
                                                mob.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_KNOCKBACK)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null &&
                                                mob.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR_TOUGHNESS)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR) != null &&
                                                mob.getAttribute(Attributes.ARMOR) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ARMOR));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null &&
                                                mob.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                                            if (livingEntity.getType() == EntityType.VILLAGER || livingEntity.getType() == EntityType.WANDERING_TRADER) {
                                                Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED)).
                                                        setBaseValue(mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 0.5f);
                                            }
                                            else {
                                                Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED)).
                                                        setBaseValue(mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 0.75f);
                                            }
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null &&
                                                mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE));
                                        }
                                        fingerOfDeathZombieEntity.setHealth(fingerOfDeathZombieEntity.getMaxHealth());
                                    }
                                }
                            }
                        }
                    }
                    else if (this.spellSubcategory == 1) {
                        if (!EntityAndItemFind.isLoreUndying(livingEntity) && entity2 instanceof LivingEntity living) {
                            if (this.level() instanceof ServerLevel serverLevel) {
                                if (livingEntity instanceof Mob mob && EntityFactionFind.isHumanoid(livingEntity)) {
                                    //特效
                                    for (int n = 0; n < 20; ++n) {
                                        serverLevel.sendParticles(JerotesParticleTypes.FINGER_OF_DEATH.get(),
                                                livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0, 0, 0, 0.125f);
                                    }
                                    ParticlesUse.summonParticle(serverLevel, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                                            0x30153c, 0x6a3279);
                                    PlayerTeam teams = (PlayerTeam) living.getTeam();
                                    JerotesZombieEntity fingerOfDeathZombieEntity = mob.convertTo(JerotesEntityType.JEROTES_ZOMBIE.get(), true);
                                    if (fingerOfDeathZombieEntity != null) {
                                        if (teams != null) {
                                            serverLevel.getScoreboard().addPlayerToTeam(fingerOfDeathZombieEntity.getStringUUID(), teams);
                                        }
                                        if (!this.isSilent()) {
                                            serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                                        }
                                        ForgeEventFactory.onLivingConvert(livingEntity, fingerOfDeathZombieEntity);
                                        fingerOfDeathZombieEntity.setBaby(livingEntity.isBaby());
                                        fingerOfDeathZombieEntity.tame(living);
                                        fingerOfDeathZombieEntity.setOrderedToSit(false);
                                        fingerOfDeathZombieEntity.setWander(false);
                                        if (living instanceof Mob mobs && mobs.getTarget() != null) {
                                            fingerOfDeathZombieEntity.setTarget(mobs.getTarget());
                                        }
                                        //属性
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.MAX_HEALTH) != null &&
                                                mob.getAttribute(Attributes.MAX_HEALTH) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MAX_HEALTH)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.MAX_HEALTH));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null &&
                                                mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_DAMAGE)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_KNOCKBACK) != null &&
                                                mob.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ATTACK_KNOCKBACK)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null &&
                                                mob.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR_TOUGHNESS)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS));
                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR) != null &&
                                                mob.getAttribute(Attributes.ARMOR) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.ARMOR)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.ARMOR));
                                        }
//                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null &&
//                                                mob.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
//                                            if (livingEntity.getType() == EntityType.VILLAGER || livingEntity.getType() == EntityType.WANDERING_TRADER) {
//                                                Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED)).
//                                                        setBaseValue(mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 0.5f);
//                                            }
//                                            else {
//                                                Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.MOVEMENT_SPEED)).
//                                                        setBaseValue(mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 0.75f);
//                                            }
//                                        }
                                        if (fingerOfDeathZombieEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null &&
                                                mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                                            Objects.requireNonNull(fingerOfDeathZombieEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).
                                                    setBaseValue(mob.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE));
                                        }
                                        fingerOfDeathZombieEntity.setHealth(fingerOfDeathZombieEntity.getMaxHealth());
                                    }
                                }
                            }
                        }
                    }
                }

            this.playSound(JerotesSoundEvents.SPELL, 3.0f, 1.0f);
            this.setUseful(false);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }
    protected void afterHasLineOfSight() {
        super.afterHasLineOfSight();
        if (!this.isUseful())
            return;
        if (!this.level().isClientSide) {
            this.setUseful(false);
        }
    }

    public BaseRayEntity getRay() {
        return new FingerOfDeathEntity(this.spellLevelDamage, spellSubcategory,this.level(), (LivingEntity) this.getOwner(), summonTod, summonTod2, summonTod3);
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.FINGER_OF_DEATH.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        if (this.isUseful()) {
            return itemStack.isEmpty() ? new ItemStack(JerotesItems.FINGER_OF_DEATH.get()) : itemStack;
        }
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }

    public float beamScale() {
        return 1.2f;
    }
    public int beamLightI() {
        return 0x30153c;
    }
    public int beamLightII() {
        return 0x6a3279;
    }
}
