package com.jerotes.jerotes.entity.Interface;

import java.util.UUID;

public interface MagicSpellLivingEntity {
    int getCastingTickJerotes();
    void setCastingTickJerotes(int tick);
    int getCastingMaxTickJerotes();
    void setCastingMaxTickJerotes(int tick);
    int getCastCooldownJerotes();
    void setCastCooldownJerotes(int tick);

    String getLastSpellTypeJerotes();
    void setLastSpellTypeJerotes(String string);
    int getLastSpellLevelJerotes();
    void setLastSpellLevelJerotes(int tick);
    UUID getLastSpellTargetJerotes();
    void setLastSpellTargetJerotes(UUID uuid);

    String getConcentrationSpellTypeJerotes();
    void setConcentrationSpellTypeJerotes(String string);
    int getConcentrationSpellLevelJerotes();
    void setConcentrationSpellLevelJerotes(int tick);
    UUID getConcentrationSpellTargetJerotes();
    void setConcentrationSpellTargetJerotes(UUID uuid);
    int getConcentrationSpellCleanTickJerotes();
    void setConcentrationSpellCleanTickJerotes(int tick);
    int getConcentrationSpellMobTickJerotes();
    void setConcentrationSpellMobTickJerotes(int tick);
}

