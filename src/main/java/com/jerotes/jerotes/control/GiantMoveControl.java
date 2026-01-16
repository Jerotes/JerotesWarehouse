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

public class GiantMoveControl extends MoveControl {
    public GiantMoveControl(Mob mob) {
        super(mob);
    }
    private float trueSpeed;
    private float additionalRot;

    public void tick() {
        if (this.operation == Operation.STRAFE) {
            float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float) this.speedModifier * f;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
            this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, f1);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }
            f4 = this.trueSpeed / f4;
            f2 *= f4;
            f3 *= f4;
            float f5 = Mth.sin(this.mob.getYRot() * ((float) Math.PI / 180F));
            float f6 = Mth.cos(this.mob.getYRot() * ((float) Math.PI / 180F));
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            if (!this.isWalkable(f7, f8)) {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
            }
            this.mob.setSpeed(trueSpeed);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = Operation.WAIT;
        } else if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            double d2 = this.wantedY - this.mob.getY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
            if (d3 < (double) 2.5000003E-7F) {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
                this.mob.setZza(trueSpeed);
                if (this.additionalRot > 0) {
                    this.additionalRot -= 0.1F;
                }
                return;
            } else {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
            float f9 = (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            if (this.additionalRot < 1) {
                this.additionalRot += 0.1F;
            }
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 5 + additionalRot * 5));
            this.mob.setSpeed(trueSpeed);
            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level().getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
            if (d2 > (double) this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getBbWidth())
                    || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double) blockpos.getY()
                    && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = Operation.JUMPING;
            }
        } else if (this.operation == Operation.JUMPING) {
            this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            this.mob.setSpeed(this.trueSpeed);
            if (this.mob.onGround()) {
                this.operation = Operation.WAIT;
            }
        } else {
            this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
            this.mob.setZza(trueSpeed);
            if (this.additionalRot > 0) {
                this.additionalRot -= 0.1F;
            }
        }
    }

    private boolean isWalkable(float f, float f2) {
        PathNavigation pathnavigation = this.mob.getNavigation();
        NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
        return nodeevaluator.getBlockPathType(this.mob.level(), Mth.floor(this.mob.getX() + (double) f), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double) f2)) == BlockPathTypes.WALKABLE;
    }

    protected float rotlerp(float f, float f2, float f3) {
        float f4 = Mth.wrapDegrees(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }

        if (f4 < -f3) {
            f4 = -f3;
        }

        float f5 = f + f4;
        if (f5 < 0.0F) {
            f5 += 360.0F;
        } else if (f5 > 360.0F) {
            f5 -= 360.0F;
        }

        return f5;
    }
}

