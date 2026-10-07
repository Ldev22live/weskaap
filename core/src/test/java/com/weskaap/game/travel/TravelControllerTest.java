package com.weskaap.game.travel;

import com.weskaap.game.world.AreaId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TravelControllerTest {
    @Test
    void repositoryContainsInitialDestinations() {
        List<TravelDestination> destinations = TravelDestinationRepository.getDestinations();

        assertTrue(destinations.stream().anyMatch(destination -> destination.getAreaId() == AreaId.RETREAT));
        assertTrue(destinations.stream().anyMatch(destination -> destination.getAreaId() == AreaId.CPUT));
        assertTrue(destinations.stream().anyMatch(destination -> destination.getAreaId() == AreaId.ROSEBANK));
    }

    @Test
    void validDestinationInvokesTransitionOnceAndClosesMenu() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);
        controller.openMenu();

        assertEquals(TravelResult.SUCCESS, controller.requestTravel(AreaId.CPUT));
        assertEquals(List.of(AreaId.CPUT), requests);
        assertFalse(controller.isMenuOpen());
    }

    @Test
    void currentDestinationIsRejected() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);

        assertEquals(TravelResult.CURRENT_AREA, controller.requestTravel(AreaId.RETREAT));
        assertTrue(requests.isEmpty());
    }

    @Test
    void lockedDestinationIsRejected() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);
        controller.setUnlocked(AreaId.CPUT, false);

        assertEquals(TravelResult.LOCKED, controller.requestTravel(AreaId.CPUT));
        assertTrue(requests.isEmpty());
    }

    @Test
    void unknownDestinationIsRejected() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);

        assertEquals(TravelResult.UNKNOWN_DESTINATION, controller.requestTravel(null));
        assertTrue(requests.isEmpty());
    }

    @Test
    void unimplementedDestinationIsRejected() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = new TravelController(AreaId.RETREAT, List.of(
            new TravelDestination(AreaId.CPUT, "CPUT", "Unavailable", true, false)), requests::add);

        assertEquals(TravelResult.NOT_IMPLEMENTED, controller.requestTravel(AreaId.CPUT));
        assertTrue(requests.isEmpty());
    }

    @Test
    void rosebankTravelInvokesExactlyOneRequest() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);

        assertEquals(TravelResult.SUCCESS, controller.requestTravel(AreaId.ROSEBANK));
        assertEquals(List.of(AreaId.ROSEBANK), requests);
    }

    @Test
    void cancelClosesMenuWithoutTransition() {
        List<AreaId> requests = new ArrayList<>();
        TravelController controller = controller(requests);
        controller.openMenu();

        assertEquals(TravelResult.CANCELLED, controller.cancel());
        assertFalse(controller.isMenuOpen());
        assertTrue(requests.isEmpty());
    }

    @Test
    void failedSelectionKeepsMenuOpen() {
        TravelController controller = controller(new ArrayList<>());
        controller.openMenu();
        controller.setUnlocked(AreaId.ROSEBANK, false);

        assertEquals(TravelResult.LOCKED, controller.requestTravel(AreaId.ROSEBANK));
        assertTrue(controller.isMenuOpen());
    }

    private TravelController controller(List<AreaId> requests) {
        return new TravelController(AreaId.RETREAT, TravelDestinationRepository.getDestinations(), requests::add);
    }
}
