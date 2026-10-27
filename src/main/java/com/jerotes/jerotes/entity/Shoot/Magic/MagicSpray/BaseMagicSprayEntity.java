package com.jerotes.jerotes.entity.Shoot.Magic.MagicSpray;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.particle.BoltParticleOptions;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.PartEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseMagicSprayEntity extends MagicAboutEntity {
    public BaseMagicSprayEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseMagicSprayEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseMagicSprayEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
    }

    @Override
    public void setDeltaMovement(Vec3 vec3){
    }

    @Override
    public void tick() {
        super.tick();
        //超过距离
        if (this.getOwner() != null && this.position().distanceTo(this.getOwner().position()) > getMaxReach()) {
            if (!this.level().isClientSide()) {
                this.lastBreak();
                this.discard();
            }
        }
        if (this.showParticle()) {
            Vec3 vec3 = this.getDeltaMovement();
            double d = this.getX() + vec3.x;
            double d2 = this.getY() + vec3.y;
            double d3 = this.getZ() + vec3.z;
            ParticleOptions particleOptions = this.getTrailParticle();
            if (particleOptions != null) {
                this.level().addParticle(particleOptions, d, d2 + this.getBbHeight() / 2, d3, 0.0, 0.0, 0.0);
            }
        }

        if (this.life <= this.getMaxLife()) {
            if (this.getOwner() != null) {
                this.moveTo(this.getOwner().getX(), this.getOwner().getY(0.5), this.getOwner().getZ());
            }
        }
        //喷射粒子
        if (this.level().isClientSide()) {
            spawnConeParticles(this.life);
        }
        if (life == getStripeStart()) {
            spawnBoltParticles();
        }
        //造成伤害
        if (this.life == this.getUseLife()) {
            handleBlockInteraction();
            Entity owner = this.getOwner();
            if (owner != null) {
                List<Entity> list = this.level().getEntitiesOfClass(Entity.class,
                        this.getBoundingBox().deflate(Main.mobWidth(this) / 2, Main.mobHeight(this) / 2, Main.mobWidth(this) / 2)
                                .inflate(getMaxReach(), getMaxReach(), getMaxReach()));
                for (Entity entity : list) {
                    if (entity == owner) continue;
                    if (entity.distanceTo(owner) > getMaxReach()) continue;
                    if (!Main.isEntityIn3DCone(owner, entity, getSpellAngle())) continue;
                    if (!Main.hasLineOfSightEntity(entity, owner)) continue;
                    if (entity instanceof LivingEntity livingEntity) {
                        this.useMagicTo(livingEntity);
                    } else if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity livingEntity) {
                        this.useMagicTo(livingEntity);
                    }
                }
            }
        }
    }
    protected void handleBlockInteraction() {
        Entity owner = this.getOwner();
        if (owner == null || this.level().isClientSide()) {
            return;
        }
        if (owner instanceof Mob && !this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }

        Level level = this.level();
        Vec3 origin = owner.getEyePosition();
        Vec3 forward = owner.getViewVector(1.0f);
        if (forward.lengthSqr() < 1.0E-6) {
            return;
        }
        forward = forward.normalize();

        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(worldUp);
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1, 0, 0);
        }
        right = right.normalize();
        Vec3 up = right.cross(forward).normalize();

        double range = getMaxReach();
        double halfAngleRad = Math.toRadians(getSpellAngle() * 0.5f);
        double tanHalfAngle = Math.tan(halfAngleRad);

        int steps = Mth.ceil(range * 2.0);
        Set<BlockPos> checked = new HashSet<>();

        for (int i = 1; i <= steps; i++) {
            double distance = range * i / steps;
            double radius = distance * tanHalfAngle;
            int radialSteps = Math.max(1, Mth.ceil(radius * 2.0));

            for (int x = -radialSteps; x <= radialSteps; x++) {
                for (int y = -radialSteps; y <= radialSteps; y++) {
                    double offsetX = x * 0.5;
                    double offsetY = y * 0.5;
                    if (offsetX * offsetX + offsetY * offsetY > radius * radius) {
                        continue;
                    }

                    Vec3 target = origin
                            .add(forward.scale(distance))
                            .add(right.scale(offsetX))
                            .add(up.scale(offsetY));

                    BlockHitResult hitResult = level.clip(
                            new ClipContext(
                                    origin,
                                    target,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    owner
                            )
                    );

                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockPos hitPos = hitResult.getBlockPos();
                        Direction direction = hitResult.getDirection();
                        BlockPos firePos = hitPos.relative(direction);

                        if (!checked.add(firePos)) continue;
                        handleBlock(firePos, level.getBlockState(firePos));
                        continue;
                    }

                    BlockPos targetPos = BlockPos.containing(target);
                    if (!checked.add(targetPos)) continue;
                    handleBlock(targetPos, level.getBlockState(targetPos));
                }
            }
        }
    }

    protected void handleBlock(BlockPos blockPos, BlockState state) {
    }
    public void useMagicTo(LivingEntity livingEntity) {
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(livingEntity, null, this, getOwner());
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        if (noDamageCooldown()) {
            livingEntity.invulnerableTime = 0;
        }
    }

    public void spawnConeParticles(int lifeTick) {
        Entity owner = this.getOwner();
        if (owner == null) return;
        if (getTrailParticle() == null) return;

        Vec3 casterPos = owner.position();
        Vec3 forward = owner.getViewVector(1.0f);
        if (forward.lengthSqr() < 1.0E-6) forward = new Vec3(0, 0, 1);
        forward = forward.normalize();

        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(worldUp).normalize();
        if (right.lengthSqr() < 1.0E-6) right = new Vec3(1, 0, 0);
        Vec3 up = right.cross(forward).normalize();

        Vec3 origin = casterPos
                .add(0, owner.getBbHeight() * 0.7, 0)
                .add(forward.scale(0.4));

        float range = getMaxReach();
        float halfAngleRad = (float) Math.toRadians(getSpellAngle() * 0.5f);
        float baseRadius = range * (float) Math.tan(halfAngleRad);

        int start = getStripeStart();
        int stop = getStripeStop();
        float progress = (lifeTick - start) / (float) (stop - start);

        int totalParticles = getParticleCount();
        RandomSource random = this.random;

        float speedScale = 0.02f + getSpellLevel() * 0.005f;

        for (int i = 0; i < totalParticles; i++) {
            float depth = Mth.clamp(progress + (random.nextFloat() - 0.5f) * 0.3f, 0f, 1f);
            float actualDist = depth * range;
            float radiusAtDepth = baseRadius * depth;
            float angleRad = random.nextFloat() * 2 * (float) Math.PI;
            float distFromCenter = radiusAtDepth * (float) Math.sqrt(random.nextFloat());

            Vec3 radialDir = right.scale(Mth.cos(angleRad)).add(up.scale(Mth.sin(angleRad)));
            Vec3 pos = origin
                    .add(forward.scale(actualDist))
                    .add(radialDir.scale(distFromCenter));

            Vec3 vel = new Vec3(
                    (random.nextFloat() - 0.5f) * speedScale,
                    (random.nextFloat() - 0.5f) * speedScale * 0.5f,
                    (random.nextFloat() - 0.5f) * speedScale
            );
            if (Main.hasLineOfSightPos(new Vec3(pos.x, pos.y, pos.z), owner)) {
                this.level().addParticle(getTrailParticle(), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            }
        }
    }

    public void spawnBoltParticles() {
        Entity owner = this.getOwner();
        if (owner == null) return;

        Vec3 casterPos = owner.position();

        Vec3 forward = owner.getViewVector(1.0f);
        if (forward.lengthSqr() < 1.0E-6) {
            forward = new Vec3(0, 0, 1);
        }
        forward = forward.normalize();

        Vec3 worldUp = new Vec3(0, 1, 0);

        Vec3 right = forward.cross(worldUp);
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1, 0, 0);
        }
        right = right.normalize();

        Vec3 up = right.cross(forward).normalize();

        Vec3 origin = casterPos
                .add(0, owner.getBbHeight() * 0.7, 0)
                .add(forward.scale(0.4));

        float range = getMaxReach();

        float halfAngleRad = (float) Math.toRadians(
                getSpellAngle() * 0.5f
        );

        float baseRadius = range * (float) Math.tan(halfAngleRad);

        int totalParticles = getParticleCount();

        RandomSource random = this.random;

        for (int i = 0; i < totalParticles; i++) {

            float depth = 0.25f + random.nextFloat() * 0.75f;
            float actualDist = depth * range;
            float radiusAtDepth = baseRadius * depth;
            float angleRad = random.nextFloat() * Mth.TWO_PI;
            float distFromCenter =
                    radiusAtDepth * (float) Math.sqrt(random.nextFloat());
            Vec3 radialDir = right
                    .scale(Mth.cos(angleRad))
                    .add(up.scale(Mth.sin(angleRad)));

            Vec3 target = origin
                    .add(forward.scale(actualDist))
                    .add(radialDir.scale(distFromCenter));

            if (!Main.hasLineOfSightPos(target, owner)) {
                continue;
            }

            Vec3 delta = target.subtract(origin);

            float[] rgb = boltParticleColor(angleRad, depth);

            float scale = 0.85f + depth * 0.35f;

            BoltParticleOptions options =
                    new BoltParticleOptions(
                            rgb[0],
                            rgb[1],
                            rgb[2],
                            scale,
                            (float) delta.x,
                            (float) delta.y,
                            (float) delta.z
                    );

            this.level().addParticle(
                    options,
                    origin.x,
                    origin.y,
                    origin.z,
                    0,
                    0,
                    0
            );
        }
    }
    // 环带取色：默认走 beamLight I/II/III 三段插值，子类可覆盖（例如按角度排彩虹）
    public int stripColor(float t) {
        return packRgb(gradientColor(beamLightI(), beamLightII(), beamLightIII(), t));
    }

    // bolt 粒子取色：默认按深度，子类可覆盖（例如按环绕角排彩虹）
    public float[] boltParticleColor(float angleRad, float depth) {
        return gradientColor(beamLightI(), beamLightII(), beamLightIII(), depth);
    }

    protected static int packRgb(float[] rgb) {
        int r = (int) (Mth.clamp(rgb[0], 0f, 1f) * 255f);
        int g = (int) (Mth.clamp(rgb[1], 0f, 1f) * 255f);
        int b = (int) (Mth.clamp(rgb[2], 0f, 1f) * 255f);
        return r << 16 | g << 8 | b;
    }

    public static float[] gradientColor(int c1, int c2, int c3, float t) {
        int r, g, b;
        if (t < 0.5f) {
            float lt = t / 0.5f;
            r = (int) Mth.lerp(lt, (c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF);
            g = (int) Mth.lerp(lt, (c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF);
            b = (int) Mth.lerp(lt, c1 & 0xFF, c2 & 0xFF);
        } else {
            float lt = (t - 0.5f) / 0.5f;
            r = (int) Mth.lerp(lt, (c2 >> 16) & 0xFF, (c3 >> 16) & 0xFF);
            g = (int) Mth.lerp(lt, (c2 >> 8) & 0xFF, (c3 >> 8) & 0xFF);
            b = (int) Mth.lerp(lt, c2 & 0xFF, c3 & 0xFF);
        }
        return new float[]{r / 255f, g / 255f, b / 255f};
    }
    public int getStripeStart() {
        return 3;
    }
    public int getStripeStop() {
        return 12;
    }
    public int getUseLife() {
        return 10;
    }

    //
    public int getParticleCount() {
        return 4;
    }
    public float getSpellAngle() { return 22.5f; }
    public int getStripCount() { return 10; }
    public float getInnerCircleRadius() { return 0.5f; }
    public float getOuterCircleRadius() { return 0.25f; }
    public float getRingSpawnRadiusMult() { return 1.6f; }
    public float getPointRadiusMul() { return 0.08f; }
    public int getCircleStart() { return 5; }
    public int getCircleStop() { return getUseLife(); }
    public float computeRingRadiusMul(float life) {
        int circleStart = getCircleStart();
        int circleStop = getCircleStop();
        if (life <= circleStart) {
            float t = Mth.clamp(life / Math.max(1, circleStart), 0f, 1f);
            return Mth.lerp(easeOutQuad(t), getRingSpawnRadiusMult(), 1.0f);
        } else if (life <= circleStop) {
            float t = Mth.clamp((life - circleStart) / Math.max(1, (circleStop - circleStart)), 0f, 1f);
            return Mth.lerp(easeInQuad(t), 1.0f, getPointRadiusMul());
        }
        return getPointRadiusMul();
    }
    private static float easeOutQuad(float t) { return 1.0f - (1.0f - t) * (1.0f - t); }
    private static float easeInQuad(float t) { return t * t; }
    //
    public boolean noDamageCooldown() {
        return true;
    }
    public boolean showParticle() {
        return false;
    }
    protected boolean shouldTail() {
        return false;
    }
    public int beamLightI() {
        return 0xff7f00;
    }
    public int beamLightII() {
        return 0xffed00;
    }
    public int beamLightIII() {
        return 0xff1500;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
    }
    public ResourceLocation TextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }
    @Override
    public int getMaxLife() {
        return 20;
    }
    public int getMaxReach() {
        return 9;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
}
