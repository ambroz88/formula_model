package com.ambroz.formula.gamemodel.turns;

import com.ambroz.formula.gamemodel.GameModel;
import com.ambroz.formula.gamemodel.datamodel.Formula;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.datamodel.Track;
import com.ambroz.formula.gamemodel.datamodel.Turns;
import com.ambroz.formula.gamemodel.enums.FormulaType;
import com.ambroz.formula.gamemodel.utils.Calc;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TurnMaker {

    public static final int FOUR_TURNS = 4;
    public static final int FIVE_TURNS = 5;
    public static final int NINE_TURNS = 9;

    private final GameModel model;
    private final HashMap<Integer, Formula> racers;
    private Turns turns;
    private int turnsCount;

    public TurnMaker(GameModel menu) {
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
                        && Track.LEFT == Calc.sidePosition(click, model.getRaceTrack().getFinish())) {
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
            model.setStage(GameModel.NORMAL_TURN);

        }
    }

    public void startPosition(Segment startLine) {
        Point first = startLine.getFirst();
        Point second = startLine.getLast();
        int difX;
        int difY;
        int startX;
        int startY;
        int numberOfPoints;

        if (first.x == second.x) {
            /*start line is vertical, so coordinate X is the same for all positions,
              up and down direction of formula is 0*/
            racers.get(1).setSpeed(0);
            startX = first.getX();
            difX = 0;
            difY = 1;

            if (second.y > first.y) {
                //direction of first move will be to the right
                racers.get(1).setSide(1);
                startY = first.getY();
            } else {
                //direction of first move will be to the left
                racers.get(1).setSide(-1);
                startY = second.getY();
            }
            numberOfPoints = Math.abs(first.getY() - second.getY()) - 1;

        } else {
            /*start line is horizontal, so coordinate Y is the same for all positions,
              left and right direction of formula is 0*/
            racers.get(1).setSide(0);
            startY = first.getY();
            difX = 1;
            difY = 0;

            if (second.x > first.x) {
                //direction of first move will be down
                racers.get(1).setSpeed(-1);
                startX = first.getX();
            } else {
                //direction of first move will be up
                racers.get(1).setSpeed(1);
                startX = second.getX();
            }

            numberOfPoints = Math.abs(first.getX() - second.getX()) - 1;

        }

        for (int i = 0; i < numberOfPoints; i++) {
            startX += difX;
            startY += difY;
            turns.getTurn(i).setPoint(new Point(startX, startY));
        }
    }

    /**
     * It creates possibilities of turns of next player.
     *
     * @param formOnTurn number of formula for which possibilities will be
     * created
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
        createTurns(center, false);
        divideTurns(rivalLast);
    }

    private void createTurns(Point center, boolean crashMode) {
        //when Turns are creating again, all turns inside are non-colision
        turns = new Turns();
        // upper-LEFT corner
        if (!crashMode) {
            turns.getTurn(0).setPoint(new Point(center.x - 1, center.y - 1));
        } else {
            turns.getTurn(0).setExist(false);
        }
        // upper center
        if (turnsCount == NINE_TURNS || crashMode) {
            turns.getTurn(1).setPoint(new Point(center.x, center.y - 1));
        } else {
            turns.getTurn(1).setExist(false);
        }
        // upper-RIGHT corner
        if (!crashMode) {
            turns.getTurn(2).setPoint(new Point(center.x + 1, center.y - 1));
        } else {
            turns.getTurn(2).setExist(false);
        }
        // LEFT
        if (turnsCount == NINE_TURNS || crashMode) {
            turns.getTurn(3).setPoint(new Point(center.x - 1, center.y));
        } else {
            turns.getTurn(3).setExist(false);
        }
        // center
        if (turnsCount == FIVE_TURNS || turnsCount == NINE_TURNS) {
            turns.getTurn(4).setPoint(center);
        } else {
            turns.getTurn(4).setExist(false);
        }
        // RIGHT
        if (turnsCount == NINE_TURNS || crashMode) {
            turns.getTurn(5).setPoint(new Point(center.x + 1, center.y));
        } else {
            turns.getTurn(5).setExist(false);
        }
        // lower-LEFT corner
        if (!crashMode) {
            turns.getTurn(6).setPoint(new Point(center.x - 1, center.y + 1));
        } else {
            turns.getTurn(6).setExist(false);
        }
        // lower center
        if (turnsCount == NINE_TURNS || crashMode) {
            turns.getTurn(7).setPoint(new Point(center.x, center.y + 1));
        } else {
            turns.getTurn(7).setExist(false);
        }
        // lower-RIGHT corner
        if (!crashMode) {
            turns.getTurn(8).setPoint(new Point(center.x + 1, center.y + 1));
        } else {
            turns.getTurn(8).setExist(false);
        }
    }

    /**
     * Metoda najde novy stred po havarii a vykresli nove moznosti novy stred je
     * prunikem kolmice kolizni hrany a kruznice se stredem v miste kolize a
     * polomerem 0.6*velikost mrizky, pricemz bod musi lezet na trati colision
     * je bod, ve kterem doslo k vyjeti z trati.
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
        createTurns(crashCenter, true);
        divideTurns(racers.get(1).getLast());
    }

    /**
     * It devides possible turns into "clean" and "dirty". Dirty turn means
     * formula crashed. In case that one possible turn is equal to rival
     * position, that turns is not allowed.
     *
     * @param rivalLast is position of rival formula
     */
    private void divideTurns(Point rivalLast) {
        Formula act = racers.get(1);
        Track track = model.getRaceTrack();
        Polyline left = track.getLeft();
        Polyline right = track.getRight();
        for (int i = 0; i < turns.getSize(); i++) {
            Point actPoint = turns.getTurn(i).getPoint();

            if (actPoint.isEqual(rivalLast) == false && turns.getTurn(i).isExist()) {
                boolean colision = false;
                Segment colLine = null;
                //----------- kontrola KOLIZE tahu s LEVOU STRANOU: -----------
                for (int k = 0; k < left.getLength() - 1; k++) {
                    Segment actLeft = left.getSegment(k);
                    Object[] cross = Calc.crossing(act.getLast(), actPoint, actLeft);
                    if ((int) cross[0] != Calc.OUTSIDE) {
                        //novy bod ma prunik nebo se dotyka leve krajnice
                        colLine = actLeft;
                        Point colPoint = (Point) cross[1];
                        colPoint.setLocation(Point.COLLISION_LEFT);
                        turns.getTurn(i).setCollision(colPoint);
                        colision = true;
                        break;
                    }
                }
                if (colision == false) { //tah nekrizi levou krajnici
                    // ---------- kontrola KOLIZE novych moznosti s PRAVOU STRANOU: -------------
                    for (int k = 0; k < right.getLength() - 1; k++) {
                        Segment actRight = right.getSegment(k);
                        Object[] cross = Calc.crossing(act.getLast(), actPoint, actRight);
                        if ((int) cross[0] != Calc.OUTSIDE) {
                            //novy bod ma prunik nebo se dotyka prave krajnice
                            colLine = actRight;
                            Point colPoint = (Point) cross[1];
                            colPoint.setLocation(Point.COLLISION_RIGHT);
                            turns.getTurn(i).setCollision(colPoint);
                            colision = true;
                            break;
                        }
                    }
                }
                if (colision == false) {
                    //tah nekrizi zadnou krajnici
                    Object[] start = Calc.crossing(act.getLast(), actPoint, track.getStart());
                    Object[] finish = Calc.crossing(act.getLast(), actPoint, track.getFinish());
                    if ((int) start[0] != Calc.OUTSIDE && Track.RIGHT == Calc.sidePosition(actPoint, track.getStart())) {
                        //tah protina start a konci vpravo od nej (projel se v protismeru)
                        colLine = track.getStart();
                        Point colPoint = (Point) start[1];
                        colPoint.setLocation(Point.COLLISION_RIGHT);
                        turns.getTurn(i).setCollision(colPoint);
                        colision = true;
                    } else if ((int) finish[0] == Calc.INSIDE) {
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
                if (colision) {
                    turns.getTurn(i).setType(Turns.Turn.COLLISION);
                    act.setColision(colLine);
                }
            } else {
                turns.getTurn(i).setExist(false);
            }
        }
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
