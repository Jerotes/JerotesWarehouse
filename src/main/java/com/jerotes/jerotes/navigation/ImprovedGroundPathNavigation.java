package com.jerotes.jerotes.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ImprovedGroundPathNavigation extends GroundPathNavigation {

    // ================= 可调参数 =================
    /** 到达一个路径点的水平距离系数（到达距离 = 宽度 × waypointReachFactor） */
    private double waypointReachFactor = 0.7;

    /** 到达路径点的垂直允许误差（格） */
    private double verticalReachTolerance = 1.2;

    /** 卡死检测时间间隔（tick） */
    private int stuckCheckInterval = 80; // 原版 100，更频繁但更准确

    /** 卡死判定所需的移动比例（实际移动距离 / 预期移动距离）低于此值视为卡住 */
    private double stuckMinMovementRatio = 0.15;

    /** 启用宽度感知寻路（需要重写 nodeEvaluator） */
    private boolean widthAwarePathfinding = true;

    // 用于性能优化和卡死检测的状态
    private Vec3 lastCheckPos = Vec3.ZERO;
    private int lastCheckTick = -100;
    private double expectedMoveDistance = 0.0;
    private int noProgressTicks = 0;
    private BlockPos lastPathTarget = null;
    public final PathFinder pathFinder;
    public boolean isStuck;

    public ImprovedGroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
        if (widthAwarePathfinding) {
            this.nodeEvaluator = new WidthAwareWalkNodeEvaluator(mob.getBbWidth());
            this.pathFinder = new PathFinder(this.nodeEvaluator, 200);
        }
        else {
            int i = Mth.floor(mob.getAttributeValue(Attributes.FOLLOW_RANGE) * 16.0D);
            this.pathFinder = this.createPathFinder(i);
        }
    }

    // 配置方法
    public void setWaypointReachFactor(double factor) { this.waypointReachFactor = factor; }
    public void setVerticalReachTolerance(double tolerance) { this.verticalReachTolerance = tolerance; }
    public void setStuckCheckInterval(int interval) { this.stuckCheckInterval = interval; }
    public void setWidthAwarePathfinding(boolean enable) { this.widthAwarePathfinding = enable; }

    @Override
    public void tick() {
        // 先执行原版基础 tick，但我们需要覆盖跟随逻辑和卡死检测
        // 为避免重复代码，我们重写 followThePath 和 doStuckDetection
        // 这里调用父类 tick 会执行原有逻辑，但我们希望在子类中完全控制，所以干脆自己实现。
        // 为了简单，我们只覆盖关键方法，但实际为了性能可重写整个 tick 逻辑。
        // 下面采用部分重写方式：仍然走父类 tick，但通过重写 followThePath 和 doStuckDetection 来改变行为。
        super.tick();
    }

    /**
     * 重写：到达路径点的判断逻辑（支持大生物）
     */
    @Override
    protected void followThePath() {
        if (this.path == null || this.path.isDone()) {
            return;
        }

        Vec3 currentPos = getTempMobPos();
        // 动态计算到达阈值
        float dynamicReach = (float) (mob.getBbWidth() * waypointReachFactor);
        // 确保最小阈值（避免太小时永远无法到达）
        dynamicReach = Math.max(dynamicReach, 0.3f);

        Node nextNode = this.path.getNextNode();
        Vec3 nextNodePos = Vec3.atBottomCenterOf(nextNode.asBlockPos());

        double dx = Math.abs(currentPos.x - nextNodePos.x);
        double dz = Math.abs(currentPos.z - nextNodePos.z);
        double dy = Math.abs(currentPos.y - nextNodePos.y);

        boolean horizontalReached = dx <= dynamicReach && dz <= dynamicReach;
        boolean verticalReached = dy <= verticalReachTolerance;

        if (horizontalReached && verticalReached) {
            // 到达当前节点，前进到下一个
            this.path.advance();
            // 重置无进展计时（因为移动了）
            noProgressTicks = 0;
        } else {
            // 记录无进展时间（用于卡死检测）
            if (horizontalReached && !verticalReached) {
                // 水平已到但垂直未到，可能是悬崖/楼梯，不算卡死
                noProgressTicks = 0;
            } else if (dx <= 0.2 && dz <= 0.2 && dy <= 1.0) {
                // 非常接近但没到达，可能是卡在方块边缘，增加计数
                noProgressTicks++;
            } else {
                noProgressTicks = 0;
            }
        }

        // 可选：剪枝优化，如果路径点数量过多且距离远，可以跳过一个点（原版中有类似逻辑但未用）
        if (this.path.getNextNodeIndex() + 1 < this.path.getNodeCount()) {
            Node nextNextNode = this.path.getNode(this.path.getNextNodeIndex() + 1);
            Vec3 nextNextPos = Vec3.atBottomCenterOf(nextNextNode.asBlockPos());
            if (canMoveDirectly(currentPos, nextNextPos)) {
                // 可直接走向下下个点，跳过当前点
                this.path.advance();
            }
        }

        // 调用父类剩余的（如发送调试包）但不希望重复移动控制，原版最后会设置 moveControl
        // 我们这里设置移动控制的目标为当前路径点（保持原版行为）
        if (!this.path.isDone()) {
            Vec3 targetPos = this.path.getNextEntityPos(mob);
            double targetY = getGroundY(targetPos);
            this.mob.getMoveControl().setWantedPosition(targetPos.x, targetY, targetPos.z, this.speedModifier);
        }
    }

    /**
     * 重写：更智能的卡死检测
     */
    @Override
    protected void doStuckDetection(Vec3 currentPos) {
        // 如果路径无效或已完成，不检测
        if (this.path == null || this.path.isDone()) {
            resetStuckStatus();
            return;
        }

        int tickDiff = this.tick - lastStuckCheck;
        if (tickDiff >= stuckCheckInterval) {
            // 计算预期移动距离（基于速度）
            double moveSpeed = this.mob.getSpeed();
            double expectedDist = moveSpeed * tickDiff; // 预期移动格数
            double actualDistSq = currentPos.distanceToSqr(lastCheckPos);
            double actualDist = Math.sqrt(actualDistSq);

            // 判断是否严重停滞
            boolean isSeverelyStuck = actualDist < expectedDist * stuckMinMovementRatio;
            // 额外条件：很长时间内路径节点没有前进
            boolean nodeNotAdvancing = (noProgressTicks > 60);

            if (isSeverelyStuck || nodeNotAdvancing) {
                // 检查是否是因阻挡而卡住（例如前方有不可通行的方块）
                if (isActuallyStuck(currentPos)) {
                    this.isStuck = true;
                    this.stop();
                    return;
                } else {
                    // 暂时卡住但可能马上通过，重置计时但不标记 stuck
                    noProgressTicks = 0;
                }
            } else {
                // 正常移动，清除卡死标记
                this.isStuck = false;
            }

            // 更新检查点
            lastCheckPos = currentPos;
            lastStuckCheck = this.tick;
        }

        // 处理超时（原版中与 timeoutCachedNode 相关，保留原版逻辑即可）
        super.doStuckDetection(currentPos);
    }

    /**
     * 辅助方法：真正判断是否被卡住（可通过向前方发射射线或检查路径是否可达）
     */
    private boolean isActuallyStuck(Vec3 currentPos) {
        if (this.path == null || this.path.isDone()) return false;
        Node nextNode = this.path.getNextNode();
        Vec3 target = Vec3.atBottomCenterOf(nextNode.asBlockPos());
        // 简单判断：如果当前点与目标点的直线路径被阻挡，并且超过 40 tick 没前进过
        boolean lineBlocked = !canMoveDirectly(currentPos, target);
        return lineBlocked && noProgressTicks > 40;
    }

    private void resetStuckStatus() {
        this.isStuck = false;
        noProgressTicks = 0;
        lastStuckCheck = this.tick - stuckCheckInterval; // 让下次检测尽快进行
    }

    /**
     * 重写：创建路径时，对大体型生物使用扩大搜索半径和宽度感知评估器
     */
    @Nullable
    @Override
    protected Path createPath(java.util.Set<BlockPos> targets, int range, boolean canJumpOutOfWater, int maxDistanceToTarget, float followRange) {
        // 对于宽体，增加搜索范围（按宽度比例）
        float adjustedRange = followRange + (float) (mob.getBbWidth() - 1.0f) * 2.0f;
        // 调用父类的实现（但父类内部会使用 nodeEvaluator，已经替换为宽度感知版本）
        return super.createPath(targets, range, canJumpOutOfWater, maxDistanceToTarget, adjustedRange);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        // 对大体型生物，检查的目标点需要更大的立足点面积
        // 简单实现：检查 pos 以及周围的 3x3 区域是否都是固体地面（避免悬空）
        if (mob.getBbWidth() > 1.5f) {
            int radius = (int) Math.ceil(mob.getBbWidth() / 2.0);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = pos.offset(dx, -1, dz);
                    if (!this.level.getBlockState(checkPos).isSolid()) {
                        return false;
                    }
                }
            }
        }
        return super.isStableDestination(pos);
    }

    // ================= 内部辅助类：宽度感知节点评估器 =================
    private static class WidthAwareWalkNodeEvaluator extends WalkNodeEvaluator {
        private final float entityWidth;

        public WidthAwareWalkNodeEvaluator(float width) {
            this.entityWidth = width;
        }

        @Override
        public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
            // 原版逻辑只检查单个方块，宽体生物需要检查生物占据的所有方块
            // 这里做简化：如果生物宽度 > 1，检查周围方块是否都是可行走类型
            if (entityWidth > 1.0f) {
                int half = (int) Math.ceil(entityWidth / 2.0);
                boolean allWalkable = true;
                for (int dx = -half; dx <= half; dx++) {
                    for (int dz = -half; dz <= half; dz++) {
                        BlockPathTypes subType = super.getBlockPathType(level, x + dx, y, z + dz);
                        if (subType != BlockPathTypes.WALKABLE && subType != BlockPathTypes.OPEN) {
                            allWalkable = false;
                            break;
                        }
                    }
                }
                if (allWalkable) return BlockPathTypes.WALKABLE;
                // 否则返回一个较差的类型，使路径规划避开
                return BlockPathTypes.BLOCKED;
            }
            return super.getBlockPathType(level, x, y, z);
        }
    }
}