package com.weskaap.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class HeroController {
    private final Vector2 movement = new Vector2();

    public Vector2 getMovement(Hero hero, float delta) {
        movement.set(getHorizontalInput(), getVerticalInput());
        if (!movement.isZero()) {
            hero.setFacingDirection(movement.x, movement.y);
            movement.nor().scl(hero.getMovementSpeed() * delta);
        }
        return movement;
    }

    private float getHorizontalInput() {
        float horizontal = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontal += 1f;
        }
        return horizontal;
    }

    private float getVerticalInput() {
        float vertical = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vertical -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            vertical += 1f;
        }
        return vertical;
    }
}
