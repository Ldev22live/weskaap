package com.weskaap.game.travel;

import com.weskaap.game.world.AreaId;

public final class TravelDestination {
    private final AreaId areaId;
    private final String displayName;
    private final String description;
    private final boolean unlocked;
    private final boolean implemented;

    public TravelDestination(AreaId areaId, String displayName, String description,
                             boolean unlocked, boolean implemented) {
        if (areaId == null) throw new IllegalArgumentException("Area id cannot be null");
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name cannot be blank");
        }
        this.areaId = areaId;
        this.displayName = displayName;
        this.description = description == null ? "" : description;
        this.unlocked = unlocked;
        this.implemented = implemented;
    }

    public AreaId getAreaId() { return areaId; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public boolean isImplemented() { return implemented; }

    public TravelDestination withUnlocked(boolean unlocked) {
        return new TravelDestination(areaId, displayName, description, unlocked, implemented);
    }
}
