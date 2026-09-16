package com.weskaap.game.dialogue;

public class DialogueOption {

    private final String text;
    private final String targetNodeId;
    private final String questId;
    private final String objectiveId;

    public DialogueOption(String text, String targetNodeId) {
        this(text, targetNodeId, null, null);
    }

    public DialogueOption(String text, String targetNodeId, String questId) {
        this(text, targetNodeId, questId, null);
    }

    public DialogueOption(String text, String targetNodeId, String questId, String objectiveId) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Option text cannot be blank");
        }
        if (targetNodeId == null || targetNodeId.isBlank()) {
            throw new IllegalArgumentException("Option target node id cannot be blank");
        }
        this.text = text;
        this.targetNodeId = targetNodeId;
        this.questId = questId;
        this.objectiveId = objectiveId;
    }

    public String getText() {
        return text;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public String getQuestId() {
        return questId;
    }

    public String getObjectiveId() {
        return objectiveId;
    }
}
