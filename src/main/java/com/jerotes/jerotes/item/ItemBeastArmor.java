package com.jerotes.jerotes.item;

public interface ItemBeastArmor {
    default int getProtection() {
        return 0;
    }
    default double getToughness() {
        return 0;
    }

    default double getKnockbackResistance() {
        return 0;
    }

    default int getColor() {
        return 0xffffff;
    }
}


