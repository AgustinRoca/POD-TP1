package ar.edu.itba.g5.server.services;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.*;
import models.vote.fptp.FPTPResult;
import models.vote.fptp.FPTPResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VoteServiceImplTest {
    private AdminServiceImpl adminService;
    private AdminServiceImpl adminServiceOpened;
    private AdminServiceImpl adminServiceClosed;
    private VoteServiceImpl voteService;
    private VoteServiceImpl voteServiceWithAdminServiceOpened;
    private VoteServiceImpl voteServiceWithAdminServiceClosed;
    private static final Vote vote1 = new Vote(new PollingStation(1000), Province.JUNGLE,
            new HashMap<Party, Integer>() {{
                put(Party.TIGER, 3);
                put(Party.LEOPARD, 2);
                put(Party.LYNX, 1);
            }}, Party.TIGER);

    private static final Vote vote2 = new Vote(new PollingStation(1001), Province.JUNGLE,
            new HashMap<Party, Integer>() {{
                put(Party.LYNX, 1);
                put(Party.TIGER, 1);
                put(Party.LEOPARD, 2);
            }}, Party.LYNX);

    private static final Vote vote3 = new Vote(new PollingStation(1000), Province.SAVANNAH, new HashMap<Party,
            Integer>() {{
        put(Party.TIGER, 3);
        put(Party.OWL, 2);
        put(Party.BUFFALO, 1);
    }}, Party.BUFFALO);


    @Before
    public void initializer() {
        try {
            adminService = new AdminServiceImpl();
            voteService = new VoteServiceImpl(adminService);
            adminServiceOpened = new AdminServiceImpl();
            adminServiceOpened.open();
            voteServiceWithAdminServiceOpened = new VoteServiceImpl(adminServiceOpened);
            adminServiceClosed = new AdminServiceImpl();
            adminServiceClosed.open();
            adminServiceClosed.close();
            voteServiceWithAdminServiceClosed = new VoteServiceImpl(adminServiceClosed);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void voteActionFailElectionNotStarted() {
        try {
            voteService.vote(vote1);
        } catch (RemoteException | ElectionNotStartedException e) {
            Assert.assertEquals(adminService.getElectionStatus(), ElectionStatus.UNINITIALIZED);
        }
    }

    @Test
    public void voteActionFailElectionFinished() {
        try {
            voteServiceWithAdminServiceClosed.vote(vote1);
        } catch (RemoteException | ElectionFinishedException e) {
            Assert.assertEquals(adminServiceClosed.getElectionStatus(), ElectionStatus.FINISHED);
        }
    }

    @Test
    public void voteActionSuccessfullyCompleted() {
        try {
            voteServiceWithAdminServiceOpened.vote(vote1);
            assertTrue(true);
        } catch (RemoteException e) {
            fail();
        }
    }

    @Test
    public void TwoVoteActionSuccessfullyCompleted() {
        Thread first = new Thread(() -> {
            try {
                voteServiceWithAdminServiceOpened.vote(vote1);
            } catch (RemoteException e) {
                fail();
            }
        });

        Thread second = new Thread(() -> {
            try {
                voteServiceWithAdminServiceOpened.vote(vote3);
            } catch (RemoteException e) {
                fail();
            }
        });

        Thread third = new Thread(() -> {
            try {
                voteServiceWithAdminServiceOpened.vote(vote2);
            } catch (RemoteException e) {
                fail();
            }
        });

        first.start();
        second.start();
        third.start();
        try {
            first.join();
            second.join();
            third.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<FPTPResult> results =
                voteServiceWithAdminServiceOpened.pollingStationResults(new PollingStation(1000)).getWhileOpenResults().getSortedResultsList();
        Assert.assertEquals(2, results.size());

        List<FPTPResult> results1 = voteServiceWithAdminServiceOpened.nationalResults().getWhileOpenResults().getSortedResultsList();
        Assert.assertEquals(3, results1.size());
    }
}
