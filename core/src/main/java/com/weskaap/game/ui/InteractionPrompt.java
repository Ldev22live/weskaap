package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class InteractionPrompt {

    private float x;
    private float y;

    public InteractionPrompt(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, BitmapFontWrapper font,
                       boolean visible, String promptText) {
        if (!visible) {
            return;
        }

        float bubbleWidth = 140f;
        float bubbleHeight = 72f;
        float bubbleX = x - bubbleWidth / 2f;
        float bubbleY = y + 32f;

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(UiConstants.PANEL_BACKGROUND);
        shapeRenderer.rect(bubbleX + 4, bubbleY - 4, bubbleWidth, bubbleHeight);
        shapeRenderer.triangle(
            x - 10, bubbleY - 4,
            x + 10, bubbleY - 4,
            x, bubbleY - 24
        );
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(UiConstants.PANEL_BORDER);
        shapeRenderer.rect(bubbleX + 4, bubbleY - 4, bubbleWidth, bubbleHeight);
        shapeRenderer.end();

        spriteBatch.begin();
        String text = promptText == null || promptText.isEmpty() ? "[E]" : "[E] " + promptText;
        font.draw(spriteBatch, text, bubbleX, bubbleY + 46, bubbleWidth, Align.center, UiConstants.TEXT_DEFAULT);
    }
}
