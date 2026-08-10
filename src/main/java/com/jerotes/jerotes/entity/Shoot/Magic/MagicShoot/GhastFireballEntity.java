package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GhastFireballEntity extends MagicAboutEntity {
    public GhastFireballEntity(EntityType<? extends GhastFireballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GhastFireballEntity(int spellLevelDamage, int spellLevelFireTime, float spellLevelExplode, int spellSubcategory, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.GHAST_FIREBALL.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelFireTime = spellLevelFireTime;
        this.spellLevelExplode = spellLevelExplode;
        this.spellSubcategory = spellSubcategory;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.FIRE_MAGIC, this, this.getOwner());
            if (livingEntity instanceof Ghast) {
                damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_MAGIC, this, this.getOwner());
                if (entity.hurt(damageSource, (6f + 6f * (spellLevelDamage - 1)) * 20)) {
                    entity.setSecondsOnFire(spellLevelFireTime);
                    if (getOwner() instanceof LivingEntity living) {
                        this.doEnchantDamageEffects(living, entity);
                    }
                }
            }
            if (entity.hurt(damageSource, 6f + 6f * (spellLevelDamage - 1))) {
                entity.setSecondsOnFire(spellLevelFireTime);
                if (getOwner() instanceof LivingEntity living) {
                    this.doEnchantDamageEffects(living, entity);
                }
            }
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner()) || this.getOwner() instanceof Player;
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.spellLevelExplode,
                    spellSubcategory == 1 && flag, spellSubcategory == 1 ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
    @Override
    public void lastBreak() {
        boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner()) || this.getOwner() instanceof Player;
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.spellLevelExplode,
                    spellSubcategory == 1 && flag, spellSubcategory == 1 ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.MOB);
        }
    }

    @Override
    public int getMaxLife() {
        return 240;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : itemstack;
    }
    public void setItem(ItemStack itemStack) {
        if (!itemStack.is(Items.FIRE_CHARGE) || itemStack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, itemStack.copyWithCount(1));
        }
    }
    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    protected boolean shouldBurn() {
        return true;
    }

    @Override
    protected float getInertia() {
        return 0.95f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 0.8F;
    }

    public boolean isPickable() {
        return true;
    }
    public float getPickRadius() {
        return 1.0F;
    }
    public boolean hurt(DamageSource p_36839_, float p_36840_) {
        if (this.isInvulnerableTo(p_36839_)) {
            return false;
        } else {
            this.markHurt();
            Entity entity = p_36839_.getEntity();
            if (entity != null) {
                if (!this.level().isClientSide) {
                    Vec3 vec3 = entity.getLookAngle();
                    this.setDeltaMovement(vec3);
                    this.xPower = vec3.x * 0.1D;
                    this.yPower = vec3.y * 0.1D;
                    this.zPower = vec3.z * 0.1D;
                    this.setOwner(entity);
                }

                return true;
            } else {
                return false;
            }
        }
    }
}
