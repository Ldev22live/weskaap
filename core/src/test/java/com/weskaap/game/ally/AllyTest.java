package com.weskaap.game.ally;

import com.weskaap.game.dialogue.Dialogue;
import com.weskaap.game.dialogue.DialogueController;
import com.weskaap.game.dialogue.DialogueRepository;
import com.weskaap.game.interaction.PrototypeNpc;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllyTest {
    @Test
    void rosebankFriendUsesExistingNpcAndDialogueArchitecture() {
        Dialogue dialogue = DialogueRepository.createRosebankFriendDialogue();
        PrototypeNpc friend = new PrototypeNpc("rosebank_friend", "RosebankFriend", 1800f, 1220f,
            "You still have a friend in Rosebank.", dialogue);
        DialogueController controller = new DialogueController();

        assertEquals("rosebank_friend", friend.getId());
        assertTrue(friend.hasDialogue());
        controller.startDialogue(friend.getDialogue());
        assertEquals("RosebankFriend", controller.getCurrentSpeaker());
        assertTrue(controller.advance());
        assertTrue(controller.getCurrentText().contains("friend in Rosebank"));
    }

    @Test
    void relationshipCanAdvanceWithoutCompanionSystems() {
        Ally ally = new Ally("rosebank_friend");

        assertEquals(AllyRelationshipState.RECOGNIZED, ally.getRelationshipState());
        ally.establishAlliance();
        assertTrue(ally.isAlly());
    }
}
