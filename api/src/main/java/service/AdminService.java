package service;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.ElectionStatus;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminService extends Remote {
    /**
     * Abre los comicios. Imprime en stdout el estado de los comicios o el error correspondiente.
     */
    void open() throws RemoteException, ElectionFinishedException;

    /**
     * Cierra los comicios. Imprime en stdout el estado de los comicios o el error correspondiente.
     */
    void close() throws RemoteException, ElectionNotStartedException;

    /**
     * Consulta el estado de los comicios. Imprime en stdout el estado de los comicios en el momento.
     */
    ElectionStatus getState() throws RemoteException;
}
