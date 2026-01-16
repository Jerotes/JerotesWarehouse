package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.Arrow.ThrownBoneThrowingSpearEntity;
import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Tool.ItemToolBaseThrowingSpearOfJavelin;
import com.jerotes.jerotes.item.Tool.ItemToolBaseThrowingSpearOfSpear;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;

public class BoneThrowingSpear {
	public static class BoneThrowingSpearOfSpear extends ItemToolBaseThrowingSpearOfSpear {
		public BoneThrowingSpearOfSpear() {
			super(new Tier() {
					  @Override
					  public int getUses() {
						  return 50;
					  }
					  @Override
					  public float getSpeed() {
						  return 2.0f;
					  }
					  @Override
					  public float getAttackDamageBonus() {
						  return 6;
					  }
					  @Override
					  public int getLevel() {
						  return 0;
					  }
					  @Override
					  public int getEnchantmentValue() {
						  return 12;
					  }
					  @Override
					  public Ingredient getRepairIngredient() {
						  return Ingredient.of(new ItemStack(Items.BONE));
					  }
				  }, new Properties().stacksTo(1).rarity(Rarity.COMMON), -1f, (1.0f / 0.9f) - 4f, 0.9f,
					0.25f, 10, (int) (0.7f * 20.0f),
					Condition.ofAttackerSpeed((int) (4.5f * 20.0f), 10.0f),
					Condition.ofAttackerSpeed((int) (9.0f * 20.0f), 5.1f),
					Condition.ofRelativeSpeed((int) (13.75f * 20.0f), 4.6f),
					0.38f, 0.82f,
					JerotesSoundEvents.SPEAR_USE,
					JerotesSoundEvents.SPEAR_HIT,
					JerotesSoundEvents.SPEAR_ATTACK,
					JerotesSoundEvents.SPEAR_HIT,
					0.5f, 3f, 0.5f, 5f, 0.125f, 0.5f, true, false);
		}

		public Item getOtherMode() {
			return JerotesItems.BONE_THROWING_SPEAR_OF_JAVELIN.get();
		}

		@Override
		public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
			return itemStack2.is(Items.BONE) || super.isValidRepairItem(itemStack, itemStack2);
		}
	}

	public static class BoneThrowingSpearOfJavelin extends ItemToolBaseThrowingSpearOfJavelin {
		public BoneThrowingSpearOfJavelin() {
			super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(50), 6f, 1.1f);
		}

		@Override
		public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
			return new ThrownBoneThrowingSpearEntity(livingEntity.level(), livingEntity, itemStack);
		}

		public Item getOtherMode() {
			return JerotesItems.BONE_THROWING_SPEAR_OF_SPEAR.get();
		}

		@Override
		public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
			return itemStack2.is(Items.BONE) || super.isValidRepairItem(itemStack, itemStack2);
		}
		@Override
		public int getEnchantmentValue() {
			return 12;
		}
	}
}


