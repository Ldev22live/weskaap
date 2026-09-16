package com.weskaap.game.quest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestControllerTest {

    @Test
    void repositoryProvidesTubbyAngelQuest() {
        Quest quest = QuestRepository.getQuest("tubby_angel_001");
        assertNotNull(quest);
        assertEquals("The Way Forward", quest.getTitle());
        assertEquals(1, quest.getObjectives().size());
    }

    @Test
    void validQuestCanStart() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        assertTrue(controller.startQuest("tubby_angel_001"));
        assertTrue(controller.isQuestActive("tubby_angel_001"));
        assertFalse(controller.isQuestCompleted("tubby_angel_001"));
    }

    @Test
    void invalidQuestIdIsHandledSafely() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        assertFalse(controller.startQuest("unknown_quest"));
        assertFalse(controller.isQuestActive("unknown_quest"));
        assertFalse(controller.isQuestCompleted("unknown_quest"));
        assertNull(controller.getQuest("unknown_quest"));
    }

    @Test
    void objectiveProgressUpdatesAndCompletesQuest() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        controller.startQuest("tubby_angel_001");
        assertTrue(controller.updateObjective("talk_to_tubby"));

        assertTrue(controller.isQuestCompleted("tubby_angel_001"));
    }

    @Test
    void invalidObjectiveIdIsHandledSafely() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        controller.startQuest("tubby_angel_001");
        assertFalse(controller.updateObjective("unknown_objective"));
    }

    @Test
    void completingQuestDirectlyWorks() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        controller.startQuest("tubby_angel_001");
        assertTrue(controller.completeQuest("tubby_angel_001"));
        assertTrue(controller.isQuestCompleted("tubby_angel_001"));
    }

    @Test
    void activeQuestsListIsUpdated() {
        QuestLog log = new QuestLog();
        QuestController controller = new QuestController(log);

        assertTrue(controller.getActiveQuests().isEmpty());
        controller.startQuest("tubby_angel_001");
        assertEquals(1, controller.getActiveQuests().size());
    }
}
