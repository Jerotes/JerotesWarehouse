package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.entity.Interface.AnesthetizedAttackEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnestheticArrowEntity extends BaseArrowEntity implements AnesthetizedAttackEntity {
    public static final ItemStack item = new ItemStack(JerotesItems.ANESTHETIC_ARROW.get());
    private static final EntityType<AnestheticArrowEntity> type = JerotesEntityType.ANESTHETIC_ARROW.get();

    public AnestheticArrowEntity(EntityType<? extends AnestheticArrowEntity> entityType, Level level) {
        super(entityType, level, item, 2.0);
    }
    public AnestheticArrowEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
        super(level, livingEntity, itemStack, type, 2.0);
    }
    public AnestheticArrowEntity(Level level, double d, double d2, double d3, ItemStack itemStack) {
        super(level, d, d2, d3, itemStack, type, 2.0);
    }

    @Override
    public int getAnesthetized() {
        return 180;
    }
}


