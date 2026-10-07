package com.weskaap.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.building.Building;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.interaction.Interactable;
import com.weskaap.game.item.PrototypeItem;

import java.util.List;

public final class AreaDefinition {
    private final AreaId id;
    private final String displayName;
    private final float width;
    private final float height;
    private final Vector2 spawnPoint;
    private final List<Rectangle> obstacles;
    private final List<Rectangle> groundFeatures;
    private final List<Building> buildings;
    private final List<Interactable> interactables;
    private final List<PrototypeItem> items;
    private final List<PrototypeEnemy> enemies;

    public AreaDefinition(AreaId id, String displayName, float width, float height, Vector2 spawnPoint,
                          List<Rectangle> obstacles, List<Rectangle> groundFeatures, List<Building> buildings,
                          List<Interactable> interactables, List<PrototypeItem> items, List<PrototypeEnemy> enemies) {
        this.id = id;
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.spawnPoint = new Vector2(spawnPoint);
        this.obstacles = List.copyOf(obstacles);
        this.groundFeatures = List.copyOf(groundFeatures);
        this.buildings = List.copyOf(buildings);
        this.interactables = List.copyOf(interactables);
        this.items = List.copyOf(items);
        this.enemies = List.copyOf(enemies);
    }

    public AreaId getId() { return id; }
    public String getDisplayName() { return displayName; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Vector2 getSpawnPoint() { return new Vector2(spawnPoint); }
    public List<Rectangle> getObstacles() { return obstacles; }
    public List<Rectangle> getGroundFeatures() { return groundFeatures; }
    public List<Building> getBuildings() { return buildings; }
    public List<Interactable> getInteractables() { return interactables; }
    public List<PrototypeItem> getItems() { return items; }
    public List<PrototypeEnemy> getEnemies() { return enemies; }
}
