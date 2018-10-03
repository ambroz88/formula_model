package com.ambroz.formula.gamemodel.utils;

import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.PointPosition;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.race.Turn;

/**
 * This is a class with different mathematical operations and methods which are static.
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public abstract class Calc {

    /**
     * This method finds out if two segments have intersect. Each segment is defined by Segment object. It doesn't
     * matter on order.
     *
     * @param segment1 is first segment (polyline with length = 2)
     * @param segment2 is second segment (polyline with length = 2)
     * @return ArrayList of length 2. First value is Integer which means if there is intersect (1 for intersect, 0 for
     * touch and -1 for no intersect. Second value in List is Point where is the intersect.
     */
    public static Point intersectSegments(Segment segment1, Segment segment2) {
        Point colPoint = calculateIntersection(segment1.getFirst(), segment1.getLast(), segment2.getFirst(), segment2.getLast());

        if (!colPoint.isEmpty()) {
            if (pointPosition(segment1, colPoint).equals(PointPosition.Inside) && pointPosition(segment2, colPoint).equals(PointPosition.Inside)) {
                //usecky se protinaji uvnitr
                colPoint.setPosition(PointPosition.Inside);
            } else if (pointPosition(segment1, colPoint).equals(PointPosition.Edge) && pointPosition(segment2, colPoint).equals(PointPosition.Edge)) {
                //usecky se spolecne dotykaji v jednom konci
                colPoint.setPosition(PointPosition.Edge);
            } else if ((pointPosition(segment1, colPoint).equals(PointPosition.Edge) && pointPosition(segment2, colPoint).equals(PointPosition.Inside))
                    || (pointPosition(segment2, colPoint).equals(PointPosition.Edge) && pointPosition(segment1, colPoint).equals(PointPosition.Inside))) {
                //konec jedne usecky se dotyka vnitrku druhe usecky
                colPoint.setPosition(PointPosition.Edge);
            } else {
                colPoint.setPosition(PointPosition.Outside);
            }
        }

        return colPoint;
    }

    /**
     * This method finds out if two segments have intersect. One segment is defined by two separated points. Second
     * segment is defined by Segment.
     *
     * @param a is first point of first segment
     * @param b is second point of first segment
     * @param segment is segment (polyline with length = 2)
     * @return ArrayList of length 2. First value is Integer which means if there is intersect (1 for intersect, 0 for
     * touch and -1 for no intersect. Second value in List is Point where is the intersect.
     */
    public static Point intersectSegments(Point a, Point b, Segment segment) {
        return intersectSegments(new Segment(a, b), segment);
    }

    /**
     * This method finds out if two segments have intersect. Both segments are defined by two separated points. It
     * doesn't matter on direction of the segment.
     *
     * @param a is first point of first segment
     * @param b is second point of first segment
     * @param c is first point of second segment
     * @param d is second point of second segment
     * @return ArrayList of length 2. First value is Integer which means if there is intersect (1 for intersect, 0 for
     * touch and -1 for no intersect. Second value in List is Point where is the intersect.
     */
    public static Point intersectSegments(Point a, Point b, Point c, Point d) {
        return intersectSegments(a, b, new Segment(c, d));
    }

    /**
     * NOT USED
     *
     * @param segment
     * @param lineStart
     * @param lineEnd
     * @return
     */
    public static Point halfLineAndSegmentIntersection(Segment segment, Point lineStart, Point lineEnd) {
        Point colPoint = calculateIntersection(segment.getFirst(), segment.getLast(), lineStart, lineEnd);

        if (!colPoint.isEmpty()) {
            PointPosition intersectPosition = pointPosition(segment, colPoint);
            if (intersectPosition.equals(PointPosition.Outside) || !intersectPosition.equals(PointPosition.Outside) && isPointBehind(lineStart, lineEnd, colPoint)) {
                //line intersects the segment
                colPoint = new Point();
            }
        }

        return colPoint;
    }

    /**
     * NOT USED.
     *
     * @param segment
     * @param lineStart
     * @param lineEnd
     * @return
     */
    public static Point calculateCollisionPoint(Segment segment, Point lineStart, Point lineEnd) {
        Point collision = calculateIntersection(segment.getFirst(), segment.getLast(), lineStart, lineEnd);

        if (!collision.isEmpty() && !pointPosition(segment, collision).equals(PointPosition.Inside)) {
            //line intersects the segment
            collision = new Point();
        }

        return collision;
    }

    private static Point calculateIntersection(Point a, Point b, Point c, Point d) {
        Point colPoint;

        //t vychazi z parametrickeho vyjadreni primky
        double t = (a.x * d.y - a.x * c.y - c.x * d.y - d.x * a.y + d.x * c.y + c.x * a.y)
                / ((d.x - c.x) * (b.y - a.y) - (b.x - a.x) * (d.y - c.y));

        if (!Double.isInfinite(t)) {
            //segments are not parallel
            double intersectX = a.x + (b.x - a.x) * t;
            double intersectY = a.y + (b.y - a.y) * t;
            //cannot be rounded!!!
            colPoint = new Point(intersectX, intersectY);
        } else {
            colPoint = new Point();
            colPoint.setPosition(PointPosition.Outside);
        }

        return colPoint;
    }

    /**
     * Method determines point position against segment AB.
     *
     * @param segment is segment where the point will be compared
     * @param point
     * @return "inside" if point belongs to segment, "outside" if it is outside segment and "insideoutsideLine" if the
     * point is identical with one of the segment vertex.
     */
    private static PointPosition pointPosition(Segment segment, Point point) {
        Point a = segment.getFirst();
        Point b = segment.getLast();

        double ix = point.x;
        double iy = point.y;

        if (point.isEqual(a) || point.isEqual(b)) {
            return PointPosition.Edge;
        } else if ((ix >= a.x && ix <= b.x || ix <= a.x && ix >= b.x)
                && (iy >= a.y && iy <= b.y || iy <= a.y && iy >= b.y)) {
            return PointPosition.Inside;
        } else {
            return PointPosition.Outside;
        }
    }

    private static boolean isPointBehind(Point lineStart, Point lineEnd, Point collision) {
        boolean isBehind = false;
        if (!collision.isEqual(lineStart)) {

            double deltaX = lineEnd.x - lineStart.x;
            double deltaCollisionX = lineStart.x - collision.x;

            if (deltaX > 0 && deltaCollisionX > 0 || deltaX < 0 && deltaCollisionX < 0) {
                isBehind = true;
            } else if (deltaX == 0 && deltaCollisionX == 0) {
                double deltaY = lineEnd.y - lineStart.y;
                double deltaCollisionY = lineStart.y - collision.y;
                if (deltaY > 0 && deltaCollisionY > 0 || deltaY < 0 && deltaCollisionY < 0) {
                    isBehind = true;
                }
            }
        } else {
            isBehind = true;
        }

        return isBehind;
    }

    /**
     * This method finds the nearest point from the list of Points where the nearest one is calculated from the point
     * 'sourcePoint'.
     *
     * @param sourcePoint is the point from which the distance is measure
     * @param data is list of points where is searched the closest one
     * @return the nearest point in the list
     */
    public static Point findNearestPoint(Point sourcePoint, List<Point> data) {
        int minIndex = findNearestIndex(sourcePoint, data);
        return data.get(minIndex);
    }

    /**
     * This method finds the nearest point from the list of Points where the nearest one is calculated from the point
     * 'sourcePoint'.
     *
     * @param sourcePoint is the point from which the distance is measure
     * @param data is list of points where is searched the closest one
     * @return the nearest point in the list
     */
    public static Point findNearestTurn(Point sourcePoint, List<Turn> data) {
        int minIndex = 0;
        for (int i = 1; i < data.size(); i++) {
            if (distance(sourcePoint, data.get(minIndex)) > distance(sourcePoint, data.get(i))) {
                minIndex = i;
            }
        }
        return data.get(minIndex);
    }

    /**
     * This method finds position of the nearest point from the list of Points. The nearest one is calculated from the
     * point 'sourcePoint'.
     *
     * @param sourcePoint is the point from which the distance is measure
     * @param data is list of points where is searched the closest one
     * @return the nearest point in the list
     */
    public static int findNearestIndex(Point sourcePoint, List<Point> data) {
        int minIndex = 0;
        for (int i = 1; i < data.size(); i++) {
            if (distance(sourcePoint, data.get(minIndex)) > distance(sourcePoint, data.get(i))) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * This method calculates angle which is given by tree points. It is possible to say on which side this angle should
     * be taken.
     *
     * @param prev is first point
     * @param mid is second point
     * @param next is third point
     * @param side is side from polyline where angle will be calculated (Side.Left or Side.Right)
     * @return angle between tree points
     */
    public static double calculateAngle(Point prev, Point mid, Point next, Side side) {
        double a = distance(mid, next);
        double b = distance(prev, mid);
        double c = distance(prev, next);
        double gamma;
        if (b == a) {
            gamma = Math.PI / 2;
        } else {
            gamma = Math.acos((c * c - a * a - b * b) / (-2 * a * b));
        }
        if (sidePosition(next, new Segment(prev, mid)) != side) {
            //subtrack angle from 180°
            gamma = 2 * Math.PI - gamma;
        }
        if (side == Side.Right) {
            gamma = -gamma;
        }
        return gamma;
    }

    /**
     * This method rotates one point around another point. Parameters of this rotation are: angle and new distance from
     * centre of rotation.
     *
     * @param rotated is point which is rotated
     * @param center is central point of rotation
     * @param angle is rotation angle in radians
     * @param newLength is new distance between centre and rotated point
     * @return point with new coordinates
     */
    public static Point rotatePoint(Point rotated, Point center, double angle, double newLength) {
        double koef = newLength / distance(rotated, center);
        double tempX = rotated.x + (center.x - rotated.x) * (1 - koef);
        double tempY = rotated.y + (center.y - rotated.y) * (1 - koef);
        double endX = (tempX - center.x) * Math.cos(angle) - (tempY - center.y) * Math.sin(angle);
        double endY = (tempX - center.x) * Math.sin(angle) + (tempY - center.y) * Math.cos(angle);
        return new Point((int) (center.x + endX), (int) (center.y + endY));
    }

    /**
     * Method finds out on which side is a enter point in relation with given segment. If the point lies on the segment,
     * position is declare as on the right side.
     *
     * @param center is investigated point
     * @param segment is investigated segment
     * @return Side.Left if point is on the left or Side.Right if it is on the right side
     */
    public static Side sidePosition(Point center, Segment segment) {
        double ux = segment.getLast().x - segment.getFirst().x;
        double uy = segment.getLast().y - segment.getFirst().y;
        double vx = center.x - segment.getFirst().x;
        double vy = center.y - segment.getFirst().y;

        // skalarni soucin dvou vektoru
        double t = ux * vy - uy * vx;
        if (t >= 0) {
            return Side.Right;
        } else {
            return Side.Left;
        }
    }

    /**
     * This method calculate distance between segment (2 points) and Point.
     *
     * @param segment is polyline with 2 points
     * @param p - distance of this point is calculated
     * @return distance in pixels
     * @deprecated
     */
    public static double distFromSegment(Segment segment, Point p) {
        Point a = segment.getFirst();
        Point b = segment.getLast();

        double segmentLength = Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
        double res = Math.abs((p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x)) / segmentLength;
        return res;
    }

    /**
     * It computes coordinates of the base of altitude when there is point and segment.
     *
     * @param segment is two points line to which is searched the base
     * @param p is point from which leads the segment to base of altitude
     * @return coordinates (Point) of base of altitude
     */
    public static Point baseOfAltitude(Segment segment, Point p) {
        Point a = segment.getFirst();
        Point b = segment.getLast();

        double ux = b.x - a.x;
        double uy = b.y - a.y;
        double nx = -uy;
        double ny = ux;

        double s = (p.y * ux - a.y * ux - p.x * uy + a.x * uy) / (nx * uy - ny * ux);
        double X = p.x + s * nx;
        double Y = p.y + s * ny;

        return new Point((int) X, (int) Y);
    }

    /**
     * This method calculates the distance between two points.
     *
     * @param p1 is first point
     * @param p2 is second point
     * @return distance between point p1 and p2 in 2 decimal numbers
     */
    public static double distance(Point p1, Point p2) {
        double dist = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        return Math.round(dist * 100.0) / 100.0;
    }

    /**
     * This method calculates quadratic equation (with two roots).
     *
     * @param a is first parameter of equation
     * @param b is second parameter of equation
     * @param c is third parameter of equation
     * @return List of roots (length can be 0-2)
     */
    public static List<Double> quadratic(double a, double b, double c) {
        List result = new ArrayList<>();
        double d = b * b - 4 * a * c;
        if (d > 0) {
            double t1 = (-b + Math.sqrt(d)) / (2 * a);
            double t2 = (-b - Math.sqrt(d)) / (2 * a);
            result.add(t1);
            result.add(t2);
        } else if (d == 0) {
            result.add(-b / (2 * a));
        }
        return result;
    }

    public static double calculateHypotenuse(int side, int speed) {
        return Math.sqrt(side * side + speed * speed);
    }

    public static Point rightAngle(Segment edge, Side side) {
        //kolmice z posledniho bodu vstupni usecky:
        Point start = edge.getLast();//z tohoto bodu bude spustena kolmice
        //smerovy vektor pro vychozi hranu na delsi strane:
        double ux = edge.getFirst().x - start.x;
        double uy = edge.getFirst().y - start.y;
        double nx;
        double ny;
        double t = 1000;
        if (side == Side.Left) {
            nx = uy;
            ny = -ux;
        } else {
            nx = -uy;
            ny = ux;
        }
        // souradnice potencialniho pruseciku:
        double X = (start.x + nx * t);
        double Y = (start.y + ny * t);
        return new Point((int) X, (int) Y);
    }

}
