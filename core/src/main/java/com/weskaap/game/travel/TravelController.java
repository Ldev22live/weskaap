package com.weskaap.game.travel;

import com.weskaap.game.world.AreaId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TravelController {
    private final Map<AreaId, TravelDestination> destinations = new EnumMap<>(AreaId.class);
    private final Consumer<AreaId> transitionRequester;
    private AreaId currentArea;
    private boolean menuOpen;

    public TravelController(AreaId currentArea, List<TravelDestination> destinations,
                            Consumer<AreaId> transitionRequester) {
        if (currentArea == null) throw new IllegalArgumentException("Current area cannot be null");
        if (transitionRequester == null) throw new IllegalArgumentException("Transition requester cannot be null");
        this.currentArea = currentArea;
        this.transitionRequester = transitionRequester;
        for (TravelDestination destination : destinations) {
            this.destinations.put(destination.getAreaId(), destination);
        }
    }

    public List<TravelDestination> getDestinations() {
        return List.copyOf(destinations.values());
    }

    public TravelDestination getDestination(AreaId areaId) {
        return destinations.get(areaId);
    }

    public AreaId getCurrentArea() { return currentArea; }
    public boolean isMenuOpen() { return menuOpen; }

    public void openMenu() {
        menuOpen = true;
    }

    public TravelResult cancel() {
        menuOpen = false;
        return TravelResult.CANCELLED;
    }

    public TravelResult requestTravel(AreaId destinationId) {
        TravelDestination destination = destinations.get(destinationId);
        if (destination == null) return TravelResult.UNKNOWN_DESTINATION;
        if (destinationId == currentArea) return TravelResult.CURRENT_AREA;
        if (!destination.isUnlocked()) return TravelResult.LOCKED;
        if (!destination.isImplemented()) return TravelResult.NOT_IMPLEMENTED;
        transitionRequester.accept(destinationId);
        menuOpen = false;
        return TravelResult.SUCCESS;
    }

    public boolean setUnlocked(AreaId areaId, boolean unlocked) {
        TravelDestination destination = destinations.get(areaId);
        if (destination == null) return false;
        destinations.put(areaId, destination.withUnlocked(unlocked));
        return true;
    }

    public void setCurrentArea(AreaId currentArea) {
        if (currentArea == null) throw new IllegalArgumentException("Current area cannot be null");
        this.currentArea = currentArea;
    }
}
