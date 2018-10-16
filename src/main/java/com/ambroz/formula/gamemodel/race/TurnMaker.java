package com.ambroz.formula.gamemodel.race;

import java.util.HashMap;

import com.ambroz.formula.gamemodel.datamodel.Collision;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.RaceOptions;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.FormulaType;
import com.ambroz.formula.gamemodel.enums.PointPosition;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.track.Track;
import com.ambroz.formula.gamemodel.utils.Calc;
import com.ambroz.formula.gamemodel.utils.TurnCalculations;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TurnMaker extends RaceOptions {

    private final RaceModel model;
    private final HashMap<Integer, Formula> racers;
    private final Turns turns;

    private int formulaID;

    public TurnMaker(RaceModel menu) {
        this.model = menu;
        formulaID = 1;
        racers = new HashMap<>();
        Formula alfred = new Formula(FormulaType.Player);
        racers.put(1, alfred);
//        Formula christina = new Formula(FormulaType.None);
//        racers.put(2, christina);

        turns = new Turns();
    }

    public void firstTurn(Point click) {
        Turn selectedTurn = turns.containsTurn(click);
        if (selectedTurn != null) {

            Formula act = getActiveFormula();
            act.addPoint(click);
            act.addPoint(new Point(click.x + act.getSide(), click.y + act.getSpeed()));

            nextTurn(formulaID, act.getLast());
            model.setStage(RaceModel.NORMAL_TURN);

        }
    }

    public void turn(Point click) {
        Turn selectedTurn = turns.containsTurn(click);
        Formula act = getActiveFormula();

        if (selectedTurn != null) {

            if (selectedTurn.getCollision() == null || selectedTurn.getPosition().contains(PointPosition.Finish)) {

                checkFinishTurn(selectedTurn, click);
                if (!act.getWin()) {
                    act.addPoint(click);
                }
                nextTurn(formulaID, act.getLast());

            } else {
                handleCrashTurn(click, selectedTurn);
            }
        } else {
            if (act.getWait() > 0) {
                act.decreaseWait();
                if (act.getWait() == 0) {
                    getTurns().createCrashTurn(act.getLast());
                    divideTurns(act.getLast());
                }
            }
        }
    }

    private void checkFinishTurn(Turn selectedTurn, Point click) {
        if (Side.Left == Calc.sidePosition(click, model.getTrack().getFinish())) {
            Formula act = getActiveFormula();

            if (selectedTurn.getPosition().equals(PointPosition.Finish)) {
                act.addPoint(selectedTurn.getCollision().getCollisionPoint());
                act.setWin(true);
            } else if (selectedTurn.getPosition().contains(PointPosition.FinishLine)) {
                act.movesUp();
                act.setWin(true);
            }

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
        getTurns().createStandardTurn(center, getTurnsCount());
        divideTurns(rivalLast);

        checkAutomaticTurn();
    }

    private void handleCrashTurn(Point click, Turn selectedTurn) {
        Formula active = getActiveFormula();
        active.setCollision(selectedTurn.getCollision());

        int maxSpeed = active.maxSpeed(click);
        active.setWait(maxSpeed);
        active.movesUp(maxSpeed);

        active.addCollisionPoint();

        Point crashCenter = TurnCalculations.generateCrashCenter(active);
        active.addPoint(crashCenter);
        getTurns().reset();

        model.fireCrash(maxSpeed);
    }

    public void checkAutomaticTurn() {
        //kontrola, jestli se mozne tahy nenachazeji mimo viditelnou oblast
        int onPaperTurns = model.getPaper().outPaperNumber(getTurns().getFreeTurns());//pocet moznosti koncici za cilem
        int outsideTurns = model.getPaper().outPaperNumber(getTurns().getCollisionTurns());//pocet moznosti koncici narazem

        //vsechny moznosti druheho hrace jsou mimo viditelnou oblast:
        // ----------------------- AUTOMATICKY TAH -------------------------
        //pocet moznych tahu: 4,5,9
        if ((outsideTurns == FOUR_TURNS && getTurnsCount() == FOUR_TURNS) || (outsideTurns == FIVE_TURNS && getTurnsCount() == FIVE_TURNS)
                || (outsideTurns == NINE_TURNS && getTurnsCount() == NINE_TURNS)) {//vsechny moznosti zpusobi naraz
            model.setStage(RaceModel.AUTO_CRASH);
            model.fireHint(HintLabels.NEXT_CLOSE_TURN);
        } else if (onPaperTurns > 0 && onPaperTurns == getTurns().getFreeTurns().size()) {
            //zadna dobra moznost neni videt - tah na nejblizsi
            model.setStage(RaceModel.AUTO_FINISH);
            model.fireHint(HintLabels.NEXT_CLOSE_TURN);
        }
    }

    /**
     * All possible turns are outside of the paper and all are crash turns. It will choose the nearest turn.
     */
    public void runAutomaticCrashTurn() {
        Formula act = getActiveFormula();
        Turn collisionTurn = Calc.findNearestTurn(act.getLast(), getTurns().getCollisionTurns());
        act.setCollision(collisionTurn.getCollision());

        int maxSpeed = act.maxSpeed(collisionTurn);
        act.setWait(maxSpeed);
        model.fireCrash(maxSpeed);
        act.movesUp(maxSpeed);
        act.addCollisionPoint();

        Point crashCenter = TurnCalculations.generateCrashCenter(act);
        act.addPoint(crashCenter);
        model.setStage(RaceModel.NORMAL_TURN);
    }

    /**
     * All possible turns are outside of the paper but they are without collision. It is obvious that all turns are
     * behind start line.
     */
    public void runAutomaticFinishTurn() {
        Formula act = getActiveFormula();
        act.addPoint(Calc.findNearestTurn(act.getLast(), getTurns().getFreeTurns()).getCollision().getCollisionPoint());
        act.setWin(true);
        model.checkWinner();
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
        Point lastPoint = getActiveFormula().getLast();

        for (int i = 0; i < turns.getSize(); i++) {
            selectedTurn = turns.getTurn(i);
            lastFormulaMove = new Segment(lastPoint, selectedTurn);

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
            Point cross = Calc.intersectSegments(lastFormulaMove, actLeft);

            if (!cross.getPosition().equals(PointPosition.Outside)) {
                //novy bod ma prunik nebo se dotyka leve krajnice a je napravo od kolizni usecky
                Segment colLine = actLeft;
                if (Calc.sidePosition(lastFormulaMove.getFirst(), actLeft).equals(Side.Right)) {
                    cross.setPosition(PointPosition.CollisionLeft);
                    selectedTurn.setCollision(new Collision(cross, colLine));
                    colision = true;
                }
            }

        }

        return colision;
    }

    private boolean checkRightSideColision(Turn selectedTurn, Segment lastFormulaMove) {
        Polyline right = model.getTrack().getRight();
        boolean colision = false;

        for (int k = 0; k < right.getLength() - 1; k++) {
            Segment actRight = right.getSegment(k);
            Point cross = Calc.intersectSegments(lastFormulaMove, actRight);

            if (!cross.getPosition().equals(PointPosition.Outside)) {
                //novy bod ma prunik nebo se dotyka prave krajnice
                Segment colLine = actRight;
                if (Calc.sidePosition(lastFormulaMove.getFirst(), actRight).equals(Side.Left)) {
                    cross.setPosition(PointPosition.CollisionRight);
                    selectedTurn.setCollision(new Collision(cross, colLine));
                    colision = true;
                }
            }
        }

        return colision;
    }

    private void checkStartColision(Turn selectedTurn, Segment lastFormulaMove) {
        Segment startLine = model.getTrack().getStart();

        Point start = Calc.intersectSegments(lastFormulaMove, startLine);
        if (!start.getPosition().equals(PointPosition.Outside) && Side.Right == Calc.sidePosition(lastFormulaMove.getLast(), startLine)) {
            //tah protina start a konci vpravo od nej (projel se v protismeru)
            start.setPosition(PointPosition.CollisionRight);
            selectedTurn.setCollision(new Collision(start, startLine));
        }

    }

    private void checkFinishCrossing(Turn selectedTurn, Segment lastFormulaMove) {
        Track track = model.getTrack();
        Point finish = Calc.intersectSegments(lastFormulaMove, track.getFinish());

        if (finish.getPosition().equals(PointPosition.Inside)) {

            //tah protina cilovou caru:
            if (selectedTurn.getCollision() != null) {
                evalateFinishColision(selectedTurn, finish);
            } else {
                selectedTurn.setCollision(new Collision(finish, track.getFinish()));
                selectedTurn.setPosition(PointPosition.Finish);
            }

        } else if (finish.getPosition().equals(PointPosition.Edge)) {

            //tah se dotyka cilove cary:
            if (selectedTurn.getCollision() != null) {
                evalateFinishColision(selectedTurn, finish);
            } else {
                selectedTurn.setCollision(new Collision(finish, track.getFinish()));
                selectedTurn.setPosition(PointPosition.FinishLine);
            }

        } else {

            Point start = Calc.intersectSegments(lastFormulaMove, track.getStart());
            if (start.getPosition().equals(PointPosition.Inside)) {
                //tah protina startovni caru:
                if (selectedTurn.getCollision() != null) {
                    evalateStartColision(selectedTurn, start);
                }
            }

        }

    }

    private void evalateFinishColision(Turn selectedTurn, Point finishColision) {
        if (Calc.distance(getActiveFormula().getLast(), finishColision)
                < Calc.distance(getActiveFormula().getLast(), selectedTurn.getCollision().getCollisionPoint())) {
            //hrac protne cil pred narazem
            selectedTurn.setCollision(new Collision(finishColision, model.getTrack().getFinish()));
            selectedTurn.setPosition(PointPosition.Finish);
        }
    }

    private void evalateStartColision(Turn selectedTurn, Point startColision) {
        if (Calc.distance(getActiveFormula().getLast(), startColision)
                < Calc.distance(getActiveFormula().getLast(), selectedTurn.getCollision().getCollisionPoint())) {
            //hrac protne start pred narazem
            startColision.setPosition(PointPosition.CollisionRight);
            selectedTurn.setCollision(new Collision(startColision, model.getTrack().getStart()));
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
        getActiveFormula().setSpeed(0);
        int startY;

        if (second.getY() > first.getY()) {
            //direction of first move will be to the right
            getActiveFormula().setSide(1);
            startY = first.getY();
        } else {
            //direction of first move will be to the left
            getActiveFormula().setSide(-1);
            startY = second.getY();
        }

        return new Point(first.getX(), startY);
    }

    private Point createHorizontalStartPositions(Point first, Point second) {
        getActiveFormula().setSide(0);
        int startX;

        if (second.getX() > first.getX()) {
            //direction of first move will be down
            getActiveFormula().setSpeed(-1);
            startX = first.getX();
        } else {
            //direction of first move will be up
            getActiveFormula().setSpeed(1);
            startX = second.getX();
        }

        return new Point(startX, first.getY());
    }

    //---------------------------------------------------------------------------
    public int getFormulaCount() {
        return racers.size();
    }

    public Formula getActiveFormula() {
        return getFormula(formulaID);
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

    public int getFormulaID() {
        return formulaID;
    }

    public void setFormulaID(int id) {
        this.formulaID = id;
    }

}
