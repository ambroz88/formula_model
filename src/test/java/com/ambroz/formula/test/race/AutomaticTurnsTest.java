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
public class AutomaticTurnsTest {

    private RaceModel model;

    @Before
    public void init() {
        model = new RaceModel(new Paper());
    }

    @Test
    public void automaticTurn() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(14, 23));
        clicks.add(new Point(17, 22));
        clicks.add(new Point(20, 20));
        clicks.add(new Point(22, 17));
        clicks.add(new Point(25, 15));
        clicks.add(new Point(29, 12));
        clicks.add(new Point(32, 8));
        clicks.add(new Point(34, 3));
        clicks.add(new Point(35, -1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(33, 3));

        String trackName = "Circuit";

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
