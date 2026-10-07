package com.weskaap.game.quest;

import com.weskaap.game.dialogue.DialogueRepository;
import com.weskaap.game.interaction.PrototypeNpc;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestFoundationTest {
    @Test
    void questMovesThroughAcceptedInProgressAndCompletedStates() {
        Quest quest = quest("lifecycle", List.of(), null);

        assertTrue(quest.accept());
        assertEquals(QuestState.ACCEPTED, quest.getState());
        assertTrue(quest.begin());
        assertEquals(QuestState.IN_PROGRESS, quest.getState());
        quest.findObjective("objective").incrementProgress();
        assertTrue(quest.complete());
        assertEquals(QuestState.COMPLETED, quest.getState());
    }

    @Test
    void decliningLeavesQuestAvailable() {
        QuestController controller = new QuestController(new QuestLog());

        assertTrue(controller.declineQuest("retreat_tubby_welcome"));
        assertTrue(controller.isQuestAvailable("retreat_tubby_welcome"));
        assertNull(controller.getQuest("retreat_tubby_welcome"));
    }

    @Test
    void prerequisiteCompletionUnlocksFollowUp() {
        QuestController controller = new QuestController(new QuestLog());

        assertFalse(controller.isQuestAvailable("retreat_station_route"));
        assertTrue(controller.startQuest("retreat_tubby_welcome"));
        assertTrue(controller.updateObjective(QuestObjectiveType.TALK_TO_NPC, "retreat_tubby_father"));
        assertTrue(controller.isQuestCompleted("retreat_tubby_welcome"));
        assertTrue(controller.isQuestAvailable("retreat_station_route"));
    }

    @Test
    void npcExposesAvailableActiveAndCompletedQuests() {
        QuestController controller = new QuestController(new QuestLog());
        PrototypeNpc tubby = new PrototypeNpc("retreat_tubby", "Tubby", 100f, 80f, "Welcome home.",
            DialogueRepository.createTubbyAngelDialogue());

        assertTrue(tubby.isQuestGiver());
        assertEquals("retreat_tubby_welcome", tubby.getAvailableQuests(controller).get(0).getId());
        controller.startQuest("retreat_tubby_welcome");
        assertEquals(1, tubby.getActiveQuests(controller).size());
        controller.updateObjective(QuestObjectiveType.TALK_TO_NPC, "retreat_tubby_father");
        assertEquals(1, tubby.getCompletedQuests(controller).size());
    }

    @Test
    void sixRetreatQuestGiversHaveDefinitions() {
        String[] giverIds = {"retreat_tubby", "retreat_tubby_father", "retreat_tubby_mother",
            "retreat_tubby_police_sister", "retreat_tubby_brother", "retreat_older_sister"};

        for (String giverId : giverIds) {
            assertFalse(QuestRepository.getQuestsForNpc(giverId).isEmpty(), giverId);
        }
    }

    @Test
    void npcWithoutQuestKeepsNormalDialogue() {
        PrototypeNpc npc = new PrototypeNpc("ordinary_npc", "Neighbour", 10f, 20f, "Howzit.",
            DialogueRepository.createMeetTheNeighbourDialogue());

        assertFalse(npc.isQuestGiver());
        assertTrue(npc.hasDialogue());
    }

    private Quest quest(String id, List<String> prerequisites, String followUp) {
        return new Quest(id, "Title", "Description", "giver",
            List.of(new QuestObjective("objective", "Do something", QuestObjectiveType.TALK_TO_NPC, "target", 1)),
            prerequisites, followUp);
    }
}
