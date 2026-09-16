package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ViciousMockeryEntity extends BaseTargetEntity {
   public ViciousMockeryEntity(EntityType<? extends ViciousMockeryEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ViciousMockeryEntity(EntityType<? extends ViciousMockeryEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public ViciousMockeryEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.VICIOUS_MOCKERY.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    @Override
    public void useMagicTo(LivingEntity livingEntity) {
       super.useMagicTo(livingEntity);
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOwner() != null && !this.getOwner().isSilent()) {
            DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, this, this.getOwner());
            boolean bl = livingEntity.hurt(damageSource,
                    //单数多出0.5
                    (spellLevelDamage > 1 && spellLevelDamage % 2 == 1) ?
                            Main.rollDice(1 + (spellLevelDamage - 2) / 2, 4, livingEntity.getRandom()) + Main.rollDice(1, 4, livingEntity.getRandom()) * 0.5f :
                            Main.rollDice(1 + (spellLevelDamage - 1) / 2, 4, livingEntity.getRandom())
            );
            if (bl) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0), this.getEffectSource());
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                MutableComponent component = Component.literal("").withStyle(ChatFormatting.RED);
                for (int i = 0; i < spellLevelDamage; ++i) {
                    int length = Main.randomReach(this.random, 6, 12);
                    for (int i2 = 0; i2 < length; ++i2) {
                        component = component.append(Component.literal("§k*§r").withStyle(ChatFormatting.RED));
                    }
                    if (i != spellLevelDamage - 1) {
                        component = component.append(Component.literal(" ").withStyle(ChatFormatting.RED));
                    }
                }
                serverPlayer.sendSystemMessage(component);
                serverPlayer.displayClientMessage(component, true);
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
        return JerotesParticleTypes.VICIOUS_MOCKERY.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.VICIOUS_MOCKERY.get()) : itemStack;
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
        return 0xb30e0b;
    }
    public int roundLightII() {
        return 0xff6461;
    }
}

