package com.jerotes.jerotes.entity.Interface;

public interface BeastToughEntity {
    //是否强韧状态
    boolean isBeastTough();
    void setBeastTough(boolean bl);
    //破坏强韧需要的伤害倍数
    default float getBeastToughBreakHurtCooldownMultiple() {
        return 3.0f;
    }
    void setBeastToughTick(int n);
    int getBeastToughTick();
    default int getBaseBeastToughTick() {
        return (int) (0.5 * 20);
    }
    void setBeastToughDamage(float f);
    float getBeastToughDamage();


    default float getBreakBeastToughNeedBaseDamage() {
        return 5.0f;
    }
    default int getBaseBreakBeastToughHoldTick() {
        return 2 * 20;
    }
    default int getBaseBreakBeastToughTick() {
        return 8 * 20;
    }
    default int getBaseBreakBeastToughCooldownTick() {
        return 16 * 20;
    }
    boolean isBreakBeastTough();
    void setBreakBeastTough(boolean bl);
    void setBreakBeastToughTick(int n);
    int getBreakBeastToughTick();
    void setBreakBeastToughCooldownTick(int n);
    int getBreakBeastToughCooldownTick();
}

