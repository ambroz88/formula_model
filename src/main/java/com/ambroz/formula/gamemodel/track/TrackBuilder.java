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
        if (getStage() < TrackBuilder.EDIT_PRESS) {
            click.toGridUnits(getPaper().getGridSize());

            configureTrackSides();

            Polyline actLine = getTrack().getLine(side);
            Polyline oppositeLine = getTrack().getLine(oppSide);
            message = HintLabels.EMPTY;

            if (!oppositeLine.isEmpty()) {
                //OPPOSITE LINE WAS ALLREADY STARTED
                if (actLine.isEmpty()) {
                    determineStartLine(click);
                } else if (actLine.getLast().isEqual(click) == false && validateSecondLine(click)) {
                    addPointToTrack(click);
                }
            } else {
                validateJustActiveLine(actLine, click);
            }

            fireHint(getMessage());
            repaintScene();
        }
    }

    private void configureTrackSides() {
        if (BUILD_LEFT == side) {
            side = Track.LEFT;
            oppSide = Track.RIGHT;
        } else {
            side = Track.RIGHT;
            oppSide = Track.LEFT;
        }
    }

    /**
     * User starting to build second track line - should choose one of the several start points.
     *
     * @param click is point where user clicked
     */
    private void determineStartLine(Point click) {
        if (getPoints().contains(click)) {
            generateFinishTurns();
            getPoints().addPoint(click);
            getTrack().addPoint(side, click);
        } else {
            message = HintLabels.WRONG_START;
        }
    }

    /**
     * This method generates points where it is possible to place last point of the track so the finish line would be in
     * vertical or horizontal plane.
     *
     * @return points as polyline
     */
    private void generateFinishTurns() {
        Polyline oppLine = getTrack().getLine(oppSide);
        Point start = oppLine.getPreLast();
        Point finish = oppLine.getLast();

        int quad = TrackUtils.findQuad(start, finish);
        setPoints(TrackUtils.generateGoalPoints(quad, finish, side));
    }

    /**
     * Position of point click is valid so it can be added to the track.
     *
     * @param click is point where user clicked
     */
    private void addPointToTrack(Point click) {
        getTrack().addPoint(side, click);

        boolean ready = getTrack().isReadyForDraw() && getPoints().contains(click);
        if (ready) {
            getTrack().finishIndexes();
        }

        fireTrackReady(ready);
    }

    /**
     * This method validates whether is point valid in situation when second track line didn't start to build.
     *
     * @param actLine is track line that is built
     * @param click is point where user clicked
     */
    private void validateJustActiveLine(Polyline actLine, Point click) {
        if (actLine.isEmpty()) {
            //first point in side is drawn
            getPoints().addPoint(click);
            getTrack().addPoint(side, click);
        } else if (!actLine.getLast().isEqual(click)) {

            //point click is not identical with the last point in builded side
            if (actLine.getLength() == 1) {
                getTrack().addPoint(side, click);
            } else if (!actLine.checkOwnCrossing(click)) {
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
    }

    /**
     * This method tests if it's possible to add <code>point click</code> to the track. It controls both side of track.
     *
     * @param click is point which is tested or added
     * @return true if <code>click</code> is possible to add
     */
    private boolean validateSecondLine(Point click) {
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

    //=================================== FUNCTIONALITY OF GUI BUTTONS ===================================
    public void startBuild(int side) {
        if (getTrack().getOppLine(side).getLength() != 1) {
            setSide(side);

            generateEndPoints();

            repaintScene();
        } else {
            if (side == Track.LEFT) {
                fireHint(HintLabels.RIGHT_SIDE_FIRST);
            } else {
                fireHint(HintLabels.LEFT_SIDE_FIRST);
            }
        }
    }

    /**
     * It generates points where the track should be finished or points where to start with second track side.
     */
    public void generateEndPoints() {
        if (getTrack().getOppLine(side).getLength() > 1 && getTrack().getLine(side).isEmpty()) {
            generateStartTurns();
        } else if (getTrack().getOppLine(side).getLength() > 1 && !getTrack().getLine(side).isEmpty()) {
            generateFinishTurns();
        }
    }

    private void setSide(int side) {
        this.side = side;

        if (side == Track.LEFT) {
            setStage(BUILD_LEFT);
            this.oppSide = Track.RIGHT;
        } else {
            setStage(BUILD_RIGHT);
            this.oppSide = Track.LEFT;
        }

        getTrack().setWidth(side);
    }

    public void startEditing() {
        setStage(TrackBuilder.EDIT_PRESS);
        fireHint(HintLabels.MOVE_POINTS);
        getPoints().clear();
        repaintScene();
    }

    public void switchTrack() {
        setStage(TrackBuilder.BUILD_LEFT);
        getTrack().switchStart();
        generateEndPoints();
        repaintScene();
    }

    /**
     * It removes last point of active track side.
     */
    public void deletePoint() {
        int actSize = getTrack().getLine(side).getLength();
        int oppSize = getTrack().getLine(oppSide).getLength();

        if (actSize > 0) {
            removeLast();

            if (actSize == 1) {
                //when side has just one point, it will be added to list of points that will be draw
                getPoints().addPoint(getTrack().getLine(side).getLast());
            } else if (actSize == 0) {

                //if last point of side is deleted, start points will be generated
                getPoints().clear();
                if (oppSize > 0) {
                    generateStartTurns();
                }

            } else if (oppSize > 1) {
                generateFinishTurns();
            }

            repaintScene();
        }
    }

    private void removeLast() {
        getTrack().removeLastPoint(side);
        if (!getTrack().getLine(side).isEmpty()) {
            boolean ready = getTrack().isReady() && getPoints().contains(getTrack().getLine(side).getLast());
            fireTrackReady(ready);
        }
    }

    /**
     * This method generates points where it is possible to place first point of the second side of the track so the
     * start line would be in vertical or horizontal plane.
     *
     * @return points as polyline
     */
    private void generateStartTurns() {
        Polyline oppLine = getTrack().getLine(oppSide);
        Point start = oppLine.getPoint(0);
        Point finish = oppLine.getPoint(1);

        int quad = TrackUtils.findQuad(start, finish);
        setPoints(TrackUtils.generateGoalPoints(quad, start, side));
    }

    public void clearScene() {
        getTrack().reset();
        getPoints().clear();
        repaintScene();
    }

    public boolean saveTrack(String trackName) {
        boolean saved;
        try {
            TrackIO.trackToJSON(getTrack(), trackName);
            // cought by TrackTopComponent:
            firePropertyChange("newTrack", false, true);
            fireHint(HintLabels.HINT_SAVED);
            saved = true;
        } catch (IOException ex) {
            fireHint(HintLabels.HINT_FAILED);
            saved = false;
        }
        return saved;
    }

    //================================================================================================
    public Polyline getPoints() {
        return points;
    }

    public void setPoints(Polyline points) {
        this.points = points;
    }

    public String getMessage() {
        return message;
    }

    public void fireTrackReady(boolean ready) {
        firePropertyChange("trackReady", !ready, ready);
    }

}
