package ar.edu.itba.g5.client.query;

import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.PollingStation;
import models.Province;
import models.Vote;
import models.vote.VoteResult;
import models.vote.fptp.FPTPResults;
import models.vote.spav.SPAVResults;
import models.vote.star.STARResults;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.QueryService;
import service.VoteService;
import utils.CommandUtils;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.CommandUtils.JAVA_OPT;
import static utils.CommandUtils.SERVER_ADDRESS_PARAMETER;

public class QueryClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static final String OUT_FILE_PARAMETER = "outPath";
    private static final String STATE_PARAMETER = "state";
    private static final String POLLING_STATION_PARAMETER = "id";

    public static void main(String[] args) throws IOException, NotBoundException, ParseException, InterruptedException {
        Properties properties = parseCommandLine(args);
        QueryService queryService =
                (QueryService) Naming.lookup("//" + properties.getProperty(SERVER_ADDRESS_PARAMETER) + "/query");
        int queryAttempts = 2;
        boolean finished = false;
        while (queryAttempts > 0 && !finished) {
            try {
                getRequestedResult(queryService, properties);
                finished = true;
            } catch (ElectionNotStartedException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (RemoteException e) {
                System.err.println("Unknown remote error, will retry in 2s");
                System.err.println(e.getMessage());
                Thread.sleep(2000);
                queryAttempts--;
            }
        }
    }

    private static Properties parseCommandLine(String[] args) throws ParseException {
        Option filepathOption = new Option(JAVA_OPT, "specifies the output file path");
        filepathOption.setArgName(OUT_FILE_PARAMETER);
        filepathOption.setRequired(true);

        Properties properties = CommandUtils.parseCommandLine(args, CommandUtils.serverAddressOption, filepathOption);
        Properties optionalProperties = parseNonRequiredCommandLine(args);

        for (String property : optionalProperties.stringPropertyNames()) {
            properties.setProperty(property, optionalProperties.getProperty(property));
        }
        return properties;
    }

    // Apache Commons CLI necesita que parseemos los opcionales antes, porque
    // el parser siempre chequea requeridos, y si mezclamos opciones requeridas
    // y no requeridas y estas ultimas no estan se tira una excepcion
    private static Properties parseNonRequiredCommandLine(String[] args) throws ParseException {
        Option stateOption = new Option(JAVA_OPT, "specifies the province's name");
        stateOption.setArgName(STATE_PARAMETER);
        stateOption.setRequired(false);

        Option pollingStationOption = new Option(JAVA_OPT, "specifies the polling station id");
        pollingStationOption.setArgName(POLLING_STATION_PARAMETER);
        pollingStationOption.setRequired(false);

        Properties properties = CommandUtils.parseCommandLine(args, stateOption, pollingStationOption);
        if (properties.getProperty(STATE_PARAMETER) != null && properties.getProperty(POLLING_STATION_PARAMETER) != null)
            throw new InvalidParameterException("You cannot provide province name and polling station id at the same " +
                    "time");

        return properties;
    }

    private static void getRequestedResult(QueryService queryService, Properties properties) throws RemoteException,
            ElectionNotStartedException {
        String filepath = properties.getProperty(OUT_FILE_PARAMETER);
        String parameter;
        parameter = properties.getProperty(STATE_PARAMETER);
        if (parameter != null) {
            VoteResult<FPTPResults, SPAVResults> voteResult =
                    queryService.provinceResults(Province.from(parameter));
            QueryWriter.writeProvinceResults(voteResult, filepath);
        } else if ((parameter = properties.getProperty(POLLING_STATION_PARAMETER)) != null) {
            VoteResult<FPTPResults, FPTPResults> voteResult =
                    queryService.pollingStationResults(new PollingStation(Integer.parseInt(parameter)));
            QueryWriter.writePollingStationResults(voteResult, filepath);
        } else {
            VoteResult<FPTPResults, STARResults> voteResult = queryService.nationalResults();
            QueryWriter.writeNationalResults(voteResult, filepath);
        }
    }
}
