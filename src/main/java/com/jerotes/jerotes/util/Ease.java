/*
 * Decompiled with CFR 0.152.
 */
package com.jerotes.jerotes.util;

import net.minecraft.util.Mth;

public class Ease {
    public static float inBack(float f) {
        float f2 = 1.70158f;
        float f3 = 2.70158f;
        return Mth.square(f) * (2.70158f * f - 1.70158f);
    }

    public static float inBounce(float f) {
        return 1.0f - Ease.outBounce(1.0f - f);
    }

    public static float inCubic(float f) {
        return cube(f);
    }

    public static float cube(float f) {
        return f * f * f;
    }
    public static float inElastic(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        float f2 = 2.0943952f;
        return (float)(-Math.pow(2.0, 10.0 * (double)f - 10.0) * Math.sin(((double)f * 10.0 - 10.75) * 2.094395160675049));
    }

    public static float inExpo(float f) {
        return f == 0.0f ? 0.0f : (float)Math.pow(2.0, 10.0 * (double)f - 10.0);
    }

    public static float inQuart(float f) {
        return Mth.square(Mth.square(f));
    }

    public static float inQuint(float f) {
        return Mth.square(Mth.square(f)) * f;
    }

    public static float inSine(float f) {
        return 1.0f - Mth.cos(f * 1.5707964f);
    }

    public static float inOutBounce(float f) {
        if (f < 0.5f) {
            return (1.0f - Ease.outBounce(1.0f - 2.0f * f)) / 2.0f;
        }
        return (1.0f + Ease.outBounce(2.0f * f - 1.0f)) / 2.0f;
    }

    public static float inOutCirc(float f) {
        if (f < 0.5f) {
            return (float)((1.0 - Math.sqrt(1.0 - Math.pow(2.0 * (double)f, 2.0))) / 2.0);
        }
        return (float)((Math.sqrt(1.0 - Math.pow(-2.0 * (double)f + 2.0, 2.0)) + 1.0) / 2.0);
    }

    public static float inOutCubic(float f) {
        if (f < 0.5f) {
            return 4.0f * cube(f);
        }
        return (float)(1.0 - Math.pow(-2.0 * (double)f + 2.0, 3.0) / 2.0);
    }

    public static float inOutQuad(float f) {
        if (f < 0.5f) {
            return 2.0f * Mth.square(f);
        }
        return (float)(1.0 - Math.pow(-2.0 * (double)f + 2.0, 2.0) / 2.0);
    }

    public static float inOutQuart(float f) {
        if (f < 0.5f) {
            return 8.0f * Mth.square(Mth.square(f));
        }
        return (float)(1.0 - Math.pow(-2.0 * (double)f + 2.0, 4.0) / 2.0);
    }

    public static float inOutQuint(float f) {
        if ((double)f < 0.5) {
            return 16.0f * f * f * f * f * f;
        }
        return (float)(1.0 - Math.pow(-2.0 * (double)f + 2.0, 5.0) / 2.0);
    }

    public static float outBounce(float f) {
        float f2 = 7.5625f;
        float f3 = 2.75f;
        if (f < 0.36363637f) {
            return 7.5625f * Mth.square(f);
        }
        if (f < 0.72727275f) {
            return 7.5625f * Mth.square(f - 0.54545456f) + 0.75f;
        }
        if ((double)f < 0.9090909090909091) {
            return 7.5625f * Mth.square(f - 0.8181818f) + 0.9375f;
        }
        return 7.5625f * Mth.square(f - 0.95454544f) + 0.984375f;
    }

    public static float outElastic(float f) {
        float f2 = 2.0943952f;
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        return (float)(Math.pow(2.0, -10.0 * (double)f) * Math.sin(((double)f * 10.0 - 0.75) * 2.094395160675049) + 1.0);
    }

    public static float outExpo(float f) {
        if (f == 1.0f) {
            return 1.0f;
        }
        return 1.0f - (float)Math.pow(2.0, -10.0 * (double)f);
    }

    public static float outQuad(float f) {
        return 1.0f - Mth.square(1.0f - f);
    }

    public static float outQuint(float f) {
        return 1.0f - (float)Math.pow(1.0 - (double)f, 5.0);
    }

    public static float outSine(float f) {
        return Mth.sin(f * 1.5707964f);
    }

    public static float inOutSine(float f) {
        return -(Mth.cos((float)Math.PI * f) - 1.0f) / 2.0f;
    }

    public static float outBack(float f) {
        float f2 = 1.70158f;
        float f3 = 2.70158f;
        return 1.0f + 2.70158f * cube(f - 1.0f) + 1.70158f * Mth.square(f - 1.0f);
    }

    public static float outQuart(float f) {
        return 1.0f - Mth.square(Mth.square(1.0f - f));
    }

    public static float outCubic(float f) {
        return 1.0f - cube(1.0f - f);
    }

    public static float inOutExpo(float f) {
        if (f < 0.5f) {
            return f == 0.0f ? 0.0f : (float)(Math.pow(2.0, 20.0 * (double)f - 10.0) / 2.0);
        }
        return f == 1.0f ? 1.0f : (float)((2.0 - Math.pow(2.0, -20.0 * (double)f + 10.0)) / 2.0);
    }

    public static float inQuad(float f) {
        return f * f;
    }

    public static float outCirc(float f) {
        return (float)Math.sqrt(1.0f - Mth.square(f - 1.0f));
    }

    public static float inOutElastic(float f) {
        float f2 = 1.3962635f;
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        double d = Math.sin((20.0 * (double)f - 11.125) * 1.3962634801864624);
        if (f < 0.5f) {
            return (float)(-(Math.pow(2.0, 20.0 * (double)f - 10.0) * d) / 2.0);
        }
        return (float)(Math.pow(2.0, -20.0 * (double)f + 10.0) * d / 2.0 + 1.0);
    }

    public static float inCirc(float f) {
        return (float)(-Math.sqrt(1.0f - f * f)) + 1.0f;
    }

    public static float inOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = 2.5949094f;
        if (f < 0.5f) {
            return 4.0f * f * f * (7.189819f * f - 2.5949094f) / 2.0f;
        }
        float f4 = 2.0f * f - 2.0f;
        return (f4 * f4 * (3.5949094f * f4 + 2.5949094f) + 2.0f) / 2.0f;
    }
}

