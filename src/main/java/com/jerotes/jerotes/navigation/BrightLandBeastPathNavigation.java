package com.jerotes.jerotes.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BrightLandBeastPathNavigation extends GroundPathNavigation {
    public BrightLandBeastPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator() {
            @Override
            public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
                return BlockPathTypes.WALKABLE;
            }
        };
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }
    protected void followThePath() {
        Vec3 vec3 = this.getTempMobPos();

        float entityWidth = this.mob.getBbWidth();
        this.maxDistanceToWaypoint = Math.max(0.5f, entityWidth * 0.75f);

        Vec3i vec3i = this.path.getNextNodePos();
        Vec3 targetCenter = new Vec3(
                vec3i.getX() + 0.5,
                vec3i.getY(),
                vec3i.getZ() + 0.5
        );

        AABB entityBox = this.mob.getBoundingBox();
        double dx = Math.max(0, Math.max(entityBox.minX - targetCenter.x, targetCenter.x - entityBox.maxX));
        double dz = Math.max(0, Math.max(entityBox.minZ - targetCenter.z, targetCenter.z - entityBox.maxZ));
        boolean flag = (dx <= this.maxDistanceToWaypoint && dz <= this.maxDistanceToWaypoint) ||
                (dx * dx + dz * dz) <= (this.maxDistanceToWaypoint * this.maxDistanceToWaypoint);

        if (flag || this.canCutCorner(this.path.getNextNode().type) &&
                this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }

        this.doStuckDetection(vec3);
    }

    @Nullable
    public Path createPathForLargeEntity(BlockPos target, int range, float entityWidth) {
        Set<BlockPos> expandedTargets = new HashSet<>();
        int radius = (int)Math.ceil(entityWidth / 2);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                expandedTargets.add(target.offset(dx, 0, dz));
            }
        }
        return this.createPath(expandedTargets, range);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 p_26560_) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!p_26560_.closerThan(vec3, 2.0)) {
                return false;
            } else if (this.canMoveDirectly(p_26560_, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 vec31 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vec32 = vec3.subtract(p_26560_);
                Vec3 vec33 = vec31.subtract(p_26560_);
                double d0 = vec32.lengthSqr();
                double d1 = vec33.lengthSqr();
                boolean flag = d1 < d0;
                boolean flag1 = d0 < 0.5;
                if (!flag && !flag1) {
                    return false;
                } else {
                    Vec3 vec34 = vec32.normalize();
                    Vec3 vec35 = vec33.normalize();
                    return vec35.dot(vec34) < 0.0;
                }
            }
        }
    }
}

