package com.weskaap.game.travel;

import com.weskaap.game.world.AreaId;

import java.util.List;

public final class TravelDestinationRepository {
    private static final List<TravelDestination> DESTINATIONS = List.of(
        new TravelDestination(AreaId.RETREAT, "Retreat", "The current Act 1 neighbourhood.", true, true),
        new TravelDestination(AreaId.CPUT, "CPUT", "Continue toward the CPUT area.", true, true),
        new TravelDestination(AreaId.ROSEBANK, "Rosebank", "Meet an old friend in Rosebank.", true, true)
    );

    private TravelDestinationRepository() {
    }

    public static List<TravelDestination> getDestinations() {
        return DESTINATIONS;
    }
}
