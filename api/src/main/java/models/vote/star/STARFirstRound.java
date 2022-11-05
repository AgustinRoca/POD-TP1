package models.vote.star;

import models.Party;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class STARFirstRound implements Iterable<STARFirstRoundResult>, Serializable {
    private final List<STARFirstRoundResult> results;

    public STARFirstRound(List<STARFirstRoundResult> firstRoundResults) {
        this.results = new LinkedList<>(firstRoundResults);
        this.results.sort(STARFirstRoundResult::compareTo);
    }

    public List<STARFirstRoundResult> getResults() {
        return new LinkedList<>(this.results);
    }

    public List<Party> getTop2() {
        List<Party> top2 = new LinkedList<>();
        top2.add(results.get(0).getParty());
        top2.add(results.get(1).getParty());
        return top2;
    }

    @Override
    public Iterator<STARFirstRoundResult> iterator() {
        return this.getResults().iterator();
    }
}