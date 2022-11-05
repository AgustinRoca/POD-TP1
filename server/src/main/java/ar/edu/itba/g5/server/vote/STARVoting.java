package ar.edu.itba.g5.server.vote;

import models.Party;
import models.vote.star.*;

import java.util.*;

public class STARVoting {
    private final Collection<Map<Party, Integer>> votes = new LinkedList<>();

    public synchronized void registerVote(final Map<Party, Integer> vote) {
        this.votes.add(vote);
    }

    // No hace falta sincronizar porque no puede llamarse mientras se registran votos
    public STARResults getResults() {
        STARFirstRound firstRound = this.firstRound();

        return new STARResults(firstRound, this.secondRound(firstRound));
    }

    // No hace falta sincronizar porque no puede llamarse mientras se registran votos
    private STARFirstRound firstRound() {
        Map<Party, Long> count = new HashMap<>();

        for (Map<Party, Integer> vote : this.votes) {
            for (Party party : vote.keySet()) {
                count.put(party, count.getOrDefault(party, 0L) + vote.get(party));
            }
        }

        List<STARFirstRoundResult> results = new LinkedList<>();
        for (Party party : count.keySet()) {
            results.add(new STARFirstRoundResult(party, count.get(party)));
        }

        return new STARFirstRound(results);
    }

    // No hace falta sincronizar porque no puede llamarse mientras se registran votos
    private STARSecondRound secondRound(STARFirstRound firstRound) {
        Party winnerA = firstRound.getTop2().get(0);
        Party winnerB = firstRound.getTop2().get(1);
        long totalVotes = 0;
        long totalA = 0;
        long totalB = 0;

        for (Map<Party, Integer> vote : this.votes) {
            int scoreForA = vote.getOrDefault(winnerA, 0);
            int scoreForB = vote.getOrDefault(winnerB, 0);
            if (scoreForA > scoreForB) {
                totalA++;
                totalVotes++;
            } else if (scoreForB > scoreForA) {
                totalB++;
                totalVotes++;
            }
        }

        double percentageA = (double) totalA / totalVotes;
        double percentageB = (double) totalB / totalVotes;

        return new STARSecondRound(
                new STARSecondRoundResult(winnerA, percentageA),
                new STARSecondRoundResult(winnerB, percentageB));
    }
}
