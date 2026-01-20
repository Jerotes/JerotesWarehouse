package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import com.jerotes.jerotes.entity.Interface.CanNotControlEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeStray;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.item.Tool.ItemToolBaseUmbrella;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow @Nullable public abstract Team getTeam();

    @Shadow public abstract float getYRot();

    @Shadow public abstract List<Entity> getPassengers();

    @Shadow public abstract boolean isVehicle();

    @Shadow public abstract InteractionResult interact(Player p_19978_, InteractionHand p_19979_);

    @Inject(method = "isInRain", at = @At("HEAD"), cancellable = true)
    private void isInRain(CallbackInfoReturnable<Boolean> cir) {
        if ((Entity)((Object)this) instanceof LivingEntity livingEntity && (livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella || livingEntity.getUseItem().getItem() instanceof ItemToolBaseUmbrella)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (!((Entity)((Object)this) instanceof Mob mob && mob.isNoAi())) {
            Entity entity = ((Entity)((Object)this)).getFirstPassenger();
            if (entity instanceof Mob mob && mob instanceof CanNotControlEntity canNotControlEntity && canNotControlEntity.isCanNotControl()) {
                cir.setReturnValue(null);
            }
        }
    }
    @Inject(method = "getTypeName", at = @At("HEAD"), cancellable = true)
    private void getTypeName(CallbackInfoReturnable<Component> cir) {
        if (this instanceof JerotesChangeCamel jerotesChangeCamel && jerotesChangeCamel.isJerotesCamelHusk())
            cir.setReturnValue(Component.translatable("entity.jerotes.camel_husk"));
        if (this instanceof JerotesChangeStray jerotesChangeStray && jerotesChangeStray.isJerotesParched())
            cir.setReturnValue(Component.translatable("entity.jerotes.parched"));
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void load(CompoundTag compoundTag, CallbackInfo ci) {
        if (this instanceof JerotesChangeCamel jerotesChangeCamel)
            jerotesChangeCamel.setJerotesCamelHusk(compoundTag.getBoolean("IsJerotesCamelHusk"));
    }

    @Inject(method = "setCustomName", at = @At("HEAD"))
    public void setCustomName(Component component, CallbackInfo ci) {
        if (component == null) {
            return;
        }
        String nameString = component.getString();
        if (nameString == null) {
            return;
        }
        if (this instanceof JerotesChangeCamel jerotesChangeCamel) {
            if (!jerotesChangeCamel.isJerotesCamelHusk() && "Jerotes_ Camel Husk".equals(nameString) && this.getPassengers().isEmpty()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    PlayerTeam teams = (PlayerTeam) getTeam();
                    Husk husk = EntityType.HUSK.spawn(serverLevel, BlockPos.containing(getX(), getY(), getZ()), MobSpawnType.MOB_SUMMONED);
                    if (husk != null) {
                        if (teams != null) {
                            serverLevel.getLevel().getScoreboard().addPlayerToTeam(husk.getStringUUID(), teams);
                        }
                        husk.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JerotesItems.IRON_SPEAR.get()));
                        husk.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
                        husk.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(husk.blockPosition()), MobSpawnType.NATURAL, null, null);
                        husk.startRiding((Entity) (Object) this);
                    }
                    Stray stray = EntityType.STRAY.spawn(serverLevel, BlockPos.containing(getX(), getY(), getZ()), MobSpawnType.MOB_SUMMONED);
                    if (stray != null) {
                        if (teams != null) {
                            serverLevel.getLevel().getScoreboard().addPlayerToTeam(stray.getStringUUID(), teams);
                        }
                        stray.setCustomName(Component.literal("Jerotes_ Parched"));
                        stray.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(stray.blockPosition()), MobSpawnType.NATURAL, null, null);
                        stray.startRiding((Entity) (Object) this);
                    }
                }
            }
            jerotesChangeCamel.setJerotesCamelHusk("Jerotes_ Camel Husk".equals(nameString));
        }
        if (this instanceof JerotesChangeStray jerotesChangeStray) {
            jerotesChangeStray.setJerotesParched("Jerotes_ Parched".equals(nameString));
        }

    }
}