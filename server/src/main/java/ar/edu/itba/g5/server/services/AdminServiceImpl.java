package ar.edu.itba.g5.server.services;

import ar.edu.itba.g5.server.services.utils.ElectionStatusAware;
import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.ElectionStatus;
import service.AdminService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Phaser;

public class AdminServiceImpl extends UnicastRemoteObject implements AdminService, ElectionStatusAware {
    private ElectionStatus electionStatus = ElectionStatus.UNINITIALIZED;
    private boolean closing = false;
    // Permite esperar a que todos los votos hayan sido registrados para cerrar los comicios
    // Similar a cuando cierran las puertas de los lugares de votacion pero la gente que ya
    // se encuentra adentro puede realizar su voto
    private final Phaser phaser = new Phaser();

    public AdminServiceImpl() throws RemoteException {
        this.phaser.register();
    }

    @Override
    public synchronized void open() throws RemoteException, ElectionFinishedException {
        if (this.electionStatus == ElectionStatus.FINISHED) throw new ElectionFinishedException();
        this.electionStatus = ElectionStatus.OPEN;
    }

    @Override
    public synchronized void close() throws RemoteException, ElectionNotStartedException {
        if (this.electionStatus == ElectionStatus.UNINITIALIZED) throw new ElectionNotStartedException();
        if (this.closing) return;

        this.closing = true;

        // Se queda esperando hasta que todos los threads registrados arriven
        // Si seguimos con la analogia del contador, una vez que llegue a 0
        // se desbloquea la ejecucion y se marcara como finalizado
        this.phaser.arriveAndAwaitAdvance();

        this.electionStatus = ElectionStatus.FINISHED;
    }

    @Override
    public ElectionStatus getState() throws RemoteException {
        return this.electionStatus;
    }

    @Override
    public ElectionStatus getElectionStatus() {
        return this.electionStatus;
    }

    @Override
    public boolean closing() {
        return this.closing;
    }

    @Override
    public Phaser getElectionPhaser() {
        return this.phaser;
    }
}
