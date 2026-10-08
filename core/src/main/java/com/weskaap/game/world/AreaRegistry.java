package com.weskaap.game.world;

import com.weskaap.game.tiled.AreaData;
import com.weskaap.game.tiled.TiledAreaLoader;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AreaRegistry {
    private final Map<AreaId, AreaDefinition> areas = new EnumMap<>(AreaId.class);
    private final Map<AreaId, AreaData> areaData = new EnumMap<>(AreaId.class);

    public void register(AreaDefinition area) {
        if (area == null) throw new IllegalArgumentException("Area cannot be null");
        areas.put(area.getId(), area);
    }

    public AreaDefinition get(AreaId id) {
        return areas.get(id);
    }

    public boolean contains(AreaId id) {
        return areas.containsKey(id);
    }

    public List<AreaDefinition> getAreas() {
        return List.copyOf(areas.values());
    }

    /**
     * Loads raw area data from a TMX file and stores it by area id.
     * The raw data is kept separate from the runtime {@link AreaDefinition} so
     * that higher-level systems can choose how to consume it.
     */
    public AreaData loadArea(TiledAreaLoader loader, AreaId id, String tmxPath) {
        if (loader == null) throw new IllegalArgumentException("Loader cannot be null");
        if (id == null) throw new IllegalArgumentException("Area id cannot be null");
        if (tmxPath == null || tmxPath.isBlank()) throw new IllegalArgumentException("TMX path cannot be blank");
        AreaData data = loader.load(tmxPath);
        areaData.put(id, data);
        return data;
    }

    public AreaData getAreaData(AreaId id) {
        return areaData.get(id);
    }

    public boolean hasAreaData(AreaId id) {
        return areaData.containsKey(id);
    }
}
