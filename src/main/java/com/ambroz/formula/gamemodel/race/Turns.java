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

    public List<Point> getFreePoints() {
        List<Point> freePoints = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getType() == Turn.FREE) {
                freePoints.add(turn.getPoint());
            }
        }
        return freePoints;
    }

    public List<Turn> getFreeTurns() {
        List<Turn> freeTurns = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getType() == Turn.FREE) {
                freeTurns.add(turn);
            }
        }
        return freeTurns;
    }

    public List<Point> getCollisionPoints() {
        List<Point> collisionPoints = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getType() == Turn.COLLISION) {
                collisionPoints.add(turn.getPoint());
            }
        }
        return collisionPoints;
    }

    public List<Turn> getCollisionTurns() {
        List<Turn> collisionTurns = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn.isExist() && turn.getType() == Turn.COLLISION) {
                collisionTurns.add(turn);
            }
        }
        return collisionTurns;
    }

    public Turn containsTurn(Point click) {
        Turn confirmed = null;
        for (Turn turn : turns) {
            if (click.isEqual(turn.getPoint())) {
                confirmed = turn;
            }
        }
        return confirmed;
    }

    public void createCornerTurns(Point center) {
        getTurn(0).setPoint(new Point(center.x - 1, center.y - 1));
        getTurn(2).setPoint(new Point(center.x + 1, center.y - 1));
        getTurn(6).setPoint(new Point(center.x - 1, center.y + 1));
        getTurn(8).setPoint(new Point(center.x + 1, center.y + 1));
    }

    public void createCrashTurns(Point center) {
        getTurn(1).setPoint(new Point(center.x, center.y - 1));
        getTurn(3).setPoint(new Point(center.x - 1, center.y));
        getTurn(5).setPoint(new Point(center.x + 1, center.y));
        getTurn(7).setPoint(new Point(center.x, center.y + 1));
    }

    public void createCenterTurn(Point center) {
        getTurn(4).setPoint(center);
    }

    public void makeCrashTurnsEmpty() {
        getTurn(1).setExist(false);
        getTurn(3).setExist(false);
        getTurn(5).setExist(false);
        getTurn(7).setExist(false);

    }

    public void makeCornerTurnsEmpty() {
        getTurn(0).setExist(false);
        getTurn(2).setExist(false);
        getTurn(6).setExist(false);
        getTurn(8).setExist(false);
    }

    public void makeCenterTurnEmpty() {
        getTurn(4).setExist(false);
    }

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

    public class Turn {

        public static final int FREE = 1;
        public static final int COLLISION = 0;
        private Point point;
        private Point collision;
        private int type; //0 is bad, 1 is good
        private boolean exist;

        public Turn() {
            point = new Point();
            type = FREE;
            exist = true;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
            setExist(true);
        }

        public Point getCollision() {
            return collision;
        }

        public void setCollision(Point collision) {
            this.collision = collision;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
            setExist(true);
        }

        public boolean isExist() {
            return exist;
        }

        public void setExist(boolean exist) {
            this.exist = exist;
        }

    }

}
