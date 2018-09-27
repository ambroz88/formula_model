package com.ambroz.formula.gamemodel.race;

import java.awt.Color;
import java.util.HashMap;

import com.ambroz.formula.gamemodel.datamodel.Collision;
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
    private final Turns turns;
    private int turnsCount;
    private int formulaID;

    public TurnMaker(RaceModel menu) {
        this.model = menu;
        formulaID = 1;
        racers = new HashMap<>();
        racers.put(formulaID, new Formula(FormulaType.Player));
        getFormula(formulaID).setColor(Color.BLUE.getRGB());
        turns = new Turns();
        turnsCount = 4;
    }

    public void firstTurn(Point click) {
        Turn selectedTurn = turns.containsTurn(click);
        if (selectedTurn != null) {

            Formula act = getFormula(formulaID);
            act.addPoint(click);
            act.addPoint(new Point(click.x + act.getSide(), click.y + act.getSpeed()));

            nextTurn(formulaID, act.getLast());
            model.setStage(RaceModel.NORMAL_TURN);

        }
    }

    public void turn(Point click) {
        Turn selectedTurn = turns.containsTurn(click);
        if (selectedTurn != null) {

            Formula act = getFormula(formulaID);
            if (selectedTurn.getCollision() == null || selectedTurn.getLocation().contains("finish")) {

                act.addPoint(click);
                act.movesUp();
                checkFinishTurn(selectedTurn, click);
                nextTurn(formulaID, act.getLast());

            } else {
                handleCrashTurn(click, selectedTurn);
            }
        }
    }

    private void checkFinishTurn(Turn selectedTurn, Point click) {
        if (selectedTurn.getLocation().contains(Point.FINISH)
                && Track.LEFT == Calc.sidePosition(click, model.getTrack().getFinish())) {
            Formula act = getFormula(formulaID);
            act.lengthUp(act.getPreLast(), selectedTurn.getCollision().getCollisionPoint());
            act.setWin(true);
        }
    }

    /**
     * It creates possibilities of turns of next player.
     *
     * @param formOnTurn number of formula for which possibilities will be created
     * @param rivalLast last point of other player
     */
    private void nextTurn(int formOnTurn, Point rivalLast) {
        Point center = racers.get(formOnTurn).calculateNextCenter();
        getTurns().createStandardTurn(center, turnsCount);
        divideTurns(rivalLast);
    }

    private void handleCrashTurn(Point click, Turn selectedTurn) {
        Formula active = getFormula(formulaID);
        active.movesUp();
        active.setCollision(selectedTurn.getCollision());

        int maxSpeed = active.maxSpeed(click);
        active.setWait(maxSpeed + 1);
        active.movesUp(maxSpeed);

        active.addCollisionPoint();
        active.lengthUp();

        Point crashCenter = TurnCalculations.generateCrashCenter(active);
        active.addPoint(crashCenter);
        getTurns().createCrashTurn(crashCenter);

        divideTurns(active.getLast());
        model.fireCrash(maxSpeed);
    }

    //-------------------------- DIVIDE TURNS methods -------------------------
    /**
     * It divides possible turns into "clean" and "dirty". Dirty turn means formula crashed. In case that one possible
     * turn is equal to rival position, that turns is not allowed.
     *
     * @param rivalLast is position of rival formula
     */
    private void divideTurns(Point rivalLast) {
        Turn selectedTurn;
        Segment lastFormulaMove;
        Point lastPoint = getFormula(formulaID).getLast();

        for (int i = 0; i < turns.getSize(); i++) {
            selectedTurn = turns.getTurn(i);
            lastFormulaMove = new Segment(selectedTurn, lastPoint);

            if (selectedTurn.isEqual(rivalLast) == false && selectedTurn.isExist()) {

                if (!checkLeftSideColision(selectedTurn, lastFormulaMove) && !checkRightSideColision(selectedTurn, lastFormulaMove)) {
                    checkStartColision(selectedTurn, lastFormulaMove);
                }

                checkFinishCrossing(selectedTurn, lastFormulaMove);
            } else {
                selectedTurn.setExist(false);
            }
        }
    }

    private boolean checkLeftSideColision(Turn selectedTurn, Segment lastFormulaMove) {
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
                selectedTurn.setCollision(new Collision(colPoint, colLine));
                colision = true;
                break;
            }

        }

        return colision;
    }

    private boolean checkRightSideColision(Turn selectedTurn, Segment lastFormulaMove) {
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
                selectedTurn.setCollision(new Collision(colPoint, colLine));
                colision = true;
                break;
            }
        }

        return colision;
    }

    private void checkStartColision(Turn selectedTurn, Segment lastFormulaMove) {
        Segment startLine = model.getTrack().getStart();

        Object[] start = Calc.crossing(lastFormulaMove, startLine);
        if ((int) start[0] != Calc.OUTSIDE && Track.RIGHT == Calc.sidePosition(lastFormulaMove.getFirst(), startLine)) {
            //tah protina start a konci vpravo od nej (projel se v protismeru)
            Point colPoint = (Point) start[1];
            colPoint.setLocation(Point.COLLISION_RIGHT);
            selectedTurn.setCollision(new Collision(colPoint, startLine));
        }

    }

    private void checkFinishCrossing(Turn selectedTurn, Segment lastFormulaMove) {
        Track track = model.getTrack();
        Object[] finish = Calc.crossing(lastFormulaMove, track.getFinish());

        if ((int) finish[0] == Calc.INSIDE) {
            //tah protina cilovou caru:
            if (selectedTurn.getCollision() != null) {
                evalateFinishColision(selectedTurn, (Point) finish[1]);
            } else {
                selectedTurn.setCollision(new Collision((Point) finish[1], track.getFinish()));
                selectedTurn.setLocation(Point.FINISH);
            }
        } else if ((int) finish[0] == Calc.EDGE) {
            //tah se dotyka cilove cary:
            if (selectedTurn.getCollision() != null) {
                evalateFinishColision(selectedTurn, (Point) finish[1]);
            } else {
                selectedTurn.setCollision(new Collision((Point) finish[1], track.getFinish()));
                selectedTurn.setLocation(Point.FINISH_LINE);
            }
        }

    }

    private void evalateFinishColision(Turn selectedTurn, Point finishColision) {
        if (Calc.distance(getFormula(formulaID).getLast(), finishColision)
                < Calc.distance(getFormula(formulaID).getLast(), selectedTurn.getCollision().getCollisionPoint())) {
            //hrac protne cil pred narazem
            selectedTurn.setCollision(new Collision(finishColision, model.getTrack().getFinish()));
            selectedTurn.setLocation(Point.FINISH);
//            selectedTurn.setType(Turn.FREE);
        }
    }

    // -------------------------- Creating START POSITIONS ------------------------
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
        getFormula(formulaID).setSpeed(0);
        int startY;

        if (second.getY() > first.getY()) {
            //direction of first move will be to the right
            getFormula(formulaID).setSide(1);
            startY = first.getY();
        } else {
            //direction of first move will be to the left
            getFormula(formulaID).setSide(-1);
            startY = second.getY();
        }

        return new Point(first.getX(), startY);
    }

    private Point createHorizontalStartPositions(Point first, Point second) {
        getFormula(formulaID).setSide(0);
        int startX;

        if (second.getX() > first.getX()) {
            //direction of first move will be down
            getFormula(formulaID).setSpeed(-1);
            startX = first.getX();
        } else {
            //direction of first move will be up
            getFormula(formulaID).setSpeed(1);
            startX = second.getX();
        }

        return new Point(startX, first.getY());
    }

    //---------------------------------------------------------------------------
    public int getFormulaCount() {
        return racers.size();
    }

    public Formula getFormula(int id) {
        return racers.get(id);
    }

    public void resetTurns() {
        turns.reset();
    }

    public void setTurnsCount(int turnsCount) {
        this.turnsCount = turnsCount;
    }

    public Turns getTurns() {
        return turns;
    }

}
