package com.ambroz.formula.gamemodel.race;

import com.ambroz.formula.gamemodel.datamodel.Collision;
import com.ambroz.formula.gamemodel.datamodel.Point;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Turn {

    public static final int FREE = 1;
    public static final int COLLISION = 0;
    private Point point;
    private Collision collision;
    private int type; //0 is bad, 1 is good
    private boolean exist;

    public Turn() {
        point = new Point();
        type = FREE;
        exist = true;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
        setExist(true);
    }

    public Collision getCollision() {
        return collision;
    }

    public void setCollision(Collision colisionObject) {
        this.collision = colisionObject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setExist(true);
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

}
