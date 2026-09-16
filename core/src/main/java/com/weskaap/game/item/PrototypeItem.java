package com.weskaap.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.interaction.Interactable;

public class PrototypeItem implements Interactable {
    public static final float SIZE = 20f;
    public static final float INTERACTION_RANGE = 60f;

    private final Vector2 position;
    private final Rectangle bounds;
    private final Item item;

    public PrototypeItem(float x, float y, Item item) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.item = item;
    }

    public Vector2 getPosition() {
        return position;
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
        return "Picked up: " + item.getName();
    }

    public Item getItem() {
        return item;
    }
}
