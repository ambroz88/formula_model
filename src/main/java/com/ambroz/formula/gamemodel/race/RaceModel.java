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
        setStage(FIRST_TURN);
    }

    public void moveWithPlayer(Point click) {
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
        if (turnMaker.getActiveFormula().getWin() == true) {
            winnerAnnouncement();
        }
    }

    private void winnerAnnouncement() {
        getTurnMaker().resetTurns();
//        firePropertyChange(RACE_WINNER, "", getTurnMaker().createWinnerMessage());
        firePropertyChange(RACE_WINNER, "", getTurnMaker().getActiveFormula().getName() + " " + getHintLabels().getValue(HintLabels.WINNER));
        setStage(GAME_OVER);
    }

    public void prepareGame(Track track) {
        if (!track.isEmpty()) {
            setTrack(track);
            resetPlayers();
        }
    }

    /**
     * Method for clearing formulas and points.
     */
    public void resetPlayers() {
        for (int i = 1; i <= turnMaker.getFormulaCount(); i++) {
            turnMaker.getFormula(i).reset();
        }
        turnMaker.resetTurns();
        repaintScene();
    }

    public void startGame() {
        turnMaker.startPosition(getTrack().getStart());
        setStage(FIRST_TURN);

        fireHint(HintLabels.START_POSITION);
        firePropertyChange(RACE_NEW_GAME, false, true);
        repaintScene();
    }

    /**
     * Fire information about who crashed and how fast.
     *
     * @param count is number of rounds that player will be wait for other player
     */
    public void fireCrash(int count) {
        String text = hintLabels.getValue(HintLabels.OUCH) + " " + turnMaker.getActiveFormula().getName() + " "
                + hintLabels.getValue(HintLabels.CRASH) + " " + count + "!!!";
        //cought by HintPanel
        firePropertyChange(RACE_CRASH, "", text);
    }

    public TurnMaker getTurnMaker() {
        return turnMaker;
    }

    public void fireLoadTrack() {
        //cought by TrackListComponent
        firePropertyChange(RACE_LOAD_TRACK, false, true);
        fireHint(HintLabels.START_POSITION);
    }

}
