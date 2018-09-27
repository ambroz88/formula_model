package com.ambroz.formula.gamemodel.race;

import com.ambroz.formula.gamemodel.datamodel.Collision;
import com.ambroz.formula.gamemodel.datamodel.Point;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Turn extends Point {

    private Collision collision;
    private boolean exist;

    public Turn() {
        exist = true;
    }

    public void setPoint(Point point) {
        setX(point.getX());
        setY(point.getY());
        setExist(true);
    }

    public Collision getCollision() {
        return collision;
    }

    public void setCollision(Collision colisionObject) {
        this.collision = colisionObject;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

}
