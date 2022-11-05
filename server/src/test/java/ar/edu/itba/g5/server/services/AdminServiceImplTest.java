package ar.edu.itba.g5.server.services;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import junit.framework.Assert;
import models.ElectionStatus;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.fail;

public class AdminServiceImplTest {
    private AdminServiceImpl adminService;
    private AdminServiceImpl adminServiceOpened;
    private AdminServiceImpl adminServiceClosed;

    @Before
    public void initialize() {
        try {
            adminService = new AdminServiceImpl();
            adminServiceOpened = new AdminServiceImpl();
            adminServiceOpened.open();
            adminServiceClosed = new AdminServiceImpl();
            adminServiceClosed.open();
            adminServiceClosed.close();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void openElectionSuccessfully() {
        try {
            adminService.open();
            Assert.assertEquals(adminService.getState(), ElectionStatus.OPEN);

            adminServiceOpened.open();
            Assert.assertEquals(adminService.getState(), ElectionStatus.OPEN);
        } catch (RemoteException e) {
            fail();
        }
    }

    @Test
    public void openElectionFailAlreadyFinished() {
        try {
            adminServiceClosed.open();
        } catch (RemoteException | ElectionFinishedException e) {
            try {
                Assert.assertEquals(adminServiceClosed.getState(), ElectionStatus.FINISHED);
            } catch (RemoteException ex) {
                fail();
            }
        }
    }

    @Test
    public void closeElectionSuccessfully() {
        try {
            adminServiceOpened.close();
            Assert.assertEquals(adminServiceOpened.getState(), ElectionStatus.FINISHED);

            adminServiceClosed.close();
            Assert.assertEquals(adminServiceClosed.getState(), ElectionStatus.FINISHED);
        } catch (RemoteException e) {
            fail();
        }
    }

    @Test
    public void closeElectionFailNotStarted() {
        try {
            adminServiceOpened.close();
        } catch (RemoteException | ElectionNotStartedException e) {
            try {
                Assert.assertEquals(adminServiceOpened.getState(), ElectionStatus.UNINITIALIZED);
            } catch (RemoteException ex) {
                fail();
            }
        }
    }

    @Test
    public void SuccessfullyVoteRegisteredIfImmediatelyElectionsAreClosed() {
        Thread first = new Thread(() -> {
            try {
                this.adminServiceOpened.getElectionPhaser().register();
                Thread.sleep(2000);
                Assert.assertEquals(adminServiceOpened.getState(), ElectionStatus.OPEN);
                this.adminServiceOpened.getElectionPhaser().arriveAndDeregister();
            } catch (RemoteException e) {
                fail();
            } catch (InterruptedException e) {
                fail();
            }
        });

        Thread second = new Thread(() -> {
            try {
                Assert.assertFalse(adminServiceOpened.closing());
                adminServiceOpened.close();
                Assert.assertTrue(adminServiceOpened.closing());
                Assert.assertEquals(adminServiceOpened.getState(), ElectionStatus.FINISHED);
            } catch (RemoteException e) {
                fail();
            }
        });

        first.start();
        second.start();

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
