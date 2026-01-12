package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity {
    public BeaconBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "applyEffects", at = @At(value = "HEAD"), cancellable = true)
    private static void applyEffects(Level p_155098_, BlockPos p_155099_, int p_155100_, MobEffect p_155101_, MobEffect p_155102_, CallbackInfo ci) {
        if (!p_155098_.isClientSide && p_155101_ != null) {
            double d0 = (double)(p_155100_ * 10 + 10);
            int i = 0;
            if (p_155100_ >= 4 && p_155101_ == p_155102_) {
                i = 1;
            }

            int j = (9 + p_155100_ * 2) * 20;
            AABB aabb = (new AABB(p_155099_)).inflate(d0).expandTowards(0.0, (double)p_155098_.getHeight(), 0.0);
            List<LivingEntity> list = p_155098_.getEntitiesOfClass(LivingEntity.class, aabb);
            list.removeIf(livingEntity -> !(livingEntity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useBeaconAsPlayer()));
            Iterator var11 = list.iterator();

            LivingEntity player1;
            while(var11.hasNext()) {
                player1 = (LivingEntity)var11.next();
                player1.addEffect(new MobEffectInstance(p_155101_, j, i, true, true));
            }

            if (p_155100_ >= 4 && p_155101_ != p_155102_ && p_155102_ != null) {
                var11 = list.iterator();

                while(var11.hasNext()) {
                    player1 = (LivingEntity)var11.next();
                    player1.addEffect(new MobEffectInstance(p_155102_, j, 0, true, true));
                }
            }
        }

    }
}