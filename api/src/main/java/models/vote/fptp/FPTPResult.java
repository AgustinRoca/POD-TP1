package models.vote.fptp;

import models.Party;

import java.io.Serializable;

public class FPTPResult implements Comparable<FPTPResult>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Party party;
    private final double percentage;

    public FPTPResult(Party party, double percentage) {
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
    public int compareTo(FPTPResult o) {
        int ans = Double.compare(o.getPercentage(), this.getPercentage()); // Porcentaje mas grande primero
        if (ans == 0) {
            return this.getParty().compareTo(o.getParty()); // En caso de empate, el primero alfabeticamente
        } else {
            return ans;
        }
    }
}
