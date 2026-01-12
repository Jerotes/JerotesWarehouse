package com.jerotes.jerotes.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class JerotesUseSpellEvent extends LivingEvent {
    private LivingEntity caster;
    private Entity target;
    private int level;

    public JerotesUseSpellEvent(LivingEntity entity, Entity target, int level) {
        super(entity);
        this.caster = entity;
        this.target = target;
        this.level = level;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }
    public Entity getTarget() {
        return this.target;
    }
    public int getLevel() {
        return this.level;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }
    public void setTarget(Entity target) {
        this.target = target;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}
