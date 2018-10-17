package com.ambroz.formula.gamemodel.track;

import com.ambroz.formula.gamemodel.race.Formula;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Record {

    private final String name;
    private final int moves;
    private final double distance;

    public Record(String name, int moves, double distance) {
        this.name = name;
        this.moves = moves;
        this.distance = distance;
    }

    public Record(Formula player) {
        name = player.getName();
        moves = player.getMoves();
        distance = player.getDist();
    }

    public String getName() {
        return name;
    }

    public int getMoves() {
        return moves;
    }

    public double getDistance() {
        return distance;
    }

}
