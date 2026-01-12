package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.CamelAbout;
import com.jerotes.jerotes.entity.JerotesChangeAbstractHorse;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements JerotesChangeAbstractHorse {
    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow protected SimpleContainer inventory;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void openHorseInventoryJerotes(Player player) {
        if (!this.level().isClientSide()) {
            player.openHorseInventory((AbstractHorse) (Object)this, this.inventory);
        }
    }
}