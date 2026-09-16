package com.weskaap.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.weskaap.game.dialogue.DialogueOption;
import com.weskaap.game.dialogue.DialogueUi;

public class DialoguePanel {

    private final DialogueUi dialogueUi;
    private final Rectangle panelBounds;
    private final float lineHeight;

    public DialoguePanel(DialogueUi dialogueUi, float x, float y, float width, float height) {
        this.dialogueUi = dialogueUi;
        this.panelBounds = new Rectangle(x, y, width, height);
        this.lineHeight = UiConstants.LINE_HEIGHT;
    }

    public void resize(float x, float y, float width, float height) {
        this.panelBounds.set(x, y, width, height);
    }

    public boolean isActive() {
        return dialogueUi.isActive();
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, BitmapFontWrapper font) {
        if (!dialogueUi.isActive()) {
            return;
        }

        drawPanelBackground(shapeRenderer);

        float textX = panelBounds.x + UiConstants.SMALL_PADDING;
        float textY = panelBounds.y + panelBounds.height - UiConstants.SMALL_PADDING;

        font.draw(spriteBatch, dialogueUi.getSpeaker().toUpperCase(), textX, textY, UiConstants.SPEAKER_COLOR);
        textY -= lineHeight;
        font.draw(spriteBatch, "─".repeat(Math.max(1, (int) (panelBounds.width / 10f))), textX, textY, UiConstants.PANEL_BORDER);
        textY -= lineHeight * 1.5f;

        font.draw(spriteBatch, dialogueUi.getText(), textX, textY, UiConstants.TEXT_DEFAULT);
        textY -= lineHeight * 2f;

        if (dialogueUi.hasOptions()) {
            int index = 1;
            for (DialogueOption option : dialogueUi.getOptions()) {
                font.draw(spriteBatch, index + ". " + option.getText(), textX + UiConstants.SMALL_PADDING,
                    textY, UiConstants.OPTION_COLOR);
                textY -= lineHeight;
                index++;
            }
        } else {
            font.draw(spriteBatch, "[E] Continue", panelBounds.x + panelBounds.width - 110f,
                panelBounds.y + UiConstants.SMALL_PADDING + lineHeight, UiConstants.HINT_COLOR);
        }
    }

    private void drawPanelBackground(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(UiConstants.PANEL_BACKGROUND);
        shapeRenderer.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(UiConstants.PANEL_BORDER);
        shapeRenderer.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        shapeRenderer.end();
    }
}
