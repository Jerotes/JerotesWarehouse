package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MagicBonfireEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout, ConcentrationSpellEntity {
    private static final EntityDataAccessor<Boolean> IS_SOUL_FIRE = SynchedEntityData.defineId(MagicBonfireEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COOK_COOLDOWN = SynchedEntityData.defineId(MagicBonfireEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(MagicBonfireEntity.class, EntityDataSerializers.INT);

    public MagicBonfireEntity(EntityType<? extends MagicBonfireEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public MagicBonfireEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.MAGIC_BONFIRE.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public int getMaxStart() {
        return 20;
    }
    public boolean isPickable() {
        return true;
    }
    public boolean canBeHitByProjectile() {
        return false;
    }
    public int getMaxLife() {
        return 60 * 20;
    }
    @Override
    public boolean canCollideWith(Entity entity) {
        return this.isAlive();
    }
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!this.isAlive()) {
            return super.interact(player, interactionHand);
        }
        if (this.getCookCooldown() == 0) {
            ItemStack handItem = player.getItemInHand(interactionHand);
            if (!handItem.isEmpty()) {
                SimpleContainer container = new SimpleContainer(handItem.copy());
                Optional<CampfireCookingRecipe> recipeOpt = this.level().getRecipeManager()
                        .getRecipeFor(RecipeType.CAMPFIRE_COOKING, container, this.level());
                if (recipeOpt.isPresent()) {
                    CampfireCookingRecipe recipe = recipeOpt.get();
                    ItemStack output = recipe.assemble(container, this.level().registryAccess());
                    if (!output.isEmpty()) {
                        if (!player.getAbilities().instabuild) {
                            handItem.shrink(1);
                        }
                        this.spawnAtLocation(output);
                        int cookingTime = recipe.getCookingTime();
                        this.setCookCooldown(cookingTime / 5);
                        this.playSound(SoundEvents.CAMPFIRE_CRACKLE, 0.5F, 1.0F);
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 20; i++) {
                                double x = this.getX() + (this.getRandom().nextDouble() - 0.5);
                                double y = this.getY() + 0.5 + this.getRandom().nextDouble() * 0.8;
                                double z = this.getZ() + (this.getRandom().nextDouble() - 0.5);
                                serverLevel.sendParticles(this.isSoulFire() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, x, y, z, 1, 0, 0.02, 0, 0);
                                if (i % 2 == 0) {
                                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 0.3, z, 1, 0, 0.03, 0, 0);
                                }
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.interact(player, interactionHand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.setCookCooldown(Math.max(0, this.getCookCooldown() - 1));
            this.setAttackTick(Math.max(0, this.getAttackTick() - 1));
        }
        if (this.getCookCooldown() > 0) {
            if (this.getCookCooldown() == 1) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 20; i++) {
                        double x = this.getX() + (this.getRandom().nextDouble() - 0.5);
                        double y = this.getY() + 0.5 + this.getRandom().nextDouble() * 0.8;
                        double z = this.getZ() + (this.getRandom().nextDouble() - 0.5);
                        serverLevel.sendParticles(this.isSoulFire() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, x, y, z, 1, 0, 0.02, 0, 0);
                        if (i % 2 == 0) {
                            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 0.3, z, 1, 0, 0.03, 0, 0);
                        }
                    }
                }
            }
        }
        if (this.level().isClientSide()) {
            if (this.tickCount % 10 == 0 && this.random.nextInt(10) == 0) {
                this.level().playLocalSound(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5,
                        SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                        0.5F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.6F, false);
            }
            if (this.random.nextInt(5) == 0) {
                int count = this.random.nextInt(1) + 1;
                for (int i = 0; i < count; i++) {
                    double px = this.getX() + (this.random.nextFloat() - 0.5) * 0.8;
                    double py = this.getY() + 0.5 + this.random.nextFloat() * 0.3;
                    double pz = this.getZ() + (this.random.nextFloat() - 0.5) * 0.8;
                    double vx = this.random.nextFloat() / 2.0;
                    double vy = 5.0E-5;
                    double vz = this.random.nextFloat() / 2.0;
                    this.level().addParticle(ParticleTypes.LAVA, px, py, pz, vx, vy, vz);
                }
            }
            boolean hasHayBelow = this.level().getBlockState(this.blockPosition().below()).is(Blocks.HAY_BLOCK);
            if (this.random.nextFloat() < 0.11F) {
                int times = this.random.nextInt(2) + 2;
                for (int i = 0; i < times; i++) {
                    ParticleOptions smokeParticle = hasHayBelow ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                    double px1 = this.getX() + this.random.nextDouble() / 3.0 * (this.random.nextBoolean() ? 1 : -1);
                    double py1 = this.getY() + this.random.nextDouble() + this.random.nextDouble();
                    double pz1 = this.getZ() + this.random.nextDouble() / 3.0 * (this.random.nextBoolean() ? 1 : -1);
                    this.level().addAlwaysVisibleParticle(smokeParticle, true, px1, py1, pz1, 0.0, 0.07, 0.0);
                    double px2 = this.getX() + this.random.nextDouble() / 4.0 * (this.random.nextBoolean() ? 1 : -1);
                    double py2 = this.getY() + 0.4;
                    double pz2 = this.getZ() + this.random.nextDouble() / 4.0 * (this.random.nextBoolean() ? 1 : -1);
                    this.level().addParticle(ParticleTypes.SMOKE, px2, py2, pz2, 0.0, 0.005, 0.0);
                }
            }
            if (this.getCookCooldown() > 0 && this.random.nextFloat() < 0.2F) {
                Direction dir = Direction.from2DDataValue(this.random.nextInt(4));
                float offset = 0.3125F;
                double px = this.getX() - (float) dir.getStepX() * offset + (float) dir.getClockWise().getStepX() * offset;
                double py = this.getY() + 0.5;
                double pz = this.getZ() - (float) dir.getStepZ() * offset + (float) dir.getClockWise().getStepZ() * offset;
                for (int i = 0; i < 4; i++) {
                    this.level().addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 5.0E-4, 0.0);
                }
            }
        }
        if (this.getLifeTick() % 10 == 0 && this.getLifeTick() > 20) {
            boolean bl = false;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().deflate(Main.mobWidth(this) / 2, Main.mobHeight(this) / 2, Main.mobWidth(this) / 2).inflate(1.5f,1.5f,1.5f).move(0,1.5f, 0));
            for (LivingEntity livingEntity : list) {
                if (this.dealDamageTo(livingEntity)) {
                    bl = true;
                }
            }
            if (bl) {
                if (!this.level().isClientSide()) {
                    this.setAttackTick(10);
                }
                this.playSound(SoundEvents.CAMPFIRE_CRACKLE);
            }
        }
        //额外清除
        if (this.getLifeTick() >= getMaxLife()) {
            this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.setTarget(null);
            this.discard();
        }
    }

    public boolean dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return false;
        }
        if (AttackFind.FindCanNotAttack(this.getOwner(), livingEntity))
            return false;
        DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_AND_NO_KNOCKBACK_MELEE, this, getOwner());
        if (livingEntity2 == null) {
            damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_AND_NO_KNOCKBACK_MELEE, this);
        }
        float damage = (getSpellLevel() > 1 && getSpellLevel() % 2 == 1) ?
                        Main.rollDice(1 + (getSpellLevel() - 2) / 2, 8, livingEntity.getRandom()) + Main.rollDice(1, 8, livingEntity.getRandom()) * 0.5f :
                        Main.rollDice(1 + (getSpellLevel() - 1) / 2, 8, livingEntity.getRandom());
        boolean bl = livingEntity.hurt(damageSource,
                damage / 12f
        );
        if (bl) {
            livingEntity.setSecondsOnFire(6 + getSpellLevel() * 3);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; i++) {
                    serverLevel.sendParticles(this.isSoulFire() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
        return true;
    }

    public ResourceLocation getTextureLocation() {
        int n = ((int) this.level().getGameTime()) % 16 / 2;
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/magic_bonfire/magic_" + (this.isSoulFire() ? "soul_" : "") + "bonfire_" + n + ".png");
    }
    public ResourceLocation getOtherTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/magic_bonfire/magic_" + (this.isSoulFire() ? "soul_" : "") + "bonfire.png");
    }


    public void setSoulFire(boolean bl) {
        this.getEntityData().set(IS_SOUL_FIRE, bl);
    }
    public boolean isSoulFire() {
        return this.getEntityData().get(IS_SOUL_FIRE);
    }
    public int getCookCooldown() {
        return this.getEntityData().get(COOK_COOLDOWN);
    }
    public void setCookCooldown(int cooldown) {
        this.getEntityData().set(COOK_COOLDOWN, cooldown);
    }
    public void setAttackTick(int n){
        this.getEntityData().set(ATTACK_TICK, n);
    }
    public int getAttackTick(){
        return this.getEntityData().get(ATTACK_TICK);
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("IsSoulFire", this.isSoulFire());
        compoundTag.putInt("CookCooldown", this.getCookCooldown());
        compoundTag.putInt("AttackTick", this.getAttackTick());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSoulFire(compoundTag.getBoolean("IsSoulFire"));
        this.setCookCooldown(compoundTag.getInt("CookCooldown"));
        this.setAttackTick(compoundTag.getInt("AttackTick"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(IS_SOUL_FIRE, false);
        this.getEntityData().define(COOK_COOLDOWN, 0);
        this.getEntityData().define(ATTACK_TICK, 0);
    }

    @Override
    public boolean endConcentration() {
        if (getOwner() != null) {
            SpellType.setConcentrationSpellTypeJerotes(getOwner(), "");
            if (getOwner() instanceof Player player) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setConcentrationSpellTarget("");
                    capability.syncPlayerVariables(player);
                });
            }
            SpellType.setConcentrationSpellTargetJerotes(getOwner(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
            SpellType.endConcentrationMessage(getOwner(), SpellType.JEROTES_CREATE_BONFIRE.magicSpellGet(1,null,null).getSpellName());
        }
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 10));
        }
        return true;
    }
}

