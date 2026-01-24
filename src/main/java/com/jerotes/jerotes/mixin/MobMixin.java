package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.item.Tool.ItemToolBaseUmbrella;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements JerotesChangeMob {
    @Shadow public abstract void tick();

    @Shadow public abstract void setPersistenceRequired();

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void isSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
        if (isJerotesParched()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (this.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella ||
                this.getUseItem().getItem() instanceof ItemToolBaseUmbrella) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void doHurtTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        AttackFind.attackOther(this, entity);
    }

    @Inject(method = "isLeftHanded", at = @At("HEAD"), cancellable = true)
    private void isLeftHanded(CallbackInfoReturnable<Boolean> cir) {
        if (ModList.get().isLoaded("tacz")) {
            if (this.getMainHandItem().getItem().getDescriptionId().contains("modern_kinetic_gun") &&
                    (this instanceof UseBowEntity useBowEntity && !useBowEntity.justBow() || this.getType() == JerotesEntityType.TEST.get())) {
                cir.setReturnValue(false);
            }
        }
    }


    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    protected void canBeLeashed(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof JerotesChangeCamel jerotesChangeCamel && jerotesChangeCamel.isJerotesCamelHusk() && jerotesChangeCamel.isJerotesMobControlled())
            cir.setReturnValue(false);
    }
    @Inject(method = "interact", at = @At("HEAD"))
    public void interact(Player p_21420_, InteractionHand p_21421_, CallbackInfoReturnable<InteractionResult> cir) {
        if (this instanceof JerotesChangeCamel jerotesChangeCamel && jerotesChangeCamel.isJerotesCamelHusk() && jerotesChangeCamel.isJerotesMobControlled())
            this.setPersistenceRequired();
    }

    private static final EntityDataAccessor<Boolean> IS_JEROTES_PARCHED = SynchedEntityData.defineId(MobMixin.class, EntityDataSerializers.BOOLEAN);

    public boolean isJerotesParched() {
        return this.getEntityData().get(IS_JEROTES_PARCHED);
    }
    public void setJerotesParched(boolean bl) {
        this.getEntityData().set(IS_JEROTES_PARCHED, bl);
        if (this instanceof JerotesChangeStray jerotesChangeStray) {
            jerotesChangeStray.setJerotesParchedOther(bl);
        }
    }
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("IsJerotesParched", this.isJerotesParched());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setJerotesParched(compoundTag.getBoolean("IsJerotesParched"));
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.getEntityData().define(IS_JEROTES_PARCHED, false);
    }
}