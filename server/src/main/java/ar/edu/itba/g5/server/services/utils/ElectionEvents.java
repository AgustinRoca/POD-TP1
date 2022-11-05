package ar.edu.itba.g5.server.services.utils;

import models.Party;
import models.PollingStation;
import utils.Event;

public abstract class ElectionEvents {
    private static final String PREFIX = "election";

    public static String getEventName(Party party, PollingStation pollingStation) {
        return PREFIX + party.name() + pollingStation;
    }

    public static Event getEvent(Party party, PollingStation pollingStation) {
        return () -> ElectionEvents.getEventName(party, pollingStation);
    }
}
