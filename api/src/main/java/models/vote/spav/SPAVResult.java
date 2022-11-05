package models.vote.spav;

import models.Party;

import java.io.Serializable;

public class SPAVResult implements Comparable<SPAVResult>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Party party;
    private final double approvalScore;

    public SPAVResult(Party party, double approvalScore) {
        this.party = party;
        this.approvalScore = approvalScore;
    }

    public Party getParty() {
        return this.party;
    }

    public double getApprovalScore() {
        return this.approvalScore;
    }

    @Override
    public int compareTo(SPAVResult o) {
        int ans = Double.compare(o.getApprovalScore(), this.getApprovalScore());
        if (ans == 0) {
            return this.getParty().compareTo(o.getParty());
        } else {
            return ans;
        }
    }
}
