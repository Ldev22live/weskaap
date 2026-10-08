package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Raw data for a building parsed from a Tiled objectgroup.
 *
 * <p>This is intentionally a data-only representation. It captures the footprint,
 * entrance, and any custom properties from the TMX so that a future building
 * factory (M13) can create {@link com.weskaap.game.building.Building} instances.
 */
public final class BuildingData {

    private final String id;
    private final String name;
    private final Rectangle footprint;
    private final Vector2 entrancePosition;
    private final String interiorId;
    private final Map<String, String> properties;

    public BuildingData(String id, String name, Rectangle footprint,
                        Vector2 entrancePosition, String interiorId,
                        Map<String, String> properties) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Building id cannot be blank");
        }
        this.id = id;
        this.name = name == null ? id : name;
        this.footprint = footprint == null ? new Rectangle() : new Rectangle(footprint);
        this.entrancePosition = entrancePosition == null ? new Vector2() : new Vector2(entrancePosition);
        this.interiorId = interiorId == null ? "" : interiorId;
        this.properties = properties == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(properties));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Rectangle getFootprint() {
        return new Rectangle(footprint);
    }

    public Vector2 getEntrancePosition() {
        return new Vector2(entrancePosition);
    }

    public boolean hasInterior() {
        return !interiorId.isBlank();
    }

    public String getInteriorId() {
        return interiorId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
