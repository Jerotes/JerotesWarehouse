package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem implements FactionEntity {
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
}