package com.weskaap.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.building.Building;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.interaction.TiledEntrance;
import com.weskaap.game.item.PrototypeItem;
import com.weskaap.game.tiled.AreaData;
import com.weskaap.game.tiled.MapObjectData;
import com.weskaap.game.tiled.SpawnData;
import com.weskaap.game.tiled.TileLayerAdapter;
import com.weskaap.game.tiled.TileLayerData;
import com.weskaap.game.tiled.TiledCoordinateConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the runtime {@link AreaDefinition} for Retreat from loaded {@link AreaData}.
 *
 * <p>This is a small M13/M14 integration boundary. It combines the Tiled data
 * (dimensions, collision, spawn, ground features, entrance markers) with the
 * existing hard-coded gameplay entities (buildings, NPCs, enemies, items) until
 * those entities can also be sourced from data in a later milestone.
 */
public final class RetreatAreaFactory {

    private static final String LAYER_ROADS = "Roads";
    private static final String LAYER_ENVIRONMENT = "Environment";
    private static final String OBJECT_TYPE_ENTRANCE = "entrance";
    private static final String SPAWN_NAME_PLAYER = "PlayerSpawn";

    private RetreatAreaFactory() {
    }

    public static AreaDefinition create(AreaData retreatData,
                                        List<Building> buildings,
                                        List<Interactable> interactables,
                                        List<PrototypeItem> items,
                                        List<PrototypeEnemy> enemies) {
        Vector2 spawnPoint = resolvePlayerSpawn(retreatData);
        List<Rectangle> groundFeatures = resolveGroundFeatures(retreatData);
        List<Interactable> allInteractables = mergeEntrances(retreatData, interactables);

        return new AreaDefinition(
            AreaId.RETREAT,
            "Retreat",
            retreatData.getWorldWidth(),
            retreatData.getWorldHeight(),
            spawnPoint,
            retreatData.getCollisionBounds(),
            groundFeatures,
            buildings,
            allInteractables,
            items,
            enemies);
    }

    private static Vector2 resolvePlayerSpawn(AreaData retreatData) {
        return retreatData.getSpawns().stream()
            .filter(spawn -> SPAWN_NAME_PLAYER.equals(spawn.getName()))
            .findFirst()
            .map(SpawnData::getPosition)
            .orElseGet(() -> new Vector2(GameWorld.WIDTH / 2f, GameWorld.HEIGHT / 2f));
    }

    /**
     * Converts the TMX {@code Roads} and {@code Environment} tile layers into
     * flat ground-feature rectangles for the existing renderer. The
     * {@code Buildings} tile layer is skipped because building footprints are
     * already represented by the collision layer obstacles.
     */
    private static List<Rectangle> resolveGroundFeatures(AreaData retreatData) {
        float mapPixelWidth = retreatData.getMapWidthTiles() * retreatData.getTileWidth();
        float mapPixelHeight = retreatData.getMapHeightTiles() * retreatData.getTileHeight();
        float scale = retreatData.getWorldWidth() / mapPixelWidth;
        TiledCoordinateConverter converter =
            new TiledCoordinateConverter(mapPixelWidth, mapPixelHeight, scale);
        TileLayerAdapter adapter =
            new TileLayerAdapter(converter, retreatData.getTileWidth(), retreatData.getTileHeight());

        List<Rectangle> features = new ArrayList<>();
        addLayerFeatures(features, adapter, retreatData.getTileLayer(LAYER_ROADS));
        addLayerFeatures(features, adapter, retreatData.getTileLayer(LAYER_ENVIRONMENT));
        return features;
    }

    private static void addLayerFeatures(List<Rectangle> target, TileLayerAdapter adapter, TileLayerData layer) {
        if (layer != null && !layer.isEmpty()) {
            target.addAll(adapter.toWorldRectangles(layer));
        }
    }

    /**
     * Adds {@link TiledEntrance} markers for Tiled objects of type
     * {@code "entrance"}. Their {@code interiorId} references are preserved for
     * future building wiring.
     */
    private static List<Interactable> mergeEntrances(AreaData retreatData, List<Interactable> base) {
        List<Interactable> result = new ArrayList<>(base);
        for (MapObjectData object : retreatData.getObjects()) {
            if (OBJECT_TYPE_ENTRANCE.equalsIgnoreCase(object.getType())) {
                result.add(new TiledEntrance(object));
            }
        }
        return result;
    }
}
