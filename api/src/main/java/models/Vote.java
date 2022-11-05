package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Vote implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PollingStation pollingStation;
    private final Province province;
    private final Map<Party, Integer> rankedVotes;
    private final Party fptpVote;

    public Vote(PollingStation pollingStation, Province province, Map<Party, Integer> rankedVotes, Party fptpVote) {
        this.pollingStation = pollingStation;
        this.province = province;
        this.rankedVotes = rankedVotes;
        this.fptpVote = fptpVote;
    }

    public PollingStation getPollingStation() {
        return this.pollingStation;
    }

    public Province getProvince() {
        return this.province;
    }

    public Map<Party, Integer> getSTARVotes() {
        return this.rankedVotes;
    }

    public Map<Party, Boolean> getSPAVVotes() {
        Map<Party, Boolean> ans = new HashMap<>();
        for (Party party : this.rankedVotes.keySet()){
            ans.put(party, this.rankedVotes.getOrDefault(party, 0) > 0);
        }
        return ans;
    }

    public Party getFPTPVote() {
        return this.fptpVote;
    }
}
