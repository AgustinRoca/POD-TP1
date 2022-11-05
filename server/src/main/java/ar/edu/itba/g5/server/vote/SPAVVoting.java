package ar.edu.itba.g5.server.vote;

import models.Party;
import models.vote.spav.SPAVRoundResult;

import java.util.*;

public class SPAVVoting {
    private final Collection<Map<Party, Boolean>> votes = new LinkedList<>();

    public synchronized void registerVote(final Map<Party, Boolean> vote) {
        this.votes.add(vote);
    }

    // No hace falta sincronizar porque no puede llamarse mientras se registran votos
    public SPAVRoundResult nextRound(final List<Party> previousWinners) {
        Map<Party, Double> roundScores = new HashMap<>();

        for (Map<Party, Boolean> vote : this.votes) {
            int n = 1;
            for (Party party : previousWinners) {
                if (vote.getOrDefault(party, false)) {
                    n++;
                }
            }

            for (Party party : vote.keySet()) {
                if (!previousWinners.contains(party)) {
                    roundScores.put(party, roundScores.getOrDefault(party, 0.0) + 1.0 / n);
                }
            }
        }

        return new SPAVRoundResult(roundScores, previousWinners);
    }
}
