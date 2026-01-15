package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin<T extends Monster> extends HumanoidModel<T> {

    public AbstractZombieModelMixin(ModelPart p_170677_) {
        super(p_170677_);
    }

    @Inject(method = "setupAnim*", at = @At("HEAD"), cancellable = true)
    private void setupAnim(T t, float f, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        if (t.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear() || t.getMainHandItem().getItem() instanceof ItemToolBasePike) {
            super.setupAnim(t, f, f2, f3, f4, f5);
            ci.cancel();
        }
    }

}