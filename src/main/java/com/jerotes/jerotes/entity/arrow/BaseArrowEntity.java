package com.jerotes.jerotes.entity.arrow;

import com.google.common.collect.Lists;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BaseArrowEntity extends BaseAbstractArrowEntity {
    private double baseDamage = 2.0;
    private int knockback;
    public SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    public float soundLevel = 1.0f;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;
    private ItemStack arrowItem = new ItemStack(Items.ARROW);

    public BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, ItemStack itemStack, double damages) {
        super(entityType, level, itemStack);
        this.arrowItem = itemStack.copy();
        this.baseDamage = damages;
    }

    public BaseArrowEntity(Level level, LivingEntity livingEntity, ItemStack itemStack, EntityType<? extends AbstractArrow> entityType, double damages) {
        super(entityType, livingEntity, level, itemStack);
        this.arrowItem = itemStack.copy();
        this.baseDamage = damages;
    }

    public BaseArrowEntity(Level level, double d, double d2, double d3, ItemStack itemStack, EntityType<? extends AbstractArrow> entityType, double damages) {
        super(entityType, d, d2, d3, level, itemStack);
        this.arrowItem = itemStack.copy();
        this.baseDamage = damages;
    }

    public byte getPierceLevelBase() {
        return super.getPierceLevel();
    }
    public byte getPierceLevel() {
        return (byte) (this.getPierceLevelBase() + this.basePierce());
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.arrowItem.copy();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() >= (int) this.getPierceLevel()) {
            return;
        }
        Entity entity;
        DamageSource damageSource;
        Entity entity2 = entityHitResult.getEntity();
        float special = this.specialDamage(entity2);
        float f = (float)this.getDeltaMovement().length();
        int n = Mth.ceil(Mth.clamp((double)f * this.baseDamage, 0.0,  (double)Integer.MAX_VALUE));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }
            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity((int)5);
            }
            if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                if (!this.canNotBreak()) {
                    this.discard();
                }
                return;
            }
            this.piercingIgnoreEntityIds.add(entity2.getId());
        }
        if (this.isCritArrow()) {
            long l = this.random.nextInt(n / 2 + 2);
            n = (int)Math.min(l + (long)n, Integer.MAX_VALUE);
        }
        damageSource = this.getDamageSource(entity2);
        if (!((entity = this.getOwner()) == null)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).setLastHurtMob(entity2);
            }
        }
        boolean bl = entity2 instanceof LivingEntity livingEntity && EntityFactionFind.isEnderman(livingEntity);
        int n2 = entity2.getRemainingFireTicks();
        boolean bl2 = entity2.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("deflects_arrows")));
        if (this.isOnFire() && !bl && !bl2) {
            entity2.setSecondsOnFire(5);
        }
        if (entity2.hurt(damageSource, n + special)) {
            if (bl && !this.canHurtEnderman()) {
                return;
            }
            //命中效果
            if (entity2 instanceof LivingEntity livingEntity) {
                if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
                    livingEntity.setArrowCount(livingEntity.getArrowCount() + 1);
                }
                this.hitLivingEntityUse(livingEntity);
                if (this.knockback > 0) {
                    double d = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale((double)this.knockback * 0.6 * d);
                    if (vec3.lengthSqr() > 0.0) {
                        livingEntity.push(vec3.x, 0.1, vec3.z);
                    }
                }
                if (!this.level().isClientSide && entity instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, entity);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity, livingEntity);
                }
                this.doPostHurtEffects(livingEntity);
                if (entity != null && livingEntity != entity && livingEntity instanceof Player && entity instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0f));
                }
                if (!entity2.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingEntity);
                }
                if (!this.level().isClientSide && entity instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)entity;
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverPlayer, this.piercedAndKilledEntities);
                    } else if (!entity2.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverPlayer, Arrays.asList(entity2));
                    }
                }
            }
            soundLevel = 1.0f;
            SoundEvent soundEvent = this.soundEvent;
            this.hitUse(entity2);
            this.playSound(soundEvent, soundLevel, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if ((this.getPierceLevel() <= 0 && !this.canNotBreak()) || this.mustBreak()) {
                this.discard();
            }
        }
        else if (bl2) {
            this.deflect();
        }
        else {
            entity2.setRemainingFireTicks(n2);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.setYRot(this.getYRot() + 180.0f);
            this.yRotO += 180.0f;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.discard();
            }
        }
        if (this.mustBreak()) {
            this.discard();
        }
    }
    public DamageSource getDamageSource(Entity entity) {
        if (this.getOwner() == null) {
            return this.damageSources().arrow(this, this);
        }
       return this.damageSources().arrow(this, this.getOwner());
    }
    public float specialDamage(Entity entity) {
        return 0f;
    }
    public int basePierce() {
        return 0;
    }
    public boolean canNotBreak() {
        return false;
    }
    public boolean mustBreak() {
        return false;
    }
    public boolean canHurtEnderman() {
        return false;
    }
    public void hitLivingEntityUse(LivingEntity entity) {
    }
    public void hitUse(Entity entity) {
    }

    protected boolean canHitEntity(Entity p_36743_) {
        if (this.getOwner() != null && p_36743_ instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity)) {
            return false;
        }
        if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() >= (int) this.getPierceLevel()) {
            return false;
        }
        return super.canHitEntity(p_36743_) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(p_36743_.getId()));
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }
        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.resetPiercedEntities();
    }

    @Override
    public void setKnockback(int n) {
        this.knockback = n;
    }
    @Override
    public int getKnockback() {
        return this.knockback;
    }
    @Override
    public void setBaseDamage(double d) {
        this.baseDamage = d;
    }
    @Override
    public double getBaseDamage() {
        return this.baseDamage;
    }

    public void deflect() {
        float f = this.random.nextFloat() * 360.0F;
        this.setDeltaMovement(this.getDeltaMovement().yRot(f * ((float)Math.PI / 180F)).scale(0.5D));
        this.setYRot(this.getYRot() + f);
        this.yRotO += f;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Arrow", 10)) {
            this.arrowItem = ItemStack.of(compoundTag.getCompound("Arrow"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("Arrow", this.arrowItem.save(new CompoundTag()));
    }

    @Override
    protected float getWaterInertia() {
        return 0.6f;
    }
}


