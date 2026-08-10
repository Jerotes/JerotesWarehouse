package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.spell.MagicSpell;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.spell.SpellTypeInterface;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class MagicSpellMixin extends Entity implements MagicSpellLivingEntity {
    public MagicSpellMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static final EntityDataAccessor<Integer> CASTING_TICK_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> CASTING_MAX_TICK_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> CAST_COOLDOWN_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<String> LAST_SPELL_TYPE_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<Integer> LAST_SPELL_LEVEL_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Optional<UUID>> LAST_SPELL_TARGET_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.OPTIONAL_UUID);

    @Unique
    private static final EntityDataAccessor<String> CONCENTRATION_SPELL_TYPE_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<Integer> CONCENTRATION_SPELL_LEVEL_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Optional<UUID>> CONCENTRATION_SPELL_TARGET_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.OPTIONAL_UUID);
    @Unique
    private static final EntityDataAccessor<Integer> CONCENTRATION_SPELL_CLEAN_TICK_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> CONCENTRATION_SPELL_MOB_TICK_JEROTES = SynchedEntityData.defineId(MagicSpellMixin.class, EntityDataSerializers.INT);


    @Unique public int getCastingTickJerotes() {
        return this.getEntityData().get(CASTING_TICK_JEROTES);
    }
    @Unique public void setCastingTickJerotes(int tick) {
        this.getEntityData().set(CASTING_TICK_JEROTES, tick);
    }
    @Unique public int getCastingMaxTickJerotes() {
        return this.getEntityData().get(CASTING_MAX_TICK_JEROTES);
    }
    @Unique public void setCastingMaxTickJerotes(int tick) {
        this.getEntityData().set(CASTING_MAX_TICK_JEROTES, tick);
    }
    @Unique public int getCastCooldownJerotes() {
        return this.getEntityData().get(CAST_COOLDOWN_JEROTES);
    }
    @Unique public void setCastCooldownJerotes(int tick) {
        this.getEntityData().set(CAST_COOLDOWN_JEROTES, tick);
    }
    @Unique public String getLastSpellTypeJerotes() {
        return this.getEntityData().get(LAST_SPELL_TYPE_JEROTES);
    }
    @Unique public void setLastSpellTypeJerotes(String string) {
        this.getEntityData().set(LAST_SPELL_TYPE_JEROTES, string);
    }
    @Unique public int getLastSpellLevelJerotes() {
        return this.getEntityData().get(LAST_SPELL_LEVEL_JEROTES);
    }
    @Unique public void setLastSpellLevelJerotes(int tick) {
        this.getEntityData().set(LAST_SPELL_LEVEL_JEROTES, tick);
    }
    public UUID getLastSpellTargetJerotes() {
        Optional<UUID> optionalUUID = this.getEntityData().get(LAST_SPELL_TARGET_JEROTES);
        return optionalUUID.orElseGet(this::getUUID);
    }
    public void setLastSpellTargetJerotes(UUID player) {
        this.getEntityData().set(LAST_SPELL_TARGET_JEROTES, Optional.of(player));
    }
    @Unique public String getConcentrationSpellTypeJerotes() {
        return this.getEntityData().get(CONCENTRATION_SPELL_TYPE_JEROTES);
    }
    @Unique public void setConcentrationSpellTypeJerotes(String string) {
        this.getEntityData().set(CONCENTRATION_SPELL_TYPE_JEROTES, string);
    }
    @Unique public int getConcentrationSpellLevelJerotes() {
        return this.getEntityData().get(CONCENTRATION_SPELL_LEVEL_JEROTES);
    }
    @Unique public void setConcentrationSpellLevelJerotes(int tick) {
        this.getEntityData().set(CONCENTRATION_SPELL_LEVEL_JEROTES, tick);
    }
    @Unique public UUID getConcentrationSpellTargetJerotes() {
        Optional<UUID> optionalUUID = this.getEntityData().get(CONCENTRATION_SPELL_TARGET_JEROTES);
        return optionalUUID.orElseGet(this::getUUID);
    }
    @Unique public void setConcentrationSpellTargetJerotes(UUID player) {
        this.getEntityData().set(CONCENTRATION_SPELL_TARGET_JEROTES, Optional.of(player));
    }
    @Unique public int getConcentrationSpellCleanTickJerotes() {
        return this.getEntityData().get(CONCENTRATION_SPELL_CLEAN_TICK_JEROTES);
    }
    @Unique public void setConcentrationSpellCleanTickJerotes(int tick) {
        this.getEntityData().set(CONCENTRATION_SPELL_CLEAN_TICK_JEROTES, tick);
    }
    @Unique public int getConcentrationSpellMobTickJerotes() {
        return this.getEntityData().get(CONCENTRATION_SPELL_MOB_TICK_JEROTES);
    }
    @Unique public void setConcentrationSpellMobTickJerotes(int tick) {
        this.getEntityData().set(CONCENTRATION_SPELL_MOB_TICK_JEROTES, tick);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putInt("CastingTickJerotes", this.getCastingTickJerotes());
        compoundTag.putInt("CastingMaxTickJerotes", this.getCastingMaxTickJerotes());
        compoundTag.putInt("CastCooldownJerotes", this.getCastCooldownJerotes());
        compoundTag.putString("LastSpellTypeJerotes", this.getLastSpellTypeJerotes());
        compoundTag.putInt("LastSpellLevelJerotes", this.getLastSpellLevelJerotes());
        compoundTag.putUUID("LastSpellTargetJerotes", this.getLastSpellTargetJerotes());
        compoundTag.putString("ConcentrationSpellTypeJerotes", this.getConcentrationSpellTypeJerotes());
        compoundTag.putInt("ConcentrationSpellLevelJerotes", this.getConcentrationSpellLevelJerotes());
        compoundTag.putUUID("ConcentrationSpellTargetJerotes", this.getConcentrationSpellTargetJerotes());
        compoundTag.putInt("ConcentrationSpellCleanTickJerotes", this.getConcentrationSpellCleanTickJerotes());
        compoundTag.putInt("ConcentrationSpellMobTickJerotes", this.getConcentrationSpellMobTickJerotes());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setCastingTickJerotes(compoundTag.getInt("CastingTickJerotes"));
        this.setCastingMaxTickJerotes(compoundTag.getInt("CastingMaxTickJerotes"));
        this.setCastCooldownJerotes(compoundTag.getInt("CastCooldownJerotes"));
        this.setLastSpellTypeJerotes(compoundTag.getString("LastSpellTypeJerotes"));
        this.setLastSpellLevelJerotes(compoundTag.getInt("LastSpellLevelJerotes"));
        if (compoundTag.hasUUID("LastSpellTargetJerotes")) {
            this.setLastSpellTargetJerotes(compoundTag.getUUID("LastSpellTargetJerotes"));
        }
        this.setConcentrationSpellTypeJerotes(compoundTag.getString("ConcentrationSpellTypeJerotes"));
        this.setConcentrationSpellLevelJerotes(compoundTag.getInt("ConcentrationSpellLevelJerotes"));
        if (compoundTag.hasUUID("ConcentrationSpellTargetJerotes")) {
            this.setConcentrationSpellTargetJerotes(compoundTag.getUUID("ConcentrationSpellTargetJerotes"));
        }
        this.setConcentrationSpellCleanTickJerotes(compoundTag.getInt("ConcentrationSpellCleanTickJerotes"));
        this.setConcentrationSpellMobTickJerotes(compoundTag.getInt("ConcentrationSpellMobTickJerotes"));
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(CASTING_TICK_JEROTES, -1);
        this.getEntityData().define(CASTING_MAX_TICK_JEROTES, 0);
        this.getEntityData().define(CAST_COOLDOWN_JEROTES, 0);
        this.getEntityData().define(LAST_SPELL_TYPE_JEROTES, "jerotes_empty");
        this.getEntityData().define(LAST_SPELL_LEVEL_JEROTES, 0);
        this.getEntityData().define(LAST_SPELL_TARGET_JEROTES, Optional.of(this.getUUID()));
        this.getEntityData().define(CONCENTRATION_SPELL_TYPE_JEROTES, "");
        this.getEntityData().define(CONCENTRATION_SPELL_LEVEL_JEROTES, 0);
        this.getEntityData().define(CONCENTRATION_SPELL_TARGET_JEROTES, Optional.of(this.getUUID()));
        this.getEntityData().define(CONCENTRATION_SPELL_CLEAN_TICK_JEROTES, 0);
        this.getEntityData().define(CONCENTRATION_SPELL_MOB_TICK_JEROTES, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    protected void tick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) ((Object) this);
        if (this.getCastingTickJerotes() > -1) {
            if (!this.getLastSpellTypeJerotes().isEmpty()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(this.getLastSpellTypeJerotes());
                    MagicSpell magicSpell = spellTypeInterface.magicSpellGet(this.getLastSpellLevelJerotes(), livingEntity, serverLevel.getEntity(getLastSpellTargetJerotes()));
                    if (magicSpell.getCastingTicks() > 0) {
                        magicSpell.CastingAbout(this.getCastingTickJerotes());
                    }
                }
            }
            if (this.getCastingTickJerotes() == 0) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(this.getLastSpellTypeJerotes());
                    MagicSpell magicSpell = spellTypeInterface.magicSpellGet(this.getLastSpellLevelJerotes(), livingEntity, serverLevel.getEntity(getLastSpellTargetJerotes()));
                    magicSpell.spellCastingStop(0);
                }
            }
        }
        if (!this.level().isClientSide()) {
            this.setCastingTickJerotes(Math.max(-1, this.getCastingTickJerotes() - 1));
            this.setCastCooldownJerotes(Math.max(0, this.getCastCooldownJerotes() - 1));
            this.setConcentrationSpellCleanTickJerotes(Math.max(0, this.getConcentrationSpellCleanTickJerotes() - 1));
            this.setConcentrationSpellMobTickJerotes(Math.max(0, this.getConcentrationSpellMobTickJerotes() - 1));
        }
        //无专注清除专注
        if (this.getConcentrationSpellCleanTickJerotes() == 5) {
            boolean bl = false;
            if (this.level() instanceof ServerLevel serverLevel) {
                if (serverLevel.getEntity(this.getConcentrationSpellTargetJerotes()) instanceof ConcentrationSpellEntity) {
                    bl = true;
                }
                if (!bl) {
                    if (!this.getConcentrationSpellTypeJerotes().isEmpty()) {
                        SpellType.endConcentration(livingEntity);
                    }
                }
            }
        }
        //非玩家取消专注
        if (livingEntity instanceof Mob mob) {
            if (!this.getConcentrationSpellTypeJerotes().isEmpty() && (mob.getTarget() != null || mob.isAggressive())) {
                if (!this.level().isClientSide()) {
                    this.setConcentrationSpellMobTickJerotes(Math.max(120, this.getConcentrationSpellMobTickJerotes()));
                }
            }
            if (this.getConcentrationSpellMobTickJerotes() == 5) {
                SpellType.endConcentration(mob);
            }
            if (!SpellType.getConcentrationSpellTypeJerotes(mob).isEmpty()) {
                if (mob.level() instanceof ServerLevel serverLevel) {
                    if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(mob)) instanceof ConcentrationSpellEntity concentrationSpellEntity) {
                        if (((Entity)concentrationSpellEntity).distanceTo(mob) > 256)
                            return;
                    }
                }
            }
        }
    }
}