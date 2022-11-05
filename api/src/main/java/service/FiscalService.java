package service;

import exceptions.ElectionFinishedException;
import exceptions.ElectionStartedException;
import models.Party;
import models.PollingStation;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FiscalService extends Remote {
    /**
     * Suscribe a un fiscal del partido y número de mesa indicado, llamando al
     * callback cada vez que un voto se realiza para su partido.
     * Solo se puede hacer si las elecciones no iniciaron.
     */
    void registerFiscal(PollingStation pollingStation, Party party, FiscalVoteCallback callback) throws RemoteException, ElectionStartedException, ElectionFinishedException;
}
