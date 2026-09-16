package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.entity.Other.OtherSpell.BestowCurseEntity;
import com.jerotes.jerotes.init.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemoveCurseEntity extends BaseTargetEntity {
    public RemoveCurseEntity(EntityType<? extends RemoveCurseEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RemoveCurseEntity(EntityType<? extends RemoveCurseEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        this(entityType, level);
        this.moveTo(d, d2, d3, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (d7 != 0.0) {
            this.xPower = d4 / d7 * 0.1;
            this.yPower = d5 / d7 * 0.1;
            this.zPower = d6 / d7 * 0.1;
        }
    }

    public RemoveCurseEntity(int spellLevelDamage, int spellLevelMainEffectTime, int spellLevelMainEffectLevel, int spellSubcategory, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.REMOVE_CURSE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelMainEffectTime = spellLevelMainEffectTime;
        this.spellLevelMainEffectLevel = spellLevelMainEffectLevel;
        this.spellSubcategory = spellSubcategory;
    }

    public void useMagicToNonLivingEntity(Entity entity) {
        super.useMagicToNonLivingEntity(entity);
        if (this.level().isClientSide) {
            return;
        }

        if (spellSubcategory == 1) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();
                if (!stack.isEmpty()) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                    if (!enchantments.isEmpty()) {
                        List<Enchantment> curses = enchantments.keySet().stream()
                                .filter(Enchantment::isCurse)
                                .toList();
                        if (!curses.isEmpty()) {
                            curses.forEach(enchantments.keySet()::remove);
                            EnchantmentHelper.setEnchantments(enchantments, stack);
                            itemEntity.setItem(stack);
                            itemEntity.getItem().getItem().onEntityItemUpdate(stack, itemEntity);
                        }
                    }
                }
            }
        }
    }
    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        //移除debuff
        if (spellSubcategory == 0) {
            List<MobEffect> negativeEffects = livingEntity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .toList();
            negativeEffects.forEach(livingEntity::removeEffect);
            List<BestowCurseEntity> list = this.level().getEntitiesOfClass(BestowCurseEntity.class, this.getBoundingBox().inflate(32, 32, 32));
            for (BestowCurseEntity bestowCurseEntity : list) {
                if (bestowCurseEntity == null) continue;
                if (bestowCurseEntity.getTarget() == livingEntity) {
                    bestowCurseEntity.endConcentration();
                }
            }
        }
        //移除全身诅咒装备附魔
        else if (spellSubcategory == 1) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = livingEntity.getItemBySlot(slot);
                if (stack.isEmpty()) continue;
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                if (enchantments.isEmpty()) continue;
                List<Enchantment> curses = enchantments.keySet().stream()
                        .filter(Enchantment::isCurse)
                        .toList();
                if (!curses.isEmpty()) {
                    curses.forEach(enchantments.keySet()::remove);
                    EnchantmentHelper.setEnchantments(enchantments, stack);
                }
            }
        }
        this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
    }

    @Override
    public int getUseLife() {
        return 10;
    }

    @Override
    public int getMaxLife() {
        return 20;
    }

    public boolean isHelp() {
        return true;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.REMOVE_CURSE.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.REMOVE_CURSE.get()) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
    public int roundLightI() {
        return 0xe0e5e5;
    }
    public int roundLightII() {
        return 0xffffff;
    }
}
