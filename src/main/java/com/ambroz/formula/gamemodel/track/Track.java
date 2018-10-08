package com.ambroz.formula.gamemodel.track;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 * This class represents track of the race. It is composed from two polylines (left and right) and next two polylines
 * are: start and finish line. During building the track there are recorded indexes of point (left and right) which show
 * where ends the last well placed track points.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Track {

    public static int LIMIT_DIST = 15;
    public static int LIMIT_NEXT = 5;

    private Polyline left;
    private Polyline right;
    private int leftIndex;
    private int rightIndex;
    private int leftWidth;
    private int rightWidth;
    private int maxWidth;
    private int maxHeight;
    private boolean ready;

    public Track() {
        left = new Polyline();
        right = new Polyline();
        leftIndex = 0;
        rightIndex = 0;
        maxWidth = 0;
        maxHeight = 0;
        leftWidth = 3;
        rightWidth = 3;
        ready = false;
    }

    public Polyline getLine(Side side) {
        if (side == Side.Left) {
            return getLeft();
        } else {
            return getRight();
        }
    }

    public void addPoint(Side side, Point point) {
        getLine(side).addPoint(point);
        calculateReady();
        checkMaximum(point);
    }

    public void removeLastPoint(Side side) {
        getLine(side).removeLast();
        if (getIndex(side) > 0 && getIndex(side) >= getLine(side).getLength()) {
            setIndex(getIndex(side) - 1, side);
            if (side == Side.Left) {
                if (getIndex(Side.Right) > getIndex(side)) {
                    setIndex(getIndex(Side.Right) - 1, Side.Right);
                }
            } else if (getIndex(Side.Left) > getIndex(side)) {
                setIndex(getIndex(Side.Left) - 1, Side.Left);
            }
        }
        calculateReady();
        calculateDimension();
    }

    private void calculateDimension() {
        for (int i = 0; i < getLong().getLength(); i++) {
            checkMaximum(getLong().getPoint(i));
            if (i < getShort().getLength()) {
                checkMaximum(getShort().getPoint(i));
            }
        }
    }

    /**
     * Method finds in input polyline "edge" the index of point, which is the nearest from input point last. Browsing is
     * limited by lower and upper index (min a max).
     *
     * @param edge - polyline in which there is found the nearest point
     * @param min - the lowest index from which searching starts
     * @param max - index of point for which searching ends
     * @return - index of the nearest point
     */
    public int findNearest(Segment edge, int min, int max) {
        Point last = edge.getLast();
        int index = min;
        for (int i = min + 1; i <= max; i++) {
            Point actPoint = getShort().getPoint(i);
            if (Calc.distance(last, getShort().getPoint(index)) > Calc.distance(last, actPoint)
                    && Calc.sidePosition(actPoint, edge) == getShortStr()) {
                index = i;
            }
        }
        return index;
    }

    /**
     * This method returns Side which is now shorter.
     *
     * @return enum Side.Left or Side.Right
     */
    private Side getShortStr() {
        if (getLeft().getLength() < getRight().getLength()) {
            return Side.Left;
        } else {
            return Side.Right;
        }
    }

    public Polyline getLeft() {
        return left;
    }

    public void setLeft(Polyline left) {
        this.left = left;
        leftIndex = getLeft().getLength() - 1;
        leftWidth = 3;
        calculateReady();
    }

    public Polyline getRight() {
        return right;
    }

    public void setRight(Polyline right) {
        this.right = right;
        rightIndex = getRight().getLength() - 1;
        rightWidth = 3;
        calculateReady();
    }

    /**
     * This method selects longer side of the track when it is prepare for race.
     *
     * @return longer polyline
     */
    public Polyline getLong() {
        if (getLeft().getLength() >= getRight().getLength()) {
            return getLeft();
        } else {
            return getRight();
        }
    }

    /**
     * This method selects shorter side of the track when it is prepare for race.
     *
     * @return shorter polyline
     */
    public Polyline getShort() {
        if (getLeft().getLength() < getRight().getLength()) {
            return getLeft();
        } else {
            return getRight();
        }
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    private void checkMaximum(Point point) {
        if (point.x > maxWidth) {
            maxWidth = (int) point.x;
        }
        if (point.y > maxHeight) {
            maxHeight = (int) point.y;
        }
    }

    /**
     * This method returns actual index of well placed point to the track. You have to specify in which side you are
     * interested.
     *
     * @param side is enum Side that represents side where you want to find index (right or left)
     * @return left or right index
     */
    public int getIndex(Side side) {
        if (side == Side.Left) {
            return leftIndex;
        } else {
            return rightIndex;
        }
    }

    /**
     * This method sets actual index on specific side.
     *
     * @param index is position of new index
     * @param side means which enum Side, which you want to set up
     */
    public void setIndex(int index, Side side) {
        if (side == Side.Left) {
            leftIndex = index;
        } else {
            rightIndex = index;
        }
    }

    public void setWidth(Side side) {
        if (side == Side.Left) {
            leftWidth = 4;
            rightWidth = 3;
        } else {
            rightWidth = 4;
            leftWidth = 3;
        }
    }

    public int getWidth(Side side) {
        if (side == Side.Left) {
            return leftWidth;
        } else {
            return rightWidth;
        }
    }

    //TODO: dokoncit drahu jak se slusi a patri
    public void finishIndexes() {
//        if (getLeft().getLength() - leftIndex >= getRight().getLength() - rightIndex) {
//            for (int l = leftIndex + 1; l < getLeft().getLength(); l++) {
//                Point actPoint = getLeft().getPoint(l);
//                double oldDist = 5000, dist;
//                int newIndex = rightIndex;
//                for (int r = newIndex; r < getRight().getLength(); r++) {
//                    dist = Calc.distance(actPoint, getRight().getPoint(r));
//                    if (dist <= oldDist) {
//                        oldDist = dist;
//                        newIndex = r;
//                    } else {
//                        break;
//                    }
//                }
//                if (newIndex != rightIndex) {
//                    rightIndex = newIndex;
//                }
//                leftIndex++;
//            }
//        } else {
//            for (int r = rightIndex + 1; r < getRight().getLength(); r++) {
//                Point actPoint = getRight().getPoint(r);
//                double oldDist = 5000, dist;
//                int newIndex = leftIndex;
//                for (int l = newIndex; l < getLeft().getLength(); l++) {
//                    dist = Calc.distance(actPoint, getLeft().getPoint(l));
//                    if (dist <= oldDist) {
//                        oldDist = dist;
//                        newIndex = l;
//                    } else {
//                        break;
//                    }
//                }
//                if (newIndex != leftIndex) {
//                    leftIndex = newIndex;
//                }
//                rightIndex++;
//            }
//        }
        leftIndex = left.getLength() - 1;
        rightIndex = right.getLength() - 1;
        setReady(true);
    }

    /**
     * This method returns two-points line which represents start line. Line always leads from left to right side.
     *
     * @return polyline of start or null when one side is empty
     */
    public Segment getStart() {
        Segment start = null;
        if (!getLeft().isEmpty() && !getRight().isEmpty()) {
            Point begin = getLeft().getPoint(0);
            Point end = getRight().getPoint(0);
            start = new Segment(begin, end);
        }
        return start;
    }

    /**
     * This method returns two-points line which represents finish line. Line always leads from left to right side.
     *
     * @return polyline of finish or null when one side is still empty
     */
    public Segment getFinish() {
        Segment finish = null;
        if (!getLeft().isEmpty() && !getRight().isEmpty()) {
            Point begin = getLeft().getLast();
            Point end = getRight().getLast();
            finish = new Segment(begin, end);
        }
        return finish;
    }

    public boolean isReadyForDraw() {
        return leftIndex > getLeft().getLength() - 4 && rightIndex > getRight().getLength() - 4
                && getLeft().getLength() > 1 && getRight().getLength() > 1;
    }

    public boolean isReady() {
        return ready;
    }

    private void setReady(boolean ready) {
        this.ready = ready;
    }

    private void calculateReady() {
        setReady(leftIndex == getLeft().getLength() - 1 && getLeft().getLength() > 1
                && rightIndex == getRight().getLength() - 1 && getRight().getLength() > 1);
    }

    public boolean isEmpty() {
        return getLeft().isEmpty() && getRight().isEmpty();
    }

    /**
     * This method returns coordinates X and Y of whole track in order: left side, right side from the end to the start.
     * Also the length (in squares) of the left and right side is measured.
     *
     * @param gridSize is size of the one square on the paper
     * @return 2-dimension array (2 rows) where row 0 means X and row 1 means Y
     */
    public int[][] getCoordinates(int gridSize) {
        int leftSize = getLeft().getLength();
        int rightSize = getRight().getLength();
        int[] xPoints = new int[leftSize + rightSize];
        int[] yPoints = new int[leftSize + rightSize];

        for (int i = 0; i < leftSize; i++) {
            xPoints[i] = getLeft().getPoint(i).getX() * gridSize;
            yPoints[i] = getLeft().getPoint(i).getY() * gridSize;
        }

        for (int i = 0; i < rightSize; i++) {
            //right side has to be saved from the end to the start
            int opIndex = rightSize - 1 - i;
            xPoints[leftSize + i] = getRight().getPoint(opIndex).getX() * gridSize;
            yPoints[leftSize + i] = getRight().getPoint(opIndex).getY() * gridSize;
        }
        return new int[][]{xPoints, yPoints};
    }

    public boolean freeDrawing(Side side, Side oppSide) {
        return getLine(side).getLength() > getIndex(side) && getIndex(oppSide) >= getLine(oppSide).getLength() - 2;
    }

    /**
     * This method change left side to right reverse side and right side change to left reverse.
     */
    public void switchStart() {
        Polyline tempRight = new Polyline(getRight());
        Polyline tempLeft = new Polyline(getLeft());
        getLeft().clear();
        getRight().clear();

        left = tempRight.reverse();
        right = tempLeft.reverse();
        leftIndex = getLeft().getLength() - 1;
        rightIndex = getRight().getLength() - 1;
    }

    public void reset() {
        left.clear();
        right.clear();
        leftIndex = 0;
        rightIndex = 0;
        maxWidth = 0;
        maxHeight = 0;
    }

    @Override
    public String toString() {
        return "left size = " + getLeft().getLength() + ", right size = " + getRight().getLength();
    }

}
