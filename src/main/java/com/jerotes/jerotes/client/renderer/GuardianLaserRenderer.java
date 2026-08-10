package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.GuardianLaserEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.ShockingGraspEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class GuardianLaserRenderer<T extends GuardianLaserEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");

    public GuardianLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        renderBeam(entity, partialTick, poseStack, buffer, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private Vec3 getPosition(Entity entity, double yOffset, float partialTick) {
        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) + yOffset;
        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        return new Vec3(x, y, z);
    }

    private void renderBeam(GuardianLaserEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = entity.getOwner();
        Entity target = entity.getTarget();

        if (owner == null || target == null || !owner.isAlive() || !target.isAlive())
            return;

        Vec3 targetPos = getPosition(target, target.getBbHeight() * 0.5D, partialTick);
        Vec3 ownerPos = getPosition(owner, owner.getEyeHeight() - (owner instanceof Player ? 0.05f : 0f), partialTick);

        Vec3 entityPos = getPosition(entity, 0.0D, partialTick);

        Vec3 start = ownerPos.subtract(entityPos);
        Vec3 end = targetPos.subtract(entityPos);

        //从施法者指向目标
        Vec3 direction = end.subtract(start);

        float length = (float)direction.length();
        if (length < 0.001F)
            return;

        direction = direction.normalize();

        float f1 = entity.life + partialTick;
        float f2 = f1 * 0.5F % 1.0F;

        float pitch = (float)Math.acos(direction.y);
        float yaw = (float)Math.atan2(direction.z, direction.x);

        poseStack.pushPose();

        poseStack.translate(start.x, start.y, start.z);

        poseStack.mulPose(
                Axis.YP.rotation(
                        ((float)Math.PI / 2F) - yaw
                )
        );

        poseStack.mulPose(
                Axis.XP.rotation(pitch)
        );

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        float progress = (float)entity.life / (float)entity.getMaxLife();
        float progress2 = progress * progress;

        int r = 64 + (int)(progress2 * 191.0F);
        int g = 32 + (int)(progress2 * 191.0F);
        int b = 128 - (int)(progress2 * 64.0F);

        float f7 = f1 * 0.05F * -1.5F;

        float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
        float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;

        float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
        float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;

        float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
        float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;

        float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
        float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;

        float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
        float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;

        float f21 = Mth.cos(f7) * 0.2F;
        float f22 = Mth.sin(f7) * 0.2F;

        float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
        float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;

        float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
        float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;

        float f29 = -1.0F + f2;
        float f30 = (length + 1.0F) * 2.5F + f29;

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityCutoutNoCull(
                        new ResourceLocation("textures/entity/guardian_beam.png")
                )
        );

        vertex(
                consumer,
                matrix,
                normal,
                f19,
                length + 1.0F,
                f20,
                r,
                g,
                b,
                0.4999F,
                f30
        );

        vertex(
                consumer,
                matrix,
                normal,
                f19,
                0.0F,
                f20,
                r,
                g,
                b,
                0.4999F,
                f29
        );

        vertex(
                consumer,
                matrix,
                normal,
                f21,
                0.0F,
                f22,
                r,
                g,
                b,
                0.0F,
                f29
        );

        vertex(
                consumer,
                matrix,
                normal,
                f21,
                length + 1.0F,
                f22,
                r,
                g,
                b,
                0.0F,
                f30
        );

        vertex(
                consumer,
                matrix,
                normal,
                f23,
                length + 1.0F,
                f24,
                r,
                g,
                b,
                0.4999F,
                f30
        );

        vertex(
                consumer,
                matrix,
                normal,
                f23,
                0.0F,
                f24,
                r,
                g,
                b,
                0.4999F,
                f29
        );

        vertex(
                consumer,
                matrix,
                normal,
                f25,
                0.0F,
                f26,
                r,
                g,
                b,
                0.0F,
                f29
        );

        vertex(
                consumer,
                matrix,
                normal,
                f25,
                length + 1.0F,
                f26,
                r,
                g,
                b,
                0.0F,
                f30
        );

        float f31 = entity.life % 2 == 0 ? 0.5F : 0.0F;

        vertex(
                consumer,
                matrix,
                normal,
                f11,
                length + 1.0F,
                f12,
                r,
                g,
                b,
                0.5F,
                f31 + 0.5F
        );

        vertex(
                consumer,
                matrix,
                normal,
                f13,
                length + 1.0F,
                f14,
                r,
                g,
                b,
                1.0F,
                f31 + 0.5F
        );

        vertex(
                consumer,
                matrix,
                normal,
                f17,
                length + 1.0F,
                f18,
                r,
                g,
                b,
                1.0F,
                f31
        );

        vertex(
                consumer,
                matrix,
                normal,
                f15,
                length + 1.0F,
                f16,
                r,
                g,
                b,
                0.5F,
                f31
        );

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z, int r, int g, int b, float u, float v) {
        consumer.vertex(matrix, x, y, z).color(r, g, b, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public boolean shouldRender(GuardianLaserEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return entity.getOwner() != null && entity.getTarget() != null;
    }
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_LOCATION;
    }
}