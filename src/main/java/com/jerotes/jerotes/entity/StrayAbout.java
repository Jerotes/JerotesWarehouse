package com.jerotes.jerotes.entity;

public interface StrayAbout {
    boolean isJerotesParched();
    void setJerotesParched(boolean bl);
    default void setJerotesParchedOther(boolean bl) {

    }
}
