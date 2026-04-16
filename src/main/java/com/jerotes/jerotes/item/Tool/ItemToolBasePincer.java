package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
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
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
    public boolean mineBlock(ItemStack p_43078_, Level p_43079_, BlockState p_43080_, BlockPos p_43081_, LivingEntity p_43082_) {
        if (!p_43079_.isClientSide && !p_43080_.is(BlockTags.FIRE)) {
            p_43078_.hurtAndBreak(1, p_43082_, (p_43076_) -> {
                p_43076_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return p_43080_.is(BlockTags.LEAVES) || p_43080_.is(Blocks.COBWEB) || p_43080_.is(Blocks.GRASS) || p_43080_.is(Blocks.FERN) || p_43080_.is(Blocks.DEAD_BUSH) || p_43080_.is(Blocks.HANGING_ROOTS) || p_43080_.is(Blocks.VINE) || p_43080_.is(Blocks.TRIPWIRE) || p_43080_.is(BlockTags.WOOL) || super.mineBlock(p_43078_, p_43079_, p_43080_, p_43081_, p_43082_);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
            return this.isMeleeWeapon();
        }
        if (enchantment instanceof SweepingEdgeEnchantment) {
            return this instanceof ItemTwoHanded;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
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

