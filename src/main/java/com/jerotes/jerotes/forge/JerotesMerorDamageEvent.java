package com.jerotes.jerotes.forge;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class JerotesMerorDamageEvent extends EntityEvent {
    private final DamageSource damageSource;
    private boolean isMerorDamage;

    public JerotesMerorDamageEvent(DamageSource damageSource) {
        super(damageSource.getEntity());
        this.damageSource = damageSource;
        this.isMerorDamage = false;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }
    public boolean isMerorDamage() {
        return this.isMerorDamage;
    }
    public void setMerorDamage(boolean bl) {
        this.isMerorDamage = bl;
    }
}
