package com.ambroz.formula.test.calculations;

import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.utils.Calc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jiri Ambroz
 */
public class DistanceTest {

    private List<Point> data;

    @Before
    public void init() {
        data = new ArrayList<>();
        data.add(new Point(5, 5));
        data.add(new Point(5, 15));
        data.add(new Point(10, 10));
        data.add(new Point(10, 20));
        data.add(new Point(20, 10));
        data.add(new Point(20, 20));
    }

    @Test
    public void findNearestTest() {
        Point nearest = Calc.findNearestPoint(new Point(18, 5), data);
        Point expected = new Point(20, 10);
        Assert.assertTrue(nearest.isEqual(expected));
    }

    @Test
    public void findNearest2Test() {
        Point nearest = Calc.findNearestPoint(new Point(1, 5), data);
        Point expected = new Point(5, 5);
        Assert.assertTrue(nearest.isEqual(expected));
    }

}
