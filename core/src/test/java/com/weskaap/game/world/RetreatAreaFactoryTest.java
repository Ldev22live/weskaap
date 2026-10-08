package com.weskaap.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.building.Building;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.TiledEntrance;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.tiled.AreaData;
import com.weskaap.game.tiled.TiledAreaLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RetreatAreaFactoryTest {

    @Test
    void createsRuntimeAreaFromLoadedTmxData() {
        TiledAreaLoader loader = new TiledAreaLoader();
        AreaData retreatData = loader.load("retreat_location.tmx");

        AreaDefinition retreat = RetreatAreaFactory.create(
            retreatData, List.of(), List.of(), List.of(), List.of());

        assertEquals(AreaId.RETREAT, retreat.getId());
        assertEquals("Retreat", retreat.getDisplayName());
        assertEquals(3200f, retreat.getWidth(), 0.001f);
        assertEquals(3200f, retreat.getHeight(), 0.001f);
        assertEquals(new Vector2(1632f, 1440f), retreat.getSpawnPoint());
        assertEquals(4, retreat.getObstacles().size(), "TMX collision bounds should become runtime obstacles");
        assertTrue(retreat.getObstacles().stream()
                .anyMatch(r -> Math.abs(r.x - 384f) < 0.001f && Math.abs(r.y - 2816f) < 0.001f),
            "Expected Building_House_1 collision footprint in runtime obstacles");
    }

    @Test
    void preservesHardCodedGameplayEntitiesInRuntimeArea() {
        TiledAreaLoader loader = new TiledAreaLoader();
        AreaData retreatData = loader.load("retreat_location.tmx");

        Building chapel = com.weskaap.game.chapel.ChapelInteriorData.createChapelBuilding();
        AreaDefinition retreat = RetreatAreaFactory.create(
            retreatData, List.of(chapel), List.of(), List.of(), List.of());

        assertEquals(1, retreat.getBuildings().size());
        assertSame(chapel, retreat.getBuildings().get(0));
    }

    @Test
    void fallsBackToWorldCentreWhenNoPlayerSpawnExists() {
        AreaData emptyData = new AreaData("empty", "Empty", 32f, 32f, 10, 10, 3200f, 3200f,
            List.of(), List.of(), List.of(), List.of());

        AreaDefinition retreat = RetreatAreaFactory.create(
            emptyData, List.of(), List.of(), List.of(), List.of());

        assertEquals(new Vector2(GameWorld.WIDTH / 2f, GameWorld.HEIGHT / 2f), retreat.getSpawnPoint());
    }

    @Test
    void roadsLayerBecomesRuntimeGroundFeatures() {
        AreaData retreatData = new TiledAreaLoader().load("retreat_location.tmx");

        AreaDefinition retreat = RetreatAreaFactory.create(
            retreatData, List.of(), List.of(), List.of(), List.of());

        assertTrue(retreat.getGroundFeatures().stream()
                .anyMatch(r -> r.x == 0f && r.width == 3200f
                    && Math.abs(r.y - 1472f) < 0.001f && Math.abs(r.height - 64f) < 0.001f),
            "Expected the TMX Roads strip converted to a world ground-feature rectangle");
    }

    @Test
    void entranceObjectsBecomeTiledEntranceMarkers() {
        AreaData retreatData = new TiledAreaLoader().load("retreat_location.tmx");

        AreaDefinition retreat = RetreatAreaFactory.create(
            retreatData, List.of(), List.of(), List.of(), List.of());

        List<Interactable> entrances = retreat.getInteractables().stream()
            .filter(i -> i instanceof TiledEntrance)
            .toList();
        assertEquals(2, entrances.size(), "Expected both TMX entrances as markers");

        TiledEntrance houseEntrance = (TiledEntrance) entrances.stream()
            .filter(i -> "House1_Entrance".equals(((TiledEntrance) i).getName()))
            .findFirst()
            .orElseThrow();
        assertEquals("retreat_house_1_interior", houseEntrance.getInteriorId());

        TiledEntrance shopEntrance = (TiledEntrance) entrances.stream()
            .filter(i -> "Shop1_Entrance".equals(((TiledEntrance) i).getName()))
            .findFirst()
            .orElseThrow();
        assertEquals("retreat_shop_1_interior", shopEntrance.getInteriorId());
    }
}
