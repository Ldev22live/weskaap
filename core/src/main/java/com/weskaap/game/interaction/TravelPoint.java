package com.weskaap.game.interaction;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.world.AreaId;

public class TravelPoint implements Interactable {
    public static final float SIZE = 48f;
    public static final float INTERACTION_RANGE = 80f;

    private final String id;
    private final String name;
    private final Vector2 position;
    private final Rectangle bounds;
    private final AreaId destination;

    public TravelPoint(String id, String name, float x, float y, AreaId destination) {
        this.id = id;
        this.name = name;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AreaId getDestination() {
        return destination;
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
        return "Choose a destination";
    }

    @Override
    public String getPromptText() {
        return "Travel";
    }
}
