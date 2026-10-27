package com.jerotes.jerotes.entity.Other.BreathAreaSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.particle.BoltParticleOptions;
import com.jerotes.jerotes.entity.Other.OtherSpell.OtherSpellBaseEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.List;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class BaseBreathAreaEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public BaseBreathAreaEntity(EntityType<? extends BaseBreathAreaEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() { return 1; }

    @Override
    public int getMaxLife() { return 40; }
    public int getCircleStart() { return 5; }
    public int getCircleStop() { return getHurtTime(); }
    public int getHurtTime() { return 10; }
    public int getBallStopStripeStart() {
        return 8;
    }
    public int getStripeStop() {
        int level = getSpellLevel();
        if (level <= 3) return 14;
        if (level <= 6) return 17;
        if (level <= 8) return 20;
        return 24;
    }

    public float getInnerCircleRadius() { return 1.0f; }
    public float getOuterCircleRadius() { return 0.75f; }

    public float getRingSpawnRadiusMult() { return 1.6f; }
    public float getPointRadiusMul() { return 0.08f; }

    public ParticleOptions getParticle() {
        return JerotesParticleTypes.POISON_BREATH.get();
    }

    public int beamLightI() { return 0x0b440e; }
    public int beamLightII() { return 0x1a751f; }
    public int beamLightIII() { return 0x063009; }

    public float getMaxReach() {
        int level = getSpellLevel();
        if (level <= 3) return 4.5f;
        if (level <= 6) return 9.0f;
        if (level <= 8) return 18.0f;
        return 27.0f;
    }
    public int getParticleCount() {
        int level = getSpellLevel();
        if (level <= 3) return 40;
        if (level <= 6) return 80;
        if (level <= 8) return 160;
        return 240;
    }

    public float getSpellAngle() { return 55.0f; }
    public int getStripCount() { return 40; }


    public boolean moveToTarget() { return true; }


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

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getLifeTick() == 10) {
                LivingEntity owner = this.getOwner();
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
                            this.dealDamageTo(livingEntity);
                        } else if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity livingEntity) {
                            this.dealDamageTo(livingEntity);
                        }
                    }
                }
            }
        }

        if (this.level().isClientSide()) {
            int lifeTick = this.getLifeTick();

            if (lifeTick >= getBallStopStripeStart() && lifeTick < getStripeStop()) {
                spawnConeParticles(lifeTick);
            }

            if (lifeTick == getBallStopStripeStart() || lifeTick == getBallStopStripeStart() + 1 || lifeTick == getBallStopStripeStart() + 2 || lifeTick == getBallStopStripeStart() + 3) {
                spawnBoltParticles();
            }
        }
    }

    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this);
        if (livingEntity2 != null) {
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.POISON, this, livingEntity2);
        }
        if (livingEntity2 != null && AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
            return false;
        }
        if (!EntityAndItemFind.savingSpellThrow(livingEntity)) {
            boolean bl = livingEntity.hurt(damageSource, Main.rollDice(getSpellLevel() * 2, 6, this.getRandom()));
            if (bl) {
                this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
            }
        } else {
            boolean bl = livingEntity.hurt(damageSource, Main.rollDice(getSpellLevel() * 2, 6, this.getRandom()) / 2);
            if (bl) {
                this.playSound(JerotesSoundEvents.SPELL, 1.0f, 1.0f);
            }
        }
        return true;
    }

    private void spawnConeParticles(int lifeTick) {
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

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

        int start = getBallStopStripeStart();
        int stop = getStripeStop();
        float progress = (lifeTick - start) / (float) (stop - start);

        int totalParticles = getParticleCount();
        RandomSource random = this.getRandom();

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
                this.level().addParticle(coneParticle(angleRad, depth), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            }
        }
    }

    private void spawnBoltParticles() {
        LivingEntity owner = this.getOwner();
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

        int totalParticles = getParticleCount() / 4;

        RandomSource random = this.getRandom();

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

    // 锥形彩尘取色：默认就是 getParticle()，子类可覆盖（例如按环绕角排彩虹）
    public ParticleOptions coneParticle(float angleRad, float depth) {
        return getParticle();
    }

    // bolt 粒子取色：默认按深度走 beamLight I/II/III 三段插值，子类可覆盖（例如按环绕角排彩虹）
    public float[] boltParticleColor(float angleRad, float depth) {
        return gradientColor(beamLightI(), beamLightII(), beamLightIII(), depth);
    }

    // 环带取色：默认走 beamLight I/II/III 三段插值，子类可覆盖
    public int stripColor(float t) {
        float[] rgb = gradientColor(beamLightI(), beamLightII(), beamLightIII(), t);
        int r = (int) (Mth.clamp(rgb[0], 0f, 1f) * 255f);
        int g = (int) (Mth.clamp(rgb[1], 0f, 1f) * 255f);
        int b = (int) (Mth.clamp(rgb[2], 0f, 1f) * 255f);
        return r << 16 | g << 8 | b;
    }

    private static float[] gradientColor(int c1, int c2, int c3, float t) {
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

    protected static Vec3 getLookAngle(Entity entity) {
        return entity.getViewVector(1.0f);
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }
}