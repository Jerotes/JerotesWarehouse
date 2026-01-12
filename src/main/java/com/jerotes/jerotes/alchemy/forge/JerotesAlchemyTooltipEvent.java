package com.jerotes.jerotes.alchemy.forge;

import com.jerotes.jerotes.alchemy.effect.AAAAlchemyEffect;
import com.jerotes.jerotes.alchemy.effect.AAANullAlchemyEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

public class JerotesAlchemyTooltipEvent extends EntityEvent {
    private final Player player;
    private boolean show;

    public JerotesAlchemyTooltipEvent(Player player) {
        super(player);
        this.player = player;
        this.show = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isShow() {
        return this.show;
    }
    public void setShow(boolean show) {
        this.show = show;
    }
}
