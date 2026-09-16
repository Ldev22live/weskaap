package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class BitmapFontWrapper {

    private final BitmapFont font;

    public BitmapFontWrapper(BitmapFont font) {
        this.font = font;
    }

    public void draw(SpriteBatch spriteBatch, String text, float x, float y) {
        font.draw(spriteBatch, text, x, y);
    }

    public void draw(SpriteBatch spriteBatch, String text, float x, float y, Color color) {
        Color previous = font.getColor();
        font.setColor(color);
        font.draw(spriteBatch, text, x, y);
        font.setColor(previous);
    }

    public void draw(SpriteBatch spriteBatch, String text, float x, float y, float targetWidth,
                     int halign, Color color) {
        Color previous = font.getColor();
        font.setColor(color);
        font.draw(spriteBatch, text, x, y, targetWidth, halign, false);
        font.setColor(previous);
    }
}
