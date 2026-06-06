package com.jerotes.jerotes.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ImprovedMoveControl extends MoveControl {
    protected float maxTurnRate = 15.0F;
    protected double acceleration = 0.15;
    protected double stopDistanceFactor = 0.6;
    protected boolean smoothTurning = true;
    protected boolean smoothSpeed = true;

    private float currentYRotSpeed = 0.0F;
    private double currentHorizontalSpeed = 0.0;
    private BlockPos lastCheckedBlockPos = null;
    private boolean lastJumpCheckResult = false;

    public ImprovedMoveControl(Mob mob) {
        super(mob);
    }

    public void setMaxTurnRate(float rate) { this.maxTurnRate = rate; }
    public void setAcceleration(double acc) { this.acceleration = acc; }
    public void setStopDistanceFactor(double factor) { this.stopDistanceFactor = factor; }
    public void setSmoothTurning(boolean enable) { this.smoothTurning = enable; }
    public void setSmoothSpeed(boolean enable) { this.smoothSpeed = enable; }

    @Override
    public void tick() {
        if (operation == Operation.STRAFE) {
            tickStrafe();
        } else if (operation == Operation.MOVE_TO) {
            tickMoveTo();
        } else if (operation == Operation.JUMPING) {
            tickJumping();
        } else {
            mob.setZza(0.0F);
            mob.setXxa(0.0F);
        }
    }

    private void tickStrafe() {
        float baseSpeed = (float) mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float targetSpeed = (float) speedModifier * baseSpeed;

        float finalSpeed = smoothSpeed ? (float) updateSpeed(targetSpeed) : targetSpeed;

        float forward = strafeForwards;
        float right = strafeRight;
        float len = Mth.sqrt(forward * forward + right * right);
        if (len < 1.0F) len = 1.0F;
        float ratio = finalSpeed / len;
        forward *= ratio;
        right *= ratio;

        float yawRad = mob.getYRot() * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yawRad);
        float cos = Mth.cos(yawRad);
        float worldForward = forward * cos - right * sin;
        float worldRight   = right * cos + forward * sin;

        if (!isWalkable(worldForward, worldRight)) {
            strafeForwards = 1.0F;
            strafeRight = 0.0F;
            forward = 1.0F;
            right = 0.0F;
            ratio = finalSpeed / 1.0F;
            forward *= ratio;
            right *= ratio;
            worldForward = forward * cos;
            worldRight   = forward * sin;
        }

        mob.setSpeed(finalSpeed);
        mob.setZza(forward);
        mob.setXxa(right);

        operation = Operation.WAIT;
    }

    private void tickMoveTo() {
        operation = Operation.WAIT;

        double dx = wantedX - mob.getX();
        double dz = wantedZ - mob.getZ();
        double dy = wantedY - mob.getY();
        double distSq = dx * dx + dy * dy + dz * dz;

        double stopDistance = stopDistanceFactor * mob.getBbWidth();
        double stopSq = stopDistance * stopDistance;
        if (distSq < stopSq) {
            mob.setZza(0.0F);
            mob.setXxa(0.0F);
            return;
        }

        float targetYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float currentYaw = mob.getYRot();

        float newYaw;
        if (smoothTurning) {
            newYaw = smoothRotate(currentYaw, targetYaw);
        } else {
            newYaw = rotlerp(currentYaw, targetYaw, 90.0F);
        }
        mob.setYRot(newYaw);

        float baseSpeed = (float) mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float targetSpeed = (float) (speedModifier * baseSpeed);
        float finalSpeed = smoothSpeed ? (float) updateSpeed(targetSpeed) : targetSpeed;
        mob.setSpeed(finalSpeed);

        mob.setZza(1.0F);
        mob.setXxa(0.0F);

        double horizDistSq = dx * dx + dz * dz;
        double stepHeight = mob.getStepHeight();
        boolean needJump = false;

        if (dy > stepHeight && horizDistSq < (2.0 * mob.getBbWidth()) * (2.0 * mob.getBbWidth())) {
            if (shouldJump(dx, dz, dy)) {
                needJump = true;
            }
        }

        if (needJump) {
            mob.getJumpControl().jump();
            operation = Operation.JUMPING;
        } else {
            operation = Operation.MOVE_TO;
        }
    }

    private void tickJumping() {
        float baseSpeed = (float) mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float targetSpeed = (float) (speedModifier * baseSpeed);
        float finalSpeed = smoothSpeed ? (float) updateSpeed(targetSpeed) : targetSpeed;
        mob.setSpeed(finalSpeed);
        mob.setZza(1.0F);
        mob.setXxa(0.0F);

        if (mob.onGround()) {
            operation = Operation.WAIT;
        }
    }

    private float smoothRotate(float currentYaw, float targetYaw) {
        float delta = Mth.wrapDegrees(targetYaw - currentYaw);
        float maxDelta = maxTurnRate;
        float clampedDelta = Mth.clamp(delta, -maxDelta, maxDelta);
        currentYRotSpeed = clampedDelta;
        float newYaw = currentYaw + clampedDelta;
        newYaw = Mth.wrapDegrees(newYaw);
        return newYaw;
    }

    private double updateSpeed(double targetSpeed) {
        if (acceleration <= 0) return targetSpeed;
        double diff = targetSpeed - currentHorizontalSpeed;
        double step = Math.min(Math.abs(diff), acceleration);
        if (diff > 0) currentHorizontalSpeed += step;
        else if (diff < 0) currentHorizontalSpeed -= step;
        else currentHorizontalSpeed = targetSpeed;
        return currentHorizontalSpeed;
    }

    private boolean shouldJump(double dx, double dz, double dy) {
        if (dy <= mob.getStepHeight() + 0.5) {
            return false;
        }

        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1e-4) return false;
        double stepX = dx / len;
        double stepZ = dz / len;

        double eyeY = mob.getY() + mob.getEyeHeight();
        double startX = mob.getX();
        double startZ = mob.getZ();
        double checkDistance = mob.getBbWidth();

        for (double d = 0.2; d <= checkDistance; d += 0.2) {
            double x = startX + stepX * d;
            double z = startZ + stepZ * d;
            BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(eyeY), (int) Math.floor(z));
            BlockState state = mob.level().getBlockState(pos);
            if (state.isAir()) continue;

            VoxelShape shape = state.getCollisionShape(mob.level(), pos);
            if (shape.isEmpty()) continue;

            double maxY = shape.max(Direction.Axis.Y);
            double blockTop = pos.getY() + maxY;
            if (blockTop > mob.getY() + mob.getStepHeight()) {
                return true;
            }
        }
        return false;
    }

    private boolean isWalkable(float xOffset, float zOffset) {
        PathNavigation nav = mob.getNavigation();
        if (nav == null) return true;
        NodeEvaluator evaluator = nav.getNodeEvaluator();
        if (evaluator == null) return true;

        int blockX = Mth.floor(mob.getX() + xOffset);
        int blockY = mob.getBlockY();
        int blockZ = Mth.floor(mob.getZ() + zOffset);

        BlockPos currentPos = new BlockPos(blockX, blockY, blockZ);
        if (currentPos.equals(lastCheckedBlockPos)) {
            return lastJumpCheckResult;
        }
        lastCheckedBlockPos = currentPos;
        boolean walkable = evaluator.getBlockPathType(mob.level(), blockX, blockY, blockZ) == BlockPathTypes.WALKABLE;
        lastJumpCheckResult = walkable;
        return walkable;
    }

    public void invalidateCache() {
        lastCheckedBlockPos = null;
    }
}