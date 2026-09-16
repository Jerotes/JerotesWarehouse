package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.event.AnesthetizedEvent;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShockingGraspEntity extends BaseTargetEntity {
    public ShockingGraspEntity(EntityType<? extends ShockingGraspEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ShockingGraspEntity(EntityType<? extends ShockingGraspEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public ShockingGraspEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.SHOCKING_GRASP.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
        super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOwner() != null) {
            float chance =  (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ? Main.rollDice(1 + (spellLevelDamage - 2) / 2, 8, livingEntity.getRandom()) + Main.rollDice(1, 8, livingEntity.getRandom()) * 0.5f :
            Main.rollDice(1 + (spellLevelDamage - 1) / 2, 8, livingEntity.getRandom());
            if (EntityFactionFind.isMachine(livingEntity) || EntityAndItemFind.isMetalArmors(livingEntity.getItemBySlot(EquipmentSlot.HEAD)) || EntityAndItemFind.isMetalArmors(livingEntity.getItemBySlot(EquipmentSlot.CHEST)) || EntityAndItemFind.isMetalArmors(livingEntity.getItemBySlot(EquipmentSlot.LEGS)) || EntityAndItemFind.isMetalArmors(livingEntity.getItemBySlot(EquipmentSlot.FEET))) {
                chance = Math.max(chance, (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ? Main.rollDice(1 + (spellLevelDamage - 2) / 2, 8, livingEntity.getRandom()) + Main.rollDice(1, 8, livingEntity.getRandom()) * 0.5f :
                Main.rollDice(1 + (spellLevelDamage - 1) / 2, 8, livingEntity.getRandom()));
            }


            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, this, this.getOwner());
            boolean bl = livingEntity.hurt(damageSource,
                    chance
            );
            if (bl) {
                if (!this.level().isClientSide()) {
                    livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.SPELL_REACTION_LOCK.get(), 120, 0, false, false));
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

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
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
        return 0x688cff;
    }
    public int roundLightII() {
        return 0x98aceb;
    }
}
