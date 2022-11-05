package models.vote.fptp;

import models.Party;
import models.vote.VotingSystemResults;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FPTPResults implements Iterable<FPTPResult>, VotingSystemResults {
    private static final long serialVersionUID = 1L;

    private final List<FPTPResult> results;

    public FPTPResults(Collection<FPTPResult> results) {
        this.results = new LinkedList<>(results);
        this.results.sort(FPTPResult::compareTo);
    }

    public List<FPTPResult> getSortedResultsList() {
        return new LinkedList<>(this.results);
    }

    public Party getWinner() {
        return this.getSortedResultsList().get(0).getParty();
    }

    @Override
    public Iterator<FPTPResult> iterator() {
        return this.getSortedResultsList().iterator();
    }
}
