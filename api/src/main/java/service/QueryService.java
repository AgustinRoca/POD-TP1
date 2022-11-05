package service;

import exceptions.ElectionNotStartedException;
import models.Province;
import models.PollingStation;
import models.vote.VoteResult;
import models.vote.fptp.FPTPResults;
import models.vote.spav.SPAVResults;
import models.vote.star.STARResults;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QueryService extends Remote {
    /**
     * Devuelve los resultados (parciales si no terminaron) de las elecciones a nivel nacional.
     *
     * Si las elecciones no terminaron, devuelve solo los porcentajes de los votos FPTP.
     * Si terminaron, devuelve el puntaje (STAR) de cada partido y los porcentajes.
     * Al final tambien incluye el ganador.
     *
     * @return un wrapper de los resultados, con resultados FPTP si las elecciones
     * siguen abiertas o STAR sino
     */
    VoteResult<FPTPResults, STARResults> nationalResults() throws RemoteException, ElectionNotStartedException;

    /**
     * Devuelve los resultados (parciales si no terminaron) de las elecciones a nivel provincial.
     *
     * Si las elecciones no terminaron, imprime solo los porcentajes de los votos FPTP.
     * Si terminaron, imprime primero el indice de aprovación (SPAV) de cada ronda de cada partido
     * con los ganadores luego de cada ronda.
     *
     * @return un wrapper de los resultados, con resultados FPTP si las elecciones
     * siguen abiertas o SPAV sino
     */
    VoteResult<FPTPResults, SPAVResults> provinceResults(Province province) throws RemoteException, ElectionNotStartedException;

    /**
     * Devuelve los resultados de las elecciones en una mesa (polling station)
     * en formato de porcentaje FPTP.
     *
     * @return un wrapper de los resultados, con resultados FPTP
     */
    VoteResult<FPTPResults, FPTPResults> pollingStationResults(PollingStation pollingStation) throws RemoteException, ElectionNotStartedException;
}
