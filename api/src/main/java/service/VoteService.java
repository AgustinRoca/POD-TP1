package service;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.Vote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VoteService extends Remote {
    /**
     * Registra un voto. Si los comicios estan sin iniciar
     * o si ya finalizaron arroja un error correspondiente
     */
    void vote(Vote vote) throws RemoteException, ElectionNotStartedException, ElectionFinishedException;
}
