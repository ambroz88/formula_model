package com.ambroz.formula.gamemodel.datamodel;

import com.ambroz.formula.gamemodel.enums.Language;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.track.Track;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class CoreModel extends PropertyChanger {

    protected HintLabels hintLabels;
    private final Paper paper;
    private Track track;
    private Language language;
    private int stage;

    public CoreModel(Paper gamePaper) {
        this.paper = gamePaper;
        track = new Track();
        language = Language.English;
        hintLabels = new HintLabels(language.toString());
    }

    public void repaintScene() {
        //cought by drawing components
        firePropertyChange("repaint", false, true);
    }

    public void fireHint(String hintLabelProperty) {
        //cought by HintComponent
        firePropertyChange("hint", null, hintLabels.getValue(hintLabelProperty));
    }

    public void fireCoordinates(String coordinates) {
        //cought by CoordinatesPanel
        firePropertyChange("mouseMoving", null, coordinates);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        String old = getLanguage().toString();
        this.language = language;
        hintLabels = new HintLabels(language.toString());
        firePropertyChange("language", old, language.toString());
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Paper getPaper() {
        return paper;
    }

    public HintLabels getHintLabels() {
        return hintLabels;
    }

}
