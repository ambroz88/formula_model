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
public class CrashTest {

    private RaceModel model;

    @Before
    public void init() {
        model = new RaceModel(new Paper());
    }

    @Test
    public void doubleCrash() {
        List<Point> clicks = new ArrayList<>();
        clicks.add(new Point(12, 24));
        clicks.add(new Point(13, 21));
        clicks.add(new Point(15, 18));
        clicks.add(new Point(18, 16));
        clicks.add(new Point(22, 15));
        clicks.add(new Point(27, 13));
        clicks.add(new Point(31, 12));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(28, 14));
        clicks.add(new Point(29, 16));
        clicks.add(new Point(29, 19));
        clicks.add(new Point(30, 23));
        clicks.add(new Point(32, 26));
        clicks.add(new Point(33, 28));
        clicks.add(new Point(35, 29));
        clicks.add(new Point(38, 29));
        clicks.add(new Point(40, 28));
        clicks.add(new Point(41, 26));
        clicks.add(new Point(41, 23));
        clicks.add(new Point(42, 19));
        clicks.add(new Point(44, 14));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(41, 17));
        clicks.add(new Point(39, 16));
        clicks.add(new Point(36, 14));
        clicks.add(new Point(32, 13));
        clicks.add(new Point(27, 11));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(31, 11));
        clicks.add(new Point(32, 9));

        String trackName = "Curved";
        runTurns(trackName, clicks);
        Assert.assertEquals(model.getTurnMaker().getActiveFormula().getLast(), clicks.get(clicks.size() - 1));
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
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(1, 1));
        clicks.add(new Point(47, 32));

        String trackName = "Snail";

        runTurns(trackName, clicks);
        Assert.assertEquals(model.getTurnMaker().getActiveFormula().getLast(), clicks.get(clicks.size() - 1));
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

        runTurns(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
        Assert.assertTrue(new Point(13, 21).isEqual(model.getTurnMaker().getActiveFormula().getLast()));
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

        runTurns(trackName, clicks);
        Assert.assertTrue(model.getStage() == RaceModel.GAME_OVER);
        Assert.assertTrue(new Point(54, 44).isEqual(model.getTurnMaker().getActiveFormula().getLast()));
    }

    private void runTurns(String trackName, List<Point> clicks) {
        model.prepareGame(TrackIO.trackFromJSON(trackName));
        model.startGame();

        for (Point p : clicks) {
            model.moveWithPlayer(p);
        }
    }
}
