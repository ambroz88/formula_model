package com.ambroz.formula.gamemodel;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Track;
import com.ambroz.formula.gamemodel.turns.TurnMaker;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class GameModel {

    public final static int BUILD_LEFT = 1;
    public final static int BUILD_RIGHT = 2;
    public final static int EDIT_PRESS = 3;
    public final static int EDIT_RELEASE = 4;
    public final static int FIRST_TURN = 5;
    public final static int NORMAL_TURN = 6;
    public final static int AUTO_CRASH = 7;
    public final static int AUTO_FINISH = 8;
    public final static int GAME_OVER = 9;

    private final Paper paper;
    private final TurnMaker turnMaker;
    private final PropertyChangeSupport prop;

    private Track track;
    private String language;
    private int stage;

    public GameModel() {
        paper = new Paper();
        turnMaker = new TurnMaker(this);
        track = new Track();
        prop = new PropertyChangeSupport(this);
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

    /**
     * Method for clearing formulas and points.
     */
    public void resetPlayers() {
        turnMaker.getFormula(1).reset();
        turnMaker.resetTurns();

        turnMaker.startPosition(getTrack().getStart());
        repaintScene();
    }

    private void repaintScene() {
        //cought by Draw
        firePropertyChange("repaint", false, true);
    }

    public Paper getPaper() {
        return paper;
    }

    public TurnMaker getTurnMaker() {
        return turnMaker;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
//        hintLabels = new HintLabels(language);
        prop.firePropertyChange("language", null, language);
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void fireLoadTrack() {
        //cought by TracksComponent
        firePropertyChange("loadTrack", false, true);
    }

    public void firePropertyChange(String prop, Object oldValue, Object newValue) {
        this.prop.firePropertyChange(prop, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        prop.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        prop.removePropertyChangeListener(listener);
    }

}
