package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesBlazeEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Blaze.class)
public abstract class BlazeMixin extends Monster implements ContractableEntity {
    protected BlazeMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public boolean isCanContract() {
        return this.getType() == EntityType.BLAZE;
    }
    public int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    public int contractShouldBaseLevel() {
        return 5;
    }
    public boolean contractUse(LivingEntity livingEntity) {
        JerotesBlazeEntity jerotesBlaze = this.convertTo(JerotesEntityType.JEROTES_BLAZE.get(), true);
        ForgeEventFactory.onLivingConvert(this, jerotesBlaze);
        if (jerotesBlaze != null) {
            jerotesBlaze.tame(livingEntity);
            jerotesBlaze.setNotPersistent(false);
            jerotesBlaze.setLifeTick(0);
        }
        return true;
    }
}