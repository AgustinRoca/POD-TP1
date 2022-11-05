package models.vote.star;

import models.Party;

import java.io.Serializable;

public class STARSecondRoundResult implements Comparable<STARSecondRoundResult>, Serializable {
    private final Party party;
    private final double percentage;

    public STARSecondRoundResult(Party party, double percentage) {
        this.party = party;
        this.percentage = percentage;
    }

    public Party getParty() {
        return this.party;
    }

    public double getPercentage() {
        return this.percentage;
    }

    @Override
    public int compareTo(STARSecondRoundResult o) {
        int ans = Double.compare(o.getPercentage(), this.getPercentage());
        if (ans == 0) {
            return this.getParty().compareTo(o.getParty());
        } else {
            return ans;
        }
    }
}
