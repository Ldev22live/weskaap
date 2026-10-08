package com.weskaap.game.tiled;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TiledAreaLoaderTest {

    private final TiledAreaLoader loader = new TiledAreaLoader();

    @Test
    void loadsRetreatMapFromClasspath() {
        AreaData area = loader.load("retreat_location.tmx");

        assertNotNull(area);
        assertEquals("retreat_location_tmx", area.getId());
        assertEquals("retreat_location.tmx", area.getName());
        assertEquals(50, area.getMapWidthTiles());
        assertEquals(50, area.getMapHeightTiles());
        assertEquals(32f, area.getTileWidth());
        assertEquals(32f, area.getTileHeight());
        assertEquals(3200f, area.getWorldWidth(), 0.001f);
        assertEquals(3200f, area.getWorldHeight(), 0.001f);
    }

    @Test
    void parsesCollisionLayer() {
        AreaData area = loader.load("retreat_location.tmx");

        assertEquals(4, area.getCollisionBounds().size(), "Collision layer should contain 4 rectangles");

        boolean foundHouse1 = area.getCollisionBounds().stream()
            .anyMatch(r -> Math.abs(r.x - 384f) < 0.001f && Math.abs(r.y - 2816f) < 0.001f);
        assertTrue(foundHouse1, "Expected Building_House_1 footprint in collision data");
    }

    @Test
    void parsesObjectsLayer() {
        AreaData area = loader.load("retreat_location.tmx");

        assertEquals(5, area.getObjects().size(), "Objects layer should contain 5 objects");

        MapObjectData houseEntrance = area.getObjects().stream()
            .filter(o -> "House1_Entrance".equals(o.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(houseEntrance);
        assertEquals("entrance", houseEntrance.getType());
        assertEquals("retreat_house_1_interior", houseEntrance.getProperty("interiorId"));
    }

    @Test
    void parsesSpawnsLayer() {
        AreaData area = loader.load("retreat_location.tmx");

        assertEquals(1, area.getSpawns().size(), "Spawns layer should contain 1 spawn");

        SpawnData playerSpawn = area.getSpawns().get(0);
        assertEquals("PlayerSpawn", playerSpawn.getName());
        assertEquals(1632f, playerSpawn.getPosition().x, 0.001f);
        assertEquals(1440f, playerSpawn.getPosition().y, 0.001f);
    }

    @Test
    void parsesTileLayers() {
        AreaData area = loader.load("retreat_location.tmx");

        assertEquals(4, area.getTileLayers().size(), "Expected 4 tile layers");

        assertNotNull(area.getTileLayer("Ground"));
        assertNotNull(area.getTileLayer("Roads"));
        assertNotNull(area.getTileLayer("Buildings"));
        assertNotNull(area.getTileLayer("Environment"));

        TileLayerData ground = area.getTileLayer("Ground");
        assertEquals(50, ground.getWidthTiles());
        assertEquals(50, ground.getHeightTiles());
        assertTrue(ground.isEmpty());

        TileLayerData roads = area.getTileLayer("Roads");
        assertEquals(100, roads.countNonZero(), "Roads strip spans all 50 columns over 2 rows");
        assertEquals(2, roads.getTile(0, 25));
        assertEquals(2, roads.getTile(49, 26));

        TileLayerData buildings = area.getTileLayer("Buildings");
        assertEquals(4, buildings.getTile(6, 3), "Expected building footprint gid at row 3 col 6");
        assertEquals(7, buildings.getTile(8, 6), "Expected door gid at row 6 col 8");

        TileLayerData environment = area.getTileLayer("Environment");
        assertEquals(6, environment.getTile(6, 2));
        assertEquals(8, environment.countNonZero());
    }

    @Test
    void noBuildingsParsedWhenNoneAreDeclared() {
        AreaData area = loader.load("retreat_location.tmx");

        assertTrue(area.getBuildings().isEmpty(),
            "The current TMX does not declare type='building' objects, so no BuildingData is produced");
    }

    @Test
    void missingFileProducesClearError() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loader.load("missing_map.tmx"));
        assertTrue(exception.getMessage().contains("not found"));
    }
}
