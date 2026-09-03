package com.weskaap.game;

import com.badlogic.gdx.Game;
import com.weskaap.game.screens.MainGameScreen;

public class WeskaapGame extends Game {
    @Override
    public void create() {
        setScreen(new MainGameScreen());
    }
}
