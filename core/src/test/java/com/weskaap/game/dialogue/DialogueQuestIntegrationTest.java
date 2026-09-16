package com.weskaap.game.dialogue;

import com.weskaap.game.quest.QuestController;
import com.weskaap.game.quest.QuestLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogueQuestIntegrationTest {

    @Test
    void selectingQuestOptionStartsAndProgressesTubbyAngelQuest() {
        QuestLog log = new QuestLog();
        QuestController questController = new QuestController(log);

        DialogueController dialogueController = new DialogueController();
        dialogueController.setQuestController(questController);
        dialogueController.startDialogue(DialogueRepository.createTubbyAngelDialogue());

        assertEquals("What do you seek?", dialogueController.getCurrentText());

        assertTrue(dialogueController.selectOption(0));

        assertEquals("Then you must continue your journey.", dialogueController.getCurrentText());
        assertTrue(questController.isQuestCompleted("tubby_angel_001"));
    }
}
