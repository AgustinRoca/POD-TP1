package models.vote.spav;

import models.Party;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SPAVRoundResult implements Iterable<SPAVResult>, Serializable {
    private static final long serialVersionUID = 1L;

    private final List<SPAVResult> resultList;
    private final List<Party> winners;

    public SPAVRoundResult(Map<Party, Double> roundResults, List<Party> previousWinners) {
        this.resultList = new LinkedList<>();
        for (Party party: roundResults.keySet()){
            resultList.add(new SPAVResult(party, roundResults.get(party)));
        }
        resultList.sort(SPAVResult::compareTo);

        this.winners = new LinkedList<>(previousWinners);
        this.winners.add(resultList.get(0).getParty());
    }

    public List<SPAVResult> getResultList() {
        return new LinkedList<>(this.resultList);
    }

    public List<Party> getWinners() {
        return new LinkedList<>(this.winners);
    }

    @Override
    public Iterator<SPAVResult> iterator() {
        return this.getResultList().iterator();
    }
}
