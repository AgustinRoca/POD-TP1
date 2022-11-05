package ar.edu.itba.g5.client.vote;

import ar.edu.itba.g5.client.CSVUtils;
import com.opencsv.CSVReader;
import models.Party;
import models.PollingStation;
import models.Province;
import models.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public abstract class VoteParser {
    private static final Logger logger = LoggerFactory.getLogger(VoteParser.class);
    private static final int TABLE_INDEX = 0;
    private static final int PROVINCE_INDEX = 1;
    private static final int CANDIDATES_INDEX = 2;
    private static final int FPTP_CANDIDATE_INDEX = 3;

    public static Collection<Vote> parse(String filepath) {
        Collection<Vote> votes = new LinkedList<>();
        try (CSVReader reader = CSVUtils.getReader(new FileReader(new File(filepath)))) {
            for (String[] line : reader) {
                PollingStation table = new PollingStation(Integer.parseInt(line[TABLE_INDEX]));
                Province province = Province.from(line[PROVINCE_INDEX]);
                Map<Party, Integer> rankedParties = parseRankedParties(line[CANDIDATES_INDEX]);
                Party fptpCandidate = Party.from(line[FPTP_CANDIDATE_INDEX]);

                votes.add(new Vote(table, province, rankedParties, fptpCandidate));
            }
        } catch (IOException e) {
            System.err.println("Error while trying to read vote's file from" + filepath);
        }
        return votes;
    }

    private static Map<Party, Integer> parseRankedParties(String line) {
        if (line.length() == 0) return Collections.emptyMap();

        Map<Party, Integer> rankedParties = new HashMap<>();

        String[] rankedCandidates = line.split(",");
        for (String rankedCandidate : rankedCandidates) {
            String[] lines = rankedCandidate.split("\\|");

            Party party = Party.from(lines[0]);
            rankedParties.put(party, Integer.valueOf(lines[1]));
        }
        return rankedParties;
    }
}
