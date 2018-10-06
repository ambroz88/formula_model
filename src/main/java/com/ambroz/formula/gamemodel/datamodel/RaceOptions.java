package com.ambroz.formula.gamemodel.datamodel;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class RaceOptions {

    public static final int WIN_COLLISION = 0;
    public static final int WIN_FIRST = 1;
    public static final int WIN_LAST_TURN = 2;

    public static final int FOUR_TURNS = 4;
    public static final int FIVE_TURNS = 5;
    public static final int NINE_TURNS = 9;

    public static final int LENGTH_3 = 3;
    public static final int LENGTH_5 = 5;
    public static final int LENGTH_10 = 10;
    public static final int LENGTH_20 = 20;
    public static final int LENGTH_MAX = 999;

    private int finishType;
    private int turnsCount;
    private int lengthHist;

    public RaceOptions() {
        turnsCount = FOUR_TURNS;
        lengthHist = LENGTH_MAX;
        finishType = WIN_FIRST;
    }

    public int getTurnsCount() {
        return turnsCount;
    }

    public void setTurnsCount(int turnsCount) {
        this.turnsCount = turnsCount;
    }

    public void setFinishType(int finishType) {
        this.finishType = finishType;
    }

    public int getFinishType() {
        return finishType;
    }

    public int getLengthHist() {
        return lengthHist;
    }

    public void setLengthHist(String len) {
        try {
            this.lengthHist = Integer.valueOf(len);
        } catch (NumberFormatException e) {
            this.lengthHist = LENGTH_MAX;
        }
    }

}
