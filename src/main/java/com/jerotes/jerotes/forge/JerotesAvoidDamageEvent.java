package com.jerotes.jerotes.forge;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class JerotesAvoidDamageEvent extends LivingEvent {
    private Entity attacker;
    private final DamageSource damageSource;
    private LivingEntity hurt;

    public JerotesAvoidDamageEvent(LivingEntity hurt, DamageSource damageSource, Entity attacker) {
        super(hurt);
        this.hurt = hurt;
        this.damageSource = damageSource;
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return this.attacker;
    }
    public LivingEntity getHurt() {
        return this.hurt;
    }
    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public void setAttacker(Entity caster) {
        this.attacker = caster;
    }
    public void setHurt(LivingEntity target) {
        this.hurt = target;
    }
}
