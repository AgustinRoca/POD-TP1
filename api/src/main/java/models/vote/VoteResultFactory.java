package models.vote;


public class VoteResultFactory {
    // Suppresses default constructor, ensuring non-instantiability.
    private VoteResultFactory(){
    }

    public static <WhileOpen extends VotingSystemResults, WhenFinished extends VotingSystemResults>
    VoteResult<WhileOpen, WhenFinished>
    withOpenElection(WhileOpen whileOpen)
    {
        return new VoteResult<>(whileOpen, null);
    }

    public static <WhileOpen extends VotingSystemResults, WhenFinished extends VotingSystemResults>
    VoteResult<WhileOpen, WhenFinished>
    withFinishedElection(WhenFinished whenFinished)
    {
        return new VoteResult<>(null, whenFinished);
    }
}
