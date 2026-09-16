package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeMob;
import com.jerotes.jerotes.item.Tool.ItemToolBaseUmbrella;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, priority = 1100)
public abstract class MobSunMixin extends LivingEntity implements JerotesChangeMob {
    protected MobSunMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void isSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
        if (Main.getJerotesPersistentData(this).contains("jerotes_sun_burn_tick")) {
            if (Main.getJerotesPersistentData(this).getDouble("jerotes_sun_burn_tick") > 0) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
        if (this.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemToolBaseUmbrella ||
                this.getUseItem().getItem() instanceof ItemToolBaseUmbrella) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}