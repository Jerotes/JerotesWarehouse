package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Mob.HumanEntity;
import com.jerotes.jerotes.entity.Interface.SkinEntity;
import com.jerotes.jerotes.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class HumanSkinEntityBodyLayer<T extends HumanEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final EntityModel<T> model;
    private final String string;

    public HumanSkinEntityBodyLayer(RenderLayerParent<T, M> renderLayerParent, M m, String string) {
        super(renderLayerParent);
        this.model = m;
        this.string = string;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        boolean bl;
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl2 = bl = minecraft.shouldEntityAppearGlowing(t) && t.isInvisible();
        if (t.isInvisible() && !bl) {
            return;
        }
        if (!t.isTrueUse()) {
            return;
        }
        if (t.getUsername() != null && !t.getUsername().getSkinName().isEmpty()) {
            return;
        }

        String string = ChatFormatting.stripFormatting(t.getName().getString());
        if (t.getCustomName() != null) {
            if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameWide.contains(string)) {
                return;
            }
            if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameSlim.contains(string)) {
                return;
            }
        }
        if ("Steve".equals(string)) {
            return;
        }
        if ("Alex".equals(string)) {
            return;
        }
        if ("Ari".equals(string)) {
            return;
        }
        if ("Kai".equals(string)) {
            return;
        }
        if ("Noor".equals(string)) {
            return;
        }
        if ("Sunny".equals(string)) {
            return;
        }
        if ("Zuri".equals(string)) {
            return;
        }
        if ("Efe".equals(string)) {
            return;
        }
        if ("Makena".equals(string)) {
            return;
        }


        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(t, f, f2, f3);
        this.model.setupAnim(t, f, f2, f4, f5, f6);
        String sexual = "male";
        if (t.IsFemale()) {
            sexual = "female";
        }
        String SkinColorHave = "null";
        String ClothesColorHave = "null";
        //皮肤
        ResourceLocation bodyLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/base.png");
        //眼睛
        ResourceLocation eyeBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/eye_base.png");
        ResourceLocation leftEyeLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/eye/eye_left.png");
        ResourceLocation rightEyeLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/eye/eye_right.png");
        //头发
        ResourceLocation hairLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/hair/hair_" + t.HairType() + ".png");
        if (t.isBaby()) {
            hairLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/baby_hair/hair_" + t.HairType() + ".png");
        }
        //附件
        ResourceLocation addLocation_1 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_" + t.AddType_1() + ".png");
        ResourceLocation addBaseLocation_1 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_base_" + t.AddType_1() + ".png");
        ResourceLocation addLocation_2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_" + t.AddType_2() + ".png");
        ResourceLocation addBaseLocation_2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_base_" + t.AddType_2() + ".png");
        ResourceLocation addLocation_3 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_" + t.AddType_3() + ".png");
        ResourceLocation addBaseLocation_3 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/add/add_base_" + t.AddType_3() + ".png");
        //上衣
        ResourceLocation jacketLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/jacket_" + t.JacketType() + ".png");
        ResourceLocation jacketBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/jacket_base_" + t.JacketType() + ".png");
        //裤子
        ResourceLocation pantsLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/pants_" + t.PantsType() + ".png");
        ResourceLocation pantsBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/pants_base_" + t.PantsType() + ".png");
        //手套
        ResourceLocation glovesLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/gloves_" + t.GlovesType() + ".png");
        ResourceLocation glovesBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/gloves_base_" + t.GlovesType() + ".png");
        //饰品
        ResourceLocation baubleLocation_1 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_" + t.BaubleType_1() + ".png");
        ResourceLocation baubleBaseLocation_1 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_base_" + t.BaubleType_1() + ".png");
        ResourceLocation baubleLocation_2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_" + t.BaubleType_2() + ".png");
        ResourceLocation baubleBaseLocation_2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_base_" + t.BaubleType_2() + ".png");
        ResourceLocation baubleLocation_3 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_" + t.BaubleType_3() + ".png");
        ResourceLocation baubleBaseLocation_3 = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/bauble/bauble_base_" + t.BaubleType_3() + ".png");
        //鞋子
        ResourceLocation shoesLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/shoes_" + t.ShoesType() + ".png");
        ResourceLocation shoesBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/shoes_base_" + t.ShoesType() + ".png");
        //帽子
        ResourceLocation hatLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/hat_" + t.HatType() + ".png");
        ResourceLocation hatBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/hat_base_" + t.HatType() + ".png");
        //外套
        ResourceLocation overcoatLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/overcoat_" + t.OvercoatType() + ".png");
        ResourceLocation overcoatBaseLocation = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skin/" + this.string + "/" + sexual + "/clothes/overcoat_base_" + t.OvercoatType() + ".png");

        //皮肤
        if (t.SkinType() > 0 && t.SkinColor() > 0) {
            String SkinColor;
            //肤色
            if (t instanceof HumanEntity) {
                if (t.SkinColor() == 1) {
                    SkinColor = "efdabf";
                } else if (t.SkinColor() == 2) {
                    SkinColor = "f9a786";
                } else if (t.SkinColor() == 3) {
                    SkinColor = "f29f5f";
                } else if (t.SkinColor() == 4) {
                    SkinColor = "df9658";
                } else if (t.SkinColor() == 5) {
                    SkinColor = "b7836b";
                } else if (t.SkinColor() == 6) {
                    SkinColor = "b9674a";
                } else if (t.SkinColor() == 7) {
                    SkinColor = "ab724c";
                } else if (t.SkinColor() == 8) {
                    SkinColor = "7e5337";
                } else if (t.SkinColor() == 9) {
                    SkinColor = "443528";
                }
                else {
                    SkinColor = "b7836b";
                }
            }
            else {
                SkinColor = "ffffff";
            }
            int[] arrf = Color.colorStringToRGBInt(SkinColor);
            float r = arrf[0] / 255f;
            float g = arrf[1] / 255f;
            float b = arrf[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, bodyLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r, g, b);
            SkinColorHave = SkinColor;
        }
        //眼睛
        if (t.EyeType() > 0) {
            //眼睛基础
            String EyeBaseColor;
            EyeBaseColor = "ffffff";
            int[] arrf = Color.colorStringToRGBInt(EyeBaseColor);
            float r = arrf[0] / 255f;
            float g = arrf[1] / 255f;
            float b = arrf[2] / 255f;
            if (!SkinColorHave.equals("null") && t.hurtTime > 0) {
                EyeBaseColor = SkinColorHave;
                arrf = Color.colorStringToRGBInt(EyeBaseColor);
                int addColor = 20;

                r = Mth.clamp((arrf[0] - addColor), 0, 255) / 255f;
                g = Mth.clamp((arrf[1] - addColor), 0, 255) / 255f;
                b = Mth.clamp((arrf[2] - addColor), 0, 255) / 255f;
            }
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, eyeBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r, g, b);
            //眼睛具体
            if (t.EyeColor() > 0 && t.hurtTime <= 0) {
                String LeftEyeColor = "000000";
                String RightEyeColor = LeftEyeColor;
                if (t instanceof HumanEntity) {
                    if (t.EyeColor() == 1) {
                        LeftEyeColor = "236224";
                        RightEyeColor = "236224";
                    } else if (t.EyeColor() == 2) {
                        LeftEyeColor = "240303";
                        RightEyeColor = "240303";
                    } else if (t.EyeColor() == 3) {
                        LeftEyeColor = "692b34";
                        RightEyeColor = "692b34";
                    } else if (t.EyeColor() == 4) {
                        LeftEyeColor = "436753";
                        RightEyeColor = "436753";
                    } else if (t.EyeColor() == 5) {
                        LeftEyeColor = "2e1800";
                        RightEyeColor = "2e1800";
                    } else if (t.EyeColor() == 6) {
                        LeftEyeColor = "582c1d";
                        RightEyeColor = "582c1d";
                    } else if (t.EyeColor() == 7) {
                        LeftEyeColor = "523d89";
                        RightEyeColor = "523d89";
                    } else if (t.EyeColor() == 8) {
                        LeftEyeColor = "000000";
                        RightEyeColor = "000000";
                    } else if (t.EyeColor() == 9) {
                        LeftEyeColor = "411e02";
                        RightEyeColor = "411e02";
                    } else {
                        LeftEyeColor = "523d89";
                        RightEyeColor = "523d89";
                    }
                }
                else {
                    LeftEyeColor = "000000";
                    RightEyeColor = LeftEyeColor;
                }
                int[] left_arrf = Color.colorStringToRGBInt(LeftEyeColor);
                float left_r = left_arrf[0] / 255f;
                float left_g = left_arrf[1] / 255f;
                float left_b = left_arrf[2] / 255f;
                int[] right_arrf = Color.colorStringToRGBInt(RightEyeColor);
                float right_r = right_arrf[0] / 255f;
                float right_g = right_arrf[1] / 255f;
                float right_b = right_arrf[2] / 255f;
                if (t.EyeType() == 1 || t.EyeType() == 2) {
                    HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, leftEyeLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, left_r, left_g, left_b);
                }
                if (t.EyeType() == 1 || t.EyeType() == 3) {
                    HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, rightEyeLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, right_r, right_g, right_b);
                }
            }
        }
        //附加
        if (!SkinColorHave.equals("null")) {
            //附加_1
            if (t.AddType_1() > 0) {
                //附加基础
                int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
                float r_base_1 = arrf_base_1[0] / 255f;
                float g_base_1 = arrf_base_1[1] / 255f;
                float b_base_1 = arrf_base_1[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addBaseLocation_1, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
                int[] arrf_1 = Color.colorStringToRGBInt(SkinColorHave);
                float r_1 = arrf_1[0] / 255f;
                float g_1 = arrf_1[1] / 255f;
                float b_1 = arrf_1[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addLocation_1, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
            }
            //附加_2
            if (t.AddType_2() > 0) {
                //附加基础
                int[] arrf_base_2 = Color.colorStringToRGBInt("ffffff");
                float r_base_2 = arrf_base_2[0] / 255f;
                float g_base_2 = arrf_base_2[1] / 255f;
                float b_base_2 = arrf_base_2[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addBaseLocation_2, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_2, g_base_2, b_base_2);
                int[] arrf_2 = Color.colorStringToRGBInt(SkinColorHave);
                float r_2 = arrf_2[0] / 255f;
                float g_2 = arrf_2[1] / 255f;
                float b_2 = arrf_2[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addLocation_2, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_2, g_2, b_2);
            }
            //附加_3
            if (t.AddType_3() > 0) {
                //附加基础
                int[] arrf_base_3 = Color.colorStringToRGBInt("ffffff");
                float r_base_3 = arrf_base_3[0] / 255f;
                float g_base_3 = arrf_base_3[1] / 255f;
                float b_base_3 = arrf_base_3[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addBaseLocation_3, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_3, g_base_3, b_base_3);
                int[] arrf_3 = Color.colorStringToRGBInt(SkinColorHave);
                float r_3 = arrf_3[0] / 255f;
                float g_3 = arrf_3[1] / 255f;
                float b_3 = arrf_3[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, addLocation_3, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_3, g_3, b_3);
            }
        }
        //上衣
        if (t.JacketType() > 0 && t.JacketColor() > 0) {
            String ClothesColor;
            int colors = t.JacketColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "fcb961";
                } else if (colors == 2) {
                    ClothesColor = "b0cfcb";
                } else if (colors == 3) {
                    ClothesColor = "0c0717";
                } else if (colors == 4) {
                    ClothesColor = "e7aa39";
                } else if (colors == 5) {
                    ClothesColor = "962210";
                } else if (colors == 6) {
                    ClothesColor = "00cccc";
                } else if (colors == 7) {
                    ClothesColor = "7a9b32";
                } else if (colors == 8) {
                    ClothesColor = "db2f4c";
                } else if (colors == 9) {
                    ClothesColor = "94c892";
                } else if (colors == 10) {
                    ClothesColor = "7c573e";
                } else if (colors == 11) {
                    ClothesColor = "1f6878";
                } else if (colors == 12) {
                    ClothesColor = "3e2536";
                } else if (colors == 13) {
                    ClothesColor = "28132b";
                } else if (colors == 14) {
                    ClothesColor = "ae5350";
                } else if (colors == 15) {
                    ClothesColor = "257451";
                } else if (colors == 16) {
                    ClothesColor = "463aa5";
                } else if (colors == 17) {
                    ClothesColor = "5987ca";
                } else if (colors == 18) {
                    ClothesColor = "d8b894";
                }
                else {
                    ClothesColor = "00cccc";
                }
            }
            else {
                ClothesColor = "ffffff";
            }
            ClothesColorHave = ClothesColor;
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, jacketBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, jacketLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
        //裤子
        if (t.PantsType() > 0 && t.PantsColor() > 0) {
            String ClothesColor;
            int colors = t.PantsColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "fcb961";
                } else if (colors == 2) {
                    ClothesColor = "b0cfcb";
                } else if (colors == 3) {
                    ClothesColor = "0c0717";
                } else if (colors == 4) {
                    ClothesColor = "e7aa39";
                } else if (colors == 5) {
                    ClothesColor = "962210";
                } else if (colors == 6) {
                    ClothesColor = "00cccc";
                } else if (colors == 7) {
                    ClothesColor = "7a9b32";
                } else if (colors == 8) {
                    ClothesColor = "db2f4c";
                } else if (colors == 9) {
                    ClothesColor = "94c892";
                } else if (colors == 10) {
                    ClothesColor = "7c573e";
                } else if (colors == 11) {
                    ClothesColor = "1f6878";
                } else if (colors == 12) {
                    ClothesColor = "3e2536";
                } else if (colors == 13) {
                    ClothesColor = "28132b";
                } else if (colors == 14) {
                    ClothesColor = "ae5350";
                } else if (colors == 15) {
                    ClothesColor = "257451";
                } else if (colors == 16) {
                    ClothesColor = "463aa5";
                } else if (colors == 17) {
                    ClothesColor = "5987ca";
                } else if (colors == 18) {
                    ClothesColor = "d8b894";
                }
                else {
                    ClothesColor = "463aa5";
                }
            }
            else {
                ClothesColor = "ffffff";
            }
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, pantsBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, pantsLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
        //手套
        if (t.GlovesType() > 0 && t.GlovesColor() > 0) {
            String ClothesColor;
            int colors = t.GlovesColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "fcb961";
                } else if (colors == 2) {
                    ClothesColor = "b0cfcb";
                } else if (colors == 3) {
                    ClothesColor = "0c0717";
                } else if (colors == 4) {
                    ClothesColor = "e7aa39";
                } else if (colors == 5) {
                    ClothesColor = "962210";
                } else if (colors == 6) {
                    ClothesColor = "00cccc";
                } else if (colors == 7) {
                    ClothesColor = "7a9b32";
                } else if (colors == 8) {
                    ClothesColor = "db2f4c";
                } else if (colors == 9) {
                    ClothesColor = "94c892";
                } else if (colors == 10) {
                    ClothesColor = "7c573e";
                } else if (colors == 11) {
                    ClothesColor = "1f6878";
                } else if (colors == 12) {
                    ClothesColor = "3e2536";
                } else if (colors == 13) {
                    ClothesColor = "28132b";
                } else if (colors == 14) {
                    ClothesColor = "ae5350";
                } else if (colors == 15) {
                    ClothesColor = "257451";
                } else if (colors == 16) {
                    ClothesColor = "463aa5";
                } else if (colors == 17) {
                    ClothesColor = "5987ca";
                } else if (colors == 18) {
                    ClothesColor = "d8b894";
                }
                else {
                    ClothesColor = "962210";
                }
            }
            else {
                ClothesColor = "ffffff";
            }
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, glovesBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, glovesLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
        //头发
        if (t.HairType() > 0 && t.HairColor() > 0) {
            String HairColor;
            //发色
            if (t instanceof HumanEntity) {
                if (t.HairColor() == 1) {
                    HairColor = "f3a858";
                }
                else if (t.HairColor() == 2) {
                    HairColor = "9d4522";
                }
                else if (t.HairColor() == 3) {
                    HairColor = "956b9e";
                }
                else if (t.HairColor() == 4) {
                    HairColor = "fcf4aa";
                }
                else if (t.HairColor() == 5) {
                    HairColor = "32191e";
                }
                else if (t.HairColor() == 6) {
                    HairColor = "593420";
                }
                else if (t.HairColor() == 7) {
                    HairColor = "3f2a15";
                }
                else if (t.HairColor() == 8) {
                    HairColor = "494949";
                }
                else if (t.HairColor() == 9) {
                    HairColor = "2b1d18";
                }
                else if (t.HairColor() == 10) {
                    HairColor = "de812e";
                }
                else if (t.HairColor() == 11) {
                    HairColor = "803012";
                }
                else if (t.HairColor() == 12) {
                    HairColor = "663f69";
                }
                else if (t.HairColor() == 13) {
                    HairColor = "f7d576";
                }
                else if (t.HairColor() == 14) {
                    HairColor = "1e0e11";
                }
                else if (t.HairColor() == 15) {
                    HairColor = "23120a";
                }
                else if (t.HairColor() == 16) {
                    HairColor = "241808";
                }
                else if (t.HairColor() == 17) {
                    HairColor = "222222";
                }
                else if (t.HairColor() == 18) {
                    HairColor = "0e0401";
                }
                else {
                    HairColor = "3f2a15";
                }
            }
            else {
                HairColor = "000000";
            }
            int[] arrf = Color.colorStringToRGBInt(HairColor);
            float r = arrf[0] / 255f;
            float g = arrf[1] / 255f;
            float b = arrf[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, hairLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r, g, b);
        }
        //饰品
        if (!SkinColorHave.equals("null")) {
        //饰品_1
            if (t.BaubleType_1() > 0) {
                //饰品基础
                int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
                float r_base_1 = arrf_base_1[0] / 255f;
                float g_base_1 = arrf_base_1[1] / 255f;
                float b_base_1 = arrf_base_1[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleBaseLocation_1, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
                int[] arrf_1 = Color.colorStringToRGBInt(ClothesColorHave);
                float r_1 = arrf_1[0] / 255f;
                float g_1 = arrf_1[1] / 255f;
                float b_1 = arrf_1[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleLocation_1, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
            }
            //饰品_2
            if (t.BaubleType_2() > 0) {
                //饰品基础
                int[] arrf_base_2 = Color.colorStringToRGBInt("ffffff");
                float r_base_2 = arrf_base_2[0] / 255f;
                float g_base_2 = arrf_base_2[1] / 255f;
                float b_base_2 = arrf_base_2[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleBaseLocation_2, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_2, g_base_2, b_base_2);
                int[] arrf_2 = Color.colorStringToRGBInt(ClothesColorHave);
                float r_2 = arrf_2[0] / 255f;
                float g_2 = arrf_2[1] / 255f;
                float b_2 = arrf_2[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleLocation_2, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_2, g_2, b_2);
            }
            //饰品_3
            if (t.BaubleType_3() > 0) {
                //饰品基础
                int[] arrf_base_3 = Color.colorStringToRGBInt("ffffff");
                float r_base_3 = arrf_base_3[0] / 255f;
                float g_base_3 = arrf_base_3[1] / 255f;
                float b_base_3 = arrf_base_3[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleBaseLocation_3, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_3, g_base_3, b_base_3);
                int[] arrf_3 = Color.colorStringToRGBInt(ClothesColorHave);
                float r_3 = arrf_3[0] / 255f;
                float g_3 = arrf_3[1] / 255f;
                float b_3 = arrf_3[2] / 255f;
                HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, baubleLocation_3, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_3, g_3, b_3);
            }
        }
        //鞋子
        if (t.ShoesType() > 0 && t.ShoesColor() > 0) {
            String ClothesColor;
            int colors = t.ShoesColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "6f6f6f";
                } else if (colors == 2) {
                    ClothesColor = "4f4f4f";
                } else if (colors == 3) {
                    ClothesColor = "5c5c5c";
                } else if (colors == 4) {
                    ClothesColor = "333536";
                } else if (colors == 5) {
                    ClothesColor = "631d36";
                } else if (colors == 6) {
                    ClothesColor = "52152b";
                } else if (colors == 7) {
                    ClothesColor = "8e3737";
                } else if (colors == 8) {
                    ClothesColor = "4f2d2d";
                } else if (colors == 9) {
                    ClothesColor = "3b3734";
                } else if (colors == 10) {
                    ClothesColor = "e0c5bc";
                } else if (colors == 11) {
                    ClothesColor = "633620";
                } else if (colors == 12) {
                    ClothesColor = "4a2614";
                } else if (colors == 13) {
                    ClothesColor = "555555";
                } else if (colors == 14) {
                    ClothesColor = "3f3f3f";
                } else if (colors == 15) {
                    ClothesColor = "bababa";
                } else if (colors == 16) {
                    ClothesColor = "5f5f5f";
                } else if (colors == 17) {
                    ClothesColor = "515151";
                } else if (colors == 18) {
                    ClothesColor = "343434";
                }
                else {
                    ClothesColor = "24c800";
                }
            }
            else {
                ClothesColor = "555555";
            }
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, shoesBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, shoesLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
        //帽子
        if (t.HatType() > 0 && t.HatColor() > 0 && !t.hasItemInSlot(EquipmentSlot.HEAD)) {
            String ClothesColor;
            int colors = t.HatColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "fcb961";
                } else if (colors == 2) {
                    ClothesColor = "b0cfcb";
                } else if (colors == 3) {
                    ClothesColor = "0c0717";
                } else if (colors == 4) {
                    ClothesColor = "e7aa39";
                } else if (colors == 5) {
                    ClothesColor = "962210";
                } else if (colors == 6) {
                    ClothesColor = "00cccc";
                } else if (colors == 7) {
                    ClothesColor = "7a9b32";
                } else if (colors == 8) {
                    ClothesColor = "db2f4c";
                } else if (colors == 9) {
                    ClothesColor = "94c892";
                } else if (colors == 10) {
                    ClothesColor = "7c573e";
                } else if (colors == 11) {
                    ClothesColor = "1f6878";
                } else if (colors == 12) {
                    ClothesColor = "3e2536";
                } else if (colors == 13) {
                    ClothesColor = "28132b";
                } else if (colors == 14) {
                    ClothesColor = "ae5350";
                } else if (colors == 15) {
                    ClothesColor = "257451";
                } else if (colors == 16) {
                    ClothesColor = "463aa5";
                } else if (colors == 17) {
                    ClothesColor = "5987ca";
                } else if (colors == 18) {
                    ClothesColor = "d8b894";
                }
                else {
                    ClothesColor = "5987ca";
                }
            }
            else {
                ClothesColor = "ffffff";
            }
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, hatBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, hatLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
        //大衣
        if (t.OvercoatType() > 0 && t.OvercoatColor() > 0) {
            String ClothesColor;
            int colors = t.OvercoatColor();
            if (t instanceof HumanEntity) {
                if (colors == 1) {
                    ClothesColor = "fcb961";
                } else if (colors == 2) {
                    ClothesColor = "b0cfcb";
                } else if (colors == 3) {
                    ClothesColor = "0c0717";
                } else if (colors == 4) {
                    ClothesColor = "e7aa39";
                } else if (colors == 5) {
                    ClothesColor = "962210";
                } else if (colors == 6) {
                    ClothesColor = "00cccc";
                } else if (colors == 7) {
                    ClothesColor = "7a9b32";
                } else if (colors == 8) {
                    ClothesColor = "db2f4c";
                } else if (colors == 9) {
                    ClothesColor = "94c892";
                } else if (colors == 10) {
                    ClothesColor = "7c573e";
                } else if (colors == 11) {
                    ClothesColor = "1f6878";
                } else if (colors == 12) {
                    ClothesColor = "3e2536";
                } else if (colors == 13) {
                    ClothesColor = "28132b";
                } else if (colors == 14) {
                    ClothesColor = "ae5350";
                } else if (colors == 15) {
                    ClothesColor = "257451";
                } else if (colors == 16) {
                    ClothesColor = "463aa5";
                } else if (colors == 17) {
                    ClothesColor = "5987ca";
                } else if (colors == 18) {
                    ClothesColor = "d8b894";
                }
                else {
                    ClothesColor = "7a9b32";
                }
            }
            else {
                ClothesColor = "ffffff";
            }
            int[] arrf_base_1 = Color.colorStringToRGBInt("ffffff");
            float r_base_1 = arrf_base_1[0] / 255f;
            float g_base_1 = arrf_base_1[1] / 255f;
            float b_base_1 = arrf_base_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, overcoatBaseLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_base_1, g_base_1, b_base_1);
            int[] arrf_1 = Color.colorStringToRGBInt(ClothesColor);
            float r_1 = arrf_1[0] / 255f;
            float g_1 = arrf_1[1] / 255f;
            float b_1 = arrf_1[2] / 255f;
            HumanSkinEntityBodyLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, overcoatLocation, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, r_1, g_1, b_1);
        }
    }
}

