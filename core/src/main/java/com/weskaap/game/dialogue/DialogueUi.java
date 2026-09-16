package com.weskaap.game.dialogue;

import java.util.List;

public class DialogueUi {

    private final DialogueController controller;

    public DialogueUi(DialogueController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("DialogueController cannot be null");
        }
        this.controller = controller;
    }

    public boolean isActive() {
        return controller.isActive();
    }

    public String getSpeaker() {
        return controller.getCurrentSpeaker();
    }

    public String getText() {
        return controller.getCurrentText();
    }

    public List<DialogueOption> getOptions() {
        return controller.getCurrentOptions();
    }

    public boolean hasOptions() {
        return !getOptions().isEmpty();
    }
}
