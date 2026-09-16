package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MendingEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public MendingEntity(EntityType<? extends MendingEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public MendingEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.MENDING.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public boolean scaleAboutBoundingBox() {
        return true;
    }
    @Override
    public int getMaxLife() {
        return 120;
    }
    public boolean moveToTarget() {
        return true;
    }
    @VisibleForTesting
    public void setSize(float f) {
        float f2 = Mth.clamp(f, 0, 128);
        super.setSize(f2);
    }
    @VisibleForTesting
    public void setTargetHeight(float f) {
        float f2 = Mth.clamp(f, 0, 128);
        super.setTargetHeight(f2);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && getTarget().level() instanceof ServerLevel serverLevel) {
            if (this.getLifeTick() == 5) {
                for (int i = 0; i < Mth.clamp(getSize() * 10, 16, 160); ++i) {
                    serverLevel.sendParticles(JerotesParticleTypes.LIFE_MEND.get(),
                            getTarget().getRandomX(0.8),
                            getTarget().getY(0.5f),
                            getTarget().getRandomZ(0.8), 0,
                            (RandomSource.create().nextFloat() - 0.5f) / 3f,
                            0.2f + RandomSource.create().nextFloat() / 5f,
                            (RandomSource.create().nextFloat() - 0.5f) / 3f, 0.25);
                }
                if (getSubcategory() == 0) {
                    if (getTarget() instanceof LivingEntity livingEntity) {
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            if (slot.getType() == EquipmentSlot.Type.HAND || slot.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack stack = livingEntity.getItemBySlot(slot);
                                if (!stack.isEmpty() && stack.isDamageableItem()) {
                                    int repairAmount = stack.getMaxDamage() / 3;
                                    int minRepairAmount = 20 * getSpellLevel() * getSpellLevel();
                                    int maxRepairAmount = 100 * getSpellLevel() * getSpellLevel();
                                    int newDamage = Math.max(0, stack.getDamageValue() - Mth.clamp(repairAmount, minRepairAmount, maxRepairAmount));
                                    stack.setDamageValue(newDamage);
                                }
                            }
                        }
                    }
                    if (this.getLifeTick() == 5) {
                        if (getTarget() instanceof ItemEntity itemEntity) {
                            ItemStack stack = itemEntity.getItem();
                            if (!stack.isEmpty() && stack.isDamageableItem()) {
                                int repairAmount = stack.getMaxDamage() / 3;
                                int minRepairAmount = 20 * getSpellLevel() * getSpellLevel();
                                int maxRepairAmount = 100 * getSpellLevel() * getSpellLevel();
                                int newDamage = Math.max(0, stack.getDamageValue() - Mth.clamp(repairAmount, minRepairAmount, maxRepairAmount));
                                stack.setDamageValue(newDamage);
                                itemEntity.setItem(stack);
                                itemEntity.getItem().getItem().onEntityItemUpdate(stack, itemEntity);
                            }
                        }
                    }
                }
                else if (getSubcategory() == 1) {
                    if (getTarget() instanceof LivingEntity livingEntity) {
                        if (EntityFactionFind.isMachine(livingEntity) || EntityFactionFind.isConstruct(livingEntity)) {
                            livingEntity.heal(
                                    //单数多出0.5
                                    (getSpellLevel() > 1 && getSpellLevel() % 2 == 1) ?
                                            Main.rollDice(1 + (getSpellLevel() - 2) / 2, 8, livingEntity.getRandom()) +
                                                    Main.rollDice(1, 8, livingEntity.getRandom()) * 0.5f + (1 + (getSpellLevel() - 1) * 0.5f) :
                                            Main.rollDice(1 + (getSpellLevel() - 1) / 2, 8, livingEntity.getRandom()) + (1 + (getSpellLevel() - 1) * 0.5f)
                            );
                        }
                    }
                }
            }
        }
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }
}

