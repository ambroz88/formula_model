package com.ambroz.formula.gamemodel.race;

import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Point;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Turns {

    private Turn[] turns;

    public Turns() {
        turns = new Turn[9];
        for (int i = 0; i < 9; i++) {
            turns[i] = new Turn();
        }
    }

    public List<Turn> getFreeTurns() {
        List<Turn> freeTurns = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getCollision() == null || turn.getLocation().contains("finish")) {
                freeTurns.add(turn);
            } else {

            }
        }
        return freeTurns;
    }

    public List<Turn> getCollisionTurns() {
        List<Turn> collisionTurns = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getCollision() != null && !turn.getLocation().contains("finish")) {
                collisionTurns.add(turn);
            }
        }
        return collisionTurns;
    }

    public Turn containsTurn(Point click) {
        Turn confirmed = null;
        for (Turn turn : turns) {
            if (click.isEqual(turn)) {
                confirmed = turn;
            }
        }
        return confirmed;
    }

    // ----------------------- Create turn types -------------------------
    public void createStandardTurn(Point center, int turnsCount) {
        reset();
        if (turnsCount == TurnMaker.FOUR_TURNS) {
            createFourTurn(center);
        } else if (turnsCount == TurnMaker.FIVE_TURNS) {
            createFiveTurn(center);
        } else if (turnsCount == TurnMaker.NINE_TURNS) {
            createNineTurn(center);
        }
    }

    public void createCrashTurn(Point center) {
        reset();
        createCrashTurns(center);
        makeCornerTurnsEmpty();
        makeCenterTurnEmpty();
    }

    private void createFourTurn(Point center) {
        createCornerTurns(center);
        makeCrashTurnsEmpty();
        makeCenterTurnEmpty();
    }

    private void createFiveTurn(Point center) {
        createCornerTurns(center);
        createCenterTurn(center);
        makeCrashTurnsEmpty();
    }

    private void createNineTurn(Point center) {
        createCornerTurns(center);
        createCrashTurns(center);
        createCenterTurn(center);
    }

    // ---------------------- executive turn methods -------------------------------------
    private void createCornerTurns(Point center) {
        getTurn(0).setPoint(new Point(center.x - 1, center.y - 1));
        getTurn(2).setPoint(new Point(center.x + 1, center.y - 1));
        getTurn(6).setPoint(new Point(center.x - 1, center.y + 1));
        getTurn(8).setPoint(new Point(center.x + 1, center.y + 1));
    }

    private void createCrashTurns(Point center) {
        getTurn(1).setPoint(new Point(center.x, center.y - 1));
        getTurn(3).setPoint(new Point(center.x - 1, center.y));
        getTurn(5).setPoint(new Point(center.x + 1, center.y));
        getTurn(7).setPoint(new Point(center.x, center.y + 1));
    }

    private void createCenterTurn(Point center) {
        getTurn(4).setPoint(center);
    }

    private void makeCrashTurnsEmpty() {
        getTurn(1).setExist(false);
        getTurn(3).setExist(false);
        getTurn(5).setExist(false);
        getTurn(7).setExist(false);

    }

    private void makeCornerTurnsEmpty() {
        getTurn(0).setExist(false);
        getTurn(2).setExist(false);
        getTurn(6).setExist(false);
        getTurn(8).setExist(false);
    }

    private void makeCenterTurnEmpty() {
        getTurn(4).setExist(false);
    }

    //-----------------------------------------------------------------
    public Turn getTurn(int pos) {
        return turns[pos];
    }

    public int getSize() {
        return turns.length;
    }

    public void reset() {
        turns = new Turn[9];
        for (int i = 0; i < 9; i++) {
            turns[i] = new Turn();
        }
    }

    @Override
    public String toString() {
        return "Size: " + turns.length;
    }

}
