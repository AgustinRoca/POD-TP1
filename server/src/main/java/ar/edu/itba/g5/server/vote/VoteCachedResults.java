package ar.edu.itba.g5.server.vote;

import ar.edu.itba.g5.server.services.utils.ElectionEventBus;
import ar.edu.itba.g5.server.services.utils.ElectionEvents;
import ar.edu.itba.g5.server.services.utils.ElectionStatusAware;
import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.ElectionStatus;
import models.PollingStation;
import models.Province;
import models.Vote;
import models.vote.VoteResult;
import models.vote.VoteResultFactory;
import models.vote.fptp.FPTPResults;
import models.vote.spav.SPAVResult;
import models.vote.spav.SPAVResults;
import models.vote.spav.SPAVRoundResult;
import models.vote.star.STARResults;
import service.QueryService;
import service.VoteService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VoteCachedResults {
    private final STARVoting nationalSTARVoting;
    private STARResults cachedNationalResults;
    private final Lock nationalResultsLock = new ReentrantLock();

    private final Map<Province, SPAVVoting> provinceSPAVVoting;
    private final Map<Province, SPAVResults> cachedProvinceResults = new HashMap<>();
    private final Lock provinceResultsLock = new ReentrantLock();

    private final Map<PollingStation, FPTPVoting> pollingStationFPTPVoting;
    private final Map<PollingStation, FPTPResults> cachedPollingStationResults = new HashMap<>();
    private final Lock pollingStationResultsLock = new ReentrantLock();


    public VoteCachedResults(STARVoting nationalSTARVoting, Map<Province, SPAVVoting> provinceSPAVVoting, Map<PollingStation, FPTPVoting> pollingStationFPTPVoting) {
        this.nationalSTARVoting = nationalSTARVoting;
        this.provinceSPAVVoting = provinceSPAVVoting;
        this.pollingStationFPTPVoting = pollingStationFPTPVoting;
    }

    public STARResults getNationalResults() {
        if (this.cachedNationalResults == null) {
            this.nationalResultsLock.lock();

            if (this.cachedNationalResults == null) {
                // Si sigue null nadie lo creo --> lo creo yo
                this.cachedNationalResults = this.nationalSTARVoting.getResults();
            }

            this.nationalResultsLock.unlock();
        }

        return this.cachedNationalResults;
    }

    public SPAVResults getProvinceResults(Province province) {
        SPAVResults cachedSPAVResults = this.cachedProvinceResults.get(province);
        if (cachedSPAVResults == null) {
            this.provinceResultsLock.lock();

            if ((cachedSPAVResults = this.cachedProvinceResults.get(province)) == null) {
                // Si sigue null nadie lo creo --> lo creo yo

                // No hace falta chequear que es null porque nunca lo es
                SPAVVoting spavVoting = this.provinceSPAVVoting.get(province);

                SPAVRoundResult firstRoundResults = spavVoting.nextRound(Collections.emptyList());
                SPAVRoundResult secondRoundResults = spavVoting.nextRound(firstRoundResults.getWinners());
                SPAVRoundResult thirdRoundResults = spavVoting.nextRound(secondRoundResults.getWinners());

                cachedSPAVResults = new SPAVResults(
                        firstRoundResults,
                        secondRoundResults,
                        thirdRoundResults
                );

                this.cachedProvinceResults.put(province, cachedSPAVResults);
            }

            this.provinceResultsLock.unlock();
        }

        return cachedSPAVResults;
    }

    public FPTPResults getPollingStationResults(PollingStation pollingStation) {
        FPTPResults cachedFPTPResults = this.cachedPollingStationResults.get(pollingStation);
        if (cachedFPTPResults == null) {
            FPTPVoting fptpVoting = this.pollingStationFPTPVoting.get(pollingStation);

            // Permite no bloquear algo que no va a tener un resultado
            if (fptpVoting == null) return new FPTPResults(Collections.emptyList());

            this.pollingStationResultsLock.lock();

            if ((cachedFPTPResults = this.cachedPollingStationResults.get(pollingStation)) == null) {
                // Si sigue null nadie lo creo --> lo creo yo

                // No hace falta chequear que es null porque nunca lo es
                cachedFPTPResults = fptpVoting.getResults();
                this.cachedPollingStationResults.put(pollingStation, cachedFPTPResults);
            }

            this.pollingStationResultsLock.unlock();
        }

        return cachedFPTPResults;
    }
}
