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
public class StartTest {

    private RaceModel model;

    @Before
    public void init() {
        model = new RaceModel(new Paper());
    }

    @Test
    public void goThroughStart() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(49, 55));
        clicks.add(new Point(50, 52));
        clicks.add(new Point(50, 51));
        clicks.add(new Point(51, 51));
        clicks.add(new Point(51, 52));
        clicks.add(new Point(52, 54));
        clicks.add(new Point(52, 55));
        clicks.add(new Point(52, 53));
        clicks.add(new Point(53, 51));

        String trackName = "Snail";

        assertSituation(trackName, clicks);
    }

    @Test
    public void goThroughStartAndTrack() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(49, 55));
        clicks.add(new Point(50, 52));
        clicks.add(new Point(50, 51));
        clicks.add(new Point(51, 51));
        clicks.add(new Point(51, 52));
        clicks.add(new Point(52, 54));
        clicks.add(new Point(52, 57));
        clicks.add(new Point(52, 53));
        clicks.add(new Point(53, 51));

        String trackName = "Snail";

        assertSituation(trackName, clicks);
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
