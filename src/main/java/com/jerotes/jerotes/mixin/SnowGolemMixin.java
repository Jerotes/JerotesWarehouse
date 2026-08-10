package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.MagicSummoned.SnowGolem.JerotesSnowGoelmEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends AbstractGolem implements ContractableEntity {
    protected SnowGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    public boolean isCanContract() {
        return this.getType() == EntityType.SNOW_GOLEM;
    }
    public int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    public int contractShouldBaseLevel() {
        return 5;
    }
    public boolean contractUse(LivingEntity livingEntity) {
        JerotesSnowGoelmEntity jerotesSnowGoelm = this.convertTo(JerotesEntityType.JEROTES_SNOW_GOLEM.get(), true);
        ForgeEventFactory.onLivingConvert(this, jerotesSnowGoelm);
        if (jerotesSnowGoelm != null) {
            jerotesSnowGoelm.tame(livingEntity);
            jerotesSnowGoelm.setNotPersistent(false);
            jerotesSnowGoelm.setLifeTick(0);
        }
        return true;
    }
}