package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(Raider.class)
public abstract class RaiderMixin extends PatrollingMonster implements FactionEntity {
    protected RaiderMixin(EntityType<? extends PatrollingMonster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getFirstFactionTypeName() {
        return "raider";
    }
    @Override
    public List<String> getFactionTypeUntilTame() {
        List<String> list = new ArrayList<>();
        list.add(getFirstFactionTypeName());
        return list;
    }
}