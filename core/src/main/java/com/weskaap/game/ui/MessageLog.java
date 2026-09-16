package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MessageLog {

    private final float x;
    private final float startY;
    private final float lineHeight;

    public MessageLog(float x, float startY, float lineHeight) {
        this.x = x;
        this.startY = startY;
        this.lineHeight = lineHeight;
    }

    public void render(SpriteBatch spriteBatch, BitmapFontWrapper font, String... messages) {
        float y = startY;
        for (String message : messages) {
            if (message != null) {
                font.draw(spriteBatch, message, x, y, UiConstants.TEXT_DEFAULT);
            }
            y -= lineHeight;
        }
    }
}
