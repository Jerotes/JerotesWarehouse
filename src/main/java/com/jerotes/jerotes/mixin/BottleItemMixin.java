package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BottleItem.class)
public abstract class BottleItemMixin extends Item {
    @Shadow
    protected abstract ItemStack turnBottleIntoItem(ItemStack p_40652_, Player p_40653_, ItemStack p_40654_);

    public BottleItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    protected void use(Level p_40656_, Player p_40657_, InteractionHand p_40658_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        List<SpellCloudEntity> list = p_40656_.getEntitiesOfClass(SpellCloudEntity.class, p_40657_.getBoundingBox().inflate(2.0D), (p_289499_) -> p_289499_ != null && p_289499_.isAlive() && p_289499_.isEnderdragonBreath());
        ItemStack itemstack = p_40657_.getItemInHand(p_40658_);
        if (!list.isEmpty()) {
            SpellCloudEntity areaeffectcloud = list.get(0);
            areaeffectcloud.setRadius(areaeffectcloud.getRadius() - 0.5F);
            p_40656_.playSound((Player)null, p_40657_.getX(), p_40657_.getY(), p_40657_.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            p_40656_.gameEvent(p_40657_, GameEvent.FLUID_PICKUP, p_40657_.position());
            if (p_40657_ instanceof ServerPlayer serverplayer) {
                CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverplayer, itemstack, areaeffectcloud);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, p_40657_, new ItemStack(Items.DRAGON_BREATH)), p_40656_.isClientSide()));
            cir.cancel();
        }
    }
}