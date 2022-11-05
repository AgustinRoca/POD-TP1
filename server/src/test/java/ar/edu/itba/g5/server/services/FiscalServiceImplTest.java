package ar.edu.itba.g5.server.services;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import exceptions.ElectionStartedException;
import models.ElectionStatus;
import models.Party;
import models.PollingStation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.FiscalVoteCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FiscalServiceImplTest {
    private AdminServiceImpl adminService;
    private AdminServiceImpl adminServiceOpened;
    private AdminServiceImpl adminServiceClosed;
    private FiscalServiceImpl fiscalService;
    private FiscalServiceImpl fiscalServiceWithAdminServiceOpened;
    private FiscalServiceImpl fiscalServiceWithAdminServiceClosed;
    private static final PollingStation pollingStation = new PollingStation(1000);
    private FiscalVoteCallback voteCallback;

    @Before
    public void initializer() {
        try {
            adminService = new AdminServiceImpl();
            fiscalService = new FiscalServiceImpl(adminService);
            adminServiceOpened = new AdminServiceImpl();
            adminServiceOpened.open();
            fiscalServiceWithAdminServiceOpened = new FiscalServiceImpl(adminServiceOpened);
            adminServiceClosed = new AdminServiceImpl();
            adminServiceClosed.open();
            adminServiceClosed.close();
            fiscalServiceWithAdminServiceClosed = new FiscalServiceImpl(adminServiceClosed);
            voteCallback = new FiscalClientCallbackMock(pollingStation, Party.BUFFALO);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fiscalRegistrationFailedElectionAlreadyStarted() {
        try {
            fiscalServiceWithAdminServiceOpened.registerFiscal(pollingStation, Party.BUFFALO, voteCallback);
        } catch (RemoteException | ElectionStartedException e) {
            Assert.assertEquals(adminServiceOpened.getElectionStatus(), ElectionStatus.OPEN);
        }
    }

    @Test
    public void fiscalRegistrationFailedElectionAlreadyFinished() {
        try {
            fiscalServiceWithAdminServiceClosed.registerFiscal(pollingStation, Party.BUFFALO, voteCallback);
        } catch (RemoteException | ElectionFinishedException e) {
            Assert.assertEquals(adminServiceClosed.getElectionStatus(), ElectionStatus.FINISHED);
        }
    }

    private static class FiscalClientCallbackMock extends UnicastRemoteObject implements FiscalVoteCallback {
        private final PollingStation pollingStation;
        private final Party party;

        public FiscalClientCallbackMock(PollingStation pollingStation, Party party) throws RemoteException {
            super();
            this.pollingStation = pollingStation;
            this.party = party;
        }

        @Override
        public void voteMade(){}
    }
}
