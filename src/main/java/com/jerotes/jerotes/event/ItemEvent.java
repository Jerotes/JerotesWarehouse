package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.UseDaggerEntity;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.item.AACreativeClaw;
import com.jerotes.jerotes.item.AAExplorationEye;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Tool.ItemToolBaseBandage;
import com.jerotes.jerotes.item.Tool.ItemToolBaseDagger;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class ItemEvent {
	@SubscribeEvent
	public static void addWeaponDamage(LivingDamageEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attackBy = event.getSource().getEntity();
		float amount = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		if (entity.getMainHandItem().getItem() instanceof AACreativeClaw || entity.getOffhandItem().getItem() instanceof AACreativeClaw) {
			event.setCanceled(true);
		}
		if (attackBy != null) {
			ItemStack handItem = null;
			ItemStack otherHandItem = null;
			if (attackBy instanceof LivingEntity living) {
				handItem = living.getMainHandItem();
				otherHandItem = living.getOffhandItem();
				if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(living)) {
					handItem = living.getOffhandItem();
					otherHandItem = living.getMainHandItem();
				}
			}
			if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(entity)) {
				handItem = entity.getOffhandItem();
				otherHandItem = entity.getMainHandItem();
			}

			if (attackBy instanceof LivingEntity living) {
				//创造爪
				if (living.getMainHandItem().getItem() instanceof AACreativeClaw || (living.getOffhandItem().getItem() instanceof AACreativeClaw)) {
					if (!entity.level().isClientSide()) {
						entity.getPersistentData().putDouble("jerotesvillage_variant_zsiein_discard", 666666);
					}
					event.setAmount(amount + Float.MAX_VALUE);
				}
				if (((attackBy instanceof Mob || attackBy instanceof Player)) && event.getSource().getDirectEntity() == living && EntityAndItemFind.isMeleeDamage(event.getSource())) {
					if (living.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
						float baseDamage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
						if (baseDamage <= 0)
							return;
						//匕首
						if (handItem.getItem() instanceof ItemToolBaseDagger || living instanceof UseDaggerEntity useDaggerEntity && useDaggerEntity.asUseDagger()) {
							if (living.isShiftKeyDown()) {
								float damage = 1.5f;
								//偷袭
								if (entity instanceof Mob mob && !mob.isAggressive() && mob.getTarget() == null) {
									damage *= 1.25f;
								}
								//背刺
								if (!Main.canSee(living, entity)) {
									damage *= 1.25f;
								}
								//非落地
								if (!living.onGround()) {
									damage *= 0.5f;
								}
								//副手匕首
								if (living.getOffhandItem().getItem() instanceof ItemToolBaseDagger) {
									damage *= 1.15f;
								}
								//体型
								if (Main.mobSizeSmall(entity)) {
									damage *= 1.15f;
								}
								if (Main.mobSizeGiant(entity)) {
									damage *= 0.75f;
								}
								//满血
								if (entity.getHealth() >= entity.getMaxHealth()) {
									damage *= 1.25f;
								}
								//残血
								if (entity.getHealth() <= entity.getMaxHealth()/5) {
									damage *= 1.25f;
								}
								//类人
								if (EntityFactionFind.isHumanoid(entity)) {
									damage *= 1.15f;
								}
								//隐身
								if (living.isInvisible()) {
									damage *= 1.25f;
								}
								//无视力
								if (EntityAndItemFind.targetBlindnessTrue(entity)) {
									damage *= 1.15f;
								}
								if (!(otherHandItem.getItem() instanceof ItemToolBaseDagger itemToolBaseDagger &&
										!itemToolBaseDagger.canShiftKeyDownDamage(living))) {
									float newAmount = DaggerCount(amount * damage, amount);
									if (Float.isNaN(newAmount) || Float.isInfinite(newAmount)) {
										newAmount = 0f;
									}
									event.setAmount(newAmount);
								}
							}
						}
					}
				}
			}
		}
	}
	public static float DaggerCount(float fs, float baseDamage) {
		if (baseDamage <= 0.001f) {
			return Math.min(fs, 500f);
		}

		final float baseMult = fs / baseDamage;
		final float softCap = 100f;
		final float hardCap = 300f;
		final float maxOutput = 500f;

		if (baseDamage > softCap) {
			float decay = 0.7f - 0.2f * (baseDamage / hardCap);
			decay = Math.max(0.3f, decay);

			float scaled = softCap * baseMult;
			float bonus = (float)(Math.log(baseDamage + 1) / Math.log(softCap)) * scaled * decay;

			float result = Math.min(Math.max(bonus, scaled), maxOutput + baseDamage * 0.2f);
			return Float.isFinite(result) ? result : maxOutput;
		}

		float result = Math.min(baseDamage * baseMult, maxOutput);
		return Float.isFinite(result) ? result : maxOutput;
	}

	@SubscribeEvent
	public static void addWeaponEffect(LivingAttackEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attackBy = event.getSource().getEntity();
		float amount = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		//创造爪
		if (entity.getMainHandItem().getItem() instanceof AACreativeClaw || entity.getOffhandItem().getItem() instanceof AACreativeClaw) {
			event.setCanceled(true);
		}
		if (attackBy != null) {
			ItemStack handItem = null;
			ItemStack otherHandItem = null;
			if (attackBy instanceof LivingEntity living) {
				handItem = living.getMainHandItem();
				otherHandItem = living.getOffhandItem();
				if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(living)) {
					handItem = living.getOffhandItem();
					otherHandItem = living.getMainHandItem();
				}
			}
			//探查眼
			if (attackBy instanceof LivingEntity living && living.getMainHandItem().getItem() instanceof AAExplorationEye aaExplorationEye) {
				event.setCanceled(true);
			}
			//特殊效果
			if (handItem != null && handItem.getItem() instanceof ItemSpecialEffect specialEffect) {
				if (attackBy instanceof LivingEntity living &&
						EntityAndItemFind.isMeleeDamage(event.getSource())) {
					if (living.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
						float baseDamage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
						if (baseDamage <= 0)
							return;
						//特殊效果
						specialEffect.attackUse(living, entity, amount > 0);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void UseItemTo(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		Entity entity = event.getTarget();
		ItemStack itemStack = event.getItemStack();
		if (!(entity instanceof LivingEntity livingEntity && player != null))
			return;
		if (itemStack.getItem() instanceof AAExplorationEye) {
			event.setCanceled(true);
		}
	}

	//双手武器格挡
	@SubscribeEvent
	public static void TwoHandedUse(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource damagesource = event.getSource();
		Entity attackBy = event.getSource().getEntity();
		if (damagesource == null || entity == null || !entity.isAlive())
			return;
		if (entity.getOffhandItem().isEmpty() && entity.getMainHandItem().getItem() instanceof ItemTwoHanded itemTwoHanded && itemTwoHanded.canBlock()) {
			if (entity.getMainHandItem().getItem() instanceof ItemSpecialEffect specialEffect) {
				float damages = 1;
				if (isDamageSourceBlocks(damagesource, entity) && entity.isUsingItem()) {
					damages -= itemTwoHanded.getBlockReduction() / 100f;
					specialEffect.blockUse(entity, attackBy, damagesource);
				}
				if (damages != 1 && !entity.isInvulnerable() &&  !entity.isInvulnerableTo(damagesource)) {
					if (!entity.isSilent()) {
						entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JerotesSounds.TWOHANDED_BLOCK, entity.getSoundSource(), 1.0f, 1.0f);
					}
				}
				float newAmount = event.getAmount() * damages;
				if (Float.isNaN(newAmount) || Float.isInfinite(newAmount)) {
					newAmount = 0f;
				}
				event.setAmount(newAmount);
			}
		}
	}
	//格挡判定
	public static boolean isDamageSourceBlocks(DamageSource damageSource, LivingEntity entitys) {
		Vec3 object;
		Entity entity = damageSource.getDirectEntity();
		boolean bl = entity instanceof AbstractArrow && ((AbstractArrow) (AbstractArrow) entity).getPierceLevel() > 0;
		if (!damageSource.is(DamageTypeTags.BYPASSES_SHIELD) && !bl && (object = damageSource.getSourcePosition()) != null) {
			Vec3 vec3 = entitys.getViewVector(0.0f);
			Vec3 vec32 = object.vectorTo(entitys.position());
			vec32 = new Vec3(vec32.x, 0.0, vec32.z).normalize();
			return vec32.dot(vec3) < 0.0;
		}
		return false;
	}

	//受伤
	@SubscribeEvent
	public static void hurts(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		//绷带
		if (entity.getUseItem().getItem() instanceof ItemToolBaseBandage) {
			entity.stopUsingItem();
		}
	}

}