package com.weskaap.game.player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    public static final float SIZE = 32f;
    public static final float COLLISION_WIDTH = 28f;
    public static final float COLLISION_HEIGHT = 28f;

    private final Vector2 position;
    private final Vector2 facingDirection;
    private final Rectangle collisionBounds;
    private final float movementSpeed;

    public Hero(float x, float y, float movementSpeed) {
        position = new Vector2(x, y);
        facingDirection = new Vector2(0f, -1f);
        collisionBounds = new Rectangle();
        this.movementSpeed = movementSpeed;
        updateCollisionBounds();
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public Vector2 getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(float x, float y) {
        if (Math.abs(x) >= Math.abs(y)) {
            facingDirection.set(Math.signum(x), 0f);
        } else {
            facingDirection.set(0f, Math.signum(y));
        }
    }

    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    public void moveBy(float xAmount, float yAmount) {
        position.add(xAmount, yAmount);
        updateCollisionBounds();
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        updateCollisionBounds();
    }

    private void updateCollisionBounds() {
        collisionBounds.set(
            position.x - COLLISION_WIDTH / 2f,
            position.y - COLLISION_HEIGHT / 2f,
            COLLISION_WIDTH,
            COLLISION_HEIGHT
        );
    }
}
