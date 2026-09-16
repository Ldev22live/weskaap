package com.weskaap.game.quest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestObjectiveTest {

    @Test
    void objectiveStartsAtZero() {
        QuestObjective objective = new QuestObjective(
            "obj_1", "Speak with NPC.", QuestObjectiveType.TALK_TO_NPC, "npc_1", 3);
        assertEquals(0, objective.getCurrentAmount());
        assertEquals(3, objective.getRequiredAmount());
        assertFalse(objective.isComplete());
    }

    @Test
    void progressIncrementsCorrectly() {
        QuestObjective objective = new QuestObjective(
            "obj_1", "Speak with NPC.", QuestObjectiveType.TALK_TO_NPC, "npc_1", 3);
        assertTrue(objective.incrementProgress());
        assertEquals(1, objective.getCurrentAmount());
        assertFalse(objective.isComplete());
    }

    @Test
    void progressCannotExceedRequiredAmount() {
        QuestObjective objective = new QuestObjective(
            "obj_1", "Speak with NPC.", QuestObjectiveType.TALK_TO_NPC, "npc_1", 2);
        objective.incrementProgress();
        objective.incrementProgress();
        assertFalse(objective.incrementProgress());
        assertEquals(2, objective.getCurrentAmount());
    }

    @Test
    void becomesCompleteAtRequiredAmount() {
        QuestObjective objective = new QuestObjective(
            "obj_1", "Speak with NPC.", QuestObjectiveType.TALK_TO_NPC, "npc_1", 1);
        assertTrue(objective.incrementProgress());
        assertEquals(1, objective.getCurrentAmount());
        assertTrue(objective.isComplete());
    }
}
