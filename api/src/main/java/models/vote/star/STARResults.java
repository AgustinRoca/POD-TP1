package models.vote.star;

import models.Party;
import models.vote.VotingSystemResults;

import java.io.Serializable;

public class STARResults implements VotingSystemResults {
    private static final long serialVersionUID = 1L;

    private final STARFirstRound firstRound;
    private final STARSecondRound secondRound;

    public STARResults(STARFirstRound firstRound, STARSecondRound secondRound) {
        this.firstRound = firstRound;
        this.secondRound = secondRound;
    }

    public STARFirstRound getFirstRound() {
        return this.firstRound;
    }

    public STARSecondRound getSecondRound() {
        return this.secondRound;
    }

    public Party getWinner() {
        return this.secondRound.getWinner();
    }
}
