package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class JerotesPlayerLeapAtTargetAndLookGoal extends Goal {
   private final JerotesPlayerEntity mob;
   private LivingEntity target;
   private final float yd;

   public JerotesPlayerLeapAtTargetAndLookGoal(JerotesPlayerEntity mob, float f) {
      this.mob = mob;
      this.yd = f;
      this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.hasControllingPassenger() || !this.mob.canAttackJump()) {
         return false;
      } else {
         this.target = this.mob.getTarget();
         if (this.target == null) {
            return false;
         }
         if (this.mob.getAttackBoundingBox().inflate(0.5f).intersects(this.target.getBoundingBox())) {
            if (!this.mob.onGround()) {
               return false;
            } else {
               return this.mob.getRandom().nextInt(reducedTickDelay(20)) == 1;
            }
         }
         return false;
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.onGround();
   }

   public void start() {
      Vec3 vec3 = this.mob.getDeltaMovement();
      Vec3 vec31 = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
      if (vec31.lengthSqr() > 1.0E-7D) {
         vec31 = vec31.normalize().scale(0.4D).add(vec3.scale(0.2D));
      }
      this.mob.setDeltaMovement(vec31.x, (double)this.yd, vec31.z);
      if (this.target != null) {
         this.mob.lookAt(this.target, 360.0f, 360.0f);
      }
   }
}