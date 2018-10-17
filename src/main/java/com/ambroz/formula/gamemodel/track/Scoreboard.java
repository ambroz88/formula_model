package com.ambroz.formula.gamemodel.track;

import java.util.List;

import com.ambroz.formula.gamemodel.race.Formula;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Scoreboard {

    private final List<Record> results;
    private final String name;

    public Scoreboard(String trackName, List<Record> resultList) {
        results = resultList;
        name = trackName;
    }

    public void addRecord(Formula player) {
        int position = identifyPosition(player);
        results.add(position, new Record(player));
    }

    public String getName() {
        return name;
    }

    public List<Record> getResults() {
        return results;
    }

    private int identifyPosition(Formula player) {
        int position = 0;
        Record record;

        for (int i = 0; i < results.size(); i++) {
            record = results.get(i);

            if (record.getDistance() > player.getDist()) {
                break;
            } else if (record.getDistance() == player.getDist()) {
                if (record.getMoves() >= player.getMoves()) {
                    break;
                }
            }

            position = i;
            if (position == results.size() - 1) {
                position = position + 1;
            }
        }

        return position;
    }

}
