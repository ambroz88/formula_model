package com.ambroz.formula.gamemodel.race;

import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.track.Track;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public final class TurnCalculations {

    /**
     * Metoda najde novy stred po havarii a vykresli nove moznosti novy stred je prunikem kolmice kolizni hrany a
     * kruznice se stredem v miste kolize a polomerem 0.6*velikost mrizky, pricemz bod musi lezet na trati colision je
     * bod, ve kterem doslo k vyjeti z trati.
     *
     * @param act
     * @return
     */
    public static Point generateCrashCenter(Formula act) {
        Point crashCenter = new Point();
        Segment collisionLine = act.getCollision().getCollisionLine();

        //smerovy vektor kolizni usecky, ktery se vyuzije pro urceni kolmice
        double ux = collisionLine.getLast().x - collisionLine.getFirst().x;
        double uy = collisionLine.getLast().y - collisionLine.getFirst().y;

        if (ux == 0) {

            //crash into vertical edge - for quadratic equation bellow it has no solution
            if (Point.COLLISION_LEFT.equals(act.getLast().getLocation()) && uy > 0
                    || Point.COLLISION_RIGHT.equals(act.getLast().getLocation()) && uy < 0) {
                crashCenter = new Point(act.getLast().getX() - 1, act.getLast().getY());
            } else {
                crashCenter = new Point(act.getLast().getX() + 1, act.getLast().getY());
            }

        } else {
            //parametr c pro kolmici na kolizni usecku, prochazejici prusecikem:
            double C = -ux * act.getLast().x - uy * act.getLast().y;
            /* rovnice pro X na kolmici: X = (-uy*Y-c)/ux
             * stredova rovnice kruznice: r^2=(x-m)^2 + (y-n)^2
             * po dosazeni X do stredove rovnice:
             * (ux^2 + uy^2)* Y^2 + (2*uy*C + 2*m*uy*ux - 2*n*ux^2) * Y + ( C^2 + 2*C*m*ux + ux^2*(n^2+m^2-r^2) ) = 0
             */
            double m = act.getLast().x; //X stredu kruznice
            double n = act.getLast().y; //Y stredu kruznice
            //parametry kvadraticke rovnice:
            double a = ux * ux + uy * uy;
            double b = 2 * uy * C + 2 * m * uy * ux - 2 * n * ux * ux;
            double c = C * C + 2 * C * m * ux + ux * ux * (n * n + m * m - Math.pow(0.75, 2));
            //ziskani korenu Y1 a Y2:
            List<Double> quadRes = Calc.quadratic(a, b, c);
            double Y1 = quadRes.get(0);
            double Y2 = quadRes.get(1);
            //vypocet prislusnych souradnic X na kolmici:
            double X1 = (-uy * Y1 - C) / ux;
            double X2 = (-uy * Y2 - C) / ux;

            Point inter1 = new Point(X1, Y1);
            Point inter2 = new Point(X2, Y2);
            switch (act.getLast().getLocation()) {
                case Point.COLLISION_LEFT:
                    //novy stred musi byt vpravo od kolizni usecky
                    if (Track.RIGHT == Calc.sidePosition(inter1, collisionLine)) {
                        crashCenter = new Point(inter1.getX(), inter1.getY());
                    } else {
                        crashCenter = new Point(inter2.getX(), inter2.getY());
                    }
                    break;
                case Point.COLLISION_RIGHT:
                    //novy stred musi byt vlevo od kolizni usecky
                    if (Track.LEFT == Calc.sidePosition(inter1, collisionLine)) {
                        crashCenter = new Point(inter1.getX(), inter1.getY());
                    } else {
                        crashCenter = new Point(inter2.getX(), inter2.getY());
                    }
                    break;
            }
        }

        return crashCenter;
    }

}
