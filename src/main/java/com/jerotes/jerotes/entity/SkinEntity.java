package com.jerotes.jerotes.entity;

public interface SkinEntity {
    default boolean isSkinA() {
        return false;
    }
    default boolean isSkinB() {
        return false;
    }
    default boolean isSkinC() {
        return false;
    }

    default boolean isTrueUse() {
        return true;
    }
    //身体
    //性别
    default boolean IsFemale() {
        return false;
    }
    //皮肤
    default int SkinType() {
        return 0;
    }
    default int SkinColor() {
        return 1;
    }
    default int MaxSkinType() {
        return 1;
    }
    default int MaxSkinColor() {
        return 8;
    }
    //眼睛
    default int EyeType() {
        return 0;
    }
    default int EyeColor() {
        return 1;
    }
    default int MaxEyeType() {
        return 4;
    }
    default int MaxEyeColor() {
        return 8;
    }
    //头发
    default int HairType() {
        return 1;
    }
    default int HairColor() {
        return 1;
    }
    default int MaxHairType() {
        return 8;
    }
    default int MaxHairColor() {
        return 16;
    }
    //附件-1
    default int AddType_1() {
        return 0;
    }
    default int MaxAddType() {
        return 8;
    }
    //附件-2
    default int AddType_2() {
        return 0;
    }
    //附件-3
    default int AddType_3() {
        return 0;
    }
    //服饰
    //上衣
    default int JacketType() {
        return 0;
    }
    default int JacketColor() {
        return 1;
    }
    default int MaxJacketType() {
        return 8;
    }
    default int MaxJacketColor() {
        return 16;
    }
    //裤子
    default int PantsType() {
        return 0;
    }
    default int PantsColor() {
        return 1;
    }
    default int MaxPantsType() {
        return 8;
    }
    default int MaxPantsColor() {
        return 16;
    }
    //手套
    default int GlovesType() {
        return 0;
    }
    default int GlovesColor() {
        return 1;
    }
    default int MaxGlovesType() {
        return 8;
    }
    default int MaxGlovesColor() {
        return 16;
    }
    //饰品-1
    default int BaubleType_1() {
        return 0;
    }
    default int MaxBaubleType() {
        return 8;
    }
    //饰品-2
    default int BaubleType_2() {
        return 0;
    }
    //饰品-3
    default int BaubleType_3() {
        return 0;
    }
    //鞋子
    default int ShoesType() {
        return 0;
    }
    default int ShoesColor() {
        return 1;
    }
    default int MaxShoesType() {
        return 8;
    }
    default int MaxShoesColor() {
        return 16;
    }
    //帽子
    default int HatType() {
        return 0;
    }
    default int HatColor() {
        return 1;
    }
    default int MaxHatType() {
        return 8;
    }
    default int MaxHatColor() {
        return 16;
    }
    //外套
    default int OvercoatType() {
        return 0;
    }
    default int OvercoatColor() {
        return 1;
    }
    default int MaxOvercoatType() {
        return 8;
    }
    default int MaxOvercoatColor() {
        return 16;
    }
}

