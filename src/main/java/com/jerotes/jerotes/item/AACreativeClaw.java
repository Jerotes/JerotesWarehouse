package com.jerotes.jerotes.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AACreativeClaw extends Item {
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;
	public AACreativeClaw() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 10000000000f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", 10000000000f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID.fromString("e91aa0ff-a0bb-4804-8bee-76f40741a61e"), "Tool modifier", 10000000000f, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (!entity.level().isClientSide) {
			if (entity instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.MORE_TOUCH.get(), 205, 5, false, false));
			}
		}
	}
	public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
		return false;
	}


	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			return this.defaultModifiers;
		}
		if (equipmentSlot == EquipmentSlot.OFFHAND) {
			return this.defaultModifiers;
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		player.startUsingItem(interactionHand);
		return InteractionResultHolder.consume(itemStack);

	}

	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}
		int n2 = this.getUseDuration(itemStack) - n;
		int spellLevel = 2;
		int num = 3;

		if (Main.getTargetedEntity(player, 256) != null
				&& Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
			//SpellFind.CogonSword(player, livingEntity2, 6, 12, 32);
		}

		if (level instanceof ServerLevel serverLevel) {
			Main.spawnUnevenBlockByPos(serverLevel, livingEntity.getOnPos(), 4);
		}
		//法术列表
		//SpellList.MagicMissile(3, livingEntity, null, true).spellUse();

		//SpellEvent.EarthquakeKick(player, 12, 6, 30, 20);
		//SpellEvent.DisarmDrag(player, player, 0, 1, 0, false);
		//SpellEvent.UncleanBloodFog(livingEntity, livingEntity, 16, 12, 12, 12, 12, 8);
		//SpellEvent.UncleanBloodRain(livingEntity, livingEntity, 16, spellLevel, spellLevel, 1, 4);
		//SpellEvent.RayofEnfeeblement(livingEntity, livingEntity, spellLevel * 6, spellLevel, 0, 1, 5, true);
		//SpellEvent.ElasticLightBall(livingEntity, livingEntity, 2, 1.2f, 0, 3, 5, true);
		//SpellEvent.ZsieinSonic(livingEntity, livingEntity, 0, 1, 0, true);
		player.awardStat(Stats.ITEM_USED.get(this));
	}

	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		//
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);

		if (entity instanceof Player player && !player.isShiftKeyDown() &&
				Main.getTargetedEntity(player, 256) != null &&
				Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
			if (player.level() instanceof ServerLevel serverLevel) {
				Vec3 vec3 = player.getEyePosition();
				Vec3 vec32 = livingEntity2.getEyePosition().subtract(vec3);
				Vec3 vec33 = vec32.normalize();
				for (int i = 1; i < Mth.floor(vec32.length()) * 6 + 1; ++i) {
					Vec3 vec34 = vec3.add(vec33.scale(i / 6f));
					serverLevel.sendParticles(ParticleTypes.ASH, vec34.x, vec34.y, vec34.z, 1, 0.0, 0.0, 0.0, 0.0);
				}
			}

			DamageSource damageSources = new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL), player);
			livingEntity2.hurt(damageSources, Float.MAX_VALUE);
			if (!livingEntity2.level().isClientSide()) {
				livingEntity2.getPersistentData().putDouble("jerotesvillage_variant_zsiein_discard", 666666);
			}
		}
		entity.swinging = false;
		return retval;
	}
}
