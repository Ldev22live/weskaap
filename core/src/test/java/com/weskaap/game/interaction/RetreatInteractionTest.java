package com.weskaap.game.interaction;

import com.weskaap.game.dialogue.DialogueRepository;
import com.weskaap.game.world.AreaId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RetreatInteractionTest {
    @Test
    void householdNpcHasUniqueIdentityAndDialogue() {
        PrototypeNpc npc = new PrototypeNpc("retreat_tubby_father", "Tubby's Father", 180f, 80f,
            "Make yourself comfortable.",
            DialogueRepository.createPlaceholderDialogue("tubby_father_dialogue", "Tubby's Father",
                "Make yourself comfortable."));

        assertEquals("retreat_tubby_father", npc.getId());
        assertEquals("Tubby's Father", npc.getName());
        assertTrue(npc.hasDialogue());
        assertEquals("Tubby's Father", npc.getDialogue().getStartingNode().getSpeaker());
    }

    @Test
    void retreatStationTargetsCputWithoutCreatingCput() {
        TravelPoint station = new TravelPoint("retreat_station_travel", "Retreat Station", 1600f, 610f, AreaId.CPUT);

        assertEquals(AreaId.CPUT, station.getDestination());
        assertEquals("Travel", station.getPromptText());
        assertEquals("Choose a destination", station.interact());
    }
}
