package com.jerotes.jerotes.entity.Other.SpellCloud;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbstractHurtingProjectile;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.EntityHitResult;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class SpellCloudEntity extends AreaEffectCloud implements MagicAbout {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TIME_BETWEEN_APPLICATIONS = 5;
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Float> PARTICLE_SCALE_MULTIPLE = SynchedEntityData.defineId(SpellCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final float MAX_RADIUS = 32.0F;
    private static final float MINIMAL_RADIUS = 0.5F;
    private static final float DEFAULT_RADIUS = 3.0F;
    public static final float DEFAULT_WIDTH = 6.0F;
    public static final float HEIGHT = 0.5F;
    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> victims = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean fixedColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public SpellCloudEntity(EntityType<? extends AreaEffectCloud> p_19704_, Level p_19705_) {
        super(p_19704_, p_19705_);
        this.noPhysics = true;
    }

    public SpellCloudEntity(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
        this(JerotesEntityType.SPELL_CLOUD.get(), p_19707_);
        this.setPos(p_19708_, p_19709_, p_19710_);
    }



    protected void defineSynchedData() {
        this.getEntityData().define(DATA_COLOR, 0);
        this.getEntityData().define(DATA_RADIUS, 3.0F);
        this.getEntityData().define(DATA_WAITING, false);
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
        this.getEntityData().define(PARTICLE_SCALE_MULTIPLE, 1f);
    }

    public void refreshDimensions() {
        super.refreshDimensions();
    }

    public int spellLevelDamage = 1;
    public float getParticleScaleMultiple() {
        return this.getEntityData().get(PARTICLE_SCALE_MULTIPLE);
    }
    public void setParticleScaleMultiple(float f) {
        this.getEntityData().set(PARTICLE_SCALE_MULTIPLE, f);
    }
    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }
    public void setRadius(float f) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(f, 0.0F, 32.0F));
        }
    }
    public Potion getPotion() {
        return this.potion;
    }
    public void setPotion(Potion p_19723_) {
        this.potion = p_19723_;
        if (!this.fixedColor) {
            this.updateColor();
        }
    }
    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }
    }
    public void addEffect(MobEffectInstance p_19717_) {
        this.effects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }
    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }
    public void setFixedColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }
    public ParticleOptions getParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }
    public void setParticle(ParticleOptions p_19725_) {
        this.getEntityData().set(DATA_PARTICLE, p_19725_);
    }
    protected void setWaiting(boolean p_19731_) {
        this.getEntityData().set(DATA_WAITING, p_19731_);
    }
    public boolean isWaiting() {
        return this.getEntityData().get(DATA_WAITING);
    }
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int p_19735_) {
        this.duration = p_19735_;
    }

    public void tick() {
        super.tick();
        boolean flag = this.isWaiting();
        float f = this.getRadius();
        if (this.level().isClientSide) {
            if (flag && this.random.nextBoolean()) {
                return;
            }
            ParticleOptions particleoptions = this.getParticle();
            int i;
            float f1;
            if (flag) {
                i = 2;
                f1 = 0.2F;
            } else {
                i = Mth.ceil((float)Math.PI * f * f);
                f1 = f;
            }
            for(int j = 0; j < i * this.getParticleScaleMultiple(); ++j) {
                float f2 = this.random.nextFloat() * ((float)Math.PI * 2F);
                float f3 = Mth.sqrt(this.random.nextFloat()) * f1;
                double d0 = this.getX() + (double)(Mth.cos(f2) * f3);
                double d2 = this.getY();
                double d4 = this.getZ() + (double)(Mth.sin(f2) * f3);
                double d5;
                double d6;
                double d7;
                if (particleoptions.getType() == ParticleTypes.ENTITY_EFFECT) {
                    int k = flag && this.random.nextBoolean() ? 16777215 : this.getColor();
                    d5 = (double)((float)(k >> 16 & 255) / 255.0F);
                    d6 = (double)((float)(k >> 8 & 255) / 255.0F);
                    d7 = (double)((float)(k & 255) / 255.0F);
                } else if (flag) {
                    d5 = 0.0D;
                    d6 = 0.0D;
                    d7 = 0.0D;
                } else {
                    d5 = (0.5D - this.random.nextDouble()) * 0.15D;
                    d6 = (double)0.01F;
                    d7 = (0.5D - this.random.nextDouble()) * 0.15D;
                }
                this.level().addAlwaysVisibleParticle(particleoptions, d0, d2, d4, d5, d6, d7);
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.discard();
                return;
            }

            boolean flag1 = this.tickCount < this.waitTime;
            if (flag != flag1) {
                this.setWaiting(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                f += this.radiusPerTick;
                if (f < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius(f);
            }

            if (this.tickCount % 5 == 0) {
                this.victims.entrySet().removeIf((p_287380_) -> {
                    return this.tickCount >= p_287380_.getValue();
                });
                List<MobEffectInstance> list = Lists.newArrayList();

                for(MobEffectInstance mobEffectInstance : this.potion.getEffects()) {
                    list.add(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.mapDuration((p_267926_) -> {
                        return p_267926_ / 4;
                    }), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible()));
                }

                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.victims.clear();
                } else {
                    List<LivingEntity> list1 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                    if (!list1.isEmpty()) {
                        for(LivingEntity livingentity : list1) {
                            applyToAbout(livingentity);
                            if (!this.victims.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
                                double d8 = livingentity.getX() - this.getX();
                                double d1 = livingentity.getZ() - this.getZ();
                                double d3 = d8 * d8 + d1 * d1;
                                if (d3 <= (double)(f * f)) {
                                    this.victims.put(livingentity, this.tickCount + this.reapplicationDelay);
                                    for(MobEffectInstance mobEffectInstance : list) {
                                        addEffectAbout(livingentity, mobEffectInstance);
                                    }
                                    if (this.radiusOnUse != 0.0F) {
                                        f += this.radiusOnUse;
                                        if (f < 0.5F) {
                                            this.discard();
                                            return;
                                        }

                                        this.setRadius(f);
                                    }

                                    if (this.durationOnUse != 0) {
                                        this.duration += this.durationOnUse;
                                        if (this.duration <= 0) {
                                            this.discard();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void addEffectAbout(LivingEntity livingEntity, MobEffectInstance mobEffectInstance) {
        if (this.getOwner() != null) {
            boolean isAlly = AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity, false) || AttackFind.FindCanNotAttack(this.getOwner(), livingEntity);
            if (isAlly) {
                if (mobEffectInstance.getEffect().getCategory() == MobEffectCategory.BENEFICIAL && !beneficialEffectCanApplyToAlly()) {
                    return;
                }
                else if (mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL && !harmfulEffectCanApplyToAlly()) {
                    return;
                }
                else if (!neutralEffectCanApplyToAlly()) {
                    return;
                }
            }
            else {
                if (mobEffectInstance.getEffect().getCategory() == MobEffectCategory.BENEFICIAL && !beneficialEffectCanApplyToOther()) {
                    return;
                }
                else if (mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL && !harmfulEffectCanApplyToOther()) {
                    return;
                }
                else if (!neutralEffectCanApplyToOther()) {
                    return;
                }
            }
        }
        if (mobEffectInstance.getEffect().isInstantenous()) {
            if (mobEffectInstance.getEffect().isInstantenous())
                mobEffectInstance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingEntity, mobEffectInstance.getAmplifier(), 0.5D);
        } else {
            livingEntity.addEffect(new MobEffectInstance(mobEffectInstance), this);
        }
    }
    public void applyToAbout(LivingEntity livingEntity) {
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }
    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }
    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }
    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }
    public int getDurationOnUse() {
        return this.durationOnUse;
    }
    public void setDurationOnUse(int n) {
        this.durationOnUse = n;
    }
    public int getWaitTime() {
        return this.waitTime;
    }
    public void setWaitTime(int n) {
        this.waitTime = n;
    }
    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }
        return this.owner;
    }
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("Age", this.tickCount);
        compoundTag.putInt("Duration", this.duration);
        compoundTag.putInt("WaitTime", this.waitTime);
        compoundTag.putInt("ReapplicationDelay", this.reapplicationDelay);
        compoundTag.putInt("DurationOnUse", this.durationOnUse);
        compoundTag.putFloat("RadiusOnUse", this.radiusOnUse);
        compoundTag.putFloat("RadiusPerTick", this.radiusPerTick);
        compoundTag.putFloat("Radius", this.getRadius());
        compoundTag.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        if (this.fixedColor) {
            compoundTag.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY) {
            compoundTag.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();
            for(MobEffectInstance mobEffectInstance : this.effects) {
                listtag.add(mobEffectInstance.save(new CompoundTag()));
            }
            compoundTag.put("Effects", listtag);
        }
    }
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.tickCount = compoundTag.getInt("Age");
        this.duration = compoundTag.getInt("Duration");
        this.waitTime = compoundTag.getInt("WaitTime");
        this.reapplicationDelay = compoundTag.getInt("ReapplicationDelay");
        this.durationOnUse = compoundTag.getInt("DurationOnUse");
        this.radiusOnUse = compoundTag.getFloat("RadiusOnUse");
        this.radiusPerTick = compoundTag.getFloat("RadiusPerTick");
        this.setRadius(compoundTag.getFloat("Radius"));
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader(compoundTag.getString("Particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup()));
            } catch (CommandSyntaxException commandsyntaxexception) {
                LOGGER.warn("Couldn't load custom particle {}", compoundTag.getString("Particle"), commandsyntaxexception);
            }
        }
        if (compoundTag.contains("Color", 99)) {
            this.setFixedColor(compoundTag.getInt("Color"));
        }
        if (compoundTag.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion(compoundTag));
        }
        if (compoundTag.contains("Effects", 9)) {
            ListTag listtag = compoundTag.getList("Effects", 10);
            this.effects.clear();
            for(int i = 0; i < listtag.size(); ++i) {
                MobEffectInstance mobEffectInstance = MobEffectInstance.load(listtag.getCompound(i));
                if (mobEffectInstance != null) {
                    this.addEffect(mobEffectInstance);
                }
            }
        }
    }
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_RADIUS.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor );
    }


    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    public boolean beneficialEffectCanApplyToAlly() {
        return true;
    }
    public boolean beneficialEffectCanApplyToOther() {
        return false;
    }
    public boolean harmfulEffectCanApplyToAlly() {
        return false;
    }
    public boolean harmfulEffectCanApplyToOther() {
        return true;
    }
    public boolean neutralEffectCanApplyToAlly() {
        return true;
    }
    public boolean neutralEffectCanApplyToOther() {
        return true;
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

