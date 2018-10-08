package com.ambroz.formula.gamemodel.track;

import java.io.IOException;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.PointPosition;
import com.ambroz.formula.gamemodel.enums.Side;
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

    public static final int BUILD_LEFT = 1;
    public static final int BUILD_RIGHT = 2;
    public static final int EDIT_PRESS = 3;
    public static final int EDIT_RELEASE = 4;

    private Side activeSide;

    private Polyline points;
    private String message;

    public TrackBuilder(Paper paper) {
        super(paper);
        points = new Polyline();
        activeSide = Side.Left;
        setStage(BUILD_LEFT);
    }

    /**
     * This is main method for building track. It controls if input <code>point click</code> can be use in track. If it
     * is a good point the method add it to given side. If it is has a bad position it will be shown some hint.
     *
     * @param click is point where user clicked.
     */
    public void buildTrack(Point click) {
        if (getStage() < TrackBuilder.EDIT_PRESS) {

            configureTrackSides();

            Polyline activeLine = getTrack().getLine(activeSide);
            Polyline oppositeLine = getTrack().getLine(activeSide.getOppositeSide());
            message = HintLabels.EMPTY;

            if (!oppositeLine.isEmpty()) {
                //OPPOSITE LINE WAS ALLREADY STARTED
                if (activeLine.isEmpty()) {
                    determineStartLine(click);
                } else if (activeLine.getLast().isEqual(click) == false && validateSecondLine(click)) {
                    addPointToTrack(click);
                }
            } else {
                validateJustActiveLine(activeLine, click);
            }

            fireHint(getMessage());
            repaintScene();
        }
    }

    private void configureTrackSides() {
        if (getStage() == BUILD_LEFT) {
            activeSide = Side.Left;
        } else {
            activeSide = Side.Right;
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
            getTrack().addPoint(activeSide, click);
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
        Polyline oppLine = getTrack().getLine(activeSide.getOppositeSide());
        Point lastSegmentStart = oppLine.getPreLast();
        Point lastSegmentEnd = oppLine.getLast();

        int quad = TrackUtils.findQuad(lastSegmentStart, lastSegmentEnd);
        setPoints(TrackUtils.generateGoalPoints(quad, lastSegmentEnd, activeSide));
    }

    /**
     * Position of point click is valid so it can be added to the track.
     *
     * @param click is point where user clicked
     */
    private void addPointToTrack(Point click) {
        getTrack().addPoint(activeSide, click);

        boolean ready = getTrack().isReadyForDraw() && getPoints().contains(click);
        if (ready) {
            getTrack().finishIndexes();
            message = HintLabels.TRACK_READY;
        }

        fireTrackReady(ready);
    }

    /**
     * This method validates whether is point valid in situation when second track line didn't start to build.
     *
     * @param activeLine is track line that is built
     * @param click is point where user clicked
     */
    private void validateJustActiveLine(Polyline activeLine, Point click) {
        if (activeLine.isEmpty()) {
            //first point in side is drawn
            getPoints().addPoint(click);
            getTrack().addPoint(activeSide, click);
        } else if (!activeLine.getLast().isEqual(click)) {

            //point click is not identical with the last point in builded side
            if (activeLine.getLength() == 1) {
                getTrack().addPoint(activeSide, click);
            } else if (!activeLine.checkOwnCrossing(click)) {
                //new edge of builded side don't cross any other edge
                if (correctDirection(activeLine, click)) {
                    getTrack().addPoint(activeSide, click);
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
        Side oppSide = activeSide.getOppositeSide();
        Polyline activeLine = getTrack().getLine(activeSide);
        Polyline oppositeLine = getTrack().getLine(oppSide);
        Segment trackEnd = new Segment(activeLine.getLast(), oppositeLine.getPoint(getTrack().getIndex(oppSide)));
        boolean success = true;

        if (Calc.intersectSegments(activeLine.getLast(), click, getTrack().getStart()).getPosition().equals(PointPosition.Inside)) {
            message = HintLabels.THROUGH_START;
            success = false;
        } else if (activeLine.getLength() == 1 && Calc.sidePosition(click, trackEnd) != activeSide) {
            // check if new side is building on right direction from the start
            message = HintLabels.FORWARD;
            success = false;
        } else if (activeLine.checkOwnCrossing(click)) {
            // check crossing of constructed side:
            message = HintLabels.CROSSING;
            success = false;
        } else if (oppositeLine.checkSegmentCrossing(activeLine.getLast(), click)) {
            // check crossing of opposite side:
            message = HintLabels.CROSSING;
            success = false;
        } else if (!correctDirection(activeLine, click)) {
            //check bad direction of constructed side
            success = false;
        } else if (!getTrack().freeDrawing(activeSide, oppSide)) {
            calculateTrackIndexes(click);
        }

        return success;
    }

    private void calculateTrackIndexes(Point click) {
        //turn is OK but it is necessary to check "building index" on opposite side
        Side oppSide = activeSide.getOppositeSide();
        Polyline activeLine = getTrack().getLine(activeSide);
        Polyline oppositeLine = getTrack().getLine(oppSide);

        boolean search = true;
        Point prev;
        Point center;
        Point next;
        Point sidePoint;
        double angle;
        int index = getTrack().getIndex(oppSide);

        while (search) {

            if (index > oppositeLine.getLength() - 2) {
                search = false;
            } else {

                if (index < oppositeLine.getLength() - 2) {
                    //create next segment that should be crossed by point click:
                    prev = oppositeLine.getPoint(index);
                    center = oppositeLine.getPoint(index + 1);
                    next = oppositeLine.getPoint(index + 2);
                } else {
                    prev = oppositeLine.getPoint(index - 1);
                    center = oppositeLine.getPoint(index);
                    next = oppositeLine.getPoint(index + 1);
                }

                angle = Calc.calculateAngle(prev, center, next, activeSide);
                sidePoint = Calc.rotatePoint(prev, center, angle / 2, 10);

                if (Calc.intersectSegments(activeLine.getLast(), click, center, sidePoint).getPosition().contains(PointPosition.Inside)) {
                    //point click went through "control segment"
                    getTrack().setIndex(index + 1, oppSide);
                }
            }
            index++;

        }
        getTrack().setIndex(activeLine.getLength(), activeSide);
    }

    /**
     * It checks if the future construction move won't be backwards inside the track.
     *
     * @param activeLine is line that is builded
     * @param click is point where should be next part of the track line
     * @return true if the position of the point click is OK, false otherwise
     */
    private boolean correctDirection(Polyline activeLine, Point click) {
        //check bad direction of constructed side
        if (activeLine.getLength() > 1) {
            Segment lastSegment = activeLine.getLastSegment();
            if (Calc.sidePosition(click, lastSegment) == activeSide.getOppositeSide()
                    && Calc.distance(lastSegment.getFirst(), lastSegment.getLast())
                    >= Calc.distance(activeLine.getPreLast(), Calc.baseOfAltitude(lastSegment, click))) {
                message = HintLabels.FORWARD;
                return false;
            }
        }
        return true;
    }

    //=================================== FUNCTIONALITY OF GUI BUTTONS ===================================
    public void startBuild(Side side) {
        if (getTrack().getLine(side.getOppositeSide()).getLength() != 1) {
            fireHint(HintLabels.EMPTY);

            setSides(side);
            generateEndPoints();

            repaintScene();
        } else {
            if (side == Side.Left) {
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
        Side oppSide = activeSide.getOppositeSide();
        if (getTrack().getLine(oppSide).getLength() > 1 && getTrack().getLine(activeSide).isEmpty()) {
            generateStartTurns();
        } else if (getTrack().getLine(oppSide).getLength() > 1 && !getTrack().getLine(activeSide).isEmpty()) {
            generateFinishTurns();
        }
    }

    private void setSides(Side active) {
        this.activeSide = active;

        if (active == Side.Left) {
            setStage(BUILD_LEFT);
        } else {
            setStage(BUILD_RIGHT);
        }

        getTrack().setWidth(active);
    }

    public void startEditing() {
        setStage(TrackBuilder.EDIT_PRESS);
        fireHint(HintLabels.MOVE_POINTS);
        getPoints().clear();
        repaintScene();
    }

    public void switchTrack() {
        fireHint(HintLabels.EMPTY);
        setStage(TrackBuilder.BUILD_LEFT);
        getTrack().switchStart();
        generateEndPoints();
        repaintScene();
    }

    /**
     * It removes last point of active track side.
     */
    public void deletePoint() {
        fireHint(HintLabels.EMPTY);
        removeLast();
        Side oppSide = activeSide.getOppositeSide();

        int actSize = getTrack().getLine(activeSide).getLength();
        int oppSize = getTrack().getLine(oppSide).getLength();

        if (actSize == 1) {
            //when side has just one point, it will be added to list of points that will be draw
            getPoints().addPoint(getTrack().getLine(activeSide).getLast());
        } else if (actSize == 0) {

            //if last point of side is deleted, start points will be generated
            getPoints().clear();
            if (oppSize > 0) {
                generateStartTurns();
                getTrack().setIndex(0, oppSide);
            }

        } else if (oppSize > 1) {
            generateFinishTurns();
        }

        repaintScene();
    }

    private void removeLast() {
        getTrack().removeLastPoint(activeSide);
        if (!getTrack().getLine(activeSide).isEmpty()) {
            boolean ready = getTrack().isReady() && getPoints().contains(getTrack().getLine(activeSide).getLast());
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
        Polyline oppLine = getTrack().getLine(activeSide.getOppositeSide());
        Point start = oppLine.getPoint(0);
        Point finish = oppLine.getPoint(1);

        int quad = TrackUtils.findQuad(start, finish);
        setPoints(TrackUtils.generateGoalPoints(quad, start, activeSide));
    }

    public void clearScene() {
        fireHint(HintLabels.EMPTY);
        getTrack().reset();
        getPoints().clear();
        fireTrackReady(false);
        repaintScene();
    }

    public boolean saveTrack(String trackName) {
        boolean saved;
        try {
            TrackIO.trackToJSON(getTrack(), trackName);
            // cought by TrackListComponent:
            firePropertyChange(TRACK_SAVED, false, true);
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
        firePropertyChange(TRACK_READY, !ready, ready);
    }

}
