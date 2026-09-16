package com.weskaap.game.interaction;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PrototypeNpc implements Interactable {
    public static final float SIZE = 32f;
    public static final float INTERACTION_RANGE = 72f;

    private final Vector2 position;
    private final Rectangle bounds;
    private final String response;
    private final String questId;
    private final String objectiveId;
    private final com.weskaap.game.dialogue.Dialogue dialogue;

    public PrototypeNpc(float x, float y, String response) {
        this(x, y, response, null, null, null);
    }

    public PrototypeNpc(float x, float y, String response, String questId, String objectiveId) {
        this(x, y, response, questId, objectiveId, null);
    }

    public PrototypeNpc(float x, float y, String response, com.weskaap.game.dialogue.Dialogue dialogue) {
        this(x, y, response, null, null, dialogue);
    }

    public PrototypeNpc(float x, float y, String response, String questId, String objectiveId,
                        com.weskaap.game.dialogue.Dialogue dialogue) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x - SIZE / 2f, y - SIZE / 2f, SIZE, SIZE);
        this.response = response;
        this.questId = questId;
        this.objectiveId = objectiveId;
        this.dialogue = dialogue;
    }

    @Override
    public Vector2 getInteractionPosition() {
        return position;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public float getInteractionRange() {
        return INTERACTION_RANGE;
    }

    @Override
    public String interact() {
        return response;
    }

    public boolean hasQuestObjective() {
        return questId != null && !questId.isBlank()
            && objectiveId != null && !objectiveId.isBlank();
    }

    public String getQuestId() {
        return questId;
    }

    public String getObjectiveId() {
        return objectiveId;
    }

    public boolean hasDialogue() {
        return dialogue != null;
    }

    public com.weskaap.game.dialogue.Dialogue getDialogue() {
        return dialogue;
    }
}
