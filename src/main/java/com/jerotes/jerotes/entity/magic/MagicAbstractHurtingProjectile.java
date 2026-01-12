package com.jerotes.jerotes.entity.magic;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class MagicAbstractHurtingProjectile extends Projectile implements MagicAbout {

    public double xPower;
    public double yPower;
    public double zPower;

    protected MagicAbstractHurtingProjectile(EntityType<? extends MagicAbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected MagicAbstractHurtingProjectile(EntityType<? extends MagicAbstractHurtingProjectile> entityType, double d, double d2, double d3, Level level) {
        this(entityType, level);
        this.setPos(d, d2, d3);
    }

    public MagicAbstractHurtingProjectile(EntityType<? extends MagicAbstractHurtingProjectile> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public MagicAbstractHurtingProjectile(EntityType<? extends MagicAbstractHurtingProjectile> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        this(entityType, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), d, d2, d3, level);
        this.setOwner(livingEntity);
        this.setRot(livingEntity.getYRot(), livingEntity.getXRot());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d2)) {
            d2 = 4.0;
        }
        return d < (d2 *= 64.0) * d2;
    }

    protected ClipContext.Block getClipType() {
        return ClipContext.Block.COLLIDER;
    }

    @Override
    public void tick() {
        float f;
        Entity entity = this.getOwner();
        if (!this.level().isClientSide && (entity != null && entity.isRemoved() || !this.level().hasChunkAt(this.blockPosition()))) {
            this.discard();
            return;
        }
        super.tick();
        if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
        }
        HitResult hitResult;

        //1.20.1//
        hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult)) {
        this.onHit(hitResult);
        }
        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d = this.getX() + vec3.x;
        double d2 = this.getY() + vec3.y;
        double d3 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2f);
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25, d2 - vec3.y * 0.25, d3 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }
            f = this.getLiquidInertia();
        } else {
            f = this.getInertia();
        }
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        ParticleOptions particleOptions = this.getTrailParticle();
        if (particleOptions != null) {
            this.level().addParticle(particleOptions, d, d2 + this.getBbHeight() / 2, d3, 0.0, 0.0, 0.0);
        }
        this.setPos(d, d2, d3);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        //法术反制
        if (!isHelp() && entity != this.getOwner() && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
            if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
            return false;
        }
        return super.canHitEntity(entity);
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getInertia() {
        return 1.0f;
    }

    protected float getLiquidInertia() {
        return 1.0f;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("power", this.newDoubleList(this.xPower, this.yPower, this.zPower));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        ListTag listTag;
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("power", 9) && (listTag = compoundTag.getList("power", 6)).size() == 3) {
            this.xPower = listTag.getDouble(0);
            this.yPower = listTag.getDouble(1);
            this.zPower = listTag.getDouble(2);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0f;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        this.markHurt();
        Entity entity = damageSource.getEntity();
        if (entity != null) {
            if (!this.level().isClientSide) {
                Vec3 vec3 = entity.getLookAngle();
                this.setDeltaMovement(vec3);
                this.xPower = vec3.x * 0.1;
                this.yPower = vec3.y * 0.1;
                this.zPower = vec3.z * 0.1;
                this.setOwner(entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int n = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(), this.getYRot(), this.getType(), n, new Vec3(this.xPower, this.yPower, this.zPower), 0.0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        double d = clientboundAddEntityPacket.getXa();
        double d2 = clientboundAddEntityPacket.getYa();
        double d3 = clientboundAddEntityPacket.getZa();
        double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
        if (d4 != 0.0) {
            this.xPower = d / d4 * 0.1;
            this.yPower = d2 / d4 * 0.1;
            this.zPower = d3 / d4 * 0.1;
        }
    }
}

