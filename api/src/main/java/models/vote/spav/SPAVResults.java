package models.vote.spav;

import models.vote.VotingSystemResults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SPAVResults implements VotingSystemResults {
    private static final long serialVersionUID = 1L;

    public static final int TOTAL_ROUNDS = 3;

    private final List<SPAVRoundResult> rounds = new ArrayList<>(TOTAL_ROUNDS);

    public SPAVResults(SPAVRoundResult firstRound, SPAVRoundResult secondRound, SPAVRoundResult thirdRound) {
        this.rounds.add(firstRound);
        this.rounds.add(secondRound);
        this.rounds.add(thirdRound);
    }

    /**
     * Retorna el resultado de la ronda indicada o tira excepcion si es un numero invalido
     * @param i la ronda, empezando desde 1
     * @return el resultado de la ronda indicada
     */
    public SPAVRoundResult getRound(int i){
        if(i >= 1 && i <= TOTAL_ROUNDS){
            return this.rounds.get(i - 1);
        } else {
            throw new IllegalArgumentException("La votacion solo tiene " + TOTAL_ROUNDS + " rondas");
        }
    }
}
