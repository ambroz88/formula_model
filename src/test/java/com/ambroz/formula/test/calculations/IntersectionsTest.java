package com.ambroz.formula.test.calculations;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.PointPosition;
import com.ambroz.formula.gamemodel.utils.Calc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class IntersectionsTest {

    private Point left;
    private Point right;
    private Point edge;
    private Point start;
    private Segment segment;

    @Before
    public void init() {
        segment = new Segment(new Point(10, 10), new Point(20, 20));
        left = new Point(20, 10);
        right = new Point(10, 20);
        edge = new Point(5, 10);
        start = new Point(5, 5);
    }

    @Test
    public void intersectInsideTest() {
        Point intersection = Calc.intersectSegments(left, right, segment);
        Point expected = new Point(15, 15);
        expected.setPosition(PointPosition.Inside);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void intersectOutsideTest() {
        Point intersection = Calc.intersectSegments(left, start, segment);
        Point expected = new Point(5, 5);
        expected.setPosition(PointPosition.Outside);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void intersectEdgeTest() {
        Point intersection = Calc.intersectSegments(left, edge, segment);
        Point expected = new Point(10, 10);
        expected.setPosition(PointPosition.Edge);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void intersectHalfLineOutsideTest() {
        Point intersection = Calc.halfLineAndSegmentIntersection(segment, left, start);
        Point expected = new Point();

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void intersectHalfLineInsideTest() {
        Point intersection = Calc.halfLineAndSegmentIntersection(segment, new Point(25, 5), left);
        Point expected = new Point(15, 15);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void intersectHalfLineEdgeTest() {
        Point intersection = Calc.halfLineAndSegmentIntersection(segment, new Point(25, 10), left);
        Point expected = new Point(10, 10);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void baseOfAltitudeInsideTest() {
        Point intersection = Calc.baseOfAltitude(segment, left);
        Point expected = new Point(15, 15);

        Assert.assertTrue(intersection.isEqual(expected));
    }

    @Test
    public void baseOfAltitudeEdgeTest() {
        Point intersection = Calc.baseOfAltitude(segment, start);
        Point expected = new Point(5, 5);

        Assert.assertTrue(intersection.isEqual(expected));
    }

}
