package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.AnesthetizedAttackEntity;
import com.jerotes.jerotes.entity.Part.BasePartLivingEntity;
import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.item.Interface.ItemAnesthetized;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class AnesthetizedEvent {
	@SubscribeEvent
	public static void Anesthetized(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null || !entity.isAlive())
			return;
		if (entity.level().isClientSide)
			return;
		if (EntityFactionFind.isMachine(entity))
			return;
		if (EntityFactionFind.isConstruct(entity))
			return;
		//如果麻醉
		if (entity.hasEffect(JerotesMobEffects.ANESTHETIZED.get())){
			int anesthetizedTicks = entity.getEffect(JerotesMobEffects.ANESTHETIZED.get()).getDuration();
			float anesthetizedTicksMinute = (float) anesthetizedTicks / 20 / 60;
			if (entity.level() instanceof ServerLevel _level && !entity.isInvisible()) {
				//散发粒子
				_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_I.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
				if (anesthetizedTicks != 0) {
					if (anesthetizedTicksMinute > (entity.getMaxHealth() + entity.getHealth() * 2) * 1/6 && !entity.isInvisible()) {
						_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_II.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
					}
					if (anesthetizedTicksMinute > (entity.getMaxHealth() + entity.getHealth() * 2) * 1/3 && !entity.isInvisible()) {
						_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_III.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
					}
					if (anesthetizedTicksMinute > (entity.getMaxHealth() + entity.getHealth() * 2) * 1/2 && !entity.isInvisible()) {
						_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_IV.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
					}
					if (anesthetizedTicksMinute > (entity.getMaxHealth() + entity.getHealth() * 2) * 2/3 && !entity.isInvisible()) {
						_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_V.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
					}
					if (anesthetizedTicksMinute > (entity.getMaxHealth() + entity.getHealth() * 2) * 5/6 && !entity.isInvisible()) {
						_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_VI.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
					}
				}
			}
			//如果无计时
			if (entity.getPersistentData().getDouble("jerotes_anesthetized") <= 0) {
				if (anesthetizedTicksMinute >= entity.getMaxHealth() + entity.getHealth() * 3) {
					entity.getPersistentData().putDouble("jerotes_anesthetized", (double) anesthetizedTicks / 6 + entity.getPersistentData().getDouble("jerotes_anesthetized"));
					entity.removeEffect(JerotesMobEffects.ANESTHETIZED.get());
				}

			}
			//如果有计时
			else {
				entity.getPersistentData().putDouble("jerotes_anesthetized", (double) anesthetizedTicks / 6 + entity.getPersistentData().getDouble("jerotes_anesthetized"));
				entity.removeEffect(JerotesMobEffects.ANESTHETIZED.get());
			}
		}
	}

	@SubscribeEvent
	public static void AnesthetizedEffect(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null)
			return;
		if (entity.level().isClientSide)
			return;
		if (EntityFactionFind.isMachine(entity))
			return;
		if (EntityFactionFind.isConstruct(entity))
			return;
		if (entity.getPersistentData().getDouble("jerotes_anesthetized") > 0) {
			//散发粒子
			if (entity.level() instanceof ServerLevel _level && !entity.isInvisible()) {
				_level.sendParticles(JerotesParticleTypes.ANESTHETIZED_VII.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 0, 0, 0, 0, 0);
			}
			//计时降低
			entity.getPersistentData().putDouble("jerotes_anesthetized", entity.getPersistentData().getDouble("jerotes_anesthetized") - 1);
			//没有buff
			if (!entity.hasEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get())) {
				entity.addEffect(new MobEffectInstance(JerotesMobEffects.ANESTHETIZED_HOLD.get(), (int) entity.getPersistentData().getDouble("jerotes_anesthetized"), 0, false, false));
			}
			//有buff
			else {
				//如果buff时间大于计时
				if (entity.getEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get()).getDuration() > entity.getPersistentData().getDouble("jerotes_anesthetized")) {
					entity.getPersistentData().putDouble("jerotes_anesthetized", entity.getEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get()).getDuration());
				}
				//如果buff时间小于计时
				else {
					entity.addEffect(new MobEffectInstance(JerotesMobEffects.ANESTHETIZED_HOLD.get(), (int) entity.getPersistentData().getDouble("jerotes_anesthetized"), 0, false, false));
				}
			}
		}
		else if (entity.hasEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get())) {
			//如果buff时间大于计时
			entity.getPersistentData().putDouble("jerotes_anesthetized", entity.getEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get()).getDuration());
		}
	}

	@SubscribeEvent
	public static void AnesthetizedHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null)
			return;
		if (entity.level().isClientSide)
			return;
		if (EntityFactionFind.isMachine(entity))
			return;
		if (EntityFactionFind.isConstruct(entity))
			return;
		//受击降低麻醉
		if (entity.getPersistentData().getDouble("jerotes_anesthetized") > 0 && entity.hasEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get()) && event.getAmount() > 0) {
			entity.getPersistentData().putDouble("jerotes_anesthetized", entity.getPersistentData().getDouble("jerotes_anesthetized") - 600 * event.getAmount());
			entity.removeEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get());
		}
	}

	@SubscribeEvent
	public static void AnesthetizedDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null)
			return;
		if (entity.level().isClientSide)
			return;
		//死亡清除
		if (entity.getPersistentData().getDouble("jerotes_anesthetized") > 0) {
			entity.getPersistentData().putDouble("jerotes_anesthetized", 0);
		}
	}

	@SubscribeEvent
	public static void AttackAnesthetizedWeapon(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attackBy = event.getSource().getEntity();
		float anesthetizedAdds = 0;
		float anesthetizedTicks = 0;
		float anesthetizedWeapons = 0;
		float anesthetizedDamages = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		if (entity.level().isClientSide)
			return;
		if (EntityFactionFind.isMachine(entity))
			return;
		if (EntityFactionFind.isConstruct(entity))
			return;
		if (entity instanceof BasePartLivingEntity basePartLivingEntity && basePartLivingEntity.getParent() != null && basePartLivingEntity.getParent() instanceof LivingEntity livingEntity) {
			entity = livingEntity;
		}
		if (attackBy != null) {
			if (attackBy instanceof LivingEntity living && EntityAndItemFind.isMeleeDamage(event.getSource())) {
				ItemStack handItem = living.getMainHandItem();
				if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(living))
					handItem = living.getOffhandItem();
				if (handItem.getItem() instanceof ItemAnesthetized itemAnesthetized) {
					if (itemAnesthetized.onlyPlayer() && !(living instanceof Player || living instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.beAnesthetizedAsPlayer())) {
						return;
					}
					if (itemAnesthetized.onlyThrowing()) {
						return;
					}
					anesthetizedWeapons = itemAnesthetized.getAnesthetized() * 20;
				}
			}
			anesthetizedAdds = anesthetizedWeapons * anesthetizedDamages;
			if (anesthetizedAdds != 0) {
				if (entity.level() instanceof ServerLevel serverLevel && !entity.isInvisible()) {
					serverLevel.sendParticles(JerotesParticleTypes.ANESTHETIZED_VIII.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 3, 0, 0, 0, 0);
				}
			}
		}
		if (entity.hasEffect(JerotesMobEffects.ANESTHETIZED.get())){
			anesthetizedTicks = entity.getEffect(JerotesMobEffects.ANESTHETIZED.get()).getDuration();
		}
		if ((anesthetizedAdds != 0 || anesthetizedTicks != 0)) {
			entity.addEffect(new MobEffectInstance(JerotesMobEffects.ANESTHETIZED.get(), (int) (anesthetizedAdds + anesthetizedTicks), 0, false, false), attackBy);
		}
	}

	@SubscribeEvent
	public static void AttackAnesthetized(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attackBy = event.getSource().getDirectEntity();
		float anesthetizedAdds = 0;
		float anesthetizedTicks = 0;
		float anesthetizedWeapons = 0;
		float anesthetizedDamages = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		if (entity.level().isClientSide)
			return;
		if (EntityFactionFind.isMachine(entity))
			return;
		if (EntityFactionFind.isConstruct(entity))
			return;
		if (entity instanceof BasePartLivingEntity basePartLivingEntity && basePartLivingEntity.getParent() != null && basePartLivingEntity.getParent() instanceof LivingEntity livingEntity) {
			entity = livingEntity;
		}
		if (attackBy != null) {
			if (attackBy instanceof AnesthetizedAttackEntity anesthetizedAttackEntity) {
				anesthetizedWeapons = anesthetizedAttackEntity.getAnesthetized() * 20;
			}
		}
		anesthetizedAdds = anesthetizedWeapons * anesthetizedDamages;
		if (anesthetizedAdds != 0) {
			if (entity.level() instanceof ServerLevel serverLevel && !entity.isInvisible()) {
				serverLevel.sendParticles(JerotesParticleTypes.ANESTHETIZED_VIII.get(), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), 3, 0, 0, 0, 0);
			}
		}
		if (entity.hasEffect(JerotesMobEffects.ANESTHETIZED.get())){
			anesthetizedTicks = entity.getEffect(JerotesMobEffects.ANESTHETIZED.get()).getDuration();
		}
		if (!entity.level().isClientSide && (anesthetizedAdds != 0 || anesthetizedTicks != 0)) {
			entity.addEffect(new MobEffectInstance(JerotesMobEffects.ANESTHETIZED.get(), (int) (anesthetizedAdds + anesthetizedTicks), 0, false, false), attackBy);
		}
	}
}

