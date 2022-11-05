package models.vote.star;

import models.Party;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class STARSecondRound implements Iterable<STARSecondRoundResult>, Serializable {

    private final List<STARSecondRoundResult> results = new LinkedList<>();

    public STARSecondRound(STARSecondRoundResult resultA, STARSecondRoundResult resultB) {
        this.results.add(resultA);
        this.results.add(resultB);
        this.results.sort(STARSecondRoundResult::compareTo);
    }

    public List<STARSecondRoundResult> getResults() {
        return new LinkedList<>(this.results);
    }

    public Party getWinner() {
        return this.results.get(0).getParty();
    }

    @Override
    public Iterator<STARSecondRoundResult> iterator() {
        return this.getResults().iterator();
    }
}