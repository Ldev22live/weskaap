package com.weskaap.game.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class QuestLogController {

    public boolean isToggleRequested() {
        return Gdx.input.isKeyJustPressed(Input.Keys.Q);
    }
}
