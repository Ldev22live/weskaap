package com.weskaap.game.world;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AreaRegistry {
    private final Map<AreaId, AreaDefinition> areas = new EnumMap<>(AreaId.class);

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
}
