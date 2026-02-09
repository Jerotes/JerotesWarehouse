package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin extends BlockEntity {
    public ConduitBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "applyEffects", at = @At(value = "HEAD"))
    private static void applyEffects(Level p_155444_, BlockPos p_155445_, List<BlockPos> p_155446_, CallbackInfo ci) {
        int i = p_155446_.size();
        int j = i / 7 * 16;
        int k = p_155445_.getX();
        int l = p_155445_.getY();
        int i1 = p_155445_.getZ();
        AABB aabb = (new AABB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).inflate((double)j).expandTowards(0.0D, (double)p_155444_.getHeight(), 0.0D);
        List<LivingEntity> list = p_155444_.getEntitiesOfClass(LivingEntity.class, aabb);
        list.removeIf(livingEntity -> !(livingEntity instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.useBeaconAsPlayer()));
        if (!list.isEmpty()) {
            for(LivingEntity player : list) {
                if (p_155445_.closerThan(player.blockPosition(), (double)j) && player.isInWaterOrRain()) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
                }
            }
        }
    }
}