package com.ambroz.formula.test.race;

import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.race.RaceModel;
import com.ambroz.formula.gamemodel.utils.TrackIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class FinishTest {

    private RaceModel model;

    @Before
    public void init() {
        model = new RaceModel(new Paper());
    }

    @Test
    public void goThroughFinish() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(15, 15));
        clicks.add(new Point(18, 16));
        clicks.add(new Point(21, 16));
        clicks.add(new Point(25, 17));
        clicks.add(new Point(30, 19));
        clicks.add(new Point(36, 20));
        clicks.add(new Point(43, 20));
        clicks.add(new Point(51, 19));
        clicks.add(new Point(60, 19));

        String trackName = "Test";

        assertSituation(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
    }

    @Test
    public void goThroughFinishLine() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(15, 15));
        clicks.add(new Point(18, 16));
        clicks.add(new Point(21, 16));
        clicks.add(new Point(25, 17));
        clicks.add(new Point(30, 19));
        clicks.add(new Point(36, 20));
        clicks.add(new Point(43, 20));
        clicks.add(new Point(51, 19));
        clicks.add(new Point(58, 19));
        clicks.add(new Point(64, 18));

        String trackName = "Test";

        assertSituation(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
    }

    private void assertSituation(String trackName, List<Point> clicks) {
        model.prepareGame(TrackIO.trackFromJSON(trackName));
        model.startGame();

        for (Point p : clicks) {
            model.moveWithPlayer(p);
        }

        Assert.assertEquals(model.getTurnMaker().getActiveFormula().getLast(), clicks.get(clicks.size() - 1));
    }
}
