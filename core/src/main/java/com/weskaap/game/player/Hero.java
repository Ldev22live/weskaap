package com.weskaap.game.player;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Hero {
    public static final float SIZE = 32f;

    private float x;
    private float y;
    private final float movementSpeed;

    public Hero(float x, float y, float movementSpeed) {
        this.x = x;
        this.y = y;
        this.movementSpeed = movementSpeed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void moveBy(float xAmount, float yAmount) {
        x += xAmount;
        y += yAmount;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
    }
}
