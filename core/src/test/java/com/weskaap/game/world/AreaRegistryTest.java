package com.weskaap.game.world;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AreaRegistryTest {
    @Test
    void registryResolvesActOneAreasGenerically() {
        AreaRegistry registry = new AreaRegistry();
        registry.register(area(AreaId.RETREAT));
        registry.register(area(AreaId.CPUT));
        registry.register(area(AreaId.ROSEBANK));

        assertTrue(registry.contains(AreaId.RETREAT));
        assertTrue(registry.contains(AreaId.CPUT));
        assertTrue(registry.contains(AreaId.ROSEBANK));
        assertEquals(AreaId.ROSEBANK, registry.get(AreaId.ROSEBANK).getId());
        assertEquals(3, registry.getAreas().size());
    }

    @Test
    void areaProvidesSpawnAndSceneData() {
        AreaDefinition rosebank = area(AreaId.ROSEBANK);

        assertEquals(new Vector2(100f, 200f), rosebank.getSpawnPoint());
        assertNotSame(rosebank.getSpawnPoint(), rosebank.getSpawnPoint());
        assertTrue(rosebank.getInteractables().isEmpty());
    }

    private AreaDefinition area(AreaId id) {
        return new AreaDefinition(id, id.name(), 3200f, 2400f, new Vector2(100f, 200f),
            List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    }
}
