package com.weskaap.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.weskaap.game.quest.QuestController;
import java.util.List;

public class DialogueController {

    private Dialogue currentDialogue;
    private DialogueNode currentNode;
    private boolean active;
    private QuestController questController;

    public void setQuestController(QuestController questController) {
        this.questController = questController;
    }

    public void startDialogue(Dialogue dialogue) {
        if (dialogue == null) {
            throw new IllegalArgumentException("Dialogue cannot be null");
        }
        this.currentDialogue = dialogue;
        this.currentNode = dialogue.getStartingNode();
        this.active = true;
        if (currentNode != null && Gdx.app != null) {
            Gdx.app.log("DialogueDebug", "startDialogue: dialogue=" + dialogue.getId()
                + " startingNode=" + currentNode.getId() + " options=" + currentNode.getOptions().size());
        }
    }

    public void endDialogue() {
        this.currentDialogue = null;
        this.currentNode = null;
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Dialogue getCurrentDialogue() {
        return currentDialogue;
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    public String getCurrentSpeaker() {
        return currentNode != null ? currentNode.getSpeaker() : null;
    }

    public String getCurrentText() {
        return currentNode != null ? currentNode.getText() : null;
    }

    public List<DialogueOption> getCurrentOptions() {
        return currentNode != null ? currentNode.getOptions() : List.of();
    }

    public boolean advance() {
        if (!active || currentNode == null) {
            return false;
        }
        if (currentNode.hasOptions()) {
            return false;
        }
        if (currentNode.isTerminal()) {
            endDialogue();
            return true;
        }
        DialogueNode next = currentDialogue.getNode(currentNode.getNextNodeId());
        if (next == null) {
            endDialogue();
            return true;
        }
        currentNode = next;
        return true;
    }

    public boolean selectOption(int index) {
        if (!active || currentNode == null) {
            return false;
        }
        List<DialogueOption> options = currentNode.getOptions();
        if (index < 0 || index >= options.size()) {
            return false;
        }
        DialogueOption selected = options.get(index);
        if (Gdx.app != null) {
            Gdx.app.log("DialogueDebug", "selectOption: index=" + index + " text=" + selected.getText()
                + " questId=" + selected.getQuestId() + " objectiveId=" + selected.getObjectiveId()
                + " target=" + selected.getTargetNodeId());
        }
        DialogueNode next = currentDialogue.getNode(selected.getTargetNodeId());
        if (next == null) {
            endDialogue();
            return true;
        }
        currentNode = next;
        if (questController != null && selected.getQuestId() != null && !selected.getQuestId().isBlank()) {
            questController.startQuest(selected.getQuestId());
        }
        if (questController != null && selected.getObjectiveId() != null && !selected.getObjectiveId().isBlank()) {
            questController.updateObjective(selected.getObjectiveId());
        }
        return true;
    }
}
