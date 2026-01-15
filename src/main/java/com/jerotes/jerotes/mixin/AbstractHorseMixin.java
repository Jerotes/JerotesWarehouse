package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeAbstractHorse;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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