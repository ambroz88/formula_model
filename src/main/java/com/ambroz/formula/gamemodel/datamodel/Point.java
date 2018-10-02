package com.ambroz.formula.gamemodel.datamodel;

import com.ambroz.formula.gamemodel.enums.PointPosition;

/**
 * This class represent one point given by coordinate X and Y. The point has also information about his position to
 * track. It is good for special cases
 * <b>e.g.</b> position on FINISH line.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Point {

    private PointPosition position;
    public double x, y;

    // =========== constructors ================
    public Point() {
        this(0, 0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        position = PointPosition.Normal;
    }

    public Point(Point p) {
        x = p.x;
        y = p.y;
        position = p.getPosition();
    }
    //=======================================

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * It converts panel coordinates into grid coordinates.
     * <br>Examples of conversions when grid size is 10:
     * <br>Point[145;53] is converted into Point[15;5]
     * <br>Point[154;47] is converted into Point[15;5]
     *
     * @param gridSize is the size of the grid
     */
    public void toGridUnits(int gridSize) {
        double roundX = Math.round(x / gridSize) * gridSize;
        double roundY = Math.round(y / gridSize) * gridSize;
        x = Math.round(roundX / gridSize);
        y = Math.round(roundY / gridSize);
    }

    /**
     * This method checks this <code>point</code> has the same coordinates like point p.
     *
     * @param p point which we want to check.
     * @return true if the points are the same. False if they are not.
     */
    public boolean isEqual(Point p) {
        return x == p.getX() && y == p.getY();
    }

    /**
     * This setter sets the location of this point
     *
     * @param position could be all static constants of Point class
     */
    public void setPosition(PointPosition position) {
        this.position = position;
    }

    public PointPosition getPosition() {
        return position;
    }

    public boolean isEmpty() {
        return x == 0 && y == 0;
    }

    @Override
    public String toString() {
        return "x = " + getX() + ", y = " + getY();
    }

}
