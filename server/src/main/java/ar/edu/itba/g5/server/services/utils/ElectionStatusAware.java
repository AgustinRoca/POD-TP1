package ar.edu.itba.g5.server.services.utils;

import models.ElectionStatus;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public interface ElectionStatusAware {
    /**
     * Devuelve el estado de los comicios.
     */
    ElectionStatus getElectionStatus();

    /**
     * @return true si los comicios estan cerrando.
     * Permite no tomar nuevos votos pero terminar de procesar los actuales
     */
    boolean closing();

    /**
     * @return el countdownlatch utilizado para evitar cerrar los comicios si hay votos entrantes
     */
    Phaser getElectionPhaser();
}
