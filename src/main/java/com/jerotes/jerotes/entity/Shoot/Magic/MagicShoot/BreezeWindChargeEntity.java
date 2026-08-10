package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.block.Interface.JerotesChangeFenceGate;
import com.jerotes.jerotes.block.Interface.JerotesChangeTrapDoor;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.List;

public class BreezeWindChargeEntity extends MagicAboutEntity implements ItemSupplier {
    public static final WindChargeExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new WindChargeExplosionDamageCalculator();

    public BreezeWindChargeEntity(EntityType<? extends BreezeWindChargeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BreezeWindChargeEntity(EntityType<? extends BreezeWindChargeEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
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

    public BreezeWindChargeEntity(int spellLevelDamage, float spellLevelExplode, Level level, LivingEntity livingEntity, double d, double d2, double d3) {
        super(JerotesEntityType.BREEZE_WIND_CHARGE.get(), livingEntity, d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelExplode = spellLevelExplode;
    }

    @Override
    protected AABB makeBoundingBox() {
        float f = this.getType().getDimensions().width / 2.0f;
        float f2 = this.getType().getDimensions().height;
        float f3 = 0.15f;
        return new AABB(this.position().x - (double)f, this.position().y - 0.15000000596046448, this.position().z - (double)f, this.position().x + (double)f, this.position().y - 0.15000000596046448 + (double)f2, this.position().z + (double)f);
    }
    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity instanceof BreezeWindChargeEntity) {
            return false;
        }
        return super.canCollideWith(entity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof BreezeWindChargeEntity) {
            return false;
        }
        if (entity.getType() == EntityType.END_CRYSTAL) {
            return false;
        }
        return super.canHitEntity(entity);
    }
    public static void explode(Entity entity, Vec3 vec3, float spellLevelExplode) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JerotesSoundEvents.WIND_BURST, entity.getSoundSource(), 1.0F, 1.0F);
        if (entity.level() instanceof ServerLevel serverLevel) {
            float radius = spellLevelExplode;
            boolean isLarge = radius >= 2.0f;
            if (isLarge) {
                int count = Mth.clamp((int)(radius * radius * 5.0F + radius * 5.0F), 8, 80);
                double spread = radius * 2.0;
                for (int i = 0; i < count; ++i) {
                    RandomSource random = RandomSource.create();
                    double d = random.nextGaussian() * 0.02;
                    double d2 = random.nextGaussian() * 0.02;
                    double d3 = random.nextGaussian() * 0.02;
                    float x = (float) (vec3.x() + (random.nextDouble() - 0.5) * spread);
                    float y = (float) (vec3.y() + (random.nextDouble() - 0.5) * spread);
                    float z = (float) (vec3.z() + (random.nextDouble() - 0.5) * spread);
                    serverLevel.sendParticles(JerotesParticleTypes.GUST.get(), x, y, z, 0, d, d2, d3, 0);
                }
            }
            else {
                int count = Mth.clamp((int)(radius * radius * 5.0F + radius * 5.0F), 8, 80);
                double spread = radius * 0.8;
                for (int i = 0; i < count; ++i) {
                    RandomSource random = RandomSource.create();
                    double d = random.nextGaussian() * 0.01;
                    double d2 = random.nextGaussian() * 0.01;
                    double d3 = random.nextGaussian() * 0.01;
                    float x = (float) (vec3.x() + (random.nextDouble() - 0.5) * spread);
                    float y = (float) (vec3.y() + (random.nextDouble() - 0.5) * spread);
                    float z = (float) (vec3.z() + (random.nextDouble() - 0.5) * spread);
                    serverLevel.sendParticles(JerotesParticleTypes.SMALL_GUST.get(), x, y, z, 0, d, d2, d3, 0);
                }
            }
        }
        explodeAboutBlock(entity, vec3, spellLevelExplode);
        explodeAboutEntity(entity, vec3, spellLevelExplode);
    }
    public static void explodeAboutBlock(Entity self, Vec3 vec3, float spellLevelExplode) {
        Level level = self.level();
        if (!(level instanceof ServerLevel serverLevel)) return;
        float radius = spellLevelExplode;
        float f2 = radius * 2.0F;
        int range = Mth.ceil(f2);
        BlockPos center = BlockPos.containing(vec3);
        BlockPos.betweenClosedStream(center.offset(-range, -range, -range), center.offset(range, range, range))
                .forEach(blockPos -> {
                    double dx = blockPos.getX() + 0.5 - vec3.x();
                    double dy = blockPos.getY() + 0.5 - vec3.y();
                    double dz = blockPos.getZ() + 0.5 - vec3.z();
                    double distSqr = dx * dx + dy * dy + dz * dz;
                    if (distSqr > f2 * f2) return;
                    BlockState blockState = serverLevel.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    //门
                    if (block instanceof DoorBlock doorBlock) {
                        if (blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER && DoorBlock.isWoodenDoor(blockState) &&
                                !blockState.getValue(DoorBlock.POWERED)) {
                            doorBlock.setOpen(null, serverLevel, blockState, blockPos, !doorBlock.isOpen(blockState));
                        }
                    }
                    //活板门
                    else if (block instanceof TrapDoorBlock && block instanceof JerotesChangeTrapDoor jerotesChangeTrapDoor) {
                        if (jerotesChangeTrapDoor.isJerotesWoodenDoor() && !blockState.getValue(TrapDoorBlock.POWERED)) {
                            BlockState blockState2 = blockState.cycle(TrapDoorBlock.OPEN);
                            level.setBlock(blockPos, blockState2, 2);
                            if (blockState2.getValue(TrapDoorBlock.WATERLOGGED)) {
                                level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                            }
                            jerotesChangeTrapDoor.playSoundJerotes(null, level, blockPos, blockState2.getValue(TrapDoorBlock.OPEN));
                        }
                    }
                    //栅栏门
                    else if (block instanceof FenceGateBlock && block instanceof JerotesChangeFenceGate jerotesChangeFenceGate && jerotesChangeFenceGate.isJerotesWoodenDoor()) {
                        if (!blockState.getValue(FenceGateBlock.POWERED)) {
                            boolean bl = blockState.getValue(FenceGateBlock.OPEN);
                            serverLevel.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(FenceGateBlock.OPEN, !bl));
                            serverLevel.playSound(null, blockPos, bl ? jerotesChangeFenceGate.getOpenSoundJerotes() : jerotesChangeFenceGate.getCloseSoundJerotes(), SoundSource.BLOCKS, 1.0f, serverLevel.getRandom().nextFloat() * 0.1f + 0.9f);
                            serverLevel.gameEvent(bl ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, blockPos, GameEvent.Context.of(blockState));
                        }
                    }
                    //按钮
                    else if (block instanceof ButtonBlock buttonBlock) {
                        if (!blockState.getValue(ButtonBlock.POWERED)) {
                            buttonBlock.press(blockState, serverLevel, blockPos);
                        }
                    }
                    //拉杆
                    else if (block instanceof LeverBlock leverBlock) {
                        leverBlock.pull(blockState, serverLevel, blockPos);
                    }
                    //钟
                    else if (block instanceof BellBlock bell) {
                        bell.attemptToRing(serverLevel, blockPos, null);
                    }
                    //蜡烛或蜡烛蛋糕
                    else if (block instanceof AbstractCandleBlock) {
                        if (blockState.getValue(AbstractCandleBlock.LIT)) {
                            AbstractCandleBlock.extinguish(null, blockState, serverLevel, blockPos);
                        }
                    }
                });
    }
    public static void explodeAboutEntity(Entity self, Vec3 vec3, float spellLevelExplode) {
        float radius = spellLevelExplode;
        float f2 = radius * 2.0F;
        AABB explosionArea = new AABB(
                Mth.floor(vec3.x() - f2 - 1.0),
                Mth.floor(vec3.y() - f2 - 1.0),
                Mth.floor(vec3.z() - f2 - 1.0),
                Mth.floor(vec3.x() + f2 + 1.0),
                Mth.floor(vec3.y() + f2 + 1.0),
                Mth.floor(vec3.z() + f2 + 1.0)
        );
        List<Entity> entities = self.level().getEntities(self, explosionArea);
        Vec3 center = new Vec3(vec3.x(), vec3.y(), vec3.z());
        for (Entity entity : entities) {
            if (entity.ignoreExplosion()) continue;
            double distanceRatio = Math.sqrt(entity.distanceToSqr(center)) / f2;
            if (distanceRatio > 1.0) continue;
            double dx = entity.getX() - vec3.x();
            double dy = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - vec3.y();
            double dz = entity.getZ() - vec3.z();
            double magnitude = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (magnitude == 0.0) continue;
            dx /= magnitude;
            dy /= magnitude;
            dz /= magnitude;
            float seen = getSeenPercent(center, entity);
            double d10 = (1.0 - distanceRatio) * seen;
            double knockback;
            if (entity instanceof LivingEntity livingEntity) {
                knockback = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, d10);
            } else {
                knockback = d10;
            }
            dx *= knockback;
            dy *= knockback;
            dz *= knockback;
            Vec3 velocity = new Vec3(dx, dy, dz);
            entity.hurtMarked = true;
            entity.setOnGround(false);
            entity.setDeltaMovement(entity.getDeltaMovement().add(velocity));
//            if (entity instanceof Player player) {
//                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
//                }
//            }
        }
    }

    public static float getSeenPercent(Vec3 vec3, Entity entity) {
        AABB aABB = entity.getBoundingBox();
        double d = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d < 0.0 || d2 < 0.0 || d3 < 0.0) {
            return 0.0f;
        }
        int n = 0;
        int n2 = 0;
        for (double d6 = 0.0; d6 <= 1.0; d6 += d) {
            for (double d7 = 0.0; d7 <= 1.0; d7 += d2) {
                for (double d8 = 0.0; d8 <= 1.0; d8 += d3) {
                    double d9 = Mth.lerp(d6, aABB.minX, aABB.maxX);
                    double d10 = Mth.lerp(d7, aABB.minY, aABB.maxY);
                    double d11 = Mth.lerp(d8, aABB.minZ, aABB.maxZ);
                    Vec3 vec32 = new Vec3(d9 + d4, d10, d11 + d5);
                    if (entity.level().clip(new ClipContext(vec32, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                        ++n;
                    }
                    ++n2;
                }
            }
        }
        return (float)n / (float)n2;
    }


    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.0f;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        explode(this, this.position(), this.spellLevelExplode);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.THUNDER, this, this.getOwner());
           entity.hurt(damageSource, spellLevelDamage);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Vec3i vec3i = blockHitResult.getDirection().getNormal();
        Vec3 vec3 = Vec3.atLowerCornerOf(vec3i).multiply(0.25, 0.25, 0.25);
        Vec3 vec32 = blockHitResult.getLocation().add(vec3);
        explode(this, vec32, this.spellLevelExplode);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }


    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public int getMaxLife() {
        return 240;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    protected ClipContext.Block getClipType() {
        return ClipContext.Block.OUTLINE;
    }
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }

    @Override
    public int getSpellLevel() {
        return spellLevelDamage;
    }

    public static final class WindChargeExplosionDamageCalculator
            extends ExplosionDamageCalculator {
        //1.20.4↑//
        //@Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return false;
        }
    }
}