package com.ambroz.formula.gamemodel.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.track.Record;
import com.ambroz.formula.gamemodel.track.Scoreboard;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public abstract class RaceResult {

    private static final String RESULT_FOLDER = "results/";
    private static final String SEPARATOR = "%";

    public static void saveResult(Scoreboard board) throws IOException {
        StringBuilder scoreBuilder = new StringBuilder();
        List<Record> records = board.getResults();
        String line;

        for (Record record : records) {
            line = record.getName() + SEPARATOR + record.getMoves() + SEPARATOR + record.getDistance() + "\n";
            scoreBuilder.append(line);
        }

        FileIO.saveFile(scoreBuilder.toString(), RESULT_FOLDER + board.getName());
    }

    public static Scoreboard loadResults(String trackName) {
        List<Record> board;
        try {
            String textBoard = FileIO.readFileToString(RESULT_FOLDER + trackName);
            board = convertTextToTable(textBoard);
        } catch (IOException e) {
            board = new ArrayList<>();
        }

        return new Scoreboard(trackName, board);
    }

    private static List<Record> convertTextToTable(String textBoard) {
        List<Record> scoreBoard = new ArrayList();
        String[] lines = textBoard.split("\n");
        String[] recordData;
        Record record;

        for (String line : lines) {
            recordData = line.split(SEPARATOR);
            if (recordData.length > 2) {
                record = new Record(recordData[0], Integer.valueOf(recordData[1]), Double.valueOf(recordData[2]));
                scoreBoard.add(record);
            }
        }

        return scoreBoard;
    }
}
