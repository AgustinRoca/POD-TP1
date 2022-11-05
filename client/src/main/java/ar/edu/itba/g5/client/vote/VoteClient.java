package ar.edu.itba.g5.client.vote;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.Vote;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.VoteService;
import utils.CommandUtils;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.CommandUtils.JAVA_OPT;
import static utils.CommandUtils.SERVER_ADDRESS_PARAMETER;

public class VoteClient {
    private static final Logger logger = LoggerFactory.getLogger(VoteClient.class);
    private static final String VOTES_PATH_PARAMETER = "votesPath";
    private static final int NUMBER_OF_THREADS = 10;

    public static void main(String[] args) throws IOException, NotBoundException, ParseException {
        Properties properties = parseCommandLine(args);
        String filepath = properties.getProperty(VOTES_PATH_PARAMETER);
        Collection<Vote> votes = VoteParser.parse(filepath);

        VoteService voteService =
                (VoteService) Naming.lookup("//" + properties.getProperty(SERVER_ADDRESS_PARAMETER) + "/vote");
        AtomicInteger emittedVotes = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        CountDownLatch countDownLatch = new CountDownLatch(NUMBER_OF_THREADS);
        for (Vote vote : votes) {
            executorService.execute(() -> {
                int voteAttempts = 2;
                boolean voted = false;
                while (voteAttempts > 0 && !voted) {
                    try {
                        voteService.vote(vote);
                        emittedVotes.getAndAdd(1);
                        voted = true;
                    } catch (ElectionNotStartedException e) {
                        System.err.println(e.getMessage());
                        voteAttempts -= 2;
                    } catch (ElectionFinishedException e) {
                        System.err.println(e.getMessage());
                        voteAttempts -= 2;
                    } catch (RemoteException e) {
                        try {
                            System.err.println("Unknown remote error, will retry in 2s");
                            System.err.println(e.getMessage());
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }
                        voteAttempts--;
                    }
                }
                countDownLatch.countDown();
            });
        }

        while (countDownLatch.getCount() != 0) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(emittedVotes + " votes registered");
    }

    private static Properties parseCommandLine(String[] args) throws ParseException {
        Option filepathOption = new Option(JAVA_OPT, "specifies the votes' file path");
        filepathOption.setArgName(VOTES_PATH_PARAMETER);
        filepathOption.setRequired(true);

        return CommandUtils.parseCommandLine(args, CommandUtils.serverAddressOption, filepathOption);
    }
}
