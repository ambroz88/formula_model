package com.ambroz.formula.gamemodel.datamodel;

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
    private String language;
    private int stage;

    public CoreModel(Paper gamePaper) {
        this.paper = gamePaper;
        track = new Track();
    }

    public void repaintScene() {
        //cought by drawing components
        firePropertyChange("repaint", false, true);
    }

    public void fireHint(String hintLabelProperty) {
        //cought by HintComponent
        firePropertyChange("hint", null, hintLabels.getValue(hintLabelProperty));
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        String old = getLanguage();
        this.language = language;
        hintLabels = new HintLabels(language);
        firePropertyChange("language", old, language);
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
