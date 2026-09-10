package com.weskaap.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InventoryController {
    public boolean isToggleRequested() {
        return Gdx.input.isKeyJustPressed(Input.Keys.I);
    }
}
