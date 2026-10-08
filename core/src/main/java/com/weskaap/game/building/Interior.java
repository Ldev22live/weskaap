package com.weskaap.game.building;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.interaction.Interactable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Interior {

    private static final float WALL_THICKNESS = 20f;
    private static final float EXIT_SIZE = 48f;

    private final String id;
    private final float width;
    private final float height;
    private final Vector2 playerSpawn;
    private final Vector2 exteriorReturnPosition;
    private final List<Rectangle> obstacles;
    private final List<Interactable> interactables;
    private final List<InteriorPlatform> platforms;
    private final List<com.weskaap.game.enemy.PrototypeEnemy> interiorEnemies;
    private final BuildingExit exit;
    private boolean combatAllowed;

    public Interior(String id, float width, float height,
                    float spawnX, float spawnY,
                    float exitX, float exitY,
                    Vector2 exteriorReturnPosition) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.playerSpawn = new Vector2(spawnX, spawnY);
        this.exteriorReturnPosition = new Vector2(exteriorReturnPosition);
        this.obstacles = createWalls();
        this.platforms = createPlatforms();
        this.exit = new BuildingExit(exitX, exitY, this);
        this.interactables = new ArrayList<>();
        this.interactables.add(this.exit);
        this.interiorEnemies = new ArrayList<>();
        this.combatAllowed = false;
    }

    public String getId() {
        return id;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getPlayerSpawn() {
        return playerSpawn;
    }

    public Vector2 getExteriorReturnPosition() {
        return exteriorReturnPosition;
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public List<Interactable> getInteractables() {
        return interactables;
    }

    public void addInteractable(Interactable interactable) {
        interactables.add(interactable);
    }

    public void addObstacle(Rectangle obstacle) {
        if (obstacle != null) {
            obstacles.add(obstacle);
        }
    }

    public void addPlatform(InteriorPlatform platform) {
        if (platform != null) {
            platforms.add(platform);
        }
    }

    public BuildingExit getExit() {
        return exit;
    }

    public List<InteriorPlatform> getPlatforms() {
        return platforms;
    }

    public boolean isCombatAllowed() {
        return combatAllowed;
    }

    public void setCombatAllowed(boolean combatAllowed) {
        this.combatAllowed = combatAllowed;
    }

    public void addInteriorEnemy(com.weskaap.game.enemy.PrototypeEnemy enemy) {
        if (enemy != null) {
            interiorEnemies.add(enemy);
        }
    }

    public List<com.weskaap.game.enemy.PrototypeEnemy> getInteriorEnemies() {
        return Collections.unmodifiableList(interiorEnemies);
    }

    public boolean hasInteriorEnemies() {
        return !interiorEnemies.isEmpty();
    }

    private List<InteriorPlatform> createPlatforms() {
        List<InteriorPlatform> result = new ArrayList<>();
        result.add(new InteriorPlatform(0f, 0f, width, WALL_THICKNESS));
        result.add(new InteriorPlatform(65f, 70f, 90f, 14f));
        result.add(new InteriorPlatform(205f, 120f, 105f, 14f));
        result.add(new InteriorPlatform(width - 70f, 45f, 50f, 14f));
        return result;
    }

    private List<Rectangle> createWalls() {
        List<Rectangle> walls = new ArrayList<>();
        walls.add(new Rectangle(0f, 0f, WALL_THICKNESS, height));
        walls.add(new Rectangle(width - WALL_THICKNESS, 0f, WALL_THICKNESS, height));
        walls.add(new Rectangle(0f, 0f, width, WALL_THICKNESS));
        walls.add(new Rectangle(0f, height - WALL_THICKNESS, width, WALL_THICKNESS));
        return walls;
    }
}
