package com.ambroz.formula.gamemodel.race;

import com.ambroz.formula.gamemodel.datamodel.Collision;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.enums.FormulaType;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 * This class represents formula. It is polyline which has some special functions and variables. In drawing this
 * polyline has the arrows in each point so it looks like a vector.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Formula extends Polyline {

    public static final String SIDE = "side";
    public static final String FORWARD = "forward";

    private int speed, side, moves, wait;
    private int rgbColor;
    private FormulaType type;
    private Collision collision;
    private String name;
    private double length; //actual distance which formula took
    private boolean winner;//is true when this formula finished the race

    public Formula(FormulaType formulaType) {
        super();
        speed = 1; //size of movement on axis Y
        side = 0; //size of movement on axis X
        winner = false;
        moves = 0; //numbers of turns of this formula
        length = 0;
        wait = 0;
        rgbColor = 0;
        name = "";
        type = formulaType;
    }

    /**
     * Reset of global variables to start values.
     */
    public void reset() {
        winner = false;
        points.clear();
        moves = 0;
        length = 0;
        wait = 0;
        firePropertyChange(FORMULA_RESET, false, true);
    }

    @Override
    public void addPoint(Point p) {
        super.addPoint(p);
        if (getLength() > 1) {
            movesUp();
            lengthUp();
        }
    }

    public void addCollisionPoint() {
        if (getCollision() != null) {
            super.addPoint(getCollision().getCollisionPoint());
            lengthUp();
        }
    }

    public void movesUp() {
        movesUp(1);
    }

    /**
     * This method updates the distance of the formula about the distance between two last points.
     */
    private void lengthUp() {
        lengthUp(getLast(), getPreLast());
    }

    public FormulaType getType() {
        return type;
    }

    public void setType(FormulaType type) {
        this.type = type;
    }

    public int getColor() {
        return rgbColor;
    }

    public void setColor(int color) {
        int old = getColor();
        this.rgbColor = color;
        firePropertyChange(FORMULA_COLOUR, old, color); //caught by PlayerPanel and Statistic panel
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = getName();
        this.name = name;
        firePropertyChange(FORMULA_NAME, old, name);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getSpeed() {
        if (points.size() < 2) {
            return speed;
        } else {
            return getLast().getY() - getPreLast().getY();
        }
    }

    /**
     * This method counts the size of vertical movement of this formula if it goes to <code>point turn</code>.
     *
     * @param turn is point where the formula is going
     * @return number of grid squeres
     */
    public int getSpeed(Point turn) {
        return turn.getY() - getLast().getY();
    }

    public int getSide() {
        if (points.size() < 2) {
            return side;
        } else {
            return getLast().getX() - getPreLast().getX();
        }
    }

    /**
     * This method counts the size of horizontal movement of this formula if it goes to <code>point turn</code>.
     *
     * @param turn is point where the formula is going
     * @return number of grid squeres
     */
    public int getSide(Point turn) {
        return turn.getX() - getLast().getX();
    }

    public int maxSpeed() {
        return maxSpeed(getPreLast());
    }

    /**
     * This method counts the maximum movement size of this formula if it goes to <code>point click</code>.
     *
     * @param click is point where the formula is going
     * @return number of grid squeres
     */
    public int maxSpeed(Point click) {
        int maxSpeed = Math.abs(getSpeed(click));
        int maxSide = Math.abs(getSide(click));
        if (maxSpeed > maxSide) {
            return maxSpeed;
        } else {
            return maxSide;
        }
    }

    /**
     * This method counts the minimum movement size of this formula if it goes to <code>point click</code>.
     *
     * @param click is point where the formula is going
     * @return number of grid squeres
     */
    public int minSpeed(Point click) {
        int maxSpeed = Math.abs(getSpeed(click));
        int maxSide = Math.abs(getSide(click));
        if (maxSpeed < maxSide) {
            return maxSpeed;
        } else {
            return maxSide;
        }
    }

    /**
     * This method finds out which movement direction would be more dominant if this formula would move to
     * <code>point click</code>. If it is horizontal or vertical movement.
     *
     * @param click is possible movement point
     * @return String with dominant direction
     */
    public String maxDirect(Point click) {
        int maxSpeed = getSpeed(click);
        int maxSide = getSide(click);
        if (maxSpeed > maxSide) {
            return FORWARD;
        } else {
            return SIDE;
        }
    }

    /**
     * It calculates next center of possible turns based on last turn.
     *
     * @return center point
     */
    public Point calculateNextCenter() {
        int cenX = getLast().getX() + getSide();
        int cenY = getLast().getY() + getSpeed();
        return new Point(cenX, cenY);
    }

    public void setCollision(Collision colisionObject) {
        this.collision = colisionObject;
    }

    /**
     * Get colision object in which the formula crashed (in case of colision)
     *
     * @return colision object
     */
    public Collision getCollision() {
        return collision;
    }

    public void setWin(boolean win) {
        winner = win;
    }

    public boolean getWin() {
        return winner;
    }

    public void setWait(int wait) {
        int old = getWait();
        this.wait = wait;
        firePropertyChange(FORMULA_WAIT, old, wait);
    }

    public void decreaseWait() {
        setWait(getWait() - 1);
    }

    public int getWait() {
        return wait;
    }

    public void movesUp(int count) {
        moves = moves + count;
        firePropertyChange(FORMULA_MOVES, 0, moves);
    }

    public int getMoves() {
        return moves;
    }

    /**
     * This method updates the distance of the formula about the distance between <code>point p1</code> and
     * <code>point p2</code>. Distance is rounded to 2 decimals.
     *
     * @param p1 first point
     * @param p2 second point
     */
    public void lengthUp(Point p1, Point p2) {
        double dist = Calc.distance(p1, p2);
        length = Math.round((length + dist) * 100.0) / 100.0;
        firePropertyChange(FORMULA_DISTANCE, 0, length);
    }

    public double getDist() {
        return length;
    }

}
