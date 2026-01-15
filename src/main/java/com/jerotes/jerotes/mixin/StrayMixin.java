package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.StrayAbout;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(Stray.class)
public abstract class StrayMixin extends AbstractSkeleton implements StrayAbout {
    @Unique
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("19045b3c-b9b3-4cf0-a90e-f4bda61e4a53");
    protected StrayMixin(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getArrow*", at = @At("HEAD"), cancellable = true)
    public void getArrow(ItemStack p_33846_, float p_33847_, CallbackInfoReturnable<AbstractArrow> cir) {
        if (isJerotesParched()) {
            AbstractArrow abstractArrow = super.getArrow(p_33846_, p_33847_);
            if (abstractArrow instanceof Arrow) {
                ((Arrow)abstractArrow).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
            }
            cir.setReturnValue(abstractArrow);
        }
    }

    public void setJerotesParchedOther(boolean bl) {
        if (!this.level().isClientSide) {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).removeModifier(HEALTH_MODIFIER_UUID);
            if (bl) {
                Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).addTransientModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "Parched Health", -4, AttributeModifier.Operation.ADDITION));
                if (this.getHealth() > this.getMaxHealth()) {
                    this.setHealth(this.getMaxHealth());
                }
            }
        }
    }
}