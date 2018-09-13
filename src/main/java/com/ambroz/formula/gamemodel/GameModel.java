package com.ambroz.formula.gamemodel;

import com.ambroz.formula.gamemodel.datamodel.Paper;
import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Track;
import com.ambroz.formula.gamemodel.track.TrackBuilder;
import static com.ambroz.formula.gamemodel.track.TrackBuilder.BUILD_LEFT;
import com.ambroz.formula.gamemodel.turns.TurnMaker;
import com.ambroz.formula.gamemodel.utils.PropertyChanger;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class GameModel extends PropertyChanger {

    public static final int FIRST_TURN = 5;
    public static final int NORMAL_TURN = 6;
    public static final int AUTO_CRASH = 7;
    public static final int AUTO_FINISH = 8;
    public static final int GAME_OVER = 9;

    private final Paper paper;
    private final TurnMaker turnMaker;
    private final TrackBuilder trackBuilder;

    private Track track;
    private String language;
    private int stage;

    public GameModel() {
        paper = new Paper();
        turnMaker = new TurnMaker(this);
        track = new Track();
        trackBuilder = new TrackBuilder(this);
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

        turnMaker.startPosition(getRaceTrack().getStart());
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

    public void repaintScene() {
        //cought by Draw
        firePropertyChange("repaint", false, true);
    }

    public Paper getPaper() {
        return paper;
    }

    public TrackBuilder getTrackBuilder() {
        return trackBuilder;
    }

    public TurnMaker getTurnMaker() {
        return turnMaker;
    }

    public Track getRaceTrack() {
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
        getTrackBuilder().setLanguage(language);
//        hintLabels = new HintLabels(language);
        firePropertyChange("language", null, language);
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

}
