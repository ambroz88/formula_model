package com.ambroz.formula.gamemodel.track;

import java.io.IOException;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.utils.Calc;
import com.ambroz.formula.gamemodel.utils.TrackIO;
import com.ambroz.formula.gamemodel.utils.TrackUtils;

/**
 * This class is used when the user builds the track. Basically there is tested if the point which user clicked could be
 * use as a part of the track and which side.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TrackBuilder extends TrackEditor {

    public static final int DIMENSION = 300;
    public static final int BUILD_LEFT = 1;
    public static final int BUILD_RIGHT = 2;
    public static final int EDIT_PRESS = 3;
    public static final int EDIT_RELEASE = 4;

    private int side;
    private int oppSide;

    private Polyline points;
    private String message;

    public TrackBuilder(Paper paper) {
        super(paper);
        points = new Polyline();
        setupPaper();
    }

    private void setupPaper() {
        getPaper().setHeight(DIMENSION);
        getPaper().setWidth(DIMENSION);
    }

    /**
     * This is main method for building track. It controls if input <code>point click</code> can be use in track. If it
     * is a good point the method add it to given side. If it is has a bad position it will be shown some hint.
     *
     * @param click is point where user clicked.
     */
    public void buildTrack(Point click) {
        click.toGridUnits(getPaper().getGridSize());

        if (BUILD_LEFT == side) {
            side = Track.LEFT;
            oppSide = Track.RIGHT;
        } else {
            side = Track.RIGHT;
            oppSide = Track.LEFT;
        }
        Polyline actLine = getTrack().getLine(side);
        Polyline oppLine = getTrack().getLine(oppSide);
        //OPPOSITE SIDE WAS ALLREADY STARTED
        message = HintLabels.EMPTY;
        if (!oppLine.isEmpty()) {
            if (actLine.isEmpty()) {
                if (getPoints().contains(click)) {
                    //builded side is still empty and user clicked on one of the start points
                    drawFinishTurns();
                    getPoints().addPoint(click);//in point of click there will be drawn a point
                    getTrack().addPoint(side, click);
                } else {
                    //builded side is still empty but user clicked out of the start points
                    message = HintLabels.WRONG_START;
                }
            } else if (actLine.getLast().isEqual(click) == false && buildSecondSide(click)) {
                //point click is good and IT IS POSSIBLE TO ADD IT to the track
                getTrack().addPoint(side, click);

                boolean ready = getTrack().isReadyForDraw() && getPoints().contains(click);
                if (ready) {
                    getTrack().finishIndexes();
                }
//                fireTrackReady(ready);
            }
        } else //OPPOSITE SIDE WASN'T STILL STARTED
         if (actLine.getLength() <= 1) {
                //create start
                if (actLine.isEmpty()) {
                    getPoints().addPoint(click); //first point in side is drawn
                }
                getTrack().addPoint(side, click);
            } else if (!actLine.getLast().isEqual(click)) {
                //point click is not identical with the last point in builded side
                if (!actLine.checkOwnCrossing(click)) {
                    //new edge of builded side don't cross any other edge
                    if (correctDirection(actLine, click)) {
                        getTrack().addPoint(side, click);
                    }
                } else {
                    message = HintLabels.CROSSING;
                }
            } else {
                message = HintLabels.IDENTICAL_POINTS;
            }

        repaintScene();
    }

    /**
     * It checks if the future construction move won't be backwards inside the track.
     *
     * @param actLine is line that is builded
     * @param click is point where should be next part of the track line
     * @return true if the position of the point click is OK, false otherwise
     */
    private boolean correctDirection(Polyline actLine, Point click) {
        //check bad direction of constructed side
        if (actLine.getLength() > 1) {
            Segment lastSegment = actLine.getLastSegment();
            if (Calc.sidePosition(click, lastSegment) == oppSide
                    && Calc.distance(lastSegment.getFirst(), lastSegment.getLast())
                    >= Calc.distance(actLine.getPreLast(), Calc.baseOfAltitude(lastSegment, click))) {
                message = HintLabels.FORWARD;
                return false;
            }
        }
        return true;
    }

    /**
     * This method generates points where it is possible to place last point of the track so the finish line would be in
     * vertical or horizontal plane.
     *
     * @return points as polyline
     */
    private void drawFinishTurns() {
        Polyline oppLine = getTrack().getLine(oppSide);
        Point start = oppLine.getPreLast();
        Point finish = oppLine.getLast();

        int quad = TrackUtils.findQuad(start, finish);
        setPoints(TrackUtils.generateGoalPoints(quad, finish, side));
    }

    /**
     * This method generates points where it is possible to place first point of the second side of the track so the
     * start line would be in vertical or horizontal plane.
     *
     * @return points as polyline
     */
    private void drawStartTurns() {
        Polyline oppLine = getTrack().getLine(oppSide);
        Point start = oppLine.getPoint(0);
        Point finish = oppLine.getPoint(1);

        int quad = TrackUtils.findQuad(start, finish);
        setPoints(TrackUtils.generateGoalPoints(quad, start, side));
    }

    /**
     * This method tests if it's possible to add <code>point click</code> to the track. It controls both side of track.
     *
     * @param click is point which is tested or added
     * @return true if <code>click</code> is possible to add
     */
    private boolean buildSecondSide(Point click) {
        Polyline actLine = getTrack().getLine(side);
        Polyline oppLine = getTrack().getLine(oppSide);
        Segment trackEnd = new Segment(actLine.getLast(), oppLine.getPoint(getTrack().getIndex(oppSide)));
        boolean success = true;

        if ((int) Calc.crossing(actLine.getLast(), click, getTrack().getStart())[0] == Calc.INSIDE) {
            message = HintLabels.THROUGH_START;
            success = false;
        } else if (actLine.getLength() == 1 && Calc.sidePosition(click, trackEnd) != side) {
            // check if new side is building on right direction from the start
            message = HintLabels.FORWARD;
            success = false;
        } else if (actLine.checkOwnCrossing(click)) {
            // check crossing of constructed side:
            message = HintLabels.CROSSING;
            success = false;
        } else if (oppLine.checkSegmentCrossing(actLine.getLast(), click)) {
            // check crossing of opposite side:
            message = HintLabels.CROSSING;
            success = false;
        } else if (!correctDirection(actLine, click)) {
            //check bad direction of constructed side
            success = false;
        } else if (getTrack().freeDrawing(side, oppSide)) {
            success = true;
        } else {
            //turn is OK but it is necessary to check "building index" on opposite side
            boolean search = true;
            Point prev, center, next, sidePoint;
            int index = getTrack().getIndex(oppSide);
            while (search) {
                if (index < oppLine.getLength() - 2) {
                    //create next segment that should be crossed by point click:
                    prev = oppLine.getPoint(index);
                    center = oppLine.getPoint(index + 1);
                    next = oppLine.getPoint(index + 2);
                    sidePoint = Calc.calculateAngle(prev, center, next, side);

                    if ((int) Calc.crossing(actLine.getLast(), click, center, sidePoint)[0] >= Calc.EDGE) {
                        //point click went through "control segment"
                        getTrack().setIndex(index + 1, oppSide);
                    }
                } else if (index == oppLine.getLength() - 2) {
                    prev = oppLine.getPoint(index - 1);
                    center = oppLine.getPoint(index);
                    next = oppLine.getPoint(index + 1);
                    sidePoint = Calc.calculateAngle(prev, center, next, side);

                    if ((int) Calc.crossing(actLine.getLast(), click, center, sidePoint)[0] >= Calc.EDGE) {
                        //point click went through "control segment"
                        getTrack().setIndex(index + 1, oppSide);
                    }
                } else {
                    search = false;
                }
                index++;
            }
            getTrack().setIndex(actLine.getLength(), side);
        }
        return success;
    }

    public void setSide(int side) {
        if (side == Track.LEFT) {
            this.oppSide = Track.RIGHT;
        } else {
            this.oppSide = Track.LEFT;
        }
        this.side = side;
        getTrack().setWidth(side);
    }

    public void generateEndPoints(int side) {
        setSide(side);
        //vykresleni moznosti tvorby pocatecnich a koncovych bodu:
        if (getTrack().getOppLine(side).getLength() > 1 && getTrack().getLine(side).isEmpty()) {
            drawStartTurns();
        } else if (getTrack().getOppLine(side).getLength() > 1 && !getTrack().getLine(side).isEmpty()) {
            drawFinishTurns();
        }
    }

    public void deletePoint() {
        //mazani poslednich bodu pri tvorbe trati - podle toho, jaka se krajnice vybrana
        int actSize = getTrack().getLine(side).getLength();
        int oppSize = getTrack().getLine(oppSide).getLength();

        if (actSize > 0) {
            removeLast(side);
            //kdyz zbyde v krajnici pouze jeden bod, tak bude vykreslen
            if (actSize == 1) {
                getPoints().addPoint(getTrack().getLine(side).getLast());
            } else if (actSize == 0) {
                //kdyz se smaze i posledni bod, smaze se take tecka znacici prvni bod
                getPoints().clear();
                if (oppSize > 0) {
                    drawStartTurns();
                }
            } else if (oppSize > 1) {
                drawFinishTurns();
            }
        }
    }

    private void removeLast(int side) {
        getTrack().removeLastPoint(side);
        if (!getTrack().getLine(side).isEmpty()) {
            boolean ready = getTrack().isReady() && getPoints().contains(getTrack().getLine(side).getLast());
            if (ready) {
//                fireTrackReady(ready);
            }
        }
        repaintScene();
    }

    public void startBuild(int side) {
        if (getTrack().getOppLine(side).getLength() != 1) {
            generateEndPoints(side);
            if (side == Track.LEFT) {
                setStage(BUILD_LEFT);
            } else {
                setStage(BUILD_RIGHT);
            }
            repaintScene();
//        } else {
//            if (side == Track.LEFT) {
//                fireHint(HintLabels.RIGHT_SIDE_FIRST);
            //caught by TrackMenu:
//                firePropertyChange("rightSide", false, true);
//            } else {
//                fireHint(HintLabels.LEFT_SIDE_FIRST);
            //caught by TrackMenu:
//                firePropertyChange("leftSide", false, true);
//            }
        }
    }

    /**
     * This is first step of track point replacing. Getting coordination and position of moved point.
     *
     * @param click is point from which user take the point (place where mouse was pressed)
     * @return
     */
    public boolean memorizeTrackPoint(Point click) {
        boolean onTrack = false;
        if (getStage() == EDIT_PRESS) {
            click.toGridUnits(getPaper().getGridSize());

            onTrack = clickOnTrack(click);
            if (!onTrack) {
//                fireHint(HintLabels.NO_POINT);
            } else {
//                fireHint(HintLabels.EMPTY);
                setStage(EDIT_RELEASE);
                repaintScene();
            }
        }

        return onTrack;
    }

    /**
     * This is second step of replacing track point. Getting new coordinations of replaced point.
     *
     * @param click is point where user placed replaced point (place where mouse was released)
     */
    public void replaceTrackPoint(Point click) {
        if (getStage() == EDIT_RELEASE) {
            click.toGridUnits(getPaper().getGridSize());

            setStage(EDIT_PRESS);
            if (!isNewPointValid(click)) {
//                fireHint(HintLabels.CROSSING);
            }
            repaintScene();
        }
    }

    public boolean saveTrack(String trackName) {
        boolean saved;
        try {
            TrackIO.trackToJSON(getTrack(), trackName);
            // cought by TrackTopComponent:
            firePropertyChange("newTrack", false, true);
//            fireHint(HintLabels.HINT_SAVED);
            saved = true;
        } catch (IOException ex) {
//            fireHint(HintLabels.HINT_FAILED);
            saved = false;
        }
        return saved;
    }

    public void repaintScene() {
        //cought by TrackBuilderComponent
        firePropertyChange("repaint", false, true);
    }

    public void reset() {
        getTrack().reset();
        getPoints().clear();
    }

    @Override
    public void setLanguage(String language) {
        super.setLanguage(language);
//        hintLabels = new HintLabels(language);
    }

    public Polyline getPoints() {
        return points;
    }

    public void setPoints(Polyline points) {
        this.points = points;
    }

    public String getMessage() {
        return message;
    }

}
