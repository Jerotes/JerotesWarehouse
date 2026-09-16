package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeLightningBolt;
import com.jerotes.jerotes.entity.Other.Other.InvisibleLightningBolt;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin extends Entity implements JerotesChangeLightningBolt {
    @Shadow
    private int life;

    @Shadow
    private int flashes;

    @Shadow
    @Final
    private Set<Entity> hitEntities;

    @Shadow
    public long seed;

    public LightningBoltMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Unique
    public boolean isCanNotSeeJerotes = false;
    public boolean isCanNotSeeJerotes() {
        return isCanNotSeeJerotes;
    }
    public void setCanNotSeeJerotes(boolean bl) {
        isCanNotSeeJerotes = bl;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof InvisibleLightningBolt) {
            super.tick();
            --this.life;
            if (this.life < 0) {
                if (this.flashes == 0) {
                    if (this.level() instanceof ServerLevel) {
                        List<Entity> list = this.level().getEntities(this, new AABB(this.getX() - 15.0D, this.getY() - 15.0D, this.getZ() - 15.0D, this.getX() + 15.0D, this.getY() + 6.0D + 15.0D, this.getZ() + 15.0D), (p_147140_) -> {
                            return p_147140_.isAlive() && !this.hitEntities.contains(p_147140_);
                        });

                        for (ServerPlayer serverplayer : ((ServerLevel) this.level()).getPlayers((p_147157_) -> {
                            return p_147157_.distanceTo(this) < 256.0F;
                        })) {
                            CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverplayer, (LightningBolt) (Object) this, list);
                        }
                    }
                    this.discard();
                } else if (this.life < -this.random.nextInt(10)) {
                    --this.flashes;
                    this.life = 1;
                    this.seed = this.random.nextLong();
                }
                ci.cancel();
            }
        }
    }
}