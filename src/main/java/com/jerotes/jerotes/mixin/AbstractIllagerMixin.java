package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin extends Raider implements FactionEntity {
    protected AbstractIllagerMixin(EntityType<? extends Raider> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getFirstFactionTypeName() {
        return "illager";
    }
    @Override
    public List<String> getFactionTypeUntilTame() {
        List<String> list = new ArrayList<>();
        list.add(getFirstFactionTypeName());
        list.add("raider");
        return list;
    }
}