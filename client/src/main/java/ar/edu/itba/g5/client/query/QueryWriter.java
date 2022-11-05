package ar.edu.itba.g5.client.query;

import ar.edu.itba.g5.client.CSVUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import models.Party;
import models.PollingStation;
import models.Province;
import models.Vote;
import models.vote.VoteResult;
import models.vote.fptp.FPTPResult;
import models.vote.fptp.FPTPResults;
import models.vote.spav.SPAVResult;
import models.vote.spav.SPAVResults;
import models.vote.spav.SPAVRoundResult;
import models.vote.star.STARFirstRoundResult;
import models.vote.star.STARResults;
import models.vote.star.STARSecondRoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

public abstract class QueryWriter {
    private static final Logger logger = LoggerFactory.getLogger(QueryWriter.class);

    private static final String PERCENTAGE_HEADER = "Percentage";
    private static final String PARTY_HEADER = "Party";
    private static final String SCORE_HEADER = "Score";
    private static final String WINNER_HEADER = "Winner";
    private static final String WINNERS_HEADER = "Winners";
    private static final String APPROVAL_HEADER = "Approval";

    private static final String ROUND_FORMAT = "Round %d";
    private static final String FLOAT_FORMAT = "%.2f";
    private static final String PERCENTAGE_FORMAT = "%.2f%%";

    public static void writeNationalResults(VoteResult<FPTPResults, STARResults> voteResult, String filepath) {
        try (ICSVWriter writer = CSVUtils.getWriter(CSVUtils.getFileWriter(filepath))) {
            if (voteResult.isOpenElectionsResults())
                writeFPTPResults(voteResult.getWhileOpenResults(), writer);
            else
                writeSTARResults(voteResult.getWhenFinishedResults(), writer);

        } catch (IOException e) {
            System.err.println("Error while trying to write National election results into" + filepath);
        }
    }

    public static void writeProvinceResults(VoteResult<FPTPResults, SPAVResults> voteResult, String filepath) {
        try (ICSVWriter writer = CSVUtils.getWriter(CSVUtils.getFileWriter(filepath))) {
            if (voteResult.isOpenElectionsResults())
                writeFPTPResults(voteResult.getWhileOpenResults(), writer);
            else
                writeSPAVResults(voteResult.getWhenFinishedResults(), writer);

        } catch (IOException e) {
            System.err.println("Error while trying to write Provincial election results into" + filepath);
        }
    }

    public static void writePollingStationResults(VoteResult<FPTPResults, FPTPResults> voteResult, String filepath) {
        try (ICSVWriter writer = CSVUtils.getWriter(CSVUtils.getFileWriter(filepath))) {
            if (voteResult.isOpenElectionsResults())
                writeFPTPResults(voteResult.getWhileOpenResults(), writer);
            else
                writeFPTPResults(voteResult.getWhenFinishedResults(), writer);

        } catch (IOException e) {
            System.err.println("Error while trying to write Polling Station election results into" + filepath);
        }
    }

    private static void writeFPTPResults(FPTPResults results, ICSVWriter writer) {
        writer.writeNext(new String[]{PERCENTAGE_HEADER, PARTY_HEADER});
        for (FPTPResult result : results) {
            writer.writeNext(new String[]{
                    String.format(PERCENTAGE_FORMAT, result.getPercentage()),
                    result.getParty().name()
            });
        }
    }

    private static void writeSTARResults(STARResults results, ICSVWriter writer) {
        // First round
        writer.writeNext(new String[]{SCORE_HEADER, PARTY_HEADER});
        for (STARFirstRoundResult result : results.getFirstRound()) {
            writer.writeNext(new String[]{
                    String.valueOf(result.getScore()),
                    result.getParty().name()
            });
        }

        // Second round
        writer.writeNext(new String[]{PERCENTAGE_HEADER, PARTY_HEADER});
        for (STARSecondRoundResult result : results.getSecondRound()) {
            writer.writeNext(new String[]{
                    String.format(PERCENTAGE_FORMAT, result.getPercentage()),
                    result.getParty().name()
            });
        }

        // Second round
        writer.writeNext(new String[]{WINNER_HEADER});
        writer.writeNext(new String[]{results.getWinner().name()});
    }

    private static void writeSPAVResults(SPAVResults results, ICSVWriter writer) {
        // Empezamos en 1 porque getRound tiene index empezando con 1
        for (int i = 1; i <= SPAVResults.TOTAL_ROUNDS; i++) {
            writer.writeNext(new String[]{String.format(ROUND_FORMAT, i)});
            writer.writeNext(new String[]{APPROVAL_HEADER, PARTY_HEADER});

            SPAVRoundResult roundResult = results.getRound(i);
            for (SPAVResult result : roundResult) {
                writer.writeNext(new String[]{
                        String.format(FLOAT_FORMAT, result.getApprovalScore()),
                        result.getParty().name()
                });
            }

            writer.writeNext(new String[]{WINNERS_HEADER});

            List<String> winners = roundResult.getWinners().stream().map(Enum::name).collect(Collectors.toList());
            writer.writeNext(new String[]{String.join(",", winners)});
        }
    }
}
