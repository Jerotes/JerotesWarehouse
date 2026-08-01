package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vex.class)
public abstract class VexMixin extends Monster implements ContractableEntity {
    protected VexMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    //能否契约
    public boolean isCanContract() {
        return this.getType() == EntityType.VEX;
    }
    //契约需要法术等级
    public int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    public int contractShouldBaseLevel() {
        return 5;
    }
    //契约
    public boolean contractUse(LivingEntity livingEntity) {
        JerotesVexEntity jerotesVex = this.convertTo(JerotesEntityType.JEROTES_VEX.get(), true);
        ForgeEventFactory.onLivingConvert(this, jerotesVex);
        if (jerotesVex != null) {
            jerotesVex.tame(livingEntity);
            jerotesVex.setNotPersistent(false);
            jerotesVex.setLifeTick(0);
        }
        return true;
    }
}