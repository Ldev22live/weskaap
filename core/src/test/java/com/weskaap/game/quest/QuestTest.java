package com.weskaap.game.quest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {

    private Quest createTestQuest() {
        return new Quest(
            "q_test",
            "Test Quest",
            "A test quest.",
            List.of(new QuestObjective(
                "obj_1",
                "Do the thing.",
                QuestObjectiveType.TALK_TO_NPC,
                "target",
                2
            ))
        );
    }

    @Test
    void questStartsCorrectly() {
        Quest quest = createTestQuest();
        assertTrue(quest.start());
        assertTrue(quest.isActive());
        assertFalse(quest.isCompleted());
    }

    @Test
    void questCannotBeStartedTwice() {
        Quest quest = createTestQuest();
        assertTrue(quest.start());
        assertFalse(quest.start());
        assertTrue(quest.isActive());
    }

    @Test
    void questCompletesCorrectly() {
        Quest quest = createTestQuest();
        assertTrue(quest.start());
        assertTrue(quest.complete());
        assertTrue(quest.isCompleted());
        assertFalse(quest.isActive());
    }

    @Test
    void completedQuestCannotBecomeActive() {
        Quest quest = createTestQuest();
        quest.start();
        quest.complete();
        assertFalse(quest.start());
        assertTrue(quest.isCompleted());
    }

    @Test
    void questFailsFromActive() {
        Quest quest = createTestQuest();
        quest.start();
        assertTrue(quest.fail());
        assertTrue(quest.isFailed());
    }
}
