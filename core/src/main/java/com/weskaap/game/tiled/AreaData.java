package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.List;

/**
 * Raw data for an area loaded from a Tiled map.
 *
 * <p>This class is intentionally a lightweight, read-only snapshot of the TMX.
 * It is not tied to runtime entities such as enemies, quests, or dialogue.
 * Higher-level systems (M13) will consume this data to build the runtime world.
 */
public final class AreaData {

    private final String id;
    private final String name;
    private final float tileWidth;
    private final float tileHeight;
    private final int mapWidthTiles;
    private final int mapHeightTiles;
    private final float worldWidth;
    private final float worldHeight;
    private final List<Rectangle> collisionBounds;
    private final List<BuildingData> buildings;
    private final List<MapObjectData> objects;
    private final List<SpawnData> spawns;
    private final List<TileLayerData> tileLayers;

    public AreaData(String id, String name,
                    float tileWidth, float tileHeight,
                    int mapWidthTiles, int mapHeightTiles,
                    float worldWidth, float worldHeight,
                    List<Rectangle> collisionBounds,
                    List<BuildingData> buildings,
                    List<MapObjectData> objects,
                    List<SpawnData> spawns) {
        this(id, name, tileWidth, tileHeight, mapWidthTiles, mapHeightTiles,
            worldWidth, worldHeight, collisionBounds, buildings, objects, spawns, List.of());
    }

    public AreaData(String id, String name,
                    float tileWidth, float tileHeight,
                    int mapWidthTiles, int mapHeightTiles,
                    float worldWidth, float worldHeight,
                    List<Rectangle> collisionBounds,
                    List<BuildingData> buildings,
                    List<MapObjectData> objects,
                    List<SpawnData> spawns,
                    List<TileLayerData> tileLayers) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Area id cannot be blank");
        }
        this.id = id;
        this.name = name == null ? id : name;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.mapWidthTiles = mapWidthTiles;
        this.mapHeightTiles = mapHeightTiles;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.collisionBounds = collisionBounds == null ? List.of() : Collections.unmodifiableList(collisionBounds);
        this.buildings = buildings == null ? List.of() : Collections.unmodifiableList(buildings);
        this.objects = objects == null ? List.of() : Collections.unmodifiableList(objects);
        this.spawns = spawns == null ? List.of() : Collections.unmodifiableList(spawns);
        this.tileLayers = tileLayers == null ? List.of() : Collections.unmodifiableList(tileLayers);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public int getMapWidthTiles() {
        return mapWidthTiles;
    }

    public int getMapHeightTiles() {
        return mapHeightTiles;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public List<Rectangle> getCollisionBounds() {
        return collisionBounds;
    }

    public List<BuildingData> getBuildings() {
        return buildings;
    }

    public List<MapObjectData> getObjects() {
        return objects;
    }

    public List<SpawnData> getSpawns() {
        return spawns;
    }

    public List<TileLayerData> getTileLayers() {
        return tileLayers;
    }

    /** Returns the named tile layer, or {@code null} if it does not exist. */
    public TileLayerData getTileLayer(String name) {
        for (TileLayerData layer : tileLayers) {
            if (layer.getName().equals(name)) {
                return layer;
            }
        }
        return null;
    }
}
