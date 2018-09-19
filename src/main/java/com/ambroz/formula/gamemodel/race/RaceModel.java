package com.ambroz.formula.gamemodel.race;

import com.ambroz.formula.gamemodel.datamodel.CoreModel;
import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.track.Track;
import static com.ambroz.formula.gamemodel.track.TrackBuilder.BUILD_LEFT;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class RaceModel extends CoreModel {

    public static final int FIRST_TURN = 5;
    public static final int NORMAL_TURN = 6;
    public static final int AUTO_CRASH = 7;
    public static final int AUTO_FINISH = 8;
    public static final int GAME_OVER = 9;

    private final TurnMaker turnMaker;

    public RaceModel(Paper gamePaper) {
        super(gamePaper);
        turnMaker = new TurnMaker(this);
    }

    public void moveWithPlayer(Point click) {
        click.toGridUnits(getPaper().getGridSize());

        if (!getPaper().isOutside(click)) {

            if (getStage() == FIRST_TURN) {
                turnMaker.firstTurn(click);
            } else if (getStage() > FIRST_TURN) {
                turnMaker.turn(click);
            }

            checkWinner();
            repaintScene();
        }
    }

    private void checkWinner() {
        if (turnMaker.getFormula(1).getWin() == true) {
            winnerAnnouncement();
        }
    }

    private void winnerAnnouncement() {
        getTurnMaker().resetTurns();
    }

    public void prepareGame(Track track) {
        setTrack(track);
        resetGame();
    }

    /**
     * Method for clearing whole scene: track, formulas and points.
     */
    public void resetGame() {
        setStage(FIRST_TURN);
        resetPlayers();
    }

    public void endGame() {
        setStage(BUILD_LEFT);
        firePropertyChange("buildTrack", false, true); // cought by TrackMenu
        firePropertyChange("startDraw", false, true); // cought by TrackMenu and Draw
        resetPlayers();
    }

    /**
     * Method for clearing formulas and points.
     */
    public void resetPlayers() {
        turnMaker.getFormula(1).reset();
        turnMaker.resetTurns();

        turnMaker.startPosition(getTrack().getStart());
        repaintScene();
    }

    public void fireTrackReady(boolean ready) {
        // cought by StartMenu, TrackMenu
        firePropertyChange("startVisible", !ready, ready);
        if (ready) {
//            fireHint(HintLabels.TRACK_READY);
            repaintScene();
        } else {
//            fireHint(HintLabels.EMPTY);
        }
    }

    public TurnMaker getTurnMaker() {
        return turnMaker;
    }

    public void fireLoadTrack() {
        //cought by TracksComponent
        firePropertyChange("loadTrack", false, true);
    }

}
