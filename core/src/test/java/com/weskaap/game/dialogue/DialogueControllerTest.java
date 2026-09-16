package com.weskaap.game.dialogue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DialogueControllerTest {

    @Test
    void initialStateIsInactive() {
        DialogueController controller = new DialogueController();
        assertFalse(controller.isActive());
        assertNull(controller.getCurrentDialogue());
        assertNull(controller.getCurrentNode());
        assertNull(controller.getCurrentSpeaker());
        assertNull(controller.getCurrentText());
        assertTrue(controller.getCurrentOptions().isEmpty());
    }

    @Test
    void startDialogueActivatesControllerAndSetsStartingNode() {
        DialogueController controller = new DialogueController();
        Dialogue dialogue = createBranchingDialogue();

        controller.startDialogue(dialogue);

        assertTrue(controller.isActive());
        assertSame(dialogue, controller.getCurrentDialogue());
        assertNotNull(controller.getCurrentNode());
        assertEquals("Tubby Angel", controller.getCurrentSpeaker());
        assertEquals("What do you seek?", controller.getCurrentText());
        assertEquals(2, controller.getCurrentOptions().size());
    }

    @Test
    void advanceOnChoiceNodeDoesNothing() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());

        boolean advanced = controller.advance();

        assertFalse(advanced);
        assertEquals("What do you seek?", controller.getCurrentText());
    }

    @Test
    void selectingOptionOneReachesCorrectBranch() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());

        boolean selected = controller.selectOption(0);

        assertTrue(selected);
        assertTrue(controller.isActive());
        assertEquals("Then you must continue your journey.", controller.getCurrentText());
    }

    @Test
    void selectingOptionTwoReachesCorrectBranch() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());

        boolean selected = controller.selectOption(1);

        assertTrue(selected);
        assertTrue(controller.isActive());
        assertEquals("I can only guide you so far.", controller.getCurrentText());
    }

    @Test
    void bothBranchesReachTerminalNode() {
        DialogueController controllerA = new DialogueController();
        controllerA.startDialogue(createBranchingDialogue());
        controllerA.selectOption(0);
        controllerA.advance();

        assertTrue(controllerA.isActive());
        assertEquals("Go carefully.", controllerA.getCurrentText());

        DialogueController controllerB = new DialogueController();
        controllerB.startDialogue(createBranchingDialogue());
        controllerB.selectOption(1);
        controllerB.advance();

        assertTrue(controllerB.isActive());
        assertEquals("Go carefully.", controllerB.getCurrentText());
    }

    @Test
    void advanceOnTerminalNodeEndsDialogue() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());
        controller.selectOption(0);
        controller.advance();

        assertTrue(controller.isActive());
        boolean ended = controller.advance();

        assertTrue(ended);
        assertFalse(controller.isActive());
        assertNull(controller.getCurrentNode());
        assertNull(controller.getCurrentSpeaker());
        assertNull(controller.getCurrentText());
    }

    @Test
    void endDialogueManuallyClearsState() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());

        controller.endDialogue();

        assertFalse(controller.isActive());
        assertNull(controller.getCurrentDialogue());
        assertNull(controller.getCurrentNode());
    }

    @Test
    void invalidOptionIndexDoesNotCrash() {
        DialogueController controller = new DialogueController();
        controller.startDialogue(createBranchingDialogue());

        assertFalse(controller.selectOption(-1));
        assertFalse(controller.selectOption(5));
        assertEquals("What do you seek?", controller.getCurrentText());
    }

    @Test
    void operationsDoNothingWhenInactive() {
        DialogueController controller = new DialogueController();

        assertFalse(controller.advance());
        assertFalse(controller.selectOption(0));
        assertTrue(controller.getCurrentOptions().isEmpty());
    }

    private Dialogue createBranchingDialogue() {
        return DialogueRepository.createTubbyAngelDialogue();
    }
}
