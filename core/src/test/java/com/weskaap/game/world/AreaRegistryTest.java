package com.weskaap.game.world;

import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.tiled.AreaData;
import com.weskaap.game.tiled.TiledAreaLoader;
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

    @Test
    void registryLoadsRetreatAreaDataFromTmx() {
        AreaRegistry registry = new AreaRegistry();
        AreaData retreatData = registry.loadArea(new TiledAreaLoader(), AreaId.RETREAT, "retreat_location.tmx");

        assertNotNull(retreatData);
        assertTrue(registry.hasAreaData(AreaId.RETREAT));
        assertSame(retreatData, registry.getAreaData(AreaId.RETREAT));
        assertEquals("retreat_location_tmx", retreatData.getId());
        assertEquals(50, retreatData.getMapWidthTiles());
        assertEquals(50, retreatData.getMapHeightTiles());
    }

    @Test
    void loadedRetreatDataContainsExpectedRuntimeElements() {
        AreaRegistry registry = new AreaRegistry();
        AreaData retreatData = registry.loadArea(new TiledAreaLoader(), AreaId.RETREAT, "retreat_location.tmx");

        assertEquals(4, retreatData.getCollisionBounds().size(), "Expected 4 TMX collision rectangles");
        assertEquals(1, retreatData.getSpawns().size(), "Expected 1 player spawn");
        assertEquals(5, retreatData.getObjects().size(), "Expected 5 TMX objects");
        assertTrue(retreatData.getBuildings().isEmpty(), "TMX has no type=building objects yet");

        boolean foundEntrance = retreatData.getObjects().stream()
            .anyMatch(o -> "House1_Entrance".equals(o.getName()) && "retreat_house_1_interior".equals(o.getProperty("interiorId")));
        assertTrue(foundEntrance, "Expected House1_Entrance with interiorId");
    }

    private AreaDefinition area(AreaId id) {
        return new AreaDefinition(id, id.name(), 3200f, 2400f, new Vector2(100f, 200f),
            List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    }
}
