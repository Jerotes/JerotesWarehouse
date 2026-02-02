package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBasePincer extends ItemToolBaseShears {
    public ItemToolBasePincer(Properties properties, float damage, float speed) {
        super(properties, damage, speed);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if (blockState.is(TagKey.create(Registries.BLOCK, new ResourceLocation(JerotesWarehouse.MODID, "pincer_breakable")))) {
            return 35.0f;
        }
        return blockState.is(BlockTags.SWORD_EFFICIENT) ? 1.5f : 1.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockState) {
        if (blockState.is(TagKey.create(Registries.BLOCK, new ResourceLocation(JerotesWarehouse.MODID, "pincer_breakable")))) {
            return true;
        }
        return super.isCorrectToolForDrops(blockState);
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(1, livingEntity, (player) -> {
                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.pincer").withStyle(ChatFormatting.YELLOW));
    }
}

