package com.ambroz.formula.gamemodel.race;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.FormulaType;
import com.ambroz.formula.gamemodel.track.Track;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TurnMaker {

    public static final int FOUR_TURNS = 4;
    public static final int FIVE_TURNS = 5;
    public static final int NINE_TURNS = 9;

    private final RaceModel model;
    private final HashMap<Integer, Formula> racers;
    private Turns turns;
    private int turnsCount;

    public TurnMaker(RaceModel menu) {
        this.model = menu;
        racers = new HashMap<>();
        racers.put(1, new Formula(FormulaType.Player));
        racers.get(1).setColor(Color.BLUE.getRGB());
        turns = new Turns();
        turnsCount = 4;
    }

    public void turn(Point click) {
        Turns.Turn selectedTurn = turns.containsTurn(click);
        if (selectedTurn != null) {

            Formula act = racers.get(1);
            if (selectedTurn.getType() == Turns.Turn.FREE) {

                act.addPoint(click);
                act.movesUp();
                if (selectedTurn.getPoint().getLocation().contains(Point.FINISH)
                        && Track.LEFT == Calc.sidePosition(click, model.getTrack().getFinish())) {
                    act.lengthUp(act.getPreLast(), selectedTurn.getCollision());
                    act.setWin(true);
                }
                nextTurn(1, act.getLast());

            } else {

                act.movesUp();
                int maxSpeed = act.maxSpeed(click);
                act.setWait(maxSpeed + 1);
                act.movesUp(maxSpeed);
                //pridani pruseciku do tahu formule:
                act.addPoint(selectedTurn.getCollision());
                act.lengthUp();
                crashTurn();
                model.fireCrash(maxSpeed);

            }
        }
    }

    public void firstTurn(Point click) {
        Turns.Turn selectedTurn = turns.containsTurn(click);
        if (selectedTurn != null) {

            Formula act = racers.get(1);
            act.addPoint(click);
            act.addPoint(new Point(click.x + act.getSide(), click.y + act.getSpeed()));

            nextTurn(1, act.getLast());
            model.setStage(RaceModel.NORMAL_TURN);

        }
    }

    public void startPosition(Segment startLine) {
        Point first = startLine.getFirst();
        Point second = startLine.getLast();
        double numberOfPoints = Calc.distance(first, second) - 1;

        Point difPoint = new Point();
        Point startPoint;

        if (first.getX() == second.getX()) {
            difPoint.y = 1;
            startPoint = createVerticalStartPoints(first, second);
        } else {
            difPoint.x = 1;
            startPoint = createHorizontalStartPositions(first, second);
        }

        for (int i = 0; i < numberOfPoints; i++) {
            startPoint.x = startPoint.getX() + difPoint.getX();
            startPoint.y = startPoint.getY() + difPoint.getY();
            turns.getTurn(i).setPoint(new Point(startPoint));
        }
    }

    private Point createVerticalStartPoints(Point first, Point second) {
        racers.get(1).setSpeed(0);
        int startY;

        if (second.getY() > first.getY()) {
            //direction of first move will be to the right
            racers.get(1).setSide(1);
            startY = first.getY();
        } else {
            //direction of first move will be to the left
            racers.get(1).setSide(-1);
            startY = second.getY();
        }

        return new Point(first.getX(), startY);
    }

    private Point createHorizontalStartPositions(Point first, Point second) {
        racers.get(1).setSide(0);
        int startX;

        if (second.getX() > first.getX()) {
            //direction of first move will be down
            racers.get(1).setSpeed(-1);
            startX = first.getX();
        } else {
            //direction of first move will be up
            racers.get(1).setSpeed(1);
            startX = second.getX();
        }

        return new Point(startX, first.getY());
    }

    /**
     * It creates possibilities of turns of next player.
     *
     * @param formOnTurn number of formula for which possibilities will be created
     * @param rivalLast last point of other player
     */
    public void nextTurn(int formOnTurn, Point rivalLast) {
        Formula act = racers.get(formOnTurn);
        int side = act.getSide();
        int speed = act.getSpeed();

        //souradnice noveho stredu
        int cenX = act.getLast().getX() + side;
        int cenY = act.getLast().getY() + speed;
        Point center = new Point(cenX, cenY);//stred moznosti

        //create possibilities of next turn
        createStandardTurns(center);
        divideTurns(rivalLast);
    }

    private void createStandardTurns(Point center) {
        turns.reset();
        if (turnsCount == FOUR_TURNS) {
            turns.createCornerTurns(center);
            turns.makeCrashTurnsEmpty();
            turns.makeCenterTurnEmpty();
        } else if (turnsCount == FIVE_TURNS) {
            turns.createCornerTurns(center);
            turns.createCenterTurn(center);
            turns.makeCrashTurnsEmpty();
        } else if (turnsCount == NINE_TURNS) {
            turns.createCornerTurns(center);
            turns.createCrashTurns(center);
            turns.createCenterTurn(center);
        }
    }

    private void createCrashTurn(Point center) {
        turns.reset();
        turns.createCrashTurns(center);
        turns.makeCornerTurnsEmpty();
        turns.makeCenterTurnEmpty();
    }

    /**
     * Metoda najde novy stred po havarii a vykresli nove moznosti novy stred je prunikem kolmice kolizni hrany a
     * kruznice se stredem v miste kolize a polomerem 0.6*velikost mrizky, pricemz bod musi lezet na trati colision je
     * bod, ve kterem doslo k vyjeti z trati.
     */
    private void crashTurn() {
        Formula act = racers.get(1);
        Point crashCenter = new Point();
        Segment collisionLine = act.getColision();

        //smerovy vektor kolizni usecky, ktery se vyuzije pro urceni kolmice
        double ux = collisionLine.getLast().x - collisionLine.getFirst().x;
        double uy = collisionLine.getLast().y - collisionLine.getFirst().y;

        if (ux == 0) {

            //crash into vertical edge - for quadratic equation bellow it has no solution
            if (Point.COLLISION_LEFT.equals(act.getLast().getLocation()) && uy > 0
                    || Point.COLLISION_RIGHT.equals(act.getLast().getLocation()) && uy < 0) {
                crashCenter = new Point(act.getLast().getX() - 1, act.getLast().getY());
            } else {
                crashCenter = new Point(act.getLast().getX() + 1, act.getLast().getY());
            }

        } else {
            //parametr c pro kolmici na kolizni usecku, prochazejici prusecikem:
            double C = -ux * act.getLast().x - uy * act.getLast().y;
            /* rovnice pro X na kolmici: X = (-uy*Y-c)/ux
             * stredova rovnice kruznice: r^2=(x-m)^2 + (y-n)^2
             * po dosazeni X do stredove rovnice:
             * (ux^2 + uy^2)* Y^2 + (2*uy*C + 2*m*uy*ux - 2*n*ux^2) * Y + ( C^2 + 2*C*m*ux + ux^2*(n^2+m^2-r^2) ) = 0
             */
            double m = act.getLast().x; //X stredu kruznice
            double n = act.getLast().y; //Y stredu kruznice
            //parametry kvadraticke rovnice:
            double a = ux * ux + uy * uy;
            double b = 2 * uy * C + 2 * m * uy * ux - 2 * n * ux * ux;
            double c = C * C + 2 * C * m * ux + ux * ux * (n * n + m * m - Math.pow(0.75, 2));
            //ziskani korenu Y1 a Y2:
            List<Double> quadRes = Calc.quadratic(a, b, c);
            double Y1 = quadRes.get(0);
            double Y2 = quadRes.get(1);
            //vypocet prislusnych souradnic X na kolmici:
            double X1 = (-uy * Y1 - C) / ux;
            double X2 = (-uy * Y2 - C) / ux;

            Point inter1 = new Point(X1, Y1);
            Point inter2 = new Point(X2, Y2);
            switch (act.getLast().getLocation()) {
                case Point.COLLISION_LEFT:
                    //novy stred musi byt vpravo od kolizni usecky
                    if (Track.RIGHT == Calc.sidePosition(inter1, act.getColision())) {
                        crashCenter = new Point(inter1.getX(), inter1.getY());
                    } else {
                        crashCenter = new Point(inter2.getX(), inter2.getY());
                    }
                    break;
                case Point.COLLISION_RIGHT:
                    //novy stred musi byt vlevo od kolizni usecky
                    if (Track.LEFT == Calc.sidePosition(inter1, act.getColision())) {
                        crashCenter = new Point(inter1.getX(), inter1.getY());
                    } else {
                        crashCenter = new Point(inter2.getX(), inter2.getY());
                    }
                    break;
            }
        }
        act.addPoint(crashCenter);
        //vykresleni X moznosti noveho tahu
        createCrashTurn(crashCenter);
        divideTurns(racers.get(1).getLast());
    }

    /**
     * It devides possible turns into "clean" and "dirty". Dirty turn means formula crashed. In case that one possible
     * turn is equal to rival position, that turns is not allowed.
     *
     * @param rivalLast is position of rival formula
     */
    private void divideTurns(Point rivalLast) {
        Formula act = racers.get(1);
        Track track = model.getTrack();

        for (int i = 0; i < turns.getSize(); i++) {
            Point actPoint = turns.getTurn(i).getPoint();
            Segment lastFormulaMove = new Segment(actPoint, act.getLast());

            if (actPoint.isEqual(rivalLast) == false && turns.getTurn(i).isExist()) {
                boolean colision = checkLeftSideColision(i, lastFormulaMove);

                if (!colision) {
                    colision = checkRightSideColision(i, lastFormulaMove);

                    if (!colision) {
                        colision = checkStartColision(i, lastFormulaMove);

                        if (!colision) {

                            Object[] finish = Calc.crossing(lastFormulaMove, track.getFinish());
                            if ((int) finish[0] == Calc.INSIDE) {
                                //tah protina cilovou caru:
                                turns.getTurn(i).setCollision((Point) finish[1]);
                                actPoint.setLocation(Point.FINISH);
                            } else if ((int) finish[0] == Calc.EDGE) {
                                //tah se dotyka cilove cary:
                                turns.getTurn(i).setCollision((Point) finish[1]);
                                actPoint.setLocation(Point.FINISH_LINE);
                            }

                        } else { //tah vede mimo trat
                            //kontrola zda hrac pred narazem projede cilem:
                            Object[] finish = Calc.crossing(act.getLast(), actPoint, track.getFinish());
                            if ((int) finish[0] != Calc.OUTSIDE && Calc.distance(act.getLast(), (Point) finish[1])
                                    < Calc.distance(act.getLast(), turns.getTurn(i).getCollision())) {
                                //hrac protne cil pred narazem
                                turns.getTurn(i).setCollision((Point) finish[1]);
                                actPoint.setLocation(Point.FINISH);
                                colision = false;
                            }
                        }
                    }
                }

                if (colision) {
                    turns.getTurn(i).setType(Turns.Turn.COLLISION);
//                    act.setColision(colLine);
                }

            } else {
                turns.getTurn(i).setExist(false);
            }
        }
    }

    private boolean checkLeftSideColision(int i, Segment lastFormulaMove) {
        Polyline left = model.getTrack().getLeft();
        boolean colision = false;

        for (int k = 0; k < left.getLength() - 1; k++) {
            Segment actLeft = left.getSegment(k);
            Object[] cross = Calc.crossing(lastFormulaMove, actLeft);
            if ((int) cross[0] != Calc.OUTSIDE) {
                //novy bod ma prunik nebo se dotyka leve krajnice
                Segment colLine = actLeft;
                Point colPoint = (Point) cross[1];
                colPoint.setLocation(Point.COLLISION_LEFT);
                turns.getTurn(i).setCollision(colPoint);
                colision = true;

                turns.getTurn(i).setType(Turns.Turn.COLLISION);
                racers.get(1).setColision(colLine);
                break;
            }
        }

        return colision;
    }

    private boolean checkRightSideColision(int i, Segment lastFormulaMove) {
        Polyline right = model.getTrack().getRight();
        boolean colision = false;

        for (int k = 0; k < right.getLength() - 1; k++) {
            Segment actRight = right.getSegment(k);
            Object[] cross = Calc.crossing(lastFormulaMove, actRight);
            if ((int) cross[0] != Calc.OUTSIDE) {
                //novy bod ma prunik nebo se dotyka prave krajnice
                Segment colLine = actRight;
                Point colPoint = (Point) cross[1];
                colPoint.setLocation(Point.COLLISION_RIGHT);
                turns.getTurn(i).setCollision(colPoint);
                colision = true;

                turns.getTurn(i).setType(Turns.Turn.COLLISION);
                racers.get(1).setColision(colLine);
                break;
            }
        }

        return colision;
    }

    private boolean checkStartColision(int i, Segment lastFormulaMove) {
        Track track = model.getTrack();
        boolean colision = false;

        Object[] start = Calc.crossing(lastFormulaMove, track.getStart());
        if ((int) start[0] != Calc.OUTSIDE && Track.RIGHT == Calc.sidePosition(lastFormulaMove.getFirst(), track.getStart())) {
            //tah protina start a konci vpravo od nej (projel se v protismeru)
            Point colPoint = (Point) start[1];
            colPoint.setLocation(Point.COLLISION_RIGHT);
            turns.getTurn(i).setCollision(colPoint);

            turns.getTurn(i).setType(Turns.Turn.COLLISION);
            racers.get(1).setColision(track.getStart());
            colision = true;
        }

        return colision;
    }

    public int getFormulaCount() {
        return racers.size();
    }

    public Formula getFormula(int id) {
        return racers.get(id);
    }

    public void resetTurns() {
        turns.reset();
    }

    public Turns getTurns() {
        return turns;
    }

}
