package com.ambroz.formula.test.race;

import java.util.ArrayList;
import java.util.List;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.enums.Language;
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
        model.setLanguage(Language.English);
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

    @Test
    public void crashBeforeFinish() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(53, 55));
        clicks.add(new Point(52, 52));
        clicks.add(new Point(50, 49));
        clicks.add(new Point(49, 45));
        clicks.add(new Point(47, 40));
        clicks.add(new Point(46, 34));
        clicks.add(new Point(44, 29));
        clicks.add(new Point(47, 32));

        String trackName = "Snail";

        assertSituation(trackName, clicks);
    }

    @Test
    public void crashToStartAfterFinish() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(14, 20));
        clicks.add(new Point(17, 19));
        clicks.add(new Point(20, 17));
        clicks.add(new Point(24, 16));
        clicks.add(new Point(27, 16));
        clicks.add(new Point(29, 15));
        clicks.add(new Point(30, 13));
        clicks.add(new Point(30, 10));
        clicks.add(new Point(31, 8));
        clicks.add(new Point(31, 7));
        clicks.add(new Point(30, 5));
        clicks.add(new Point(28, 4));
        clicks.add(new Point(25, 4));
        clicks.add(new Point(21, 5));
        clicks.add(new Point(18, 5));
        clicks.add(new Point(14, 6));
        clicks.add(new Point(11, 8));
        clicks.add(new Point(9, 11));
        clicks.add(new Point(8, 15));
        clicks.add(new Point(8, 18));
        clicks.add(new Point(9, 20));
        clicks.add(new Point(11, 21));
        clicks.add(new Point(14, 21));

        String trackName = "Circuit";

        assertSituation(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
    }

    @Test
    public void crashAfterFinish() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(55, 51));
        clicks.add(new Point(52, 50));
        clicks.add(new Point(49, 48));
        clicks.add(new Point(47, 45));
        clicks.add(new Point(44, 41));
        clicks.add(new Point(42, 38));
        clicks.add(new Point(41, 34));
        clicks.add(new Point(41, 31));
        clicks.add(new Point(42, 29));
        clicks.add(new Point(44, 28));
        clicks.add(new Point(45, 28));
        clicks.add(new Point(47, 29));
        clicks.add(new Point(48, 31));
        clicks.add(new Point(50, 34));
        clicks.add(new Point(51, 36));
        clicks.add(new Point(53, 39));
        clicks.add(new Point(54, 43));
        clicks.add(new Point(56, 48));

        String trackName = "Test Finish";

        assertSituation(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
    }

    private void assertSituation(String trackName, List<Point> clicks) {
        model.prepareGame(TrackIO.trackFromJSON(trackName));

        for (Point p : clicks) {
            model.moveWithPlayer(p);
        }

        Assert.assertEquals(model.getTurnMaker().getFormula(1).getLast(), clicks.get(clicks.size() - 1));
    }
}
