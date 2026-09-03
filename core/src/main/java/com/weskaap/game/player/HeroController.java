package com.weskaap.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class HeroController {
    public void update(Hero hero, float delta) {
        float horizontal = getHorizontalInput();
        float vertical = getVerticalInput();

        if (horizontal == 0f && vertical == 0f) {
            return;
        }

        float length = (float) Math.sqrt(horizontal * horizontal + vertical * vertical);
        float distance = hero.getMovementSpeed() * delta;
        hero.moveBy(horizontal / length * distance, vertical / length * distance);
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
