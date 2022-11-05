package ar.edu.itba.g5.server.services;

import ar.edu.itba.g5.server.services.utils.ElectionEvents;
import ar.edu.itba.g5.server.services.utils.ElectionStatusAware;
import ar.edu.itba.g5.server.services.utils.ElectionEventBus;
import ar.edu.itba.g5.server.vote.FPTPVoting;
import ar.edu.itba.g5.server.vote.SPAVVoting;
import ar.edu.itba.g5.server.vote.STARVoting;
import ar.edu.itba.g5.server.vote.VoteCachedResults;
import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.*;
import models.vote.VoteResult;
import models.vote.VoteResultFactory;
import models.vote.fptp.FPTPResults;
import models.vote.spav.SPAVResults;
import models.vote.spav.SPAVRoundResult;
import models.vote.star.*;
import service.QueryService;
import service.VoteService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VoteServiceImpl extends UnicastRemoteObject implements VoteService, QueryService {
    private final ElectionStatusAware electionStatusAware;

    private final STARVoting nationalSTARVoting = new STARVoting();
    private final FPTPVoting nationalFPTPVoting = new FPTPVoting();

    private Map<Province, SPAVVoting> provinceSPAVVoting = new HashMap<>();
    private Map<Province, FPTPVoting> provinceFPTPVoting = new HashMap<>();

    private final Map<PollingStation, FPTPVoting> pollingStationFPTPVoting = new HashMap<>();
    private final Lock pollingStationLock = new ReentrantLock();

    private final VoteCachedResults cachedResults;

    public VoteServiceImpl(ElectionStatusAware electionStatusAware) throws RemoteException {
        this.electionStatusAware = electionStatusAware;

        for (Province province : Province.values()) {
            this.provinceSPAVVoting.put(province, new SPAVVoting());
            this.provinceFPTPVoting.put(province, new FPTPVoting());
        }

        this.provinceSPAVVoting = Collections.unmodifiableMap(this.provinceSPAVVoting);
        this.provinceFPTPVoting = Collections.unmodifiableMap(this.provinceFPTPVoting);

        this.cachedResults = new VoteCachedResults(this.nationalSTARVoting, this.provinceSPAVVoting,
                this.pollingStationFPTPVoting);
    }

    @Override
    public void vote(Vote vote) throws ElectionNotStartedException, ElectionFinishedException, RemoteException {
        if (!this.hasElectionStarted()) throw new ElectionNotStartedException();
        if (this.electionStatusAware.closing()) throw new ElectionFinishedException();

        // El register permite saber que un thread llego, es como incrementar un contador
        // Permite que si justo se llama a cerrar comicios, se quede esperando hasta realmente cerrarlos
        this.electionStatusAware.getElectionPhaser().register();

        if (this.electionStatusAware.getElectionStatus() == ElectionStatus.FINISHED) {

            // Marco que arriva y deregistro el thread actual para desbloquear ejecucion
            // en AdminService. Es como decrementar el contador
            this.electionStatusAware.getElectionPhaser().arriveAndDeregister();
            return;
        }

        this.registerVote(vote);

        // Marco que arriva y deregistro el thread actual para desbloquear ejecucion
        // en AdminService. Es como decrementar el contador
        this.electionStatusAware.getElectionPhaser().arriveAndDeregister();
    }

    @Override
    public VoteResult<FPTPResults, STARResults> nationalResults() {
        if (!this.hasElectionStarted()) throw new ElectionNotStartedException();

        if (this.isElectionOpen())
            return VoteResultFactory.withOpenElection(this.nationalFPTPVoting.getResults());

        return VoteResultFactory.withFinishedElection(this.cachedResults.getNationalResults());
    }

    @Override
    public VoteResult<FPTPResults, SPAVResults> provinceResults(Province province) {
        if (!this.hasElectionStarted()) throw new ElectionNotStartedException();

        if (this.isElectionOpen())
            return VoteResultFactory.withOpenElection(this.provinceFPTPVoting.get(province).getResults());

        return VoteResultFactory.withFinishedElection(this.cachedResults.getProvinceResults(province));
    }

    @Override
    public VoteResult<FPTPResults, FPTPResults> pollingStationResults(PollingStation pollingStation) {
        if (!this.hasElectionStarted()) throw new ElectionNotStartedException();

        if (this.isElectionOpen())
            return VoteResultFactory.withOpenElection(this.pollingStationFPTPVoting.getOrDefault(pollingStation,
                    new FPTPVoting()).getResults());

        return VoteResultFactory.withFinishedElection(this.cachedResults.getPollingStationResults(pollingStation));
    }

    private void registerVote(Vote vote) {
        this.nationalFPTPVoting.registerVote(vote.getFPTPVote());
        this.nationalSTARVoting.registerVote(vote.getSTARVotes());

        this.provinceSPAVVoting.get(vote.getProvince()).registerVote(vote.getSPAVVotes());
        this.provinceFPTPVoting.get(vote.getProvince()).registerVote(vote.getFPTPVote());

        // Bloqueo porque puedo llegar a reemplazar un valor en el mapa
        this.pollingStationLock.lock();
        if (!this.pollingStationFPTPVoting.containsKey(vote.getPollingStation())) {
            this.pollingStationFPTPVoting.put(vote.getPollingStation(), new FPTPVoting());
        }
        this.pollingStationLock.unlock();

        this.pollingStationFPTPVoting.get(vote.getPollingStation()).registerVote(vote.getFPTPVote());

        ElectionEventBus.eventBus.fire(ElectionEvents.getEvent(vote.getFPTPVote(), vote.getPollingStation()));
    }

    private boolean isElectionOpen() {
        return this.electionStatusAware.getElectionStatus() == ElectionStatus.OPEN;
    }

    private boolean hasElectionStarted() {
        return this.electionStatusAware.getElectionStatus() != ElectionStatus.UNINITIALIZED;
    }
}
