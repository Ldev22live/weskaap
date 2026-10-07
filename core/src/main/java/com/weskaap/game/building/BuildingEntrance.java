package com.weskaap.game.building;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.interaction.Interactable;

public class BuildingEntrance implements Interactable {

    public static final float SIZE = 48f;
    public static final float INTERACTION_RANGE = 72f;

    private final Vector2 position;
    private final Rectangle bounds;
    private final Building building;

    public BuildingEntrance(float x, float y, Building building) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.building = building;
    }

    @Override
    public Vector2 getInteractionPosition() {
        return position;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public float getInteractionRange() {
        return INTERACTION_RANGE;
    }

    @Override
    public String interact() {
        return "Entering " + building.getId();
    }

    @Override
    public String getPromptText() {
        return "Enter";
    }

    public Building getBuilding() {
        return building;
    }
}
