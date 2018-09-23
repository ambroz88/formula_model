package com.ambroz.formula.gamemodel.race;

import com.ambroz.formula.gamemodel.datamodel.CoreModel;
import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.track.Track;

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
            fireHint(HintLabels.EMPTY);

            if (getStage() == FIRST_TURN) {
                turnMaker.firstTurn(click);
            } else if (getStage() > FIRST_TURN) {
                turnMaker.turn(click);
            }

            checkWinner();
            repaintScene();
        } else {
            fireHint(HintLabels.OUTSIDE);
        }
    }

    private void checkWinner() {
        if (turnMaker.getFormula(1).getWin() == true) {
            winnerAnnouncement();
        }
    }

    private void winnerAnnouncement() {
        getTurnMaker().resetTurns();
//        firePropertyChange("winner", "", getTurnMaker().createWinnerMessage());
        firePropertyChange("winner", "", "Player  " + getHintLabels().getValue(HintLabels.WINNER));
        setStage(GAME_OVER);
    }

    public void prepareGame(Track track) {
        setTrack(track);
        resetGame();
        fireHint(HintLabels.START_POSITION);
    }

    /**
     * Method for clearing whole scene: track, formulas and points.
     */
    public void resetGame() {
        setStage(FIRST_TURN);
        resetPlayers();
    }

    public void endGame() {
        setStage(FIRST_TURN);
        firePropertyChange("buildTrack", false, true); // cought by TrackMenu
        firePropertyChange("startDraw", false, true); // cought by TrackMenu and Draw
        resetPlayers();
    }

    /**
     * Fire information about who crashed and how fast.
     *
     * @param count is number of rounds that player will be wait for other player
     */
    public void fireCrash(int count) {
//        String text = hintLabels.getValue(HintLabels.OUCH) + " " + turnMaker.getFormula(turnMaker.getActID()).getName() + " "
        String text = hintLabels.getValue(HintLabels.OUCH) + " Player "
                + hintLabels.getValue(HintLabels.CRASH) + " " + count + "!!!";
        //cought by HintPanel
        firePropertyChange("crash", "", text);
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

    public TurnMaker getTurnMaker() {
        return turnMaker;
    }

    public void fireLoadTrack() {
        //cought by TrackListComponent
        firePropertyChange("loadTrack", false, true);
        fireHint(HintLabels.START_POSITION);
    }

}
