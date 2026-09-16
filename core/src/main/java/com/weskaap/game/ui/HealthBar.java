package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.weskaap.game.player.Hero;

import java.util.Locale;

public class HealthBar {

    private final Hero hero;
    private final float x;
    private final float y;

    public HealthBar(Hero hero, float x, float y) {
        this.hero = hero;
        this.x = x;
        this.y = y;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, BitmapFontWrapper font) {
        float maxHealth = Math.max(1, hero.getMaximumHealth());
        float healthRatio = Math.min(1f, Math.max(0f, hero.getHealth() / maxHealth));
        float fillWidth = UiConstants.HEALTH_BAR_WIDTH * healthRatio;

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(UiConstants.HEALTH_BAR_BACKGROUND);
        shapeRenderer.rect(x, y, UiConstants.HEALTH_BAR_WIDTH, UiConstants.HEALTH_BAR_HEIGHT);
        shapeRenderer.setColor(UiConstants.HEALTH_BAR_FILL);
        shapeRenderer.rect(x, y, fillWidth, UiConstants.HEALTH_BAR_HEIGHT);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(UiConstants.PANEL_BORDER);
        shapeRenderer.rect(x, y, UiConstants.HEALTH_BAR_WIDTH, UiConstants.HEALTH_BAR_HEIGHT);
        shapeRenderer.end();

        spriteBatch.begin();
        String label = String.format(Locale.ROOT, "HP %d / %d", hero.getHealth(), hero.getMaximumHealth());
        font.draw(spriteBatch, label, x, y + UiConstants.HEALTH_BAR_HEIGHT + UiConstants.LINE_HEIGHT,
            UiConstants.TEXT_DEFAULT);
    }
}
