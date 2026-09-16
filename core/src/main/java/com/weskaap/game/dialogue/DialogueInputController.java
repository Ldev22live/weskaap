package com.weskaap.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class DialogueInputController {

    private final DialogueController dialogueController;

    public DialogueInputController(DialogueController dialogueController) {
        if (dialogueController == null) {
            throw new IllegalArgumentException("DialogueController cannot be null");
        }
        this.dialogueController = dialogueController;
    }

    public void update() {
        if (!dialogueController.isActive()) {
            return;
        }
        DialogueNode node = dialogueController.getCurrentNode();
        if (node == null) {
            return;
        }
        if (node.hasOptions()) {
            handleOptionInput(node);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            dialogueController.advance();
        }
    }

    private void handleOptionInput(DialogueNode node) {
        int optionCount = node.getOptions().size();
        for (int i = 0; i < optionCount; i++) {
            if (Gdx.input.isKeyJustPressed(optionKeyForIndex(i))) {
                dialogueController.selectOption(i);
                return;
            }
        }
    }

    private int optionKeyForIndex(int index) {
        return switch (index) {
            case 0 -> Input.Keys.NUM_1;
            case 1 -> Input.Keys.NUM_2;
            case 2 -> Input.Keys.NUM_3;
            case 3 -> Input.Keys.NUM_4;
            case 4 -> Input.Keys.NUM_5;
            case 5 -> Input.Keys.NUM_6;
            case 6 -> Input.Keys.NUM_7;
            case 7 -> Input.Keys.NUM_8;
            case 8 -> Input.Keys.NUM_9;
            default -> Input.Keys.UNKNOWN;
        };
    }
}
