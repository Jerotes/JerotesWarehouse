package com.jerotes.jerotes.entity.Magic.MagicMissile;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class MagicMissileEntity extends BaseMagicMissileEntity {
    public MagicMissileEntity(EntityType<? extends MagicMissileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MagicMissileEntity(EntityType<? extends MagicMissileEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public MagicMissileEntity(int spellLevelDamage, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.MAGIC_MISSILE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (this.getOwner() instanceof LivingEntity && entity != this.getTarget() && this.getTarget() != null) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            Entity entity2 = this.getOwner();
            livingEntity.hurt(this.damageSources().indirectMagic(this, entity2), Main.randomReach(RandomSource.create(), 1, 4) + 1);
            this.playSound(JerotesSounds.SPELL, 3.0f, 1.0f);
            this.discard();
        }
    }

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.MAGIC_MISSILE.get();
    }
    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(JerotesItems.MAGIC_MISSILE.get()) : itemStack;
    }
}
