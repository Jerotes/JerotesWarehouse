package com.jerotes.jerotes.forge;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class JerotesFactionEvent extends LivingEvent {
    private LivingEntity living1;
    private LivingEntity living2;
    private boolean isFriend;
    private boolean isEnemy;

    public JerotesFactionEvent(LivingEntity living1, LivingEntity living2) {
        super(living1);
        this.living1 = living1;
        this.living2 = living2;
        this.isFriend = false;
        this.isEnemy = false;
    }

    public LivingEntity getLiving1() {
        return this.living1;
    }
    public LivingEntity getLiving2() {
        return this.living2;
    }
    public boolean isFriend() {
        return this.isFriend;
    }
    public void setFriend(boolean bl) {
        this.isFriend = bl;
    }
    public boolean isEnemy() {
        return this.isEnemy;
    }
    public void setEnemy(boolean bl) {
        this.isEnemy = bl;
    }
}
