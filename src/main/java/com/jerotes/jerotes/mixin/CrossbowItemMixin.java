package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.UseCrossbowEntity;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.Interface.NotNormalCrossbow;
import com.jerotes.jerotes.item.Tool.ItemToolBaseCrossbow;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ProjectileWeaponItem {

    @Shadow
    private static void addChargedProjectile(ItemStack p_40929_, ItemStack p_40930_) {
    }

    @Shadow
    public static void performShooting(Level p_40888_, LivingEntity p_40889_, InteractionHand p_40890_, ItemStack p_40891_, float p_40892_, float p_40893_) {
    }

    @Shadow
    public static boolean isCharged(ItemStack p_40933_) {
        return false;
    }

    @Unique
    private static AbstractArrow jerotes_villager_$getArrow(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2) {
        ArrowItem arrowitem = (ArrowItem) (itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemStack2, livingEntity);
        if (livingEntity instanceof Player) {
            abstractarrow.setCritArrow(true);
        }

        abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        abstractarrow.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, itemStack);
        if (i > 0) {
            abstractarrow.setPierceLevel((byte) i);
        }

        return abstractarrow;
    }

    public CrossbowItemMixin(Properties properties) {
        super(properties);
    }


    @Inject(method = "shootProjectile", at = @At(value = "HEAD"), cancellable = true)
    private static void shootProjectile(Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float f2, float f3, float f4, CallbackInfo ci) {
        if (itemStack.getItem() instanceof ItemToolBaseCrossbow shrapnelLauncher) {
            shrapnelLauncher.customShootProjectile(level, livingEntity, interactionHand, itemStack, itemStack2, f, bl, f2, f3, f4);
        }

        //消耗
        if (livingEntity instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities) {
            if (!level.isClientSide) {
                boolean flag = itemStack2.is(Items.FIREWORK_ROCKET);
                Projectile projectile;
                if (flag) {
                    projectile = new FireworkRocketEntity(level, itemStack2, livingEntity, livingEntity.getX(), livingEntity.getEyeY() - (double) 0.15F, livingEntity.getZ(), true);
                } else {
                    projectile = jerotes_villager_$getArrow(level, livingEntity, itemStack, itemStack2);
                    if (bl || f4 != 0.0F) {
                        ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                }

                if (livingEntity instanceof CrossbowAttackMob crossbowattackmob) {
                    if (itemStack.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow && itemToolBaseCrossbow.useBaseShootArrow() && livingEntity.getRandom().nextFloat() <= itemToolBaseCrossbow.customBaseShootArrowChance(livingEntity)
                            && projectile.getType() == EntityType.ARROW && projectile instanceof Arrow arrow && PotionUtils.getPotion(itemStack2) == Potions.EMPTY) {
                        projectile = itemToolBaseCrossbow.customBaseShootArrow(livingEntity, itemStack);
                        if (bl || f4 != 0.0F) {
                            ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }
                    }
                    //背包箭矢
                    crossbowattackmob.shootCrossbowProjectile(Objects.requireNonNull(crossbowattackmob.getTarget()), itemStack, projectile, f4);
                }
                else {
                    Vec3 vec3 = livingEntity.getUpVector(1.0F);
                    Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(f4 * ((float) Math.PI / 180F), vec3.x, vec3.y, vec3.z);
                    Vec3 vec31 = livingEntity.getViewVector(1.0F);
                    Vector3f vector3f = vec31.toVector3f().rotate(quaternionf);
                    projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), f2, f3);
                }
                if (JerotesGameRules.JEROTES_RANGE_CAN_BREAK != null && livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_RANGE_CAN_BREAK)) {
                    itemStack.hurtAndBreak(flag ? 3 : 1, livingEntity, (players) -> {
                        players.broadcastBreakEvent(interactionHand);
                    });
                }
                level.addFreshEntity(projectile);
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, f);
            }
            ci.cancel();
        }
    }

    @Inject(method = "loadProjectile", at = @At(value = "HEAD"), cancellable = true)
    private static void loadProjectile(LivingEntity p_40863_, ItemStack p_40864_, ItemStack itemStack1, boolean p_40866_, boolean p_40867_, CallbackInfoReturnable<Boolean> cir) {
        if (p_40863_ instanceof UseCrossbowEntity && !MainConfig.MobUseCrossbowShrinkArrow) {
            if (itemStack1.isEmpty()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
            else {
                boolean flag = p_40867_ && itemStack1.getItem() instanceof ArrowItem;
                ItemStack itemStack2;
                if (!flag && !p_40867_ && !p_40866_) {
                    int i = Math.min(1, itemStack1.getCount());
                    itemStack2 = itemStack1.copyWithCount(i);
                    if (itemStack1.isEmpty() && p_40863_ instanceof Player) {
                        ((Player) p_40863_).getInventory().removeItem(itemStack1);
                    }
                } else {
                    itemStack2 = itemStack1.copy();
                }
                addChargedProjectile(p_40864_, itemStack2);
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() instanceof ItemToolBaseCrossbow crossbow && crossbow.useCustomUse()) {
            cir.setReturnValue(crossbow.customUse(level, player, interactionHand));
        }
        if (itemStack.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow) {
            if (isCharged(itemStack)) {
                performShooting(level, player, interactionHand, itemStack, itemToolBaseCrossbow.getShootingPower(itemStack), itemToolBaseCrossbow.getArrowInaccuracy());
                if (ItemToolBaseCrossbow.getBullet(itemStack) > 1) {
                    ItemToolBaseCrossbow.setBullet(itemStack, ItemToolBaseCrossbow.getBullet(itemStack) - 1);
                    int n = itemToolBaseCrossbow.mobUseCooldownTick(itemStack);
                    if (n > 4) {
                        player.getCooldowns().addCooldown(itemStack.getItem(), n);
                    }
                }
                else {
                    ItemToolBaseCrossbow.setBullet(itemStack,0);
                    ItemToolBaseCrossbow.setCharged(itemStack, false);
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }

    @Inject(method = "clearChargedProjectiles", at = @At(value = "HEAD"), cancellable = true)
    private static void clearChargedProjectiles(ItemStack itemStack, CallbackInfo ci) {
        if (itemStack.getItem() instanceof ItemToolBaseCrossbow) {
            if (ItemToolBaseCrossbow.getBullet(itemStack) > 1) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "appendHoverText", at = @At(value = "HEAD"), cancellable = true)
    private void appendHoverText(ItemStack itemStack, Level p_40881_, List<Component> p_40882_, TooltipFlag p_40883_, CallbackInfo ci) {
        if (itemStack.getItem() instanceof NotNormalCrossbow) {
            ci.cancel();
        }
    }

}