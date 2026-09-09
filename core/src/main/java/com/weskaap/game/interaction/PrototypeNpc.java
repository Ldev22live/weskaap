package com.weskaap.game.interaction;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PrototypeNpc implements Interactable {
    public static final float SIZE = 32f;
    public static final float INTERACTION_RANGE = 72f;

    private final Vector2 position;
    private final Rectangle bounds;
    private final String response;

    public PrototypeNpc(float x, float y, String response) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.response = response;
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
        return response;
    }
}
