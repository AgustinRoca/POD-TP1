package models.vote;

import java.io.Serializable;

public class VoteResult<WhileOpen extends VotingSystemResults, WhenFinished extends VotingSystemResults> implements Serializable {
    private final WhileOpen whileOpen;
    private final WhenFinished whenFinished;

    protected VoteResult(WhileOpen whileOpen, WhenFinished whenFinished) {
        this.whileOpen = whileOpen;
        this.whenFinished = whenFinished;
    }

    public WhileOpen getWhileOpenResults() {
        if (!this.isOpenElectionsResults()) throw new IllegalStateException();
        return this.whileOpen;
    }

    public WhenFinished getWhenFinishedResults() {
        if (this.isOpenElectionsResults()) throw new IllegalStateException();
        return this.whenFinished;
    }

    public boolean isOpenElectionsResults(){
        return this.whileOpen != null;
    }

}
