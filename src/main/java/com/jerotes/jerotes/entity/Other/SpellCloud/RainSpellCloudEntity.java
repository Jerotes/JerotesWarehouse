package com.jerotes.jerotes.entity.Other.SpellCloud;

import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class RainSpellCloudEntity extends SpellCloudEntity {
    private int spellUsefulHeight = 1;

    private static final EntityDataAccessor<ParticleOptions> DATA_RAIN_PARTICLE = SynchedEntityData.defineId(RainSpellCloudEntity.class, EntityDataSerializers.PARTICLE);

    public RainSpellCloudEntity(EntityType<? extends RainSpellCloudEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RainSpellCloudEntity(Level level, double d, double d2, double d3) {
        this(JerotesEntityType.RAIN_SPELL_CLOUD.get(), level);
        this.setPos(d, d2, d3);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_RAIN_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    public ParticleOptions getRainParticle() {
        return this.getEntityData().get(DATA_RAIN_PARTICLE);
    }

    public void setRainParticle(ParticleOptions p_19725_) {
        this.getEntityData().set(DATA_RAIN_PARTICLE, p_19725_);
    }

    public void setSpellUsefulHeight(int n) {
        this.spellUsefulHeight = n;
    }

    public int life = 0;
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
        compoundTag.putInt("Life", this.life);
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
        this.life = compoundTag.getInt("Life");
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
    public void tick() {
        super.tick();
        this.life += 1;
        if (!this.level().isClientSide){
            this.rainParticles(this.getRainParticle());

            if (this.level() instanceof ServerLevel serverLevel && this.getOwner() instanceof LivingEntity && life > 20) {
                List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0, spellUsefulHeight / 2f, 0).move(0, -spellUsefulHeight / 2f, 0));
                for (LivingEntity livingEntityEffect : list) {
                    if (livingEntityEffect == null) continue;
                    if ((this.distanceTo(livingEntityEffect)) > spellUsefulHeight * 8) continue;
                    if (livingEntityEffect == this.getOwner()) continue;
                    if (livingEntityEffect.isAlliedTo(this.getOwner())) continue;
                    int distance = (int) (this.getY() - livingEntityEffect.getY());
                    if (emptySpaceBetween(this.level(), livingEntityEffect.blockPosition(), distance, true)) {
                        this.addRainEffect(livingEntityEffect, this.getOwner());
                    }
                }
            }
        }
    }

    public void addRainEffect(LivingEntity livingEntity, LivingEntity livingEntity1) {
    }

    public void rainParticles(ParticleOptions particleRain){
        if (this.level() instanceof ServerLevel serverWorld){
            float f = getRadius();
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(particleRain, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
        }
    }

    public static boolean emptySpaceBetween(Level level, BlockPos blockPos, int distance, boolean up){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        boolean flag = false;
        if (up){
            while (blockpos$mutable.getY() < blockPos.getY() + distance && (level.getBlockState(blockpos$mutable).isAir() || canBeReplaced(level, blockpos$mutable))){
                blockpos$mutable.move(Direction.UP);
                flag = true;
            }
        } else {
            while (blockpos$mutable.getY() > blockPos.getY() - distance && level.getBlockState(blockpos$mutable).isAir() || canBeReplaced(level, blockpos$mutable)){
                blockpos$mutable.move(Direction.DOWN);
                flag = true;
            }
        }
        if (!level.getBlockState(blockpos$mutable).isAir()){
            flag = false;
        }
        return flag;
    }

    public static boolean canBeReplaced(Level pLevel, BlockPos pReplaceablePos){
        return canBeReplaced(pLevel, pReplaceablePos, pReplaceablePos);
    }

    public static boolean canBeReplaced(Level pLevel, BlockPos pReplaceablePos, BlockPos pReplacedBlockPos){
        return pLevel.getBlockState(pReplaceablePos).canBeReplaced(new DirectionalPlaceContext(pLevel, pReplacedBlockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
    }
}