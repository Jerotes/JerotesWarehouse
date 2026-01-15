package com.jerotes.jerotes.entity.Interface;

public interface StrayAbout {
    boolean isJerotesParched();
    void setJerotesParched(boolean bl);
    default void setJerotesParchedOther(boolean bl) {

    }
}
