package com.jerotes.jerotes.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class JerotesMeleeDamageFromMainHandIsOffHandEvent extends EntityEvent {
    private final LivingEntity livingEntity;
    private boolean isOffHand;

    public JerotesMeleeDamageFromMainHandIsOffHandEvent(LivingEntity livingEntity) {
        super(livingEntity);
        this.livingEntity = livingEntity;
        this.isOffHand = false;
    }

    public LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
    public boolean isOffHand() {
        return this.isOffHand;
    }
    public void setOffHand(boolean bl) {
        this.isOffHand = bl;
    }
}
