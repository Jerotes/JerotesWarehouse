package com.jerotes.jerotes.entity.Interface;

public interface JerotesChangeStray {
    boolean isJerotesParched();
    void setJerotesParched(boolean bl);
    default void setJerotesParchedOther(boolean bl) {

    }
}
