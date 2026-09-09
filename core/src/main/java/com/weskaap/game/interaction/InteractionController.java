package com.weskaap.game.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InteractionController {
    public boolean isInteractionRequested() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E);
    }
}
