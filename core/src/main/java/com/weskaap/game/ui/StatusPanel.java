package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.weskaap.game.player.Hero;

public class StatusPanel {

    private final Hero hero;
    private final float x;
    private final float y;

    public StatusPanel(Hero hero, float x, float y) {
        this.hero = hero;
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch spriteBatch, BitmapFontWrapper font) {
        int inventoryCount = hero.getInventory().size();
        int activeQuests = hero.getQuestLog().getActiveQuests().size();
        int completedQuests = hero.getQuestLog().getCompletedQuests().size();

        font.draw(spriteBatch, "Inventory: " + inventoryCount, x, y, UiConstants.OPTION_COLOR);
        font.draw(spriteBatch, "Quests: " + activeQuests + " active | " + completedQuests + " completed",
            x, y - UiConstants.LINE_HEIGHT, UiConstants.OPTION_COLOR);
    }
}
