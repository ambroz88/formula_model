package com.ambroz.formula.gamemodel.track;

import com.ambroz.formula.gamemodel.datamodel.CoreModel;
import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class TrackEditor extends CoreModel {

    private int movePointIndex;
    private int side;

    public TrackEditor(Paper paper) {
        super(paper);
    }

    /**
     * It finds out which point will be moved, resp it's side and position. Start and finish can't be moved.
     *
     * @param click is point where user clicked - potential point that will be moved
     * @return true if the point is part of the track
     */
    public boolean clickOnTrack(Point click) {
        //check if player clicked on the point from left side of the track
        for (int i = 1; i < getTrack().getLeft().getLength() - 1; i++) {
            if (click.isEqual(getTrack().getLeft().getPoint(i))) {
                movePointIndex = i;
                side = Track.LEFT;
                break;
            }
        }
        if (movePointIndex == 0) {
            //check if player clicked on the point from right side of the track
            for (int i = 1; i < getTrack().getRight().getLength() - 1; i++) {
                if (click.isEqual(getTrack().getRight().getPoint(i))) {
                    movePointIndex = i;
                    side = Track.RIGHT;
                    break;
                }
            }
        }

        return movePointIndex > 0;
    }

    /**
     * It validates if user moves the point in correct position so newly created segments don't have intersect with any
     * other segments.
     *
     * @param click is point where user placed certain point of the track
     * @return true if there is no intersection with the rest of the track, false otherwise
     */
    public boolean isNewPointValid(Point click) {
        Point newEdgeStart = getTrack().getLine(side).getPoint(movePointIndex - 1);
        Point newEdgeEnd = getTrack().getLine(side).getPoint(movePointIndex + 1);

        // new segments can't cross start, finish or opposite side
        boolean intersect = crossEndLines(newEdgeStart, newEdgeEnd, click)
                || getTrack().getOppLine(side).checkSegmentCrossing(newEdgeStart, click)
                || getTrack().getOppLine(side).checkSegmentCrossing(newEdgeEnd, click);

        if (!intersect) {

            for (int i = 0; i < getTrack().getLine(side).getLength() - 1; i++) {
                // new segments can't cross it's own side but it can touch it
                if (i < movePointIndex - 1 || i > movePointIndex) {

                    Segment actRight = getTrack().getLine(side).getSegment(i);
                    if ((int) Calc.crossing(click, newEdgeStart, actRight)[0] == Calc.INSIDE
                            || (int) Calc.crossing(click, newEdgeEnd, actRight)[0] == Calc.INSIDE) {
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

            if ((int) Calc.crossing(edgeStart, click, getTrack().getStart())[0] == Calc.INSIDE
                    || (int) Calc.crossing(edgeEnd, click, getTrack().getStart())[0] == Calc.INSIDE) {
                intersect = true;
            } else if (getTrack().getFinish() != null) {
                if ((int) Calc.crossing(edgeStart, click, getTrack().getFinish())[0] == Calc.INSIDE
                        || (int) Calc.crossing(edgeEnd, click, getTrack().getFinish())[0] == Calc.INSIDE) {
                    intersect = true;
                }
            }

        }
        return intersect;
    }

}
