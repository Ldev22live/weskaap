package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.weskaap.game.dialogue.DialogueUi;
import com.weskaap.game.player.Hero;
import com.weskaap.game.world.GameWorld;

public class GameHud {

    private HealthBar healthBar;
    private StatusPanel statusPanel;
    private final InteractionPrompt interactionPrompt;
    private MessageLog messageLog;
    private final DialoguePanel dialoguePanel;

    public GameHud(GameWorld world, DialogueUi dialogueUi, float viewportWidth, float viewportHeight) {
        this.interactionPrompt = new InteractionPrompt(UiConstants.PADDING, UiConstants.INTERACTION_PROMPT_Y);
        this.messageLog = new MessageLog(UiConstants.PADDING, viewportHeight - 100f, UiConstants.LINE_HEIGHT);
        this.dialoguePanel = new DialoguePanel(dialogueUi,
            UiConstants.PANEL_MARGIN,
            UiConstants.PANEL_MARGIN,
            viewportWidth - UiConstants.PANEL_MARGIN * 2f,
            UiConstants.DIALOGUE_PANEL_HEIGHT);
        updateLayout(world, viewportWidth, viewportHeight);
    }

    public void setInteractionPromptPosition(float x, float y) {
        interactionPrompt.setPosition(x, y);
    }

    public void resize(GameWorld world, float viewportWidth, float viewportHeight) {
        this.dialoguePanel.resize(
            UiConstants.PANEL_MARGIN,
            UiConstants.PANEL_MARGIN,
            viewportWidth - UiConstants.PANEL_MARGIN * 2f,
            UiConstants.DIALOGUE_PANEL_HEIGHT);
        this.messageLog = new MessageLog(UiConstants.PADDING, viewportHeight - 100f, UiConstants.LINE_HEIGHT);
        updateLayout(world, viewportWidth, viewportHeight);
    }

    private void updateLayout(GameWorld world, float viewportWidth, float viewportHeight) {
        this.healthBar = new HealthBar(world.getHero(),
            UiConstants.PADDING,
            viewportHeight - UiConstants.HEALTH_BAR_HEIGHT - UiConstants.PADDING);
        this.statusPanel = new StatusPanel(world.getHero(),
            viewportWidth - 300f,
            viewportHeight - UiConstants.PADDING);
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, BitmapFontWrapper font,
                       GameWorld world, float viewportWidth, float viewportHeight) {
        updateLayout(world, viewportWidth, viewportHeight);

        healthBar.render(shapeRenderer, spriteBatch, font);
        statusPanel.render(spriteBatch, font);

        messageLog.render(spriteBatch, font,
            world.getInteractionMessage(),
            world.getCombatMessage(),
            world.getAiStateMessage(),
            world.getQuestMessage());

        if (world.isInventoryVisible()) {
            font.draw(spriteBatch, world.getInventoryText(), UiConstants.PADDING,
                viewportHeight - 140f, UiConstants.TEXT_DEFAULT);
        }

        if (world.isQuestLogVisible()) {
            font.draw(spriteBatch, world.getQuestLogText(), UiConstants.PADDING,
                viewportHeight - 160f, UiConstants.TEXT_DEFAULT);
        }

        font.draw(spriteBatch, "[Q] Quest Log", UiConstants.PADDING, UiConstants.QUEST_LOG_PROMPT_Y, UiConstants.HINT_COLOR);

        boolean showInteractPrompt = world.hasCurrentInteractable() && !world.isDialogueActive();
        interactionPrompt.render(shapeRenderer, spriteBatch, font, showInteractPrompt, world.getCurrentPromptText());
    }
}
