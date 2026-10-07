package com.weskaap.game.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Building {

    public static final float WALL_THICKNESS = 20f;
    public static final float ROOF_THICKNESS = 12f;
    public static final float DOOR_RETURN_OFFSET = 30f;

    private final String id;
    private final String name;
    private final Vector2 position;
    private final float width;
    private final float depth;
    private final float height;
    private final BuildingEntrance entrance;
    private final Interior interior;
    private final List<Rectangle> wallObstacles;

    public Building(String id, String interiorId,
                    float x, float y, float width, float depth, float height,
                    float interiorWidth, float interiorHeight,
                    float spawnX, float spawnY,
                    float exitX, float exitY) {
        this(id, id, interiorId, x, y, width, depth, height,
            interiorWidth, interiorHeight, spawnX, spawnY, exitX, exitY);
    }

    public Building(String id, String name, String interiorId,
                    float x, float y, float width, float depth, float height,
                    float interiorWidth, float interiorHeight,
                    float spawnX, float spawnY,
                    float exitX, float exitY) {
        this.id = id;
        this.name = name;
        this.position = new Vector2(x, y);
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.entrance = new BuildingEntrance(position.x + width / 2f, position.y - WALL_THICKNESS, this);
        Vector2 exteriorReturnPosition = new Vector2(
            entrance.getInteractionPosition().x,
            entrance.getInteractionPosition().y - DOOR_RETURN_OFFSET);
        this.interior = new Interior(interiorId, interiorWidth, interiorHeight, spawnX, spawnY, exitX, exitY, exteriorReturnPosition);
        this.wallObstacles = createWallObstacles();
        if (Gdx.app != null) {
            Gdx.app.log("BuildingDebug", "Created entrance: id=" + id
                + " position=" + entrance.getInteractionPosition()
                + " prompt=" + entrance.getPromptText());
            Gdx.app.log("BuildingDebug", "Created exit: interior=" + interior.getId()
                + " position=" + interior.getExit().getInteractionPosition()
                + " prompt=" + interior.getExit().getPromptText());
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getDepth() {
        return depth;
    }

    public float getHeight() {
        return height;
    }

    public BuildingEntrance getEntrance() {
        return entrance;
    }

    public Interior getInterior() {
        return interior;
    }

    public Vector2 getExteriorReturnPosition() {
        return interior.getExteriorReturnPosition();
    }

    public List<Rectangle> getWallObstacles() {
        return wallObstacles;
    }

    public Rectangle getRoofBounds() {
        return new Rectangle(position.x, position.y, width, depth);
    }

    private List<Rectangle> createWallObstacles() {
        List<Rectangle> walls = new ArrayList<>();
        float doorWidth = BuildingEntrance.SIZE;
        float doorLeft = entrance.getInteractionPosition().x - doorWidth / 2f;
        float doorRight = entrance.getInteractionPosition().x + doorWidth / 2f;

        float leftWidth = doorLeft - position.x;
        if (leftWidth > 0f) {
            walls.add(new Rectangle(position.x, position.y, leftWidth, WALL_THICKNESS));
        }

        float rightX = doorRight;
        float rightWidth = position.x + width - rightX;
        if (rightWidth > 0f) {
            walls.add(new Rectangle(rightX, position.y, rightWidth, WALL_THICKNESS));
        }

        walls.add(new Rectangle(position.x, position.y + depth - WALL_THICKNESS, width, WALL_THICKNESS));
        walls.add(new Rectangle(position.x, position.y, WALL_THICKNESS, depth));
        walls.add(new Rectangle(position.x + width - WALL_THICKNESS, position.y, WALL_THICKNESS, depth));

        return walls;
    }
}
