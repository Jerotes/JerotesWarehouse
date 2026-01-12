package com.jerotes.jerotes.entity.magic;

import com.google.common.collect.Lists;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class MagicAboutThrowEntity extends ThrowableProjectile implements ItemSupplier, MagicAbout {
    private static final EntityDataAccessor<Optional<UUID>> HURT_ID = SynchedEntityData.defineId(MagicAboutThrowEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(MagicAboutThrowEntity.class, EntityDataSerializers.ITEM_STACK);

    public MagicAboutThrowEntity(EntityType<? extends MagicAboutThrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MagicAboutThrowEntity(EntityType<? extends MagicAboutThrowEntity> entityType, double d, double d2, double d3, Level level) {
        super(entityType, d, d2, d3, level);
    }

    public MagicAboutThrowEntity(EntityType<? extends MagicAboutThrowEntity> entityType, LivingEntity livingEntity, Level level) {
        super(entityType, livingEntity, level);
    }

    public void setItem(ItemStack itemStack) {
        if (!itemStack.is(this.getDefaultItem()) || itemStack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, itemStack.copyWithCount(1));
        }
    }

    protected abstract Item getDefaultItem();

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemStack;
    }

    protected boolean canHitEntity(Entity p_36743_) {
        if (this.getOwner() != null && p_36743_ instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity)) {
            return false;
        }
        return super.canHitEntity(p_36743_);
    }

    public void lookAt(Entity entity, float f, float f2) {
        double d0 = entity.getX() - this.getX();
        double d2 = entity.getZ() - this.getZ();
        double d1;
        if (entity instanceof LivingEntity livingentity) {
            d1 = livingentity.getEyeY() - this.getEyeY();
        } else {
            d1 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - this.getEyeY();
        }

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f3 = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
        float f4 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
        this.setXRot(this.rotlerp(this.getXRot(), f4, f2));
        this.setYRot(this.rotlerp(this.getYRot(), f3, f));
    }

    private float rotlerp(float f, float f2, float f3) {
        float f4 = Mth.wrapDegrees(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }
        if (f4 < -f3) {
            f4 = -f3;
        }
        return f + f4;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() == this.getLastHurt() && !this.canNotHurtLastHurt()) {
            return;
        }
        if (isHurt(entityHitResult.getEntity().getUUID()) && this.useUUIDFindHurtAndDisable()) {
            return;
        }
        if (entityHitResult.getEntity() == this.getOwner()) {
            return;
        }
        //法术反制
        if (!isHelp() && entityHitResult.getEntity() != this.getOwner() && entityHitResult.getEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
             if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
            return;
        }
        this.setLastHurt(entityHitResult.getEntity());
        this.addHurtUUID(entityHitResult.getEntity().getUUID());
        super.onHitEntity(entityHitResult);
    }
    protected void customHurt(Entity entity) {
        if (entity == this.getLastHurt() && !this.canNotHurtLastHurt()) {
            return;
        }
        if (isHurt(entity.getUUID()) && this.useUUIDFindHurtAndDisable()) {
            return;
        }
        if (entity == this.getOwner()) {
            return;
        }
        //法术反制
        if (!isHelp() && entity != this.getOwner() && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
            if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
            return;
        }
        this.setLastHurt(entity);
        this.addHurtUUID(entity.getUUID());
    }

    //LastHurt
    @Nullable
    private Entity lastHurt;
    @Nullable
    private UUID lastHurtUUID;
    @Nullable
    public Entity getLastHurt() {
        Entity entity;
        if (this.lastHurt == null && this.lastHurtUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.lastHurtUUID)) instanceof Entity) {
            this.lastHurt = (Entity)entity;
        }
        return this.lastHurt;
    }
    public void setLastHurt(@Nullable Entity lastHurt) {
        this.lastHurt = lastHurt;
        this.lastHurtUUID = lastHurt == null ? null : lastHurt.getUUID();
    }
    public List<UUID> getHurtUUIDs() {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(this.entityData.get(HURT_ID).orElse(null));
        return arrayList;
    }
    public void addHurtUUID(@Nullable UUID uUID) {
        this.entityData.set(HURT_ID, Optional.ofNullable(uUID));
    }
    @Override
    public int getSpellLevel() {
        return spellLevelDamage;
    }
    public int life = 0;
    public int lifeOfLastHurt = 0;
    //伤害
    public int spellLevelDamage = 1;
    //最大伤害
    public int spellLevelMaxDamage = 1;
    //燃烧时间
    public int spellLevelFireTime = 1;
    //冰冻时间
    public int spellLevelFreezeTime = 1;
    //爆炸强度
    public float spellLevelExplode = 1;
    //主要效果时间
    public int spellLevelMainEffectTime = 1;
    //主要效果等级
    public int spellLevelMainEffectLevel = 1;
    //次要效果时间
    public int spellLevelOtherEffectTime = 1;
    //次要效果等级
    public int spellLevelOtherEffectLevel = 1;
    //XZ推动
    public float spellLevelXZPush = 1;
    //XZ推动基础
    public float spellLevelXZPushBase = 1;
    //Y推动
    public float spellLevelYPush = 1;
    //Y推动基础
    public float spellLevelYPushBase = 1;
    //高度
    public int spellLevelHeight = 1;
    //移动距离
    public int spellLevelMoveDistance = 1;
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ItemStack itemStack = this.getItemRaw();
        if (!itemStack.isEmpty()) {
            compoundTag.put("Item", itemStack.save(new CompoundTag()));
        }
        if (this.lastHurtUUID != null) {
            compoundTag.putUUID("LastHurt", this.lastHurtUUID);
        }
        List<UUID> list = this.getHurtUUIDs();
        ListTag listTag = new ListTag();
        for (UUID uUID : list) {
            if (uUID == null) continue;
            listTag.add(NbtUtils.createUUID(uUID));
        }
        compoundTag.put("Hurt", listTag);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("LifeOfLastHurt", this.lifeOfLastHurt);
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("SpellLevelMaxDamage", this.spellLevelMaxDamage);
        compoundTag.putInt("SpellLevelFireTime", this.spellLevelFireTime);
        compoundTag.putInt("SpellLevelFreezeTime", this.spellLevelFreezeTime);
        compoundTag.putFloat("SpellLevelExplode", this.spellLevelExplode);
        compoundTag.putInt("SpellLevelMainEffectTime", this.spellLevelMainEffectTime);
        compoundTag.putInt("SpellLevelMainEffectLevel", this.spellLevelMainEffectLevel);
        compoundTag.putInt("SpellLevelOtherEffectTime", this.spellLevelOtherEffectTime);
        compoundTag.putInt("SpellLevelOtherEffectLevel", this.spellLevelOtherEffectLevel);
        compoundTag.putFloat("SpellLevelXZPush", this.spellLevelXZPush);
        compoundTag.putFloat("SpellLevelXZPushBase", this.spellLevelXZPushBase);
        compoundTag.putFloat("SpellLevelYPush", this.spellLevelYPush);
        compoundTag.putFloat("SpellLevelYPushBase", this.spellLevelYPushBase);
        compoundTag.putInt("SpellLevelHeight", this.spellLevelHeight);
        compoundTag.putInt("SpellLevelMoveDistance", this.spellLevelMoveDistance);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ItemStack itemStack = ItemStack.of(compoundTag.getCompound("Item"));
        this.setItem(itemStack);
        if (compoundTag.hasUUID("LastHurt")) {
            this.lastHurtUUID = compoundTag.getUUID("LastHurt");
        }
        ListTag listTag = compoundTag.getList("Hurt", 11);
        for (Tag tag : listTag) {
            this.addHurtUUID(NbtUtils.loadUUID(tag));
        }
        this.life = compoundTag.getInt("Life");
        this.lifeOfLastHurt = compoundTag.getInt("LifeOfLastHurt");
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.spellLevelMaxDamage = compoundTag.getInt("SpellLevelMaxDamage");
        this.spellLevelFireTime = compoundTag.getInt("SpellLevelFireTime");
        this.spellLevelFreezeTime = compoundTag.getInt("SpellLevelFreezeTime");
        this.spellLevelExplode = compoundTag.getFloat("SpellLevelExplode");
        this.spellLevelMainEffectTime = compoundTag.getInt("SpellLevelMainEffectTime");
        this.spellLevelMainEffectLevel = compoundTag.getInt("SpellLevelMainEffectLevel");
        this.spellLevelOtherEffectTime = compoundTag.getInt("SpellLevelOtherEffectTime");
        this.spellLevelOtherEffectLevel = compoundTag.getInt("SpellLevelOtherEffectLevel");
        this.spellLevelXZPush = compoundTag.getFloat("SpellLevelXZPush");
        this.spellLevelXZPushBase = compoundTag.getFloat("SpellLevelXZPushBase");
        this.spellLevelYPush = compoundTag.getFloat("SpellLevelYPush");
        this.spellLevelYPushBase = compoundTag.getFloat("SpellLevelYPushBase");
        this.spellLevelHeight = compoundTag.getInt("SpellLevelHeight");
        this.spellLevelMoveDistance = compoundTag.getInt("SpellLevelMoveDistance");
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
        this.getEntityData().define(HURT_ID, Optional.empty());
    }
    boolean isHurt(UUID uUID) {
        return this.getHurtUUIDs().contains(uUID);
    }

    @Override
    public void tick() {
        super.tick();
        //消失
        if (this.life >= this.getMaxLife()) {
            this.lastBreak();
            this.discard();
        }
        else {
            this.life += 1;
        }
        if (this.lifeOfLastHurt >= this.timeOfHurtLastHurt()) {
            this.setLastHurt(null);
        }
        else if (this.getLastHurt() != null) {
            this.lifeOfLastHurt += 1;
        }
    }
    public void lastBreak() {
    }

    public abstract int getMaxLife();

    public boolean canNotHurtLastHurt() {
        return true;
    }
    public boolean useUUIDFindHurtAndDisable() {
        return false;
    }
    public int timeOfHurtLastHurt() {
        return 10;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected float getGravity() {
        return 0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
}

