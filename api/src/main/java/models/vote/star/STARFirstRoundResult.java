package models.vote.star;

import models.Party;

import java.io.Serializable;

public class STARFirstRoundResult implements Comparable<STARFirstRoundResult>, Serializable {
    private final Party party;
    private final long score;

    public STARFirstRoundResult(Party party, long score) {
        this.party = party;
        this.score = score;
    }

    public Party getParty() {
        return this.party;
    }

    public long getScore() {
        return this.score;
    }

    @Override
    public int compareTo(STARFirstRoundResult o) {
        int ans = Double.compare(o.getScore(), this.getScore());
        if (ans == 0) {
            return this.getParty().compareTo(o.getParty());
        } else {
            return ans;
        }
    }
}
