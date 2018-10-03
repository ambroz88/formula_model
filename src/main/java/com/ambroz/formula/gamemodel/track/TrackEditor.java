package com.ambroz.formula.gamemodel.track;

import com.ambroz.formula.gamemodel.datamodel.CoreModel;
import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.PointPosition;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TrackEditor extends CoreModel {

    private int movePointIndex;
    private Side side;

    public TrackEditor(Paper paper) {
        super(paper);
    }

    /**
     * This is first step of track point replacing. Getting coordination and position of moved point.
     *
     * @param click is point from which user take the point (place where mouse was pressed)
     * @return
     */
    public boolean memorizeTrackPoint(Point click) {
        boolean onTrack = false;
        if (getStage() == TrackBuilder.EDIT_PRESS) {

            onTrack = clickOnTrack(click);
            if (!onTrack) {
                fireHint(HintLabels.NO_POINT);
            } else {
                fireHint(HintLabels.EMPTY);
                setStage(TrackBuilder.EDIT_RELEASE);
                repaintScene();
            }
        }

        return onTrack;
    }

    /**
     * It finds out which point will be moved, resp it's side and position. Start and finish can't be moved.
     *
     * @param click is point where user clicked - potential point that will be moved
     * @return true if the point is part of the track
     */
    private boolean clickOnTrack(Point click) {
        //check if player clicked on the point from left side of the track
        for (int i = 1; i < getTrack().getLeft().getLength() - 1; i++) {
            if (click.isEqual(getTrack().getLeft().getPoint(i))) {
                movePointIndex = i;
                side = Side.Left;
                break;
            }
        }
        if (movePointIndex == 0) {
            //check if player clicked on the point from right side of the track
            for (int i = 1; i < getTrack().getRight().getLength() - 1; i++) {
                if (click.isEqual(getTrack().getRight().getPoint(i))) {
                    movePointIndex = i;
                    side = Side.Right;
                    break;
                }
            }
        }

        return movePointIndex > 0;
    }

    /**
     * This is second step of replacing track point. Getting new coordinations of replaced point.
     *
     * @param click is point where user placed replaced point (place where mouse was released)
     */
    public void replaceTrackPoint(Point click) {
        if (getStage() == TrackBuilder.EDIT_RELEASE) {

            setStage(TrackBuilder.EDIT_PRESS);
            if (!isNewPointValid(click)) {
                fireHint(HintLabels.CROSSING);
            }
            repaintScene();
        }
    }

    /**
     * It validates if user moves the point in correct position so newly created segments don't have intersect with any
     * other segments.
     *
     * @param click is point where user placed certain point of the track
     * @return true if there is no intersection with the rest of the track, false otherwise
     */
    private boolean isNewPointValid(Point click) {
        Point newEdgeStart = getTrack().getLine(side).getPoint(movePointIndex - 1);
        Point newEdgeEnd = getTrack().getLine(side).getPoint(movePointIndex + 1);

        // new segments can't cross start, finish or opposite side
        boolean intersect = crossEndLines(newEdgeStart, newEdgeEnd, click)
                || getTrack().getLine(side.getOppositeSide()).checkSegmentCrossing(newEdgeStart, click)
                || getTrack().getLine(side.getOppositeSide()).checkSegmentCrossing(newEdgeEnd, click);

        if (!intersect) {

            for (int i = 0; i < getTrack().getLine(side).getLength() - 1; i++) {
                // new segments can't cross it's own side but it can touch it
                if (i < movePointIndex - 1 || i > movePointIndex) {

                    Segment actRight = getTrack().getLine(side).getSegment(i);
                    if (Calc.intersectSegments(click, newEdgeStart, actRight).getPosition().equals(PointPosition.Inside)
                            || Calc.intersectSegments(click, newEdgeEnd, actRight).getPosition().equals(PointPosition.Inside)) {
                        intersect = true;
                    }

                }
            }

        }

        if (intersect == false) {
            //overwrite point of the track
            getTrack().getLine(side).changePoint(click, movePointIndex);
        }
        movePointIndex = 0;
        return !intersect;
    }

    private boolean crossEndLines(Point edgeStart, Point edgeEnd, Point click) {
        boolean intersect = false;
        if (getTrack().getStart() != null) {

            if (Calc.intersectSegments(edgeStart, click, getTrack().getStart()).getPosition().equals(PointPosition.Inside)
                    || Calc.intersectSegments(edgeEnd, click, getTrack().getStart()).getPosition().equals(PointPosition.Inside)) {
                intersect = true;
            } else if (getTrack().getFinish() != null) {
                if (Calc.intersectSegments(edgeStart, click, getTrack().getFinish()).getPosition().equals(PointPosition.Inside)
                        || Calc.intersectSegments(edgeEnd, click, getTrack().getFinish()).getPosition().equals(PointPosition.Inside)) {
                    intersect = true;
                }
            }

        }
        return intersect;
    }

}
