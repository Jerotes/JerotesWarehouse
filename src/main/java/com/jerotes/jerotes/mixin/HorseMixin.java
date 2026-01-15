package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Interface.ItemBeastArmor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(Horse.class)
public abstract class HorseMixin extends AbstractHorse {
    @Unique
    private static final UUID ARMOR_TOUGHNESS_MODIFIER_UUID = UUID.fromString("5b87399a-c768-4ea5-a08f-4b36e7fa1d34");
    @Unique
    private static final UUID KNOCKBACK_RESISTANCE_MODIFIER_UUID = UUID.fromString("12c0b1a0-18c1-4047-a6b6-f71dc7fe87ee");
    protected HorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setArmorEquipment", at = @At("TAIL"))
    private void setArmorEquipment(ItemStack itemStack, CallbackInfo ci) {
        if (!this.level().isClientSide) {
            double n2;
            double n3;
            Objects.requireNonNull(this.getAttribute(Attributes.ARMOR_TOUGHNESS)).removeModifier(ARMOR_TOUGHNESS_MODIFIER_UUID);
            Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).removeModifier(KNOCKBACK_RESISTANCE_MODIFIER_UUID);
            if (itemStack.getItem() instanceof ItemBeastArmor armor && (n2 = armor.getToughness()) != 0) {
                Objects.requireNonNull(this.getAttribute(Attributes.ARMOR_TOUGHNESS)).addTransientModifier(new AttributeModifier(ARMOR_TOUGHNESS_MODIFIER_UUID, "War Beast Armor toughness bonus", n2, AttributeModifier.Operation.ADDITION));
            }
            if (itemStack.getItem() instanceof ItemBeastArmor armor && (n3 = armor.getKnockbackResistance() * 0.1) != 0) {
                Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).addTransientModifier(new AttributeModifier(KNOCKBACK_RESISTANCE_MODIFIER_UUID, "War Beast Armor knockback resistance bonus", n3, AttributeModifier.Operation.ADDITION));
            }
        }
    }
}