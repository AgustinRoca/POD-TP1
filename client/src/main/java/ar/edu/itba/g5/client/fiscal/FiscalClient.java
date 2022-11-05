package ar.edu.itba.g5.client.fiscal;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import exceptions.ElectionStartedException;
import models.Party;
import models.PollingStation;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.FiscalService;
import service.FiscalVoteCallback;
import utils.CommandUtils;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import static utils.CommandUtils.JAVA_OPT;
import static utils.CommandUtils.SERVER_ADDRESS_PARAMETER;

public class FiscalClient {
    private static final Logger logger = LoggerFactory.getLogger(FiscalClient.class);
    private static final String POLLING_STATION_PARAMETER = "id";
    private static final String PARTY_NAME_PARAMETER = "party";

    public static void main(String[] args) throws IOException, NotBoundException, ParseException {
        Properties properties = parseCommandLine(args);

        PollingStation pollingStation =
                new PollingStation(Integer.parseInt(properties.getProperty(POLLING_STATION_PARAMETER)));
        Party party = Party.from(properties.getProperty(PARTY_NAME_PARAMETER));

        FiscalService fiscalService =
                (FiscalService) Naming.lookup("//" + properties.getProperty(SERVER_ADDRESS_PARAMETER) + "/fiscal");
        FiscalVoteCallback voteCallback = new FiscalClientCallback(pollingStation, party);

        try {
            fiscalService.registerFiscal(pollingStation, party, voteCallback);
        } catch (RemoteException e) {
            System.err.println("Unknown remote error");
            e.printStackTrace();
        } catch (ElectionStartedException e) {
            System.err.println(e.getMessage());
        } catch (ElectionFinishedException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Fiscal of " + party + " registered on polling place " + pollingStation);
    }

    private static Properties parseCommandLine(String[] args) throws ParseException {
        Option pollingStationOption = new Option(JAVA_OPT, "specifies the polling place number");
        pollingStationOption.setArgName(POLLING_STATION_PARAMETER);
        pollingStationOption.setRequired(true);

        Option partyNameOption = new Option(JAVA_OPT, "specifies the party name");
        partyNameOption.setArgName(PARTY_NAME_PARAMETER);
        partyNameOption.setRequired(true);

        return CommandUtils.parseCommandLine(args, CommandUtils.serverAddressOption, pollingStationOption,
                partyNameOption);
    }

    private static class FiscalClientCallback extends UnicastRemoteObject implements FiscalVoteCallback {
        private final PollingStation pollingStation;
        private final Party party;

        public FiscalClientCallback(PollingStation pollingStation, Party party) throws RemoteException {
            super();
            this.pollingStation = pollingStation;
            this.party = party;
        }

        @Override
        public void voteMade() throws RemoteException {
            System.out.println("New vote for " + this.party + "on polling place " + this.pollingStation);
        }
    }
}
