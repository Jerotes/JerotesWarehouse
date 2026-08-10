package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.Interface.FactionEntity;
import com.jerotes.jerotes.entity.MagicSummoned.IronGolem.JerotesIronGoelmEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem implements FactionEntity, ContractableEntity {
    protected IronGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getFirstFactionTypeName() {
        return "villager";
    }
    @Override
    public List<String> getFactionTypeUntilTame() {
        List<String> list = new ArrayList<>();
        list.add(getFirstFactionTypeName());
        return list;
    }

    public boolean isCanContract() {
        return this.getType() == EntityType.IRON_GOLEM;
    }
    public int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    public int contractShouldBaseLevel() {
        return 5;
    }
    public boolean contractUse(LivingEntity livingEntity) {
        JerotesIronGoelmEntity jerotesIronGoelm = this.convertTo(JerotesEntityType.JEROTES_IRON_GOLEM.get(), true);
        ForgeEventFactory.onLivingConvert(this, jerotesIronGoelm);
        if (jerotesIronGoelm != null) {
            jerotesIronGoelm.tame(livingEntity);
            jerotesIronGoelm.setNotPersistent(false);
            jerotesIronGoelm.setLifeTick(0);
        }
        return true;
    }
}