package com.ambroz.formula.gamemodel.datamodel;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Collision {

    private final Point collisionPoint;
    private final Segment collisionLine;

    public Collision(Point colPoint, Segment colLine) {
        this.collisionPoint = colPoint;
        this.collisionLine = colLine;
    }

    public Point getCollisionPoint() {
        return collisionPoint;
    }

    public Segment getCollisionLine() {
        return collisionLine;
    }

}
