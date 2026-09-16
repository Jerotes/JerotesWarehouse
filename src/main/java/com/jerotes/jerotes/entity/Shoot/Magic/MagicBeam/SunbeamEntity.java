package com.jerotes.jerotes.entity.Shoot.Magic.MagicBeam;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.block.JerotesLightBlock;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class SunbeamEntity extends BaseMagicBeamEntity implements ConcentrationSpellEntity {

    private static final EntityDataAccessor<Integer> SCALE_TICK = SynchedEntityData.defineId(SunbeamEntity.class, EntityDataSerializers.INT);

    public SunbeamEntity(EntityType<? extends SunbeamEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SunbeamEntity(EntityType<? extends SunbeamEntity> entityType, Level level, int spellLevelDamage, int spellLevelSubcategory) {
        super(entityType, level, spellLevelDamage, spellLevelSubcategory);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SCALE_TICK, 10);
    }

    public int getScaleTick() {
        return this.getEntityData().get(SCALE_TICK);
    }

    public void setScaleTick(int tick) {
        this.getEntityData().set(SCALE_TICK, tick);
    }

    @Override
    public float getBeamScale() {
        return 2.5f * this.getScaleTick() / 10f * getBeamScaleAdd();
    }

    @Override
    public double getMaxDistance() {
        return 18;
    }

    @Override
    public void customHurtMagic(Entity entity) {
        if (spellLevelSubcategory == 1) {
            if (entity instanceof LivingEntity livingEntity) {
                boolean bl = EntityAndItemFind.savingSpellThrow(livingEntity);
                boolean bl2 = entity.hurt(AttackFind.findDamageType(this, JerotesDamageTypes.RADIANT, this, this.getOwner()),
                        Main.rollDice(spellLevelDamage, 8, this.random) / (bl ? 2 : 1)
                );
                if (!bl && bl2) {
                    if (!entity.level().isClientSide) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0, false, false));
                    }
                }
            }
        } else {
            if (entity instanceof LivingEntity livingEntity) {
                boolean bl = EntityAndItemFind.savingSpellThrow(livingEntity);
                boolean bl2 = entity.hurt(AttackFind.findDamageType(this, JerotesDamageTypes.RADIANT, this, this.getOwner()),
                        Main.rollDice(spellLevelDamage, 8, this.random) / 24 / (bl ? 2 : 1) * (float) (MainConfig.BeamDamageMultipler)
                );
                if (!bl && bl2) {
                    if (!entity.level().isClientSide) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                    }
                }
            }
        }
        if (!this.level().isClientSide()) {
            setScaleTick(10);
        }
    }

    @Override
    public int attackCooldown() {
        if (spellLevelSubcategory == 1) {
            return 120;
        }
        return 5;
    }

    @Override
    public int getMaxTick() {
        return 60 * 20;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ScaleTick", getScaleTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setScaleTick(compoundTag.getInt("ScaleTick"));
    }

    @Override
    public ResourceLocation getBeamLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/sunbeam.png");
    }

    @Override
    public void tick() {
        super.tick();

        if (spellLevelSubcategory == 1) {
            if (!this.level().isClientSide()) {
                if (this.tickCount % 120 < 10 && this.tickCount % 120 >= 5)
                    setScaleTick(Math.min(10, getScaleTick() + 2));
                else
                    setScaleTick(Math.max(0, getScaleTick() - 1));
            }
        }

        if (this.getOwner() != null) {
            LivingEntity owner = getOwner();
            List<Entity> list = this.level().getEntitiesOfClass(Entity.class,
                    this.getOwner().getBoundingBox().deflate(Main.mobWidth(this) / 2, Main.mobHeight(this) / 2, Main.mobWidth(this) / 2)
                            .inflate(18, 18, 18));
            for (Entity entity : list) {
                if (entity == null) continue;
                if ((owner.distanceToSqr(entity)) > 18) continue;
                if (entity instanceof JerotesEntity jerotes && jerotes.hasSunlightFear()) {
                    if (this.getOwner() != null && AttackFind.FindCanNotAttack(this.getOwner(), entity)) continue;
                }
                if (Main.hasLineOfSightEntity(entity, owner)) {
                    Main.getJerotesPersistentData(entity).putDouble("jerotes_sun_burn_tick",
                            Math.max(2, Main.getJerotesPersistentData(entity).getDouble("jerotes_sun_burn_tick")));
                }
            }
            BlockPos pos = owner.getOnPos().above().above();
            switch (this.tickCount % 5) {
                case 1 -> pos = pos.east();
                case 2 -> pos = pos.south();
                case 3 -> pos = pos.west();
                case 4 -> pos = pos.north();
            }
            Vec3 targetPos = Vec3.atCenterOf(pos);
            if (Main.hasLineOfSightPos(targetPos, owner)) {
                JerotesLightBlock.placeLight(this.level(), pos, 15, 20);
            }
        }
    }

    @Override
    public boolean endConcentration() {
        if (getOwner() != null) {
            SpellType.setConcentrationSpellTypeJerotes(getOwner(), "");
            if (getOwner() instanceof Player player) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setConcentrationSpellTarget("");
                    capability.syncPlayerVariables(player);
                });
            }
            SpellType.setConcentrationSpellTargetJerotes(getOwner(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_SUNBEAM.magicSpellGet(1,null,null).getSpellName());
        }
        this.discard();
        return true;
    }
}