package com.ambroz.formula.gamemodel.datamodel;

/**
 * This class represent one point given by coordinate X and Y. The point has also information about his position to
 * track. It is good for special cases
 * <b>e.g.</b> position on FINISH line.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Point {

    public final static String COLLISION_LEFT = "leftCol";
    public final static String COLLISION_RIGHT = "rightCol";
    public final static String FINISH_LINE = "finishLine";
    public final static String FINISH = "finish";
    public final static String NORMAL = "normal";

    private String location;
    public double x, y;

    // =========== constructors ================
    public Point() {
        this(0, 0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        location = NORMAL;
    }

    public Point(Point p) {
        x = p.x;
        y = p.y;
        location = p.getLocation();
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
     * @param location could reach these values: leftCol, rightCol, FINISH, FINISH_LINE, NORMAL
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "x = " + getX() + ", y = " + getY();
    }

}
