package com.weskaap.game.building;

import com.badlogic.gdx.math.Rectangle;

public class InteriorPlatform {
    private final Rectangle bounds;

    public InteriorPlatform(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
