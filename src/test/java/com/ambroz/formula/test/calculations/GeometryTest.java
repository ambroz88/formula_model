package com.ambroz.formula.test.calculations;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Segment;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.utils.Calc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class GeometryTest {

    private Point left;
    private Point right;
    private Segment segment;

    @Before
    public void init() {
        segment = new Segment(new Point(10, 10), new Point(20, 20));
        left = new Point(20, 10);
        right = new Point(10, 20);
    }

    @Test
    public void leftSidePointTest() {
        Side leftSide = Calc.sidePosition(left, segment);
        Assert.assertTrue(leftSide == Side.Left);
    }

    @Test
    public void rightSidePointTest() {
        Side rightSide = Calc.sidePosition(right, segment);
        Assert.assertTrue(rightSide == Side.Right);
    }

    @Test
    public void rotatePointRightTest() {
        Point rotated = Calc.rotatePoint(left, segment.getFirst(), Math.toRadians(90), 10);
        Point expected = right;

        Assert.assertTrue(rotated.isEqual(expected));
    }

    @Test
    public void rotatePointLeftTest() {
        Point rotated = Calc.rotatePoint(segment.getLast(), segment.getFirst(), Math.toRadians(-45), 10);
        Point expected = left;

        Assert.assertTrue(rotated.isEqual(expected));
    }

    @Test
    public void calculateLeftAngleTest() {
        double angle = Calc.calculateAngle(left, segment.getFirst(), right, Side.Left);
        double expected = Math.toRadians(90);
        Assert.assertEquals(angle, expected, 0);
    }

    @Test
    public void calculateRightAngleTest() {
        double angle = Calc.calculateAngle(left, segment.getFirst(), right, Side.Right);
        double expected = Math.toRadians(-270);
        Assert.assertEquals(angle, expected, 0);
    }

}
